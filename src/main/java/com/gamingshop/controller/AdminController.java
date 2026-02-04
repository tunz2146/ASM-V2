package com.gamingshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // Xử lý đường dẫn /admin/dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Sau này có thể thêm các thống kê (số đơn hàng, doanh thu...) vào đây
        model.addAttribute("pageTitle", "Admin Dashboard - Gaming Shop");
        return "admin/dashboard"; // Trả về file templates/admin/dashboard.html
    }
}