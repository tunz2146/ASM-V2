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
@RequestMapping("/admin/products") // Gom nhóm URL bắt đầu bằng /admin/products
public class AdminProductController {

    @Autowired
    private SanPhamService sanPhamService;
    
    @Autowired
    private HangRepository hangRepository;
    
    @Autowired
    private LoaiSanPhamRepository loaiSanPhamRepository;

    // 1. Xem danh sách
    @GetMapping("")
    public String index(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<SanPham> pageProduct = sanPhamService.getAllProducts(null, null, page);
        model.addAttribute("products", pageProduct);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageProduct.getTotalPages());
        return "admin/product/index"; // Trả về giao diện danh sách
    }

    // 2. Hiện form thêm mới
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("product", new SanPham()); // Object rỗng
        model.addAttribute("brands", hangRepository.findAll()); // List hãng
        model.addAttribute("categories", loaiSanPhamRepository.findAll()); // List loại
        return "admin/product/form"; // Trả về giao diện form
    }

    // 3. Xử lý lưu (Thêm mới hoặc Cập nhật)
    @PostMapping("/save")
    public String save(@ModelAttribute("product") SanPham sanPham,
                       @RequestParam("imageFile") MultipartFile imageFile,
                       @RequestParam("imageUrl") String imageUrl) throws IOException { // Thêm tham số imageUrl
        
        // Logic giữ ảnh cũ: Nếu không up ảnh mới VÀ không nhập URL mới -> Lấy lại ảnh cũ
        if (sanPham.getId() != null && imageFile.isEmpty() && (imageUrl == null || imageUrl.isEmpty())) {
            SanPham oldProduct = sanPhamService.getProductById(sanPham.getId());
            if (oldProduct != null) {
                sanPham.setHinhAnh(oldProduct.getHinhAnh());
            }
        }
        
        // Gọi service với 3 tham số
        sanPhamService.saveProduct(sanPham, imageFile, imageUrl);
        
        return "redirect:/admin/products";
    }

    // 4. Hiện form sửa
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        SanPham sanPham = sanPhamService.getProductById(id);
        if (sanPham == null) {
            return "redirect:/admin/products";
        }
        model.addAttribute("product", sanPham);
        model.addAttribute("brands", hangRepository.findAll());
        model.addAttribute("categories", loaiSanPhamRepository.findAll());
        return "admin/product/form"; // Tái sử dụng form
    }

    // 5. Xóa
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        sanPhamService.deleteProduct(id);
        return "redirect:/admin/products";
    }
}