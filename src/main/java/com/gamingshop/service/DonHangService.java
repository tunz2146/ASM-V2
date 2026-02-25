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

    @Autowired private DonHangRepository donHangRepository;
    @Autowired private GioHangRepository gioHangRepository;
    @Autowired private NguoiDungRepository nguoiDungRepository;

    // ============================================================
    // USER: Tạo đơn hàng từ giỏ hàng
    // ============================================================
    @Transactional
    public DonHang createOrder(String email, String thongTinGiaoHang, String donViVanChuyen) {

        NguoiDung user = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<GioHang> cartItems = gioHangRepository.findByNguoiDung_Id(user.getId());
        if (cartItems.isEmpty()) throw new RuntimeException("Giỏ hàng trống!");

        long tongTien = cartItems.stream()
                .mapToLong(GioHang::getThanhTien)
                .sum();
        long phiVC = tongTien >= 1_000_000 ? 0L : 30_000L;

        DonHang donHang = new DonHang();
        donHang.setNguoiDung(user);
        donHang.setNgayDat(LocalDate.now());
        donHang.setTinhTrang("CHO_XAC_NHAN");
        donHang.setTongTien(tongTien + phiVC);
        donHang.setThongTinGiaoHang(thongTinGiaoHang);
        donHang.setDonViVanChuyen(donViVanChuyen != null ? donViVanChuyen : "GHN");
        donHang.setPhiVanChuyen(phiVC);
        donHang.setKhuyenMai(0L);

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
    // ✅ KEY FIX: Chỉ truyền Sort vào Pageable
    //    KHÔNG dùng findByXxxOrderByYyyDesc() vì Hibernate sẽ
    //    ghép thêm ORDER BY thứ 2 → lỗi "column specified more than once"
    // ============================================================
    public Page<DonHang> getAllOrders(String status, int page) {
        Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "ngayDat"));

        if (status != null && !status.isEmpty()) {
            // findByTinhTrang + Pageable(Sort) → chỉ 1 ORDER BY
            return donHangRepository.findByTinhTrang(status, pageable);
        }
        // findAll + Pageable(Sort) → chỉ 1 ORDER BY
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

    // Thống kê
    public long countByStatus(String status) {
        return donHangRepository.countByTinhTrang(status);
    }

    public Long getTotalRevenue() {
        return donHangRepository.sumDoanhThu();
    }

    public long countAll() {
        return donHangRepository.count();
    }
}