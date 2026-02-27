package com.gamingshop.controller;

import com.gamingshop.entity.SanPham;
import com.gamingshop.repository.HangRepository;
import com.gamingshop.repository.LoaiSanPhamRepository;
import com.gamingshop.service.DonHangService;
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

    @Autowired private SanPhamService sanPhamService;
    @Autowired private HangRepository hangRepository;
    @Autowired private LoaiSanPhamRepository loaiSanPhamRepository;
    @Autowired private DonHangService donHangService;

    @GetMapping("")
    public String index(Model model,
                        @RequestParam(defaultValue = "0")  int page,
                        @RequestParam(required = false)    String keyword) {
        Page<SanPham> pageProduct = sanPhamService.getAllProducts(keyword, null, page);
        model.addAttribute("products",      pageProduct);
        model.addAttribute("currentPage",   page);
        model.addAttribute("totalPages",    pageProduct.getTotalPages());
        model.addAttribute("keyword",       keyword);
        model.addAttribute("pageTitle",     "Quản lý sản phẩm");
        model.addAttribute("pendingOrders", donHangService.countByStatus("CHO_XAC_NHAN"));
        return "admin/product/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("product",       new SanPham());
        model.addAttribute("brands",        hangRepository.findAll());
        model.addAttribute("categories",    loaiSanPhamRepository.findAll());
        model.addAttribute("pageTitle",     "Thêm sản phẩm");
        model.addAttribute("pendingOrders", donHangService.countByStatus("CHO_XAC_NHAN"));
        return "admin/product/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("product") SanPham sanPham,
                       @RequestParam("imageFile") MultipartFile imageFile,
                       @RequestParam("imageUrl")  String imageUrl) throws IOException {
        if (sanPham.getId() != null && imageFile.isEmpty() && (imageUrl == null || imageUrl.isEmpty())) {
            SanPham old = sanPhamService.getProductById(sanPham.getId());
            if (old != null) sanPham.setHinhAnh(old.getHinhAnh());
        }
        sanPhamService.saveProduct(sanPham, imageFile, imageUrl);
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        SanPham sanPham = sanPhamService.getProductById(id);
        if (sanPham == null) return "redirect:/admin/products";
        model.addAttribute("product",       sanPham);
        model.addAttribute("brands",        hangRepository.findAll());
        model.addAttribute("categories",    loaiSanPhamRepository.findAll());
        model.addAttribute("pageTitle",     "Sửa sản phẩm");
        model.addAttribute("pendingOrders", donHangService.countByStatus("CHO_XAC_NHAN"));
        return "admin/product/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        sanPhamService.deleteProduct(id);
        return "redirect:/admin/products";
    }
}