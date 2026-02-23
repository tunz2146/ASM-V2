package com.gamingshop.repository;

import com.gamingshop.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Long> {

    // Lấy toàn bộ giỏ hàng của user
    List<GioHang> findByNguoiDung_Id(Long userId);

    // Tìm 1 item cụ thể trong giỏ (user + sản phẩm)
    Optional<GioHang> findByNguoiDung_IdAndSanPham_Id(Long userId, Long productId);

    // Đếm số lượng item trong giỏ (hiển thị badge)
    @Query("SELECT COALESCE(SUM(g.soLuong), 0) FROM GioHang g WHERE g.nguoiDung.id = :userId")
    Integer countTotalItemsByUserId(Long userId);

    // Xóa toàn bộ giỏ hàng của user
    void deleteByNguoiDung_Id(Long userId);
}