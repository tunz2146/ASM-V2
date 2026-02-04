package com.gamingshop.repository;

import com.gamingshop.entity.LoaiSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiSanPhamRepository extends JpaRepository<LoaiSanPham, Long> {
    // Tìm loại theo slug để hiển thị tiêu đề trang web cho đẹp
    LoaiSanPham findBySlug(String slug);
}