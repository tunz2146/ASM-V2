package com.gamingshop.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Loai_San_Pham")
public class LoaiSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ten; 
    
    private String slug; 

    @ManyToMany(mappedBy = "loaiSanPhams")
    private List<SanPham> sanPhams;

    public LoaiSanPham() {
    }

    public LoaiSanPham(Long id, String ten, String slug, List<SanPham> sanPhams) {
        this.id = id;
        this.ten = ten;
        this.slug = slug;
        this.sanPhams = sanPhams;
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTen() { // 👈 Đây chính là hàm đang bị thiếu
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public List<SanPham> getSanPhams() {
        return sanPhams;
    }

    public void setSanPhams(List<SanPham> sanPhams) {
        this.sanPhams = sanPhams;
    }
}