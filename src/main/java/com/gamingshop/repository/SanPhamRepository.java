package com.gamingshop.repository;

import com.gamingshop.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {
    
    // Tìm theo tên
    Page<SanPham> findByTenSanPhamContaining(String keyword, Pageable pageable);
    
    // 👇 HÀM MỚI: Tìm theo Slug của LoaiSanPham (Đi qua bảng trung gian)
    Page<SanPham> findDistinctByLoaiSanPhams_Slug(String slug, Pageable pageable);

    SanPham findBySlug(String slug);
}