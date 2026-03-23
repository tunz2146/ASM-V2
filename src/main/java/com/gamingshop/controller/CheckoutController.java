package com.gamingshop.controller;

import com.gamingshop.entity.DonHang;
import com.gamingshop.entity.NguoiDung;
import com.gamingshop.repository.NguoiDungRepository;
import com.gamingshop.service.DonHangService;
import com.gamingshop.service.GioHangService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class CheckoutController {

    @Autowired private GioHangService  gioHangService;
    @Autowired private DonHangService  donHangService;
    @Autowired private NguoiDungRepository nguoiDungRepository;

    // ===== TRANG CHECKOUT =====
    @GetMapping("/checkout")
    public String checkoutPage(Model model, Authentication auth, HttpSession session) {
        String email = auth.getName();
        NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(null);

        // Điền sẵn thông tin từ đơn hàng cũ
        String lastName = null, lastPhone = null, lastAddress = null;
        String lastCity = null, lastDistrict = null, lastWard = null, lastNote = null;

        List<DonHang> prevOrders = donHangService.getOrdersByEmail(email);
        if (!prevOrders.isEmpty()) {
            DonHang lastOrder = prevOrders.get(0);
            String json = lastOrder.getThongTinGiaoHang();
            if (json != null && !json.isEmpty()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<?, ?> info = mapper.readValue(json, Map.class);
                    lastName     = (String) info.get("fullName");
                    lastPhone    = (String) info.get("phone");
                    lastAddress  = (String) info.get("address");
                    lastCity     = (String) info.get("city");
                    lastDistrict = (String) info.get("district");
                    lastWard     = (String) info.get("ward");
                    lastNote     = (String) info.get("note");
                } catch (Exception ignored) {}
            }
        }
        // Nếu không có đơn cũ → dùng thông tin profile tên và sđt của user
        if (lastName  == null && user != null) lastName  = user.getTen(); 
        if (lastPhone == null && user != null) lastPhone = user.getSoDienThoai();

        // ✅ Đọc coupon từ Session
        String appliedCoupon  = (String) session.getAttribute("appliedCoupon");
        Long   discountAmount = (Long)   session.getAttribute("discountAmount");
        if (discountAmount == null) discountAmount = 0L;

        long subtotal   = gioHangService.getTotalAmount(email);
        long shipping   = subtotal >= 1_000_000 ? 0L : 30_000L;
        long finalTotal = Math.max(0, subtotal - discountAmount + shipping);

        model.addAttribute("cartItems",       gioHangService.getCartByEmail(email));
        model.addAttribute("subtotal",        subtotal);
        model.addAttribute("shippingFee",     shipping);
        model.addAttribute("discountAmount",  discountAmount);
        model.addAttribute("appliedCoupon",   appliedCoupon);
        model.addAttribute("totalAmount",     finalTotal);   // ← Tổng đã trừ giảm giá
        model.addAttribute("pageTitle",       "Thanh toán - Gaming Shop");
        model.addAttribute("userEmail",       email);
        model.addAttribute("prefillName",     lastName     != null ? lastName     : "");
        model.addAttribute("prefillPhone",    lastPhone    != null ? lastPhone    : "");
        model.addAttribute("prefillAddress",  lastAddress  != null ? lastAddress  : "");
        model.addAttribute("prefillCity",     lastCity     != null ? lastCity     : "");
        model.addAttribute("prefillDistrict", lastDistrict != null ? lastDistrict : "");
        model.addAttribute("prefillWard",     lastWard     != null ? lastWard     : "");
        model.addAttribute("prefillNote",     lastNote     != null ? lastNote     : "");
        model.addAttribute("hasPreviousOrder", !prevOrders.isEmpty());

        return "checkout";
    }

    // ===== API: LƯU ĐƠN HÀNG =====
    @PostMapping("/checkout/place-order")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> placeOrder(
            @RequestBody Map<String, String> body,
            Authentication auth,
            HttpSession session) {

        try {
            String email    = auth.getName();
            String fullName = body.get("fullName");
            String phone    = body.get("phone");
            String address  = body.get("address");
            String city     = body.get("city");
            String district = body.get("district");
            String ward     = body.get("ward");
            String note     = body.get("note");

            // Validate
            if (fullName == null || fullName.trim().isEmpty())
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Vui lòng nhập họ và tên"));
            if (phone == null || phone.trim().isEmpty())
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Vui lòng nhập số điện thoại"));
            if (!phone.trim().matches("^(0|\\+84)[0-9]{8,10}$"))
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Số điện thoại không hợp lệ (VD: 0794612606)"));
            if (address == null || address.trim().isEmpty())
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Vui lòng nhập địa chỉ giao hàng"));

            // ✅ Đọc discount từ Session
            Long discountAmount = (Long) session.getAttribute("discountAmount");
            String appliedCoupon = (String) session.getAttribute("appliedCoupon");
            if (discountAmount == null) discountAmount = 0L;

            // Build JSON thông tin giao hàng
            ObjectMapper mapper = new ObjectMapper();
            String thongTinGiaoHang = mapper.writeValueAsString(Map.of(
                "fullName", fullName.trim(),
                "phone",    phone.trim(),
                "address",  address.trim(),
                "city",     city     != null ? city.trim()     : "",
                "district", district != null ? district.trim() : "",
                "ward",     ward     != null ? ward.trim()     : "",
                "note",     note     != null ? note.trim()     : ""
            ));

            // ✅ Tạo đơn hàng có trừ giảm giá
            DonHang donHang = donHangService.createOrder(
                email, thongTinGiaoHang, "GHN", discountAmount, appliedCoupon
            );

            // ✅ Xóa coupon khỏi Session sau khi đặt hàng thành công
            session.removeAttribute("appliedCoupon");
            session.removeAttribute("discountAmount");

            return ResponseEntity.ok(Map.of(
                "success",   true,
                "orderId",   donHang.getId(),
                "orderCode", "GS-" + String.format("%06d", donHang.getId()),
                "total",     donHang.getTongTien(),
                "message",   "Đặt hàng thành công!"
            ));

        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("trống"))
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Giỏ hàng của bạn đang trống!"));
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Lỗi hệ thống: " + msg));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Đã xảy ra lỗi, vui lòng thử lại!"));
        }
    }
}