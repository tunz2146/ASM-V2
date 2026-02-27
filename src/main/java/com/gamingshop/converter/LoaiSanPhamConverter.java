package com.gamingshop.converter;

import com.gamingshop.entity.LoaiSanPham;
import com.gamingshop.repository.LoaiSanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LoaiSanPhamConverter implements Converter<String, LoaiSanPham> {

    @Autowired
    private LoaiSanPhamRepository loaiSanPhamRepository;

    @Override
    public LoaiSanPham convert(String id) {
        if (id == null || id.isEmpty()) return null;
        return loaiSanPhamRepository.findById(Long.parseLong(id)).orElse(null);
    }
}