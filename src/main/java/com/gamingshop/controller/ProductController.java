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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private SanPhamService sanPhamService;
    
    @Autowired
    private LoaiSanPhamRepository loaiSanPhamRepository; // Inject thêm cái này

    @GetMapping("/products")
    public String listProducts(Model model, 
                               @RequestParam(name = "keyword", required = false) String keyword,
                               @RequestParam(name = "category", required = false) String categorySlug, // Nhận param category
                               @RequestParam(name = "page", defaultValue = "0") int page) {
        
        // Lấy danh sách sản phẩm (đã lọc theo category nếu có)
        Page<SanPham> pageProduct = sanPhamService.getAllProducts(keyword, categorySlug, page);
        
        // Lấy danh sách danh mục để hiển thị Sidebar
        List<LoaiSanPham> categories = loaiSanPhamRepository.findAll();

        model.addAttribute("products", pageProduct);
        model.addAttribute("categories", categories); // Gửi list danh mục sang View
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentCategory", categorySlug); // Để đánh dấu active menu
        model.addAttribute("totalPages", pageProduct.getTotalPages());
        
        // Cập nhật title trang cho xịn
        if (categorySlug != null) {
            LoaiSanPham currentLoai = loaiSanPhamRepository.findBySlug(categorySlug);
            model.addAttribute("pageTitle", currentLoai != null ? currentLoai.getTen() : "Sản phẩm");
        } else {
            model.addAttribute("pageTitle", "Tất cả sản phẩm");
        }
        
        return "product/list";
    }
}