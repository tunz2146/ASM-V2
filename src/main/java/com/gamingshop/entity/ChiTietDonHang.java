package com.gamingshop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Chi_Tiet_Don_Hang")
public class ChiTietDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_don_hang")
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_san_pham")
    private SanPham sanPham;

    @Column(name = "gia")
    private Long gia;

    @Column(name = "khuyen_mai")
    private Integer khuyenMai;

    @Column(name = "so_luong")
    private Integer soLuong;

    // Helper: tính thành tiền
    public Long getThanhTien() {
        if (gia == null || soLuong == null) return 0L;
        if (khuyenMai != null && khuyenMai > 0) {
            return (gia - gia * khuyenMai / 100) * soLuong;
        }
        return gia * soLuong;
    }

    // ===== GETTERS & SETTERS =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }

    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }

    public Long getGia() { return gia; }
    public void setGia(Long gia) { this.gia = gia; }

    public Integer getKhuyenMai() { return khuyenMai; }
    public void setKhuyenMai(Integer khuyenMai) { this.khuyenMai = khuyenMai; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
}