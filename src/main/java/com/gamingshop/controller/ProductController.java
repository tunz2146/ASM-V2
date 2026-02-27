package com.gamingshop.controller;

import com.gamingshop.entity.LoaiSanPham;
import com.gamingshop.entity.SanPham;
import com.gamingshop.repository.LoaiSanPhamRepository;
import com.gamingshop.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private LoaiSanPhamRepository loaiSanPhamRepository;

    // ===== DANH SÁCH SẢN PHẨM =====
    @GetMapping("/products")
    public String listProducts(Model model,
                               @RequestParam(name = "keyword", required = false) String keyword,
                               @RequestParam(name = "category", required = false) String categorySlug,
                               @RequestParam(name = "page", defaultValue = "0") int page) {

        Page<SanPham> pageProduct = sanPhamService.getAllProducts(keyword, categorySlug, page);
        List<LoaiSanPham> categories = loaiSanPhamRepository.findAll();

        model.addAttribute("products", pageProduct);
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentCategory", categorySlug);
        model.addAttribute("totalPages", pageProduct.getTotalPages());

        if (categorySlug != null) {
            LoaiSanPham currentLoai = loaiSanPhamRepository.findBySlug(categorySlug);
            model.addAttribute("pageTitle", currentLoai != null ? currentLoai.getTen() : "Sản phẩm");
        } else {
            model.addAttribute("pageTitle", "Tất cả sản phẩm");
        }

        return "product/list";
    }

    // ===== CHI TIẾT SẢN PHẨM =====
    @GetMapping("/products/{slug}")
    public String productDetail(@PathVariable String slug, Model model) {

        SanPham sanPham = sanPhamService.getProductBySlug(slug);

        if (sanPham == null) {
            return "redirect:/products";
        }

        // Lấy sản phẩm cùng danh mục để hiện "Sản phẩm liên quan"
        List<SanPham> relatedProducts = sanPhamService.getRelatedProducts(sanPham, 4);

        model.addAttribute("product", sanPham);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("pageTitle", sanPham.getTenSanPham() + " - Gaming Shop");

        return "product/detail";
    }
}