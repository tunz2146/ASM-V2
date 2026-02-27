package com.gamingshop.config;

import com.gamingshop.converter.HangConverter;
import com.gamingshop.converter.LoaiSanPhamConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired private HangConverter hangConverter;
    @Autowired private LoaiSanPhamConverter loaiSanPhamConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(hangConverter);
        registry.addConverter(loaiSanPhamConverter);
    }
}