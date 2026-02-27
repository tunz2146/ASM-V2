package com.gamingshop.controller;

import com.gamingshop.entity.NguoiDung;
import com.gamingshop.repository.NguoiDungRepository;
import com.gamingshop.service.DonHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private DonHangService donHangService;

    // ===== DANH SÁCH KHÁCH HÀNG =====
    @GetMapping("")
    public String index(Model model,
                        @RequestParam(defaultValue = "0")   int page,
                        @RequestParam(defaultValue = "10")  int size,
                        @RequestParam(required = false)     String keyword,
                        @RequestParam(required = false)     String role) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<NguoiDung> users;

        if (keyword != null && !keyword.isBlank()) {
            users = nguoiDungRepository.searchUsers(keyword.trim(), pageable);
        } else if (role != null && !role.isBlank()) {
            users = nguoiDungRepository.findByRole(role, pageable);
        } else {
            users = nguoiDungRepository.findAll(pageable);
        }

        long totalAdmin = nguoiDungRepository.countByRole("ADMIN");
        long totalUser  = nguoiDungRepository.countByRole("USER");

        model.addAttribute("users",        users);
        model.addAttribute("currentPage",  page);
        model.addAttribute("totalPages",   users.getTotalPages());
        model.addAttribute("keyword",      keyword);
        model.addAttribute("currentRole",  role);
        model.addAttribute("totalAdmin",   totalAdmin);
        model.addAttribute("totalUser",    totalUser);
        model.addAttribute("totalAll",     totalAdmin + totalUser);
        model.addAttribute("pageTitle",    "Quản lý khách hàng");
        model.addAttribute("pendingOrders", donHangService.countByStatus("CHO_XAC_NHAN"));
        return "admin/user/index";
    }

    // ===== CHI TIẾT KHÁCH HÀNG =====
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Optional<NguoiDung> userOpt = nguoiDungRepository.findById(id);
        if (userOpt.isEmpty()) return "redirect:/admin/users";

        NguoiDung user = userOpt.get();
        long orderCount = donHangService.countByUserId(id);

        model.addAttribute("user",          user);
        model.addAttribute("orderCount",    orderCount);
        model.addAttribute("orders",        donHangService.getOrdersByUserId(id));
        model.addAttribute("pageTitle",     "Chi tiết: " + user.getEmail());
        model.addAttribute("pendingOrders", donHangService.countByStatus("CHO_XAC_NHAN"));
        return "admin/user/detail";
    }

    // ===== ĐỔI ROLE (AJAX) =====
    @PostMapping("/{id}/role")
    @ResponseBody
    public ResponseEntity<?> changeRole(@PathVariable Long id,
                                        @RequestParam String role) {
        Optional<NguoiDung> userOpt = nguoiDungRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User không tồn tại"));
        }
        NguoiDung user = userOpt.get();
        user.setRole(role);
        nguoiDungRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true, "message", "Đã cập nhật role thành " + role));
    }

    // ===== RESET MẬT KHẨU VỀ "1" (AJAX) =====
    @PostMapping("/{id}/reset-password")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        Optional<NguoiDung> userOpt = nguoiDungRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User không tồn tại"));
        }
        NguoiDung user = userOpt.get();
        user.setPassword("1");
        nguoiDungRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true, "message", "Đã reset mật khẩu về '1'"));
    }

    // ===== XÓA KHÁCH HÀNG =====
    @PostMapping("/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<NguoiDung> userOpt = nguoiDungRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User không tồn tại"));
        }
        // Không cho xóa ADMIN
        if ("ADMIN".equals(userOpt.get().getRole())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Không thể xóa tài khoản ADMIN!"));
        }
        nguoiDungRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Đã xóa tài khoản"));
    }
}