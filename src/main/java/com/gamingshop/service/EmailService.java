package com.gamingshop.service;

import com.gamingshop.entity.SanPham;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String SHOP_URL = "http://localhost:8080";
    private static final String SHOP_NAME = "GamingShop";

    // ============================================================
    // 1. GỬI EMAIL RESET MẬT KHẨU
    // ============================================================
    public void sendPasswordResetEmail(String toEmail, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, SHOP_NAME);
            helper.setTo(toEmail);
            helper.setSubject("🔐 [GamingShop] Đặt lại mật khẩu thành công");
            helper.setText(buildResetPasswordHtml(userName, toEmail), true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage(), e);
        }
    }

    // ============================================================
    // 2. GỬI EMAIL CHÀO MỪNG NEWSLETTER
    // ============================================================
    public void sendNewsletterWelcomeEmail(String toEmail, List<SanPham> featuredProducts) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, SHOP_NAME);
            helper.setTo(toEmail);
            helper.setSubject("🎮 [GamingShop] Chào mừng! Ưu đãi độc quyền đang chờ bạn");
            helper.setText(buildNewsletterHtml(toEmail, featuredProducts), true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage(), e);
        }
    }

    // ============================================================
    // TEMPLATE: RESET PASSWORD
    // ============================================================
    private String buildResetPasswordHtml(String userName, String email) {
        String displayName = (userName != null && !userName.isBlank()) ? userName : email.split("@")[0];
        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'/></head>" +
            "<body style='margin:0;padding:0;background:#f0f2f7;font-family:Segoe UI,Arial,sans-serif;'>" +
            "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f0f2f7;padding:40px 16px;'>" +
            "<tr><td align='center'>" +
            "<table width='520' cellpadding='0' cellspacing='0' style='background:#fff;border-radius:20px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.1);'>" +

            // Header
            "<tr><td style='background:linear-gradient(135deg,#1a1a2e,#16213e);padding:32px;text-align:center;'>" +
            "<div style='font-size:26px;font-weight:900;color:#fff;letter-spacing:-0.5px;'>🎮 GAMING<span style='color:#dc3545;'>SHOP</span></div>" +
            "<div style='color:rgba(255,255,255,0.45);font-size:13px;margin-top:4px;'>Thiết bị gaming chính hãng</div>" +
            "</td></tr>" +

            // Body
            "<tr><td style='padding:36px 32px;'>" +
            "<div style='text-align:center;margin-bottom:24px;'>" +
            "<div style='width:68px;height:68px;background:linear-gradient(135deg,#dc3545,#e83e8c);border-radius:50%;display:inline-flex;align-items:center;justify-content:center;font-size:28px;'>🔐</div>" +
            "</div>" +
            "<h2 style='color:#1a1a2e;font-size:20px;font-weight:800;text-align:center;margin:0 0 12px;'>Mật khẩu đã được đặt lại!</h2>" +
            "<p style='color:#666;font-size:14px;text-align:center;margin:0 0 24px;'>Xin chào <strong style='color:#1a1a2e;'>" + displayName + "</strong>,</p>" +
            "<p style='color:#777;font-size:14px;line-height:1.7;margin:0 0 20px;'>Chúng tôi đã nhận và xử lý yêu cầu đặt lại mật khẩu tài khoản <strong>" + email + "</strong>. Mật khẩu mới của bạn là:</p>" +

            // Password box
            "<div style='background:#f8f9fc;border:2px dashed #dc3545;border-radius:14px;padding:24px;text-align:center;margin:0 0 24px;'>" +
            "<div style='font-size:12px;color:#999;text-transform:uppercase;letter-spacing:1.5px;margin-bottom:10px;'>MẬT KHẨU TẠM THỜI</div>" +
            "<div style='font-size:48px;font-weight:900;color:#dc3545;letter-spacing:12px;'>1</div>" +
            "<div style='font-size:12px;color:#bbb;margin-top:8px;'>Hãy đổi mật khẩu ngay sau khi đăng nhập</div>" +
            "</div>" +

            // Warning
            "<div style='background:#fffbeb;border-left:4px solid #f59e0b;border-radius:10px;padding:14px 16px;margin-bottom:28px;'>" +
            "<strong style='color:#92400e;font-size:13px;'>⚠️ Lưu ý quan trọng:</strong>" +
            "<ul style='color:#92400e;font-size:13px;margin:8px 0 0;padding-left:18px;line-height:1.8;'>" +
            "<li>Đăng nhập ngay bằng mật khẩu <strong>\"1\"</strong></li>" +
            "<li>Vào <strong>Hồ sơ cá nhân → Đổi mật khẩu</strong> ngay</li>" +
            "<li>Không chia sẻ mật khẩu với bất kỳ ai</li>" +
            "</ul></div>" +

            // CTA button
            "<div style='text-align:center;'>" +
            "<a href='" + SHOP_URL + "/login' style='background:linear-gradient(135deg,#dc3545,#e83e8c);color:#fff;padding:14px 40px;border-radius:50px;text-decoration:none;font-weight:700;font-size:15px;display:inline-block;'>🚀 Đăng nhập ngay</a>" +
            "</div>" +

            "<p style='color:#ccc;font-size:12px;text-align:center;margin-top:24px;'>Nếu bạn không yêu cầu, hãy bỏ qua email này.</p>" +
            "</td></tr>" +

            // Footer
            "<tr><td style='background:#f8f9fc;padding:18px 32px;text-align:center;border-top:1px solid #f0f0f0;'>" +
            "<p style='color:#bbb;font-size:12px;margin:0;'>© 2025 GamingShop · 88 Nguyễn Viết Xuân, Đà Nẵng<br/>" +
            "<a href='mailto:khanhquynhcute1528@gmail.com' style='color:#dc3545;text-decoration:none;'>khanhquynhcute1528@gmail.com</a></p>" +
            "</td></tr>" +

            "</table></td></tr></table></body></html>";
    }

    // ============================================================
    // TEMPLATE: NEWSLETTER WELCOME
    // ============================================================
    private String buildNewsletterHtml(String email, List<SanPham> products) {
        String displayName = email.split("@")[0];

        // Build product cards
        StringBuilder productCards = new StringBuilder();
        if (products != null && !products.isEmpty()) {
            for (SanPham p : products) {
                String img = (p.getHinhAnh() != null && p.getHinhAnh().startsWith("http"))
                        ? p.getHinhAnh() : "https://placehold.co/200x180?text=Gaming";
                long price = p.getGiaSauGiam() != null ? p.getGiaSauGiam() : p.getGiaSanPham();
                String priceStr = String.format("%,d", price).replace(",", ".");
                String link = SHOP_URL + "/products/" + (p.getSlug() != null ? p.getSlug() : p.getId());

                productCards.append(
                    "<td width='50%' style='padding:6px;vertical-align:top;'>" +
                    "<div style='background:#fff;border-radius:12px;border:1px solid #f0f0f0;overflow:hidden;text-align:center;'>" +
                    "<img src='" + img + "' width='100%' style='height:130px;object-fit:cover;display:block;' alt='" + p.getTenSanPham() + "'/>" +
                    "<div style='padding:12px 10px;'>" +
                    "<div style='font-size:13px;font-weight:700;color:#1a1a2e;margin-bottom:6px;line-height:1.4;'>" + p.getTenSanPham() + "</div>" +
                    "<div style='font-size:15px;font-weight:800;color:#dc3545;margin-bottom:10px;'>" + priceStr + "₫</div>" +
                    "<a href='" + link + "' style='background:#dc3545;color:#fff;padding:7px 18px;border-radius:50px;font-size:12px;font-weight:700;text-decoration:none;display:inline-block;'>Xem ngay</a>" +
                    "</div></div></td>"
                );
            }
        }

        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'/></head>" +
            "<body style='margin:0;padding:0;background:#f0f2f7;font-family:Segoe UI,Arial,sans-serif;'>" +
            "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f0f2f7;padding:40px 16px;'>" +
            "<tr><td align='center'>" +
            "<table width='520' cellpadding='0' cellspacing='0' style='background:#fff;border-radius:20px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.1);'>" +

            // Header gradient
            "<tr><td style='background:linear-gradient(135deg,#1a1a2e,#0d1b4b);padding:36px 32px;text-align:center;'>" +
            "<div style='font-size:26px;font-weight:900;color:#fff;'>🎮 GAMING<span style='color:#dc3545;'>SHOP</span></div>" +
            "<div style='color:rgba(255,255,255,0.4);font-size:12px;margin-top:4px;'>Thiết bị gaming chính hãng</div>" +
            "<div style='margin-top:20px;'><span style='background:linear-gradient(135deg,#dc3545,#e83e8c);color:#fff;padding:10px 28px;border-radius:50px;font-weight:800;font-size:15px;display:inline-block;'>🎉 Chào mừng thành viên mới!</span></div>" +
            "</td></tr>" +

            // Body
            "<tr><td style='padding:32px;'>" +
            "<p style='color:#555;font-size:15px;line-height:1.8;margin:0 0 16px;'>Xin chào <strong style='color:#1a1a2e;'>" + displayName + "</strong>,</p>" +
            "<p style='color:#666;font-size:14px;line-height:1.8;margin:0 0 20px;'>Chào mừng bạn gia nhập cộng đồng <strong>GamingShop</strong>! Tài khoản của bạn đã được tạo tự động.</p>" +

            // Account info box
            "<div style='background:#f0f7ff;border-radius:14px;padding:20px;margin:0 0 20px;border-left:4px solid #3b82f6;'>" +
            "<table width='100%' style='border-collapse:collapse;'>" +
            "<tr><td style='color:#555;font-size:13px;padding:5px 0;'>📧 Email đăng nhập:</td><td style='color:#1a1a2e;font-weight:700;font-size:13px;'>" + email + "</td></tr>" +
            "<tr><td style='color:#555;font-size:13px;padding:5px 0;'>🔐 Mật khẩu tạm thời:</td><td style='color:#dc3545;font-weight:900;font-size:22px;letter-spacing:6px;'>1</td></tr>" +
            "</table></div>" +

            // Note
            "<div style='background:#fffbeb;border-left:4px solid #f59e0b;border-radius:10px;padding:14px 16px;margin-bottom:24px;'>" +
            "<strong style='color:#92400e;font-size:13px;'>💡 Sau khi đăng nhập hãy vào <u>Hồ sơ cá nhân</u> để đổi mật khẩu mới!</strong>" +
            "</div>" +

            // Ưu đãi banner
            "<div style='background:linear-gradient(135deg,#fff5f5,#ffe8e8);border-radius:14px;padding:20px;text-align:center;margin-bottom:24px;'>" +
            "<div style='font-size:28px;margin-bottom:8px;'>🎁</div>" +
            "<div style='font-weight:800;color:#dc3545;font-size:16px;margin-bottom:8px;'>ƯU ĐÃI THÀNH VIÊN MỚI</div>" +
            "<div style='color:#666;font-size:13px;line-height:1.8;'>✅ Miễn phí vận chuyển đơn từ 500K<br/>✅ Bảo hành chính hãng 24 tháng<br/>✅ Đổi trả dễ dàng trong 7 ngày</div>" +
            "</div>" +

            // Sản phẩm nổi bật
            (productCards.length() > 0 ?
            "<div style='margin-bottom:24px;'>" +
            "<div style='font-weight:800;color:#1a1a2e;font-size:15px;text-align:center;margin-bottom:14px;'>🔥 Sản phẩm nổi bật dành cho bạn</div>" +
            "<table width='100%' cellpadding='0' cellspacing='0'><tr>" + productCards + "</tr></table></div>"
            : "") +

            // CTA
            "<div style='text-align:center;margin-top:28px;'>" +
            "<a href='" + SHOP_URL + "/login' style='background:linear-gradient(135deg,#dc3545,#e83e8c);color:#fff;padding:14px 44px;border-radius:50px;text-decoration:none;font-weight:700;font-size:15px;display:inline-block;'>🚀 Đăng nhập & Mua sắm ngay</a>" +
            "</div>" +
            "</td></tr>" +

            // Footer
            "<tr><td style='background:#f8f9fc;padding:18px 32px;text-align:center;border-top:1px solid #f0f0f0;'>" +
            "<p style='color:#bbb;font-size:12px;margin:0;'>© 2025 GamingShop · 88 Nguyễn Viết Xuân, Đà Nẵng<br/>" +
            "<a href='mailto:khanhquynhcute1528@gmail.com' style='color:#dc3545;text-decoration:none;'>khanhquynhcute1528@gmail.com</a>" +
            " · <a href='" + SHOP_URL + "' style='color:#dc3545;text-decoration:none;'>gamingshop.vn</a></p>" +
            "</td></tr>" +

            "</table></td></tr></table></body></html>";
    }
}