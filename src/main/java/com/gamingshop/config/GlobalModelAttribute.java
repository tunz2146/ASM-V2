package com.gamingshop.config;

import com.gamingshop.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttribute {

    @Autowired
    private GioHangService gioHangService;

    // Tự động thêm globalCartCount vào mọi Model trong toàn bộ app
    @ModelAttribute("globalCartCount")
    public int globalCartCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !auth.getName().equals("anonymousUser")) {
            return gioHangService.countCartItems(auth.getName());
        }
        return 0;
    }
}