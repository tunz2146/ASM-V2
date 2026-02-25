package com.gamingshop.controller;

import com.gamingshop.entity.DonHang;
import com.gamingshop.service.DonHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
public class UserOrderController {

    @Autowired
    private DonHangService donHangService;

    // Danh sách đơn hàng của user
    @GetMapping("")
    public String myOrders(Model model, Authentication auth) {
        String email = auth.getName();
        List<DonHang> orders = donHangService.getOrdersByEmail(email);
        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Đơn hàng của tôi");
        return "user/orders";
    }

    // Chi tiết 1 đơn hàng
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model, Authentication auth) {
        String email = auth.getName();
        DonHang order = donHangService.getOrderById(id);

        // Chỉ xem được đơn của mình
        if (order == null || !order.getNguoiDung().getEmail().equals(email)) {
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        model.addAttribute("pageTitle", "Đơn hàng #GS-" + id);
        return "user/order-detail";
    }

    // Hủy đơn (AJAX)
    @PostMapping("/{id}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        boolean ok = donHangService.cancelOrder(id, email);
        if (ok) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã hủy đơn hàng thành công"));
        }
        return ResponseEntity.badRequest().body(Map.of(
            "success", false,
            "message", "Không thể hủy đơn hàng này (chỉ hủy được khi đang chờ xác nhận)"
        ));
    }
}