package com.gamingshop.service;

import com.gamingshop.entity.LoaiSanPham;
import com.gamingshop.entity.SanPham;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SanPhamService {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    // ==========================================================
    // FRONTEND
    // ==========================================================

    /** 8 sản phẩm mới nhất cho trang chủ */
    public List<SanPham> getLatestProducts() {
        return sanPhamRepository.findAll(PageRequest.of(0, 8)).getContent();
    }

    /** Danh sách phân trang + tìm kiếm + lọc danh mục */
    public Page<SanPham> getAllProducts(String keyword, String categorySlug, int pageNo) {
        int pageSize = 12;
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        if (keyword != null && !keyword.isEmpty()) {
            return sanPhamRepository.findByTenSanPhamContaining(keyword, pageable);
        }
        if (categorySlug != null && !categorySlug.isEmpty()) {
            return sanPhamRepository.findDistinctByLoaiSanPhams_Slug(categorySlug, pageable);
        }
        return sanPhamRepository.findAll(pageable);
    }

    /** Chi tiết sản phẩm theo slug */
    public SanPham getProductBySlug(String slug) {
        return sanPhamRepository.findBySlug(slug);
    }

    /**
     * Lấy sản phẩm liên quan:
     * - Ưu tiên cùng danh mục đầu tiên của sản phẩm
     * - Loại trừ chính nó
     * - Tối đa `limit` sản phẩm
     */
    public List<SanPham> getRelatedProducts(SanPham current, int limit) {
        if (current == null || current.getLoaiSanPhams() == null || current.getLoaiSanPhams().isEmpty()) {
            // Không có danh mục → lấy sản phẩm mới nhất
            return sanPhamRepository.findAll(PageRequest.of(0, limit + 1))
                    .getContent()
                    .stream()
                    .filter(p -> !p.getId().equals(current.getId()))
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        // Lấy slug của danh mục đầu tiên
        LoaiSanPham firstCategory = current.getLoaiSanPhams().get(0);
        String slug = firstCategory.getSlug();

        return sanPhamRepository
                .findDistinctByLoaiSanPhams_Slug(slug, PageRequest.of(0, limit + 1))
                .getContent()
                .stream()
                .filter(p -> !p.getId().equals(current.getId()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ==========================================================
    // ADMIN
    // ==========================================================

    /** Chi tiết sản phẩm theo ID (dùng cho admin edit) */
    public SanPham getProductById(Long id) {
        return sanPhamRepository.findById(id).orElse(null);
    }

    /** Lưu sản phẩm (hỗ trợ upload file hoặc URL) */
    public void saveProduct(SanPham sanPham, MultipartFile imageFile, String imageUrl) throws IOException {

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            String uploadDir = "src/main/resources/static/images/products/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                sanPham.setHinhAnh(fileName);
            } catch (IOException ioe) {
                throw new IOException("Lỗi: Không thể lưu file ảnh " + fileName, ioe);
            }
        } else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            sanPham.setHinhAnh(imageUrl.trim());
        }

        sanPhamRepository.save(sanPham);
    }

    /** Xóa sản phẩm */
    public void deleteProduct(Long id) {
        sanPhamRepository.deleteById(id);
    }
}