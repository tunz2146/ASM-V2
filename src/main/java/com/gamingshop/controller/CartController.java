package com.gamingshop.controller;

import com.gamingshop.entity.GioHang;
import com.gamingshop.service.GioHangService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    @Autowired
    private GioHangService gioHangService;

    // ===== Danh sách mã giảm giá hợp lệ =====
    // (Sau này có thể chuyển sang DB/bảng Coupon)
    private static final Map<String, Object[]> COUPONS = Map.of(
        "GAMING10",  new Object[]{"percent", 10,  500_000L},   // Giảm 10%, đơn tối thiểu 500k
        "MOUSE20",   new Object[]{"percent", 20,  800_000L},   // Giảm 20%, đơn tối thiểu 800k
        "FREE_SHIP", new Object[]{"ship",    30000, 0L}         // Miễn phí ship
    );

    // ===== TRANG GIỎ HÀNG =====
    @GetMapping("/cart")
    public String cartPage(Model model, Authentication auth, HttpSession session) {
        String email = auth.getName();
        List<GioHang> cartItems = gioHangService.getCartByEmail(email);
        long totalAmount = gioHangService.getTotalAmount(email);

        // Lấy thông tin coupon đang áp dụng từ session (nếu có)
        String appliedCoupon   = (String) session.getAttribute("appliedCoupon");
        Long   discountAmount  = (Long)   session.getAttribute("discountAmount");

        model.addAttribute("cartItems",       cartItems);
        model.addAttribute("totalAmount",     totalAmount);
        model.addAttribute("appliedCoupon",   appliedCoupon);
        model.addAttribute("discountAmount",  discountAmount != null ? discountAmount : 0L);
        model.addAttribute("pageTitle",       "Giỏ hàng - Gaming Shop");
        return "cart";
    }

    // ===== API: ÁP DỤNG MÃ GIẢM GIÁ → LƯU SESSION =====
    @PostMapping("/cart/apply-coupon")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> applyCoupon(
            @RequestParam String code,
            Authentication auth,
            HttpSession session) {

        Map<String, Object> res = new HashMap<>();
        String upper = code.trim().toUpperCase();

        long subtotal = gioHangService.getTotalAmount(auth.getName());

        if (!COUPONS.containsKey(upper)) {
            // Xóa coupon cũ nếu nhập sai
            session.removeAttribute("appliedCoupon");
            session.removeAttribute("discountAmount");
            res.put("success", false);
            res.put("message", "Mã không hợp lệ hoặc đã hết hạn!");
            return ResponseEntity.ok(res);
        }

        Object[] coupon = COUPONS.get(upper);
        String type    = (String) coupon[0];
        int    value   = (Integer) coupon[1];
        long   minOrder = (Long) coupon[2];

        if (subtotal < minOrder) {
            res.put("success", false);
            res.put("message", "Đơn hàng tối thiểu " + formatVND(minOrder) + " để dùng mã này!");
            return ResponseEntity.ok(res);
        }

        // Tính tiền giảm
        long discountAmount = 0;
        if ("percent".equals(type)) {
            discountAmount = subtotal * value / 100;
        } else if ("ship".equals(type)) {
            discountAmount = value;
        }

        // ✅ Lưu vào Session — sẽ dùng lại ở trang Checkout
        session.setAttribute("appliedCoupon",  upper);
        session.setAttribute("discountAmount", discountAmount);

        long finalTotal = Math.max(0, subtotal - discountAmount);

        res.put("success",        true);
        res.put("code",           upper);
        res.put("discountAmount", discountAmount);
        res.put("finalTotal",     finalTotal);
        res.put("message",        "Áp dụng mã thành công! Giảm " + formatVND(discountAmount));
        return ResponseEntity.ok(res);
    }

    // ===== API: HỦY MÃ GIẢM GIÁ =====
    @PostMapping("/cart/remove-coupon")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeCoupon(
            Authentication auth,
            HttpSession session) {

        session.removeAttribute("appliedCoupon");
        session.removeAttribute("discountAmount");

        long subtotal = gioHangService.getTotalAmount(auth.getName());

        Map<String, Object> res = new HashMap<>();
        res.put("success",    true);
        res.put("finalTotal", subtotal);
        return ResponseEntity.ok(res);
    }

    // ===== API: THÊM VÀO GIỎ (AJAX) =====
    @PostMapping("/cart/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            Authentication auth) {

        Map<String, Object> response = new HashMap<>();

        if (auth == null || !auth.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập để thêm vào giỏ hàng!");
            response.put("redirect", "/login");
            return ResponseEntity.ok(response);
        }

        String result = gioHangService.addToCart(auth.getName(), productId, quantity);

        if ("OK".equals(result)) {
            int newCount = gioHangService.countCartItems(auth.getName());
            response.put("success",   true);
            response.put("message",   "Đã thêm vào giỏ hàng!");
            response.put("cartCount", newCount);
        } else {
            response.put("success", false);
            response.put("message", result);
        }

        return ResponseEntity.ok(response);
    }

    // ===== API: CẬP NHẬT SỐ LƯỢNG (AJAX) =====
    @PostMapping("/cart/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCart(
            @RequestParam Long cartItemId,
            @RequestParam int quantity,
            Authentication auth) {

        Map<String, Object> response = new HashMap<>();
        String result = gioHangService.updateQuantity(auth.getName(), cartItemId, quantity);

        List<GioHang> cartItems = gioHangService.getCartByEmail(auth.getName());
        long total = gioHangService.getTotalAmount(auth.getName());
        int count  = gioHangService.countCartItems(auth.getName());

        response.put("success",   true);
        response.put("message",   result);
        response.put("total",     total);
        response.put("cartCount", count);

        if (!"DELETED".equals(result)) {
            cartItems.stream()
                    .filter(i -> i.getId().equals(cartItemId))
                    .findFirst()
                    .ifPresent(i -> response.put("itemTotal", i.getThanhTien()));
        }

        return ResponseEntity.ok(response);
    }

    // ===== API: XÓA 1 SẢN PHẨM (AJAX) =====
    @PostMapping("/cart/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeItem(
            @RequestParam Long cartItemId,
            Authentication auth) {

        gioHangService.removeItem(auth.getName(), cartItemId);

        Map<String, Object> response = new HashMap<>();
        response.put("success",   true);
        response.put("cartCount", gioHangService.countCartItems(auth.getName()));
        response.put("total",     gioHangService.getTotalAmount(auth.getName()));
        return ResponseEntity.ok(response);
    }

    // ===== API: XÓA TOÀN BỘ GIỎ =====
    @PostMapping("/cart/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearCart(Authentication auth) {
        gioHangService.clearCart(auth.getName());
        Map<String, Object> response = new HashMap<>();
        response.put("success",   true);
        response.put("cartCount", 0);
        return ResponseEntity.ok(response);
    }

    // Helper format tiền
    private String formatVND(long amount) {
        return String.format("%,d₫", amount).replace(',', '.');
    }
}