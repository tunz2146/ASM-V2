package com.gamingshop.controller;

import com.gamingshop.service.OtpService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminOtpController {

    @Autowired
    private OtpService otpService;

    @GetMapping("/verify-otp")
    public String verifyOtpPage(HttpSession session, Principal principal) {

        if (session.getAttribute("OTP_VERIFIED") != null) {
            return "redirect:/admin/dashboard";
        }

        String otp = otpService.generateOtp();
        session.setAttribute("OTP", otp);
        session.setAttribute("OTP_TIME", System.currentTimeMillis());

        // Email lấy từ user đăng nhập
        String email = principal.getName();
        otpService.sendOtp(email, otp);

        return "admin/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String otpInput,
            HttpSession session,
            Model model
    ) {
        String otp = (String) session.getAttribute("OTP");
        Long time = (Long) session.getAttribute("OTP_TIME");

        if (otp == null || time == null ||
            System.currentTimeMillis() - time > 5 * 60 * 1000) {
            model.addAttribute("error", "OTP đã hết hạn");
            return "admin/verify-otp";
        }

        if (!otp.equals(otpInput)) {
            model.addAttribute("error", "OTP không đúng");
            return "admin/verify-otp";
        }

        session.setAttribute("OTP_VERIFIED", true);
        session.removeAttribute("OTP");

        return "redirect:/admin/dashboard";
    }
}
