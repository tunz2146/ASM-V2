package com.gamingshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * Service xử lý OTP (One-Time Password) cho xác thực email
 * Hỗ trợ: Quên mật khẩu, xác thực Admin, etc.
 */
@Service
public class OtpService {
    
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    
    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALIDITY_MINUTES = 5; // 5 phút có hiệu lực
    
    // Lưu OTP tạm thời: email -> {otp, createdTime}
    private static final Map<String, OtpData> otpStore = new HashMap<>(); 
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * Class lưu trữ dữ liệu OTP
     */
    private static class OtpData {
        String otp;
        LocalDateTime createdTime;
        
        OtpData(String otp, LocalDateTime createdTime) {
            this.otp = otp;
            this.createdTime = createdTime;
        }
        
        boolean isExpired() {
            return LocalDateTime.now().isAfter(
                createdTime.plusMinutes(OTP_VALIDITY_MINUTES)
            );
        }
    }
    
    /**
     * Tạo mã OTP 6 chữ số ngẫu nhiên
     */
    public String generateOtp() {
        int otp = (int)(Math.random() * 900000) + 100000;
        logger.info("OTP được tạo: {}", otp);
        return String.valueOf(otp);
    }
    
    /**
     * Lưu OTP và gửi qua email
     * @param toEmail Email người dùng
     * @param purpose Mục đích sử dụng OTP (ví dụ: "Quên mật khẩu", "Xác thực Admin")
     */
    public void sendOtp(String toEmail, String purpose) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            logger.error("Email không hợp lệ: {}", toEmail);
            throw new IllegalArgumentException("Email không được để trống");
        }
        
        String otp = generateOtp();
        
        try {
            // Gửi email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail.trim());
            message.setSubject("Mã xác nhận " + purpose); 
            message.setText("Mã OTP của bạn là: " + otp + "\n\n" +
                          "⏱️  Có hiệu lực trong " + OTP_VALIDITY_MINUTES + " phút.\n" +
                          "🔒 Không chia sẻ mã này với ai khác.\n\n" +
                          "Nếu bạn không yêu cầu, vui lòng bỏ qua email này.");
            
            mailSender.send(message);
            
            // Lưu OTP vào store
            otpStore.put(toEmail.trim(), new OtpData(otp, LocalDateTime.now())); 
            
            logger.info("OTP được gửi tới: {}  |  Purpose: {}", toEmail, purpose);
            
        } catch (Exception e) {
            logger.error("Lỗi gửi email OTP tới: {}", toEmail, e);
            throw new RuntimeException("Không thể gửi OTP. Vui lòng thử lại sau!");
        }
    }
    
    /**
     * Xác thực OTP nhập vào
     * @param email Email người dùng
     * @param inputOtp Mã OTP người dùng nhập
     * @return true nếu OTP hợp lệ, false nếu không
     */
    public boolean verifyOtp(String email, String inputOtp) {
        if (email == null || inputOtp == null) {
            logger.warn("Email hoặc OTP null");
            return false;
        }
        
        email = email.trim();
        inputOtp = inputOtp.trim();
        
        OtpData otpData = otpStore.get(email);
        
        if (otpData == null) {
            logger.warn("OTP không tìm thấy cho email: {}", email);
            return false;
        }
        
        // Kiểm tra OTP hết hạn
        if (otpData.isExpired()) {
            logger.warn("OTP hết hạn cho email: {}", email);
            otpStore.remove(email);
            return false;
        }
        
        // So sánh OTP
        boolean isValid = otpData.otp.equals(inputOtp);
        
        if (isValid) {
            logger.info("OTP hợp lệ cho email: {}", email);
            // Xóa OTP sau khi xác thực thành công
            otpStore.remove(email);
        } else {
            logger.warn("OTP không khớp cho email: {}. Input: {}, Expected: {}", 
                       email, inputOtp, otpData.otp);
        }
        
        return isValid;
    }
    
    /**
     * Kiểm tra OTP có tồn tại và chưa hết hạn
     * @param email Email người dùng
     * @return true nếu OTP tồn tại và chưa hết hạn
     */
    public boolean isOtpValid(String email) {
        if (email == null) return false;
        
        OtpData otpData = otpStore.get(email.trim());
        
        if (otpData == null) {
            return false;
        }
        
        if (otpData.isExpired()) {
            otpStore.remove(email.trim());
            return false;
        }
        
        return true;
    }
    
    /**
     * Xóa OTP cho email (sau khi sử dụng)
     * @param email Email người dùng
     */
    public void clearOtp(String email) {
        if (email != null) {
            otpStore.remove(email.trim());
            logger.info("OTP được xóa cho email: {}", email);
        }
    }
    
    /**
     * Lấy thời gian còn lại của OTP (tính bằng giây)
     * @param email Email người dùng
     * @return Thời gian còn lại (giây), -1 nếu OTP không tồn tại
     */
    public long getOtpRemainingSeconds(String email) {
        if (email == null) return -1;
        
        OtpData otpData = otpStore.get(email.trim());
        
        if (otpData == null || otpData.isExpired()) {
            return -1;
        }
        
        LocalDateTime expiryTime = otpData.createdTime.plusMinutes(OTP_VALIDITY_MINUTES);
        long secondsRemaining = java.time.temporal.ChronoUnit.SECONDS
                                .between(LocalDateTime.now(), expiryTime);
        
        return Math.max(0, secondsRemaining);
    }
}
