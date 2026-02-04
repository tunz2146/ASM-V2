package com.gamingshop.controller;

import com.gamingshop.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private SanPhamService sanPhamService;

    @GetMapping("/")
    public String home(Model model) {
        // Lấy dữ liệu từ DB
        model.addAttribute("latestProducts", sanPhamService.getLatestProducts());
        
        // Title cho thẻ <title> trong base.html
        model.addAttribute("pageTitle", "Gaming Shop - Trang chủ");
        
        return "home"; // Trả về home.html
    }
}