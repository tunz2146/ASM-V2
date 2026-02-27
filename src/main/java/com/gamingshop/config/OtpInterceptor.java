package com.gamingshop.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class OtpInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();
        System.out.println("🔥 OTP CHECK: " + uri);

        if (uri.startsWith("/admin") && !uri.contains("verify-otp")) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("OTP_VERIFIED") == null) {
                response.sendRedirect("/admin/verify-otp");
                return false;
            }
        }
        return true;
    }
}

