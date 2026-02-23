package com.gamingshop.controller;

import com.gamingshop.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired
    private SanPhamService sanPhamService;

    @GetMapping("/deals")
    public String deals(Model model) {
        // Lấy sản phẩm có chiết khấu > 0
        model.addAttribute("pageTitle", "Khuyến mãi Hot - Gaming Shop");
        model.addAttribute("dealProducts", sanPhamService.getLatestProducts());
        return "deals";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "Về chúng tôi - Gaming Shop");
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Liên hệ - Gaming Shop");
        return "contact";
    }

    @GetMapping("/shipping")
    public String shipping(Model model) {
        model.addAttribute("pageTitle", "Chính sách vận chuyển - Gaming Shop");
        return "policy/shipping";
    }

    @GetMapping("/warranty")
    public String warranty(Model model) {
        model.addAttribute("pageTitle", "Chính sách bảo hành - Gaming Shop");
        return "policy/warranty";
    }

    @GetMapping("/return")
    public String returnPolicy(Model model) {
        model.addAttribute("pageTitle", "Chính sách đổi trả - Gaming Shop");
        return "policy/return";
    }

    @GetMapping("/privacy")
    public String privacy(Model model) {
        model.addAttribute("pageTitle", "Chính sách bảo mật - Gaming Shop");
        return "policy/privacy";
    }

    @GetMapping("/terms")
    public String terms(Model model) {
        model.addAttribute("pageTitle", "Điều khoản sử dụng - Gaming Shop");
        return "policy/terms";
    }
}