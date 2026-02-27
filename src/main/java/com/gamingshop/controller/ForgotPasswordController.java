package com.gamingshop.controller;

import com.gamingshop.entity.NguoiDung;
import com.gamingshop.entity.SanPham;
import com.gamingshop.repository.NguoiDungRepository;
import com.gamingshop.repository.SanPhamRepository;
import com.gamingshop.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ForgotPasswordController {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private EmailService emailService;

    // ================================================================
    // QUÊN MẬT KHẨU - Hiển thị trang
    // ================================================================
    @GetMapping("/forgot-password")
    public String showForgotPage() {
        return "forgot-password";
    }

    // ================================================================
    // QUÊN MẬT KHẨU - Xử lý reset
    // ================================================================
    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam String email,
            RedirectAttributes redirect) {

        Optional<NguoiDung> opt = nguoiDungRepository.findByEmail(email.trim());

        if (opt.isEmpty()) {
            redirect.addFlashAttribute("error", "Email này chưa đăng ký tài khoản!");
            return "redirect:/forgot-password";
        }

        NguoiDung user = opt.get();

        // Reset mật khẩu về "1"
        user.setPassword("1");
        nguoiDungRepository.save(user);

        // Gửi email thông báo
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getTen());
            redirect.addFlashAttribute("success",
                    "✅ Mật khẩu đã reset! Kiểm tra email <strong>" + email + "</strong> để đăng nhập bằng mật khẩu mới.");
        } catch (Exception e) {
            redirect.addFlashAttribute("warning",
                    "Mật khẩu đã reset thành '1' nhưng không gửi được email. Hãy đăng nhập ngay!");
        }

        return "redirect:/forgot-password";
    }

    // ================================================================
    // NEWSLETTER - Đăng ký nhận ưu đãi (AJAX)
    // ================================================================
    @PostMapping("/newsletter/subscribe")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> subscribe(@RequestParam String email) {

        email = email.trim().toLowerCase();

        // Validate định dạng email
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[a-z]{2,}$")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Email không hợp lệ!"
            ));
        }

        Optional<NguoiDung> existing = nguoiDungRepository.findByEmail(email);

        if (existing.isPresent()) {
            // Đã có tài khoản → chỉ gửi mail ưu đãi, không tạo thêm
            try {
                emailService.sendNewsletterWelcomeEmail(email, getFeatured());
            } catch (Exception ignored) {}

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Email đã có tài khoản! Chúng tôi đã gửi thông tin ưu đãi vào hộp thư của bạn."
            ));
        }

        // Tạo tài khoản mới, password mặc định = "1"
        NguoiDung newUser = new NguoiDung();
        newUser.setEmail(email);
        newUser.setPassword("1");
        newUser.setTen(email.split("@")[0]); // lấy phần trước @ làm tên
        newUser.setRole("USER");
        nguoiDungRepository.save(newUser);

        // Gửi email chào mừng + sản phẩm nổi bật
        try {
            emailService.sendNewsletterWelcomeEmail(email, getFeatured());
        } catch (Exception ignored) {}

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "🎉 Đăng ký thành công! Kiểm tra email để nhận ưu đãi và thông tin tài khoản."
        ));
    }

    // Lấy 2 sản phẩm đầu để đưa vào mail
    private List<SanPham> getFeatured() {
        try {
            return sanPhamRepository.findAll(PageRequest.of(0, 2)).getContent();
        } catch (Exception e) {
            return List.of();
        }
    }
}