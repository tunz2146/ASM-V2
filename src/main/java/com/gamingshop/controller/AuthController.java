package com.gamingshop.controller;

import com.gamingshop.entity.NguoiDung;
import com.gamingshop.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    // ===== LOGIN =====
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ===== REGISTER =====
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        return "register";
    }

    @PostMapping("/do-register")
    public String doRegister(@ModelAttribute NguoiDung user, RedirectAttributes redirect) {
        if (nguoiDungRepository.findByEmail(user.getEmail()).isPresent()) {
            redirect.addAttribute("error", "exists");
            return "redirect:/register";
        }
        user.setRole("USER");
        nguoiDungRepository.save(user);
        redirect.addAttribute("registered", true);
        return "redirect:/login";
    }

    // ===== PROFILE - Hiển thị =====
    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        String email = auth.getName();
        NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(new NguoiDung());
        model.addAttribute("nguoiDung", user);
        model.addAttribute("pageTitle", "Hồ sơ cá nhân");
        return "profile";
    }

    // ===== PROFILE - Cập nhật =====
    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam(name = "ten",          required = false) String ten,
            @RequestParam(name = "soDienThoai",  required = false) String soDienThoai,
            @RequestParam(name = "gioiTinh",     required = false) String gioiTinhStr,
            @RequestParam(name = "ngaySinh",     required = false) String ngaySinhStr,
            @RequestParam(name = "newPassword",  required = false) String newPassword,
            @RequestParam(name = "confirmPassword", required = false) String confirmPassword,
            Authentication auth,
            RedirectAttributes redirect) {

        String email = auth.getName();
        NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        // Cập nhật tên
        if (ten != null && !ten.trim().isEmpty()) {
            user.setTen(ten.trim());
        }

        // Cập nhật SĐT
        if (soDienThoai != null && !soDienThoai.trim().isEmpty()) {
            user.setSoDienThoai(soDienThoai.trim());
        } else {
            user.setSoDienThoai(null);
        }

        // Cập nhật giới tính (Boolean)
        if (gioiTinhStr != null && !gioiTinhStr.isEmpty()) {
            user.setGioiTinh("true".equals(gioiTinhStr));
        }

        // Cập nhật ngày sinh
        if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                user.setNgaySinh(sdf.parse(ngaySinhStr));
            } catch (Exception ignored) {}
        } else {
            user.setNgaySinh(null);
        }

        // Đổi mật khẩu (nếu có)
        if (newPassword != null && !newPassword.isBlank()) {
            if (!newPassword.equals(confirmPassword)) {
                redirect.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
                return "redirect:/profile";
            }
            if (newPassword.length() < 6) {
                redirect.addFlashAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
                return "redirect:/profile";
            }
            user.setPassword(newPassword);
        }

        nguoiDungRepository.save(user);
        redirect.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        return "redirect:/profile";
    }
}