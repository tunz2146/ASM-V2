package com.gamingshop.config;

import com.gamingshop.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    // Trang chủ & sản phẩm
                    "/", "/home",
                    "/products/**",

                    // Thông tin & chính sách
                    "/deals", "/about", "/contact",
                    "/shipping", "/warranty", "/return",
                    "/privacy", "/terms",

                    // Auth
                    "/login", "/register", "/do-register",

                    //  Quên mật khẩu
                    "/forgot-password",

                    //  Đăng ký newsletter
                    "/newsletter/subscribe",

                    // Static resources
                    "/css/**", "/js/**", "/images/**",
                    "/webjars/**", "/favicon.ico"
                ).permitAll()

                // Chỉ ADMIN mới vào được /admin/**
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Các trang còn lại cần đăng nhập
                .anyRequest().authenticated()
            )

            // Cấu hình form login
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/do-login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // Cấu hình logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}