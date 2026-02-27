package com.gamingshop.controller;

import com.gamingshop.entity.GioHang;
import com.gamingshop.service.GioHangService;
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

    // ===== TRANG GIỎ HÀNG =====
    @GetMapping("/cart")
    public String cartPage(Model model, Authentication auth) {
        String email = auth.getName();
        List<GioHang> cartItems = gioHangService.getCartByEmail(email);
        long totalAmount = gioHangService.getTotalAmount(email);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("pageTitle", "Giỏ hàng - Gaming Shop");
        return "cart";
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
            response.put("success", true);
            response.put("message", "Đã thêm vào giỏ hàng!");
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
        int count = gioHangService.countCartItems(auth.getName());

        response.put("success", true);
        response.put("message", result);
        response.put("total", total);
        response.put("cartCount", count);

        // Tính lại thành tiền của item
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
        response.put("success", true);
        response.put("cartCount", gioHangService.countCartItems(auth.getName()));
        response.put("total", gioHangService.getTotalAmount(auth.getName()));
        return ResponseEntity.ok(response);
    }

    // ===== API: XÓA TOÀN BỘ GIỎ =====
    @PostMapping("/cart/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearCart(Authentication auth) {
        gioHangService.clearCart(auth.getName());
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("cartCount", 0);
        return ResponseEntity.ok(response);
    }

    
}
