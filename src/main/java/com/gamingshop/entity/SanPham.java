package com.gamingshop.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "San_Pham")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_san_pham")
    private String tenSanPham;

    @Column(name = "gia_san_pham")
    private Long giaSanPham;

    @Column(name = "chiet_khau")
    private Integer chietKhau;

    private Integer thue;

    @Column(name = "ton_kho")
    private Integer tonKho;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "thong_so", columnDefinition = "NVARCHAR(MAX)")
    private String thongSo;

    private String slug;

    @ManyToOne
    @JoinColumn(name = "id_hang")
    private Hang hang;

    @ManyToMany
    @JoinTable(
        name = "Product_Category",
        joinColumns = @JoinColumn(name = "id_san_pham"),
        inverseJoinColumns = @JoinColumn(name = "id_loai_san_pham")
    )
    private List<LoaiSanPham> loaiSanPhams;

    // Helper method
    public Long getGiaSauGiam() {
        if (chietKhau == null || chietKhau == 0) return giaSanPham;
        return giaSanPham - (giaSanPham * chietKhau / 100);
    }

    // ==========================================
    // 👇 GETTER & SETTER THỦ CÔNG (ĐẦY ĐỦ) 
    // ==========================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }

    public Long getGiaSanPham() { return giaSanPham; }
    public void setGiaSanPham(Long giaSanPham) { this.giaSanPham = giaSanPham; }

    public Integer getChietKhau() { return chietKhau; }
    public void setChietKhau(Integer chietKhau) { this.chietKhau = chietKhau; }

    public Integer getThue() { return thue; }
    public void setThue(Integer thue) { this.thue = thue; }

    public Integer getTonKho() { return tonKho; }
    public void setTonKho(Integer tonKho) { this.tonKho = tonKho; }

    // 👇 ĐÂY LÀ HÀM BẠN ĐANG THIẾU
    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getThongSo() { return thongSo; }
    public void setThongSo(String thongSo) { this.thongSo = thongSo; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Hang getHang() { return hang; }
    public void setHang(Hang hang) { this.hang = hang; }

    public List<LoaiSanPham> getLoaiSanPhams() { return loaiSanPhams; }
    public void setLoaiSanPhams(List<LoaiSanPham> loaiSanPhams) { this.loaiSanPhams = loaiSanPhams; }
}