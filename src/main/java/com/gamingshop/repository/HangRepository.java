package com.gamingshop.repository;

import com.gamingshop.entity.Hang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HangRepository extends JpaRepository<Hang, Long> {
    // Hiện tại chưa cần hàm custom nào, để trống là được
}