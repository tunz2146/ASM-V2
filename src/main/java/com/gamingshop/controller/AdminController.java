package com.gamingshop.controller;

import com.gamingshop.repository.SanPhamRepository;
import com.gamingshop.repository.NguoiDungRepository;
import com.gamingshop.service.DonHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DonHangService donHangService;

    @Autowired(required = false)
    private SanPhamRepository sanPhamRepository;

    @Autowired(required = false)
    private NguoiDungRepository nguoiDungRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Thống kê đơn hàng
        model.addAttribute("countAll",       donHangService.countAll());
        model.addAttribute("countCho",       donHangService.countByStatus("CHO_XAC_NHAN"));
        model.addAttribute("countXacNhan",   donHangService.countByStatus("DA_XAC_NHAN"));
        model.addAttribute("countDangGiao",  donHangService.countByStatus("DANG_GIAO"));
        model.addAttribute("countDaGiao",    donHangService.countByStatus("DA_GIAO"));
        model.addAttribute("countHuy",       donHangService.countByStatus("DA_HUY"));
        model.addAttribute("tongDoanhThu",   donHangService.getTotalRevenue());

        // Đơn chờ duyệt (hiện badge trên sidebar)
        model.addAttribute("pendingOrders",  donHangService.countByStatus("CHO_XAC_NHAN"));

        // Thống kê sản phẩm & user (nếu có repository)
        if (sanPhamRepository != null) {
            model.addAttribute("totalProducts", sanPhamRepository.count());
        }
        if (nguoiDungRepository != null) {
            model.addAttribute("totalUsers", nguoiDungRepository.count());
        }

        model.addAttribute("pageTitle", "Dashboard");
        return "admin/dashboard";
    }
}