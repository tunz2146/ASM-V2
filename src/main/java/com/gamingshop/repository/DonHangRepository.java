package com.gamingshop.repository;

import com.gamingshop.entity.DonHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {

    // ✅ Dùng method đơn giản, KHÔNG kết hợp OrderByXxx vì Pageable đã có Sort
    Page<DonHang> findByTinhTrang(String tinhTrang, Pageable pageable);

    // Đếm theo trạng thái
    long countByTinhTrang(String tinhTrang);

    // Tổng doanh thu đơn đã giao
    @Query("SELECT COALESCE(SUM(d.tongTien), 0) FROM DonHang d WHERE d.tinhTrang = 'DA_GIAO'")
    Long sumDoanhThu();

    // User: lấy đơn theo email
    @Query("SELECT d FROM DonHang d WHERE d.nguoiDung.email = :email ORDER BY d.ngayDat DESC")
    List<DonHang> findByNguoiDungEmail(@Param("email") String email);

    // User: lấy đơn theo userId
    List<DonHang> findByNguoiDung_IdOrderByNgayDatDesc(Long userId);
}