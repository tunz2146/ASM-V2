package com.gamingshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private OtpInterceptor otpInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(otpInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/verify-otp",
                        "/admin/send-otp",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                );
    }
}