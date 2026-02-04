package com.gamingshop.service;

import com.gamingshop.entity.SanPham;
import com.gamingshop.repository.HangRepository;
import com.gamingshop.repository.LoaiSanPhamRepository;
import com.gamingshop.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class SanPhamService {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private HangRepository hangRepository;

    @Autowired
    private LoaiSanPhamRepository loaiSanPhamRepository;

    // ==========================================================
    // PHẦN 1: DÀNH CHO NGƯỜI DÙNG (FRONTEND)
    // ==========================================================

    /**
     * Lấy 8 sản phẩm mới nhất hiển thị Trang chủ
     */
    public List<SanPham> getLatestProducts() {
        // PageRequest.of(0, 8) nghĩa là lấy trang đầu tiên, 8 phần tử
        return sanPhamRepository.findAll(PageRequest.of(0, 8)).getContent();
    }

    /**
     * Lấy danh sách sản phẩm có Phân trang, Tìm kiếm & Lọc danh mục
     * Dùng cho trang Cửa hàng (/products) và trang Admin index
     */
    public Page<SanPham> getAllProducts(String keyword, String categorySlug, int pageNo) {
        int pageSize = 12; // Số lượng hiển thị trên 1 trang
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        // Ưu tiên 1: Nếu có từ khóa tìm kiếm -> Tìm theo tên
        if (keyword != null && !keyword.isEmpty()) {
            return sanPhamRepository.findByTenSanPhamContaining(keyword, pageable);
        }

        // Ưu tiên 2: Nếu có slug danh mục -> Lọc theo loại sản phẩm
        if (categorySlug != null && !categorySlug.isEmpty()) {
            return sanPhamRepository.findByLoaiSanPhams_Slug(categorySlug, pageable);
        }

        // Mặc định: Lấy tất cả
        return sanPhamRepository.findAll(pageable);
    }

    // ==========================================================
    // PHẦN 2: DÀNH CHO ADMIN (BACKEND QUẢN LÝ)
    // ==========================================================

    /**
     * Lấy chi tiết 1 sản phẩm theo ID
     * Dùng khi bấm nút "Sửa"
     */
    public SanPham getProductById(Long id) {
        return sanPhamRepository.findById(id).orElse(null);
    }

    /**
     * Lưu sản phẩm mới hoặc Cập nhật sản phẩm cũ
     * Bao gồm logic Upload ảnh vào thư mục source
     */
    public void saveProduct(SanPham sanPham, MultipartFile imageFile) throws IOException {
        
        // 1. Xử lý upload ảnh nếu người dùng có chọn file ảnh mới
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            
            // Đường dẫn lưu ảnh: Lưu thẳng vào thư mục source của project để dev thấy ngay
            String uploadDir = "src/main/resources/static/images/products/";
            
            Path uploadPath = Paths.get(uploadDir);
            
            // Nếu thư mục chưa có thì tạo mới
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Thực hiện lưu file (Đè lên nếu trùng tên)
            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                
                // Gán tên file ảnh vào đối tượng SanPham để lưu xuống Database
                sanPham.setHinhAnh(fileName);
            } catch (IOException ioe) {
                throw new IOException("Lỗi: Không thể lưu file ảnh " + fileName, ioe);
            }
        }
        
        // 2. Lưu thông tin sản phẩm xuống SQL Server
        sanPhamRepository.save(sanPham);
    }

    /**
     * Xóa sản phẩm theo ID
     */
    public void deleteProduct(Long id) {
        sanPhamRepository.deleteById(id);
    }
}