package com.hackathon.agriculture_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class EmailService {
    
    private final Optional<JavaMailSender> mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${app.email.from:noreply@smartagriculture.com}")
    private String fromEmail;
    
    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    public EmailService(@Autowired(required = false) JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = Optional.ofNullable(mailSender);
        this.templateEngine = templateEngine;
    }
    
    public void sendEmailConfirmation(String to, String name, String confirmationToken) {
        log.info("Attempting to send email confirmation to: {}", to);
        log.info("Email enabled: {}, Mail sender present: {}", emailEnabled, mailSender.isPresent());
        
        if (!emailEnabled || !mailSender.isPresent()) {
            log.warn("Email sending is disabled or mail sender not available. Skipping email confirmation for: {}", to);
            System.out.println("EMAIL DEBUG: Email sending is disabled or mail sender not available");
            return;
        }
        
        try {
            MimeMessage message = mailSender.get().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Confirm Your Email - Smart Agriculture");
            
            // Create email content
            String confirmationUrl = frontendUrl + "/auth/confirm-email?token=" + confirmationToken;
            String emailContent = createEmailConfirmationTemplate(name, confirmationUrl);
            
            helper.setText(emailContent, true);
            
            mailSender.get().send(message);
            log.info("Email confirmation sent to: {}", to);
            System.out.println("EMAIL DEBUG: Email confirmation successfully sent to: " + to);
            
        } catch (MessagingException e) {
            log.error("Failed to send email confirmation to: {} - Error: {}", to, e.getMessage());
            // Don't throw exception, just log the error to prevent registration failure
            log.warn("Email sending failed, but user registration will continue. Check email configuration.");
        } catch (Exception e) {
            log.error("Unexpected error sending email confirmation to: {} - Error: {}", to, e.getMessage());
            log.warn("Email sending failed, but user registration will continue. Check email configuration.");
        }
    }
    
    public void sendPasswordResetEmail(String to, String name, String resetToken) {
        if (!emailEnabled || !mailSender.isPresent()) {
            log.warn("Email sending is disabled or mail sender not available. Skipping password reset email for: {}", to);
            return;
        }
        
        try {
            MimeMessage message = mailSender.get().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Reset Your Password - Smart Agriculture");
            
            // Create email content
            String resetUrl = frontendUrl + "/auth/reset-password?token=" + resetToken;
            String emailContent = createPasswordResetTemplate(name, resetUrl);
            
            helper.setText(emailContent, true);
            
            mailSender.get().send(message);
            log.info("Password reset email sent to: {}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {} - Error: {}", to, e.getMessage());
            // Don't throw exception, just log the error to prevent password reset failure
            log.warn("Email sending failed, but password reset will continue. Check email configuration.");
        } catch (Exception e) {
            log.error("Unexpected error sending password reset email to: {} - Error: {}", to, e.getMessage());
            log.warn("Email sending failed, but password reset will continue. Check email configuration.");
        }
    }
    
    private String createEmailConfirmationTemplate(String name, String confirmationUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Email Confirmation - Smart Agriculture</title>
                <style>
                    body { 
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; 
                        line-height: 1.6; 
                        color: #333; 
                        margin: 0; 
                        padding: 0; 
                        background-color: #f8fafc;
                    }
                    .container { 
                        max-width: 600px; 
                        margin: 0 auto; 
                        padding: 20px; 
                        background-color: #ffffff;
                        border-radius: 12px;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }
                    .header { 
                        background: linear-gradient(135deg, #3b82f6 0%%, #1d4ed8 50%%, #10b981 100%%); 
                        color: white; 
                        padding: 40px 30px; 
                        text-align: center; 
                        border-radius: 12px 12px 0 0; 
                        margin: -20px -20px 0 -20px;
                    }
                    .header h1 { 
                        margin: 0; 
                        font-size: 28px; 
                        font-weight: 700;
                        text-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    }
                    .header p { 
                        margin: 8px 0 0 0; 
                        font-size: 16px; 
                        opacity: 0.9;
                    }
                    .content { 
                        padding: 40px 30px; 
                        border-radius: 0 0 12px 12px; 
                    }
                    .content h2 { 
                        color: #1f2937; 
                        font-size: 24px; 
                        margin: 0 0 20px 0; 
                        font-weight: 600;
                    }
                    .content p { 
                        margin: 16px 0; 
                        font-size: 16px; 
                        color: #4b5563;
                    }
                    .button { 
                        display: inline-block; 
                        background: linear-gradient(135deg, #3b82f6 0%%, #10b981 100%%); 
                        color: white; 
                        padding: 16px 32px; 
                        text-decoration: none; 
                        border-radius: 8px; 
                        margin: 24px 0; 
                        font-weight: 600;
                        font-size: 16px;
                        text-align: center;
                        box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
                        transition: all 0.2s ease;
                    }
                    .button:hover { 
                        transform: translateY(-2px);
                        box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4);
                    }
                    .link-box { 
                        background: #f3f4f6; 
                        padding: 16px; 
                        border-radius: 8px; 
                        border-left: 4px solid #3b82f6;
                        margin: 20px 0;
                        word-break: break-all;
                        font-family: 'Courier New', monospace;
                        font-size: 14px;
                        color: #374151;
                    }
                    .warning { 
                        background: #fef3c7; 
                        border: 1px solid #f59e0b; 
                        color: #92400e; 
                        padding: 16px; 
                        border-radius: 8px; 
                        margin: 20px 0;
                        font-weight: 500;
                    }
                    .footer { 
                        text-align: center; 
                        margin-top: 40px; 
                        padding-top: 20px; 
                        border-top: 1px solid #e5e7eb;
                        color: #6b7280; 
                        font-size: 14px; 
                    }
                    .footer p { 
                        margin: 8px 0; 
                    }
                    .features { 
                        display: flex; 
                        justify-content: space-around; 
                        margin: 30px 0; 
                        flex-wrap: wrap;
                    }
                    .feature { 
                        text-align: center; 
                        margin: 10px; 
                        flex: 1; 
                        min-width: 120px;
                    }
                    .feature-icon { 
                        font-size: 24px; 
                        margin-bottom: 8px;
                    }
                    .feature-text { 
                        font-size: 12px; 
                        color: #6b7280; 
                        font-weight: 500;
                    }
                    @media (max-width: 600px) {
                        .container { margin: 10px; padding: 15px; }
                        .header { padding: 30px 20px; }
                        .content { padding: 30px 20px; }
                        .features { flex-direction: column; }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🌱 Smart Agriculture</h1>
                        <p>Welcome to the future of farming!</p>
                    </div>
                    <div class="content">
                        <h2>Hello %s!</h2>
                        <p>Thank you for registering with Smart Agriculture! We're excited to help you revolutionize your farming operations with cutting-edge technology.</p>
                        
                        <p>To complete your registration and start managing your farm, please confirm your email address by clicking the button below:</p>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="button">✅ Confirm Email Address</a>
                        </div>
                        
                        <div class="warning">
                            <strong>⏰ Important:</strong> This confirmation link will expire in 24 hours for security reasons.
                        </div>
                        
                        <p>If the button doesn't work, you can copy and paste this link into your browser:</p>
                        <div class="link-box">%s</div>
                        
                        <div class="features">
                            <div class="feature">
                                <div class="feature-icon">💧</div>
                                <div class="feature-text">Smart Irrigation</div>
                            </div>
                            <div class="feature">
                                <div class="feature-icon">📊</div>
                                <div class="feature-text">Analytics</div>
                            </div>
                            <div class="feature">
                                <div class="feature-icon">🔔</div>
                                <div class="feature-text">Alerts</div>
                            </div>
                        </div>
                        
                        <p><strong>What's next?</strong> Once you confirm your email, you'll have access to:</p>
                        <ul style="color: #4b5563; margin: 16px 0;">
                            <li>AI-powered irrigation recommendations</li>
                            <li>Real-time crop monitoring</li>
                            <li>Weather-based alerts</li>
                            <li>Disease detection and prevention</li>
                        </ul>
                        
                        <p>If you didn't create an account with Smart Agriculture, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p><strong>© 2024 Smart Agriculture. All rights reserved.</strong></p>
                        <p>This is an automated message, please do not reply.</p>
                        <p>For support, contact us at support@smartagriculture.com</p>
                    </div>
                </div>
            </body>
            </html>
            """, name, confirmationUrl, confirmationUrl);
    }
    
    private String createPasswordResetTemplate(String name, String resetUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Reset - Smart Agriculture</title>
                <style>
                    body { 
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; 
                        line-height: 1.6; 
                        color: #333; 
                        margin: 0; 
                        padding: 0; 
                        background-color: #f8fafc;
                    }
                    .container { 
                        max-width: 600px; 
                        margin: 0 auto; 
                        padding: 20px; 
                        background-color: #ffffff;
                        border-radius: 12px;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }
                    .header { 
                        background: linear-gradient(135deg, #ef4444 0%%, #dc2626 50%%, #b91c1c 100%%); 
                        color: white; 
                        padding: 40px 30px; 
                        text-align: center; 
                        border-radius: 12px 12px 0 0; 
                        margin: -20px -20px 0 -20px;
                    }
                    .header h1 { 
                        margin: 0; 
                        font-size: 28px; 
                        font-weight: 700;
                        text-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    }
                    .header p { 
                        margin: 8px 0 0 0; 
                        font-size: 16px; 
                        opacity: 0.9;
                    }
                    .content { 
                        padding: 40px 30px; 
                        border-radius: 0 0 12px 12px; 
                    }
                    .content h2 { 
                        color: #1f2937; 
                        font-size: 24px; 
                        margin: 0 0 20px 0; 
                        font-weight: 600;
                    }
                    .content p { 
                        margin: 16px 0; 
                        font-size: 16px; 
                        color: #4b5563;
                    }
                    .button { 
                        display: inline-block; 
                        background: linear-gradient(135deg, #ef4444 0%%, #dc2626 100%%); 
                        color: white; 
                        padding: 16px 32px; 
                        text-decoration: none; 
                        border-radius: 8px; 
                        margin: 24px 0; 
                        font-weight: 600;
                        font-size: 16px;
                        text-align: center;
                        box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
                        transition: all 0.2s ease;
                    }
                    .button:hover { 
                        transform: translateY(-2px);
                        box-shadow: 0 6px 16px rgba(239, 68, 68, 0.4);
                    }
                    .link-box { 
                        background: #f3f4f6; 
                        padding: 16px; 
                        border-radius: 8px; 
                        border-left: 4px solid #ef4444;
                        margin: 20px 0;
                        word-break: break-all;
                        font-family: 'Courier New', monospace;
                        font-size: 14px;
                        color: #374151;
                    }
                    .warning { 
                        background: #fef2f2; 
                        border: 1px solid #fca5a5; 
                        color: #991b1b; 
                        padding: 16px; 
                        border-radius: 8px; 
                        margin: 20px 0;
                        font-weight: 500;
                    }
                    .security-info { 
                        background: #f0f9ff; 
                        border: 1px solid #0ea5e9; 
                        color: #0c4a6e; 
                        padding: 16px; 
                        border-radius: 8px; 
                        margin: 20px 0;
                    }
                    .footer { 
                        text-align: center; 
                        margin-top: 40px; 
                        padding-top: 20px; 
                        border-top: 1px solid #e5e7eb;
                        color: #6b7280; 
                        font-size: 14px; 
                    }
                    .footer p { 
                        margin: 8px 0; 
                    }
                    .security-tips { 
                        background: #f9fafb; 
                        padding: 20px; 
                        border-radius: 8px; 
                        margin: 20px 0;
                        border: 1px solid #e5e7eb;
                    }
                    .security-tips h3 { 
                        color: #1f2937; 
                        margin: 0 0 12px 0; 
                        font-size: 16px;
                        font-weight: 600;
                    }
                    .security-tips ul { 
                        margin: 8px 0; 
                        padding-left: 20px; 
                        color: #4b5563;
                    }
                    .security-tips li { 
                        margin: 4px 0; 
                        font-size: 14px;
                    }
                    @media (max-width: 600px) {
                        .container { margin: 10px; padding: 15px; }
                        .header { padding: 30px 20px; }
                        .content { padding: 30px 20px; }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔒 Smart Agriculture</h1>
                        <p>Password Reset Request</p>
                    </div>
                    <div class="content">
                        <h2>Hello %s!</h2>
                        <p>We received a request to reset your password for your Smart Agriculture account. This is a secure process to help you regain access to your account.</p>
                        
                        <p>Click the button below to reset your password:</p>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="button">🔑 Reset My Password</a>
                        </div>
                        
                        <div class="warning">
                            <strong>⏰ Important:</strong> This reset link will expire in 1 hour for security reasons.
                        </div>
                        
                        <p>If the button doesn't work, you can copy and paste this link into your browser:</p>
                        <div class="link-box">%s</div>
                        
                        <div class="security-info">
                            <strong>🛡️ Security Notice:</strong> If you didn't request this password reset, please ignore this email. Your account remains secure and your password will not be changed.
                        </div>
                        
                        <div class="security-tips">
                            <h3>💡 Password Security Tips:</h3>
                            <ul>
                                <li>Use a strong, unique password with at least 8 characters</li>
                                <li>Include a mix of uppercase, lowercase, numbers, and symbols</li>
                                <li>Avoid using personal information or common words</li>
                                <li>Consider using a password manager for better security</li>
                            </ul>
                        </div>
                        
                        <p>If you continue to have issues accessing your account, please contact our support team at support@smartagriculture.com</p>
                    </div>
                    <div class="footer">
                        <p><strong>© 2024 Smart Agriculture. All rights reserved.</strong></p>
                        <p>This is an automated message, please do not reply.</p>
                        <p>For support, contact us at support@smartagriculture.com</p>
                    </div>
                </div>
            </body>
            </html>
            """, name, resetUrl, resetUrl);
    }
}
