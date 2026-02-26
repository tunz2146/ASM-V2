package com.gamingshop.controller;

import com.gamingshop.entity.NguoiDung;
import com.gamingshop.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

@Controller
public class AuthController {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @GetMapping("/login")
    public String login() {
        return "login"; // Trả về file login.html
    }

    // trang đăng ký
    @GetMapping("/register")
    public String register(org.springframework.ui.Model model) {
        // thêm object rỗng để Thymeleaf binding
        model.addAttribute("nguoiDung", new NguoiDung());
        return "register"; // file register.html sẽ tạo mới
    }

    // xử lý đăng ký
    @PostMapping("/do-register")
    public String doRegister(@ModelAttribute NguoiDung user, RedirectAttributes redirect) {
        // kiểm tra email đã tồn tại chưa
        if (nguoiDungRepository.findByEmail(user.getEmail()).isPresent()) {
            redirect.addAttribute("error", "exists");
            return "redirect:/register";
        }
        // gán quyền mặc định
        user.setRole("USER");
        // Lưu tài khoản (mật khẩu chưa mã hóa vì hệ thống hiện dùng NoOpPasswordEncoder)
        nguoiDungRepository.save(user);
        redirect.addAttribute("registered", true);
        return "redirect:/login";
    }

    // Hiển thị trang profile (mục thông tin người dùng)
    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        String email = auth.getName();
        NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(new NguoiDung());
        model.addAttribute("nguoiDung", user);
        return "profile"; // tạo file profile.html
    }

    // Cập nhật thông tin profile
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute NguoiDung formUser,
                                Authentication auth,
                                RedirectAttributes redirect) {
        String email = auth.getName();
        NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        // cập nhật từng trường cho phép thay đổi
        user.setTen(formUser.getTen());
        user.setSoDienThoai(formUser.getSoDienThoai());
        user.setGioiTinh(formUser.getGioiTinh());
        user.setNgaySinh(formUser.getNgaySinh());
        // nếu điền mật khẩu mới thì cập nhật
        if (formUser.getPassword() != null && !formUser.getPassword().isBlank()) {
            user.setPassword(formUser.getPassword());
        }
        nguoiDungRepository.save(user);
        redirect.addAttribute("updated", true);
        return "redirect:/profile";
    }
}