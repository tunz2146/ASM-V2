package com.gamingshop.service;

import com.gamingshop.entity.*;
import com.gamingshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DonHangService {

    @Autowired private DonHangRepository     donHangRepository;
    @Autowired private GioHangRepository     gioHangRepository;
    @Autowired private NguoiDungRepository   nguoiDungRepository;

    // ============================================================
    // USER: Tạo đơn hàng từ giỏ hàng (CÓ HỖ TRỢ MÃ GIẢM GIÁ)
    // ============================================================
    @Transactional
    public DonHang createOrder(String email,
                               String thongTinGiaoHang,
                               String donViVanChuyen,
                               Long discountAmount,     // ← Thêm mới
                               String couponCode) {     // ← Thêm mới

        NguoiDung user = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<GioHang> cartItems = gioHangRepository.findByNguoiDung_Id(user.getId());
        if (cartItems.isEmpty()) throw new RuntimeException("Giỏ hàng trống!");

        long subtotal = cartItems.stream().mapToLong(GioHang::getThanhTien).sum();
        long phiVC    = subtotal >= 1_000_000 ? 0L : 30_000L;

        // ✅ Đảm bảo discount không âm và không vượt quá subtotal
        if (discountAmount == null) discountAmount = 0L;
        discountAmount = Math.min(discountAmount, subtotal); // Không giảm quá subtotal

        long tongTien = Math.max(0, subtotal - discountAmount + phiVC);

        DonHang donHang = new DonHang();
        donHang.setNguoiDung(user);
        donHang.setNgayDat(LocalDate.now());
        donHang.setTinhTrang("CHO_XAC_NHAN");
        donHang.setTongTien(tongTien);
        donHang.setThongTinGiaoHang(thongTinGiaoHang);
        donHang.setDonViVanChuyen(donViVanChuyen != null ? donViVanChuyen : "GHN");
        donHang.setPhiVanChuyen(phiVC);
        donHang.setKhuyenMai(discountAmount);   // ✅ Lưu số tiền giảm vào DB
        // Nếu entity DonHang có field maCoupon thì thêm: donHang.setMaCoupon(couponCode);

        DonHang saved = donHangRepository.save(donHang);

        for (GioHang item : cartItems) {
            ChiTietDonHang ct = new ChiTietDonHang();
            ct.setDonHang(saved);
            ct.setSanPham(item.getSanPham());
            ct.setGia(item.getSanPham().getGiaSauGiam());
            ct.setSoLuong(item.getSoLuong());
            ct.setKhuyenMai(0);
            saved.getChiTietDonHangs().add(ct);
        }

        donHangRepository.save(saved);
        gioHangRepository.deleteByNguoiDung_Id(user.getId());
        return saved;
    }

    // ============================================================
    // Overload giữ tương thích với code cũ (không có coupon)
    // ============================================================
    @Transactional
    public DonHang createOrder(String email, String thongTinGiaoHang, String donViVanChuyen) {
        return createOrder(email, thongTinGiaoHang, donViVanChuyen, 0L, null);
    }

    // ============================================================
    // USER: Xem đơn hàng
    // ============================================================
    public List<DonHang> getOrdersByEmail(String email) {
        return donHangRepository.findByNguoiDungEmail(email);
    }

    public DonHang getOrderById(Long id) {
        return donHangRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean cancelOrder(Long orderId, String email) {
        DonHang dh = donHangRepository.findById(orderId).orElse(null);
        if (dh == null) return false;
        if (!dh.getNguoiDung().getEmail().equals(email)) return false;
        if (!"CHO_XAC_NHAN".equals(dh.getTinhTrang())) return false;
        dh.setTinhTrang("DA_HUY");
        donHangRepository.save(dh);
        return true;
    }

    // ============================================================
    // ADMIN: Quản lý đơn hàng
    // ============================================================
    public Page<DonHang> getAllOrders(String status, int page) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "ngayDat"));
        if (status != null && !status.isEmpty()) {
            return donHangRepository.findByTinhTrang(status, pageable);
        }
        return donHangRepository.findAll(pageable);
    }

    @Transactional
    public boolean updateStatus(Long orderId, String newStatus) {
        DonHang dh = donHangRepository.findById(orderId).orElse(null);
        if (dh == null) return false;
        dh.setTinhTrang(newStatus);
        donHangRepository.save(dh);
        return true;
    }

    // ============================================================
    // ADMIN: Thống kê
    // ============================================================
    public long countByStatus(String status) { return donHangRepository.countByTinhTrang(status); }
    public Long getTotalRevenue()            { return donHangRepository.sumDoanhThu(); }
    public long countAll()                   { return donHangRepository.count(); }

    public long countByUserId(Long userId) {
        return donHangRepository.findByNguoiDung_IdOrderByNgayDatDesc(userId).size();
    }

    public List<DonHang> getOrdersByUserId(Long userId) {
        return donHangRepository.findByNguoiDung_IdOrderByNgayDatDesc(userId);
    }
}