package com.gamingshop.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Don_Hang")
public class DonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ngay_dat")
    private LocalDate ngayDat;

    @Column(name = "tinh_trang")
    private String tinhTrang; // CHO_XAC_NHAN | DA_XAC_NHAN | DANG_GIAO | DA_GIAO | DA_HUY

    @Column(name = "tong_tien")
    private Long tongTien;

    @Column(name = "thong_tin_giao_hang", columnDefinition = "NVARCHAR(MAX)")
    private String thongTinGiaoHang; // JSON string

    @Column(name = "don_vi_van_chuyen")
    private String donViVanChuyen;

    @Column(name = "khuyen_mai")
    private Long khuyenMai;

    @Column(name = "phi_van_chuyen")
    private Long phiVanChuyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung")
    private NguoiDung nguoiDung;

    @OneToMany(mappedBy = "donHang", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChiTietDonHang> chiTietDonHangs = new java.util.ArrayList<>();

    // ===== GETTERS & SETTERS =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getNgayDat() { return ngayDat; }
    public void setNgayDat(LocalDate ngayDat) { this.ngayDat = ngayDat; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public Long getTongTien() { return tongTien; }
    public void setTongTien(Long tongTien) { this.tongTien = tongTien; }

    public String getThongTinGiaoHang() { return thongTinGiaoHang; }
    public void setThongTinGiaoHang(String thongTinGiaoHang) { this.thongTinGiaoHang = thongTinGiaoHang; }

    public String getDonViVanChuyen() { return donViVanChuyen; }
    public void setDonViVanChuyen(String donViVanChuyen) { this.donViVanChuyen = donViVanChuyen; }

    public Long getKhuyenMai() { return khuyenMai; }
    public void setKhuyenMai(Long khuyenMai) { this.khuyenMai = khuyenMai; }

    public Long getPhiVanChuyen() { return phiVanChuyen; }
    public void setPhiVanChuyen(Long phiVanChuyen) { this.phiVanChuyen = phiVanChuyen; }

    public NguoiDung getNguoiDung() { return nguoiDung; }
    public void setNguoiDung(NguoiDung nguoiDung) { this.nguoiDung = nguoiDung; }

    public List<ChiTietDonHang> getChiTietDonHangs() { return chiTietDonHangs; }
    public void setChiTietDonHangs(List<ChiTietDonHang> chiTietDonHangs) { this.chiTietDonHangs = chiTietDonHangs; }

    // Helper: label trạng thái tiếng Việt
    public String getTinhTrangLabel() {
        if (tinhTrang == null) return "Không rõ";
        return switch (tinhTrang) {
            case "CHO_XAC_NHAN" -> "Chờ xác nhận";
            case "DA_XAC_NHAN"  -> "Đã xác nhận";
            case "DANG_GIAO"    -> "Đang giao hàng";
            case "DA_GIAO"      -> "Đã giao hàng";
            case "DA_HUY"       -> "Đã hủy";
            default -> tinhTrang;
        };
    }

    // Helper: badge color Bootstrap
    public String getTinhTrangBadge() {
        if (tinhTrang == null) return "secondary";
        return switch (tinhTrang) {
            case "CHO_XAC_NHAN" -> "warning";
            case "DA_XAC_NHAN"  -> "primary";
            case "DANG_GIAO"    -> "info";
            case "DA_GIAO"      -> "success";
            case "DA_HUY"       -> "danger";
            default -> "secondary";
        };
    }
}