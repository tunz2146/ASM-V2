package com.gamingshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "Hang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_hang")
    private String tenHang;

    @OneToMany(mappedBy = "hang")
    private List<SanPham> sanPhams;

    public void setTenHang(String tenHang) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getSanPhams() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}