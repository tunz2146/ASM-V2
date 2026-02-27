package com.gamingshop.repository;

import com.gamingshop.entity.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {

    // Tìm theo email
    Optional<NguoiDung> findByEmail(String email);

    // Phân trang theo role
    Page<NguoiDung> findByRole(String role, Pageable pageable);

    // Đếm theo role
    long countByRole(String role);

    // Tìm kiếm theo tên hoặc email
    @Query("SELECT u FROM NguoiDung u WHERE " +
           "LOWER(u.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "u.soDienThoai LIKE CONCAT('%', :keyword, '%')")
    Page<NguoiDung> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}