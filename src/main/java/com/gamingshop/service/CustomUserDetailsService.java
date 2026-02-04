package com.gamingshop.service;

import com.gamingshop.entity.NguoiDung;
import com.gamingshop.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        NguoiDung user = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String roleName = user.getRole(); 
        if (roleName == null) roleName = "USER"; // Mặc định nếu null
        
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // Lấy thẳng chuỗi "123456" hoặc "2146" từ DB
                .roles(roleName.replace("ROLE_", ""))
                .build();
    }
}