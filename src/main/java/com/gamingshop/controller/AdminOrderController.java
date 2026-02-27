package com.gamingshop.controller;

import com.gamingshop.entity.DonHang;
import com.gamingshop.service.DonHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private DonHangService donHangService;

    // Danh sách đơn hàng
    @GetMapping("")
    public String index(Model model,
                        @RequestParam(required = false) String status,
                        @RequestParam(defaultValue = "0") int page) {

        Page<DonHang> orders = donHangService.getAllOrders(status, page);

        model.addAttribute("orders",       orders);
        model.addAttribute("currentPage",  page);
        model.addAttribute("totalPages",   orders.getTotalPages());
        model.addAttribute("currentStatus",status);
        model.addAttribute("pageTitle",    "Quản lý đơn hàng");

        long pending = donHangService.countByStatus("CHO_XAC_NHAN");
        model.addAttribute("countAll",       donHangService.countAll());
        model.addAttribute("countCho",       pending);
        model.addAttribute("countXacNhan",   donHangService.countByStatus("DA_XAC_NHAN"));
        model.addAttribute("countDangGiao",  donHangService.countByStatus("DANG_GIAO"));
        model.addAttribute("countDaGiao",    donHangService.countByStatus("DA_GIAO"));
        model.addAttribute("countHuy",       donHangService.countByStatus("DA_HUY"));
        model.addAttribute("tongDoanhThu",   donHangService.getTotalRevenue());
        model.addAttribute("pendingOrders",  pending); // badge sidebar

        return "admin/order/index";
    }

    // Chi tiết đơn hàng
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        DonHang order = donHangService.getOrderById(id);
        if (order == null) return "redirect:/admin/orders";

        model.addAttribute("order",         order);
        model.addAttribute("pageTitle",     "Đơn hàng #GS-" + id);
        model.addAttribute("pendingOrders", donHangService.countByStatus("CHO_XAC_NHAN"));
        return "admin/order/detail";
    }

    // Cập nhật trạng thái (AJAX)
    @PostMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam String status) {
        boolean ok = donHangService.updateStatus(id, status);
        if (ok) {
            DonHang dh = donHangService.getOrderById(id);
            return ResponseEntity.ok(Map.of(
                "success",   true,
                "message",   "Cập nhật thành công",
                "newStatus", dh.getTinhTrangLabel(),
                "newBadge",  dh.getTinhTrangBadge()
            ));
        }
        return ResponseEntity.badRequest().body(
            Map.of("success", false, "message", "Lỗi cập nhật"));
    }
}