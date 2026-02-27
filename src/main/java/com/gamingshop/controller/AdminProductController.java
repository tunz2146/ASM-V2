package com.gamingshop.controller;

import com.gamingshop.entity.SanPham;
import com.gamingshop.repository.HangRepository;
import com.gamingshop.repository.LoaiSanPhamRepository;
import com.gamingshop.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private HangRepository hangRepository;

    @Autowired
    private LoaiSanPhamRepository loaiSanPhamRepository;

    // 1. Danh sách
    @GetMapping("")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "keyword", required = false) String keyword) {
        Page<SanPham> pageProduct = sanPhamService.getAllProducts(keyword, null, page);
        model.addAttribute("products", pageProduct);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageProduct.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "admin/product/index";
    }

    // 2. Form thêm mới
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("product", new SanPham());
        model.addAttribute("brands", hangRepository.findAll());
        model.addAttribute("categories", loaiSanPhamRepository.findAll());
        return "admin/product/form";
    }

    // 3. Lưu (Thêm mới hoặc Cập nhật)
    @PostMapping("/save")
    public String save(
            @ModelAttribute("product") SanPham sanPham,
            // ✅ required = false để không báo lỗi khi không chọn file
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl
    ) throws IOException {

        // Giữ ảnh cũ nếu không upload file mới VÀ không nhập URL mới
        if (sanPham.getId() != null) {
            boolean noNewFile = (imageFile == null || imageFile.isEmpty());
            boolean noNewUrl  = (imageUrl == null || imageUrl.trim().isEmpty());

            if (noNewFile && noNewUrl) {
                // Lấy ảnh cũ từ DB
                SanPham oldProduct = sanPhamService.getProductById(sanPham.getId());
                if (oldProduct != null) {
                    sanPham.setHinhAnh(oldProduct.getHinhAnh());
                }
            }
        }

        sanPhamService.saveProduct(sanPham, imageFile, imageUrl);
        return "redirect:/admin/products";
    }

    // 4. Form sửa
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        SanPham sanPham = sanPhamService.getProductById(id);
        if (sanPham == null) {
            return "redirect:/admin/products";
        }
        model.addAttribute("product", sanPham);
        model.addAttribute("brands", hangRepository.findAll());
        model.addAttribute("categories", loaiSanPhamRepository.findAll());
        return "admin/product/form";
    }

    // 5. Xóa
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        sanPhamService.deleteProduct(id);
        return "redirect:/admin/products";
    }
}