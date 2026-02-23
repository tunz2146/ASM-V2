package com.gamingshop.service;

import com.gamingshop.entity.GioHang;
import com.gamingshop.entity.NguoiDung;
import com.gamingshop.entity.SanPham;
import com.gamingshop.repository.GioHangRepository;
import com.gamingshop.repository.NguoiDungRepository;
import com.gamingshop.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GioHangService {

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    // Lấy danh sách giỏ hàng theo email
    public List<GioHang> getCartByEmail(String email) {
        NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(null);
        if (user == null) return List.of();
        return gioHangRepository.findByNguoiDung_Id(user.getId());
    }

    // Đếm tổng số sản phẩm trong giỏ
    public int countCartItems(String email) {
        NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(null);
        if (user == null) return 0;
        Integer count = gioHangRepository.countTotalItemsByUserId(user.getId());
        return count != null ? count : 0;
    }

    // Tính tổng tiền
    public long getTotalAmount(String email) {
        return getCartByEmail(email).stream()
                .mapToLong(GioHang::getThanhTien)
                .sum();
    }

    // Thêm vào giỏ hàng
    @Transactional
    public String addToCart(String email, Long productId, int quantity) {
        NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(null);
        if (user == null) return "Không tìm thấy người dùng!";

        SanPham sanPham = sanPhamRepository.findById(productId).orElse(null);
        if (sanPham == null) return "Sản phẩm không tồn tại!";

        if (sanPham.getTonKho() == null || sanPham.getTonKho() <= 0) {
            return "Sản phẩm đã hết hàng!";
        }

        Optional<GioHang> existing = gioHangRepository
                .findByNguoiDung_IdAndSanPham_Id(user.getId(), productId);

        if (existing.isPresent()) {
            // Đã có → tăng số lượng
            GioHang item = existing.get();
            int newQty = item.getSoLuong() + quantity;
            if (newQty > sanPham.getTonKho()) {
                return "Số lượng vượt quá tồn kho (" + sanPham.getTonKho() + " sản phẩm)!";
            }
            item.setSoLuong(newQty);
            gioHangRepository.save(item);
        } else {
            // Chưa có → thêm mới
            GioHang newItem = new GioHang();
            newItem.setNguoiDung(user);
            newItem.setSanPham(sanPham);
            newItem.setSoLuong(quantity);
            gioHangRepository.save(newItem);
        }
        return "OK";
    }

    // Cập nhật số lượng
    @Transactional
    public String updateQuantity(String email, Long cartItemId, int quantity) {
        GioHang item = gioHangRepository.findById(cartItemId).orElse(null);
        if (item == null) return "Không tìm thấy!";

        // Bảo mật: chỉ user sở hữu mới được sửa
        if (!item.getNguoiDung().getEmail().equals(email)) return "Không có quyền!";

        if (quantity <= 0) {
            gioHangRepository.delete(item);
            return "DELETED";
        }

        if (quantity > item.getSanPham().getTonKho()) {
            return "Vượt tồn kho!";
        }

        item.setSoLuong(quantity);
        gioHangRepository.save(item);
        return "OK";
    }

    // Xóa 1 sản phẩm khỏi giỏ
    @Transactional
    public void removeItem(String email, Long cartItemId) {
        GioHang item = gioHangRepository.findById(cartItemId).orElse(null);
        if (item != null && item.getNguoiDung().getEmail().equals(email)) {
            gioHangRepository.delete(item);
        }
    }

    // Xóa toàn bộ giỏ
    @Transactional
    public void clearCart(String email) {
        NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(null);
        if (user != null) {
            gioHangRepository.deleteByNguoiDung_Id(user.getId());
        }
    }
}