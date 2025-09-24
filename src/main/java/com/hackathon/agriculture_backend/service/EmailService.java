package com.hackathon.agriculture_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${app.email.from:noreply@smartagriculture.com}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    public void sendEmailConfirmation(String to, String name, String confirmationToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Confirm Your Email - Smart Agriculture");
            
            // Create email content
            String confirmationUrl = frontendUrl + "/auth/confirm-email?token=" + confirmationToken;
            String emailContent = createEmailConfirmationTemplate(name, confirmationUrl);
            
            helper.setText(emailContent, true);
            
            mailSender.send(message);
            log.info("Email confirmation sent to: {}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send email confirmation to: {}", to, e);
            throw new RuntimeException("Failed to send email confirmation", e);
        }
    }
    
    public void sendPasswordResetEmail(String to, String name, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Reset Your Password - Smart Agriculture");
            
            // Create email content
            String resetUrl = frontendUrl + "/auth/reset-password?token=" + resetToken;
            String emailContent = createPasswordResetTemplate(name, resetUrl);
            
            helper.setText(emailContent, true);
            
            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", to, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    private String createEmailConfirmationTemplate(String name, String confirmationUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Email Confirmation</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .button { display: inline-block; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 14px; }
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
                        <p>Thank you for registering with Smart Agriculture. To complete your registration and start managing your farm, please confirm your email address.</p>
                        <p>Click the button below to confirm your email:</p>
                        <a href="%s" class="button">Confirm Email Address</a>
                        <p>If the button doesn't work, you can copy and paste this link into your browser:</p>
                        <p style="word-break: break-all; background: #eee; padding: 10px; border-radius: 5px;">%s</p>
                        <p><strong>This link will expire in 24 hours.</strong></p>
                        <p>If you didn't create an account with Smart Agriculture, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>© 2024 Smart Agriculture. All rights reserved.</p>
                        <p>This is an automated message, please do not reply.</p>
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
                <title>Password Reset</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .button { display: inline-block; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🌱 Smart Agriculture</h1>
                        <p>Password Reset Request</p>
                    </div>
                    <div class="content">
                        <h2>Hello %s!</h2>
                        <p>We received a request to reset your password for your Smart Agriculture account.</p>
                        <p>Click the button below to reset your password:</p>
                        <a href="%s" class="button">Reset Password</a>
                        <p>If the button doesn't work, you can copy and paste this link into your browser:</p>
                        <p style="word-break: break-all; background: #eee; padding: 10px; border-radius: 5px;">%s</p>
                        <p><strong>This link will expire in 1 hour.</strong></p>
                        <p>If you didn't request a password reset, please ignore this email. Your password will remain unchanged.</p>
                    </div>
                    <div class="footer">
                        <p>© 2024 Smart Agriculture. All rights reserved.</p>
                        <p>This is an automated message, please do not reply.</p>
                    </div>
                </div>
            </body>
            </html>
            """, name, resetUrl, resetUrl);
    }
}
