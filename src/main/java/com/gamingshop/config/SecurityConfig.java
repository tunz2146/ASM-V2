package com.gamingshop.config;

import com.gamingshop.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // ❌ Bỏ dòng này
import org.springframework.security.crypto.password.NoOpPasswordEncoder; // ✅ Thêm dòng này
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // 👇 SỬA LẠI ĐOẠN NÀY ĐỂ DÙNG PASSWORD THƯỜNG
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Trả về NoOpPasswordEncoder (Deprecated nhưng dùng được cho test/học tập)
        return NoOpPasswordEncoder.getInstance();
    }
    
    // ... Các phần bean authenticationProvider và filterChain giữ nguyên như cũ ...
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // ... (Giữ nguyên code phần authorizeHttpRequests cũ) ...
        http
            .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/", "/home",
                "/products/**",
                "/css/**", "/js/**", "/images/**",
                "/register", "/do-register", "/login",

                // Các trang public thêm vào
                "/deals", "/about", "/contact",
                "/shipping", "/warranty", "/return",
                "/privacy", "/terms"
            ).permitAll()

            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/cart/**").authenticated()
            
            .anyRequest().authenticated()
        )
            .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/do-login")
            .defaultSuccessUrl("/", true)
            .failureUrl("/login?error=true")
            .permitAll()
        )

        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .permitAll()
        );
        
        return http.build();
    }
}