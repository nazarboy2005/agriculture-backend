package com.hackathon.agriculture_backend.controller;

import com.hackathon.agriculture_backend.dto.ApiResponse;
import com.hackathon.agriculture_backend.model.User;
import com.hackathon.agriculture_backend.model.Token;
import com.hackathon.agriculture_backend.service.UserService;
import com.hackathon.agriculture_backend.service.EmailService;
import com.hackathon.agriculture_backend.service.TokenService;
import com.hackathon.agriculture_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {
    "http://localhost:3000", 
    "http://127.0.0.1:3000",
    "https://agriculture-frontend-two.vercel.app",
    "https://agriculture-frontend.vercel.app"
}, allowCredentials = "true")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TokenService tokenService;
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("User not authenticated"));
            }
            
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            log.info("Getting user info for email: {}, role: {}", email, user.getRole());
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("email", user.getEmail());
            userInfo.put("name", user.getName());
            userInfo.put("profilePictureUrl", user.getProfilePictureUrl());
            userInfo.put("role", user.getRole());
            userInfo.put("isEnabled", user.getIsEnabled());
            userInfo.put("createdAt", user.getCreatedAt());
            
            return ResponseEntity.ok(ApiResponse.success(userInfo));
            
        } catch (Exception e) {
            log.error("Error getting current user: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to get user information: " + e.getMessage()));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("User not authenticated"));
            }
            
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            
            String newToken = jwtUtil.generateToken(user);
            
            Map<String, String> response = new HashMap<>();
            response.put("token", newToken);
            response.put("type", "Bearer");
            
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to refresh token: " + e.getMessage()));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        try {
            // Stateless logout - clear any potential session data
            request.getSession(false);
            if (request.getSession(false) != null) {
                request.getSession().invalidate();
            }
            
            // In a stateless JWT setup, logout is handled on the client side
            // by removing the token from storage
            return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
            
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to logout: " + e.getMessage()));
        }
    }

    @PostMapping("/create-test-user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createTestUser() {
        try {
            String testEmail = "mbappezu@gmail.com";
            String testName = "Test User";
            String testPassword = "password123";
            
            // Check if user already exists
            Optional<User> existingUser = userService.findByEmailOptional(testEmail);
            if (existingUser.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Test user already exists", 
                    createUserResponse(existingUser.get())));
            }
            
            // Create test user
            User user = new User();
            user.setEmail(testEmail);
            user.setName(testName);
            user.setPassword(passwordEncoder.encode(testPassword));
            user.setEmailVerified(true); // Skip email verification for test user
            user.setIsEnabled(true);
            user.setRole(User.Role.USER);
            
            log.info("Creating test user with email: {}", testEmail);
            User savedUser = userService.save(user);
            log.info("Test user created successfully with ID: {}", savedUser.getId());
            
            return ResponseEntity.ok(ApiResponse.success("Test user created successfully", 
                createUserResponse(savedUser)));
            
        } catch (Exception e) {
            log.error("Error creating test user: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to create test user: " + e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Check if user already exists
            Optional<User> existingUser = userService.findByEmailOptional(request.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("User with this email already exists"));
            }
            
            // Create new user
            User user = new User();
            user.setEmail(request.getEmail());
            user.setName(request.getName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmailVerified(false);
            user.setIsEnabled(true);
            user.setRole(User.Role.USER);
            
            log.info("Creating user with role: {}", user.getRole());
            User savedUser = userService.save(user);
            log.info("User saved with role: {}", savedUser.getRole());
            
            // Generate email confirmation token
            String confirmationToken = tokenService.generateEmailConfirmationToken(request.getEmail());
            savedUser.setEmailVerificationToken(confirmationToken);
            userService.save(savedUser);
            
            // Send confirmation email
            emailService.sendEmailConfirmation(request.getEmail(), request.getName(), confirmationToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful. Please check your email to confirm your account.");
            response.put("email", request.getEmail());
            
            return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
            
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Login attempt for email: {}", request.getEmail());
            Optional<User> userOpt = userService.findByEmailOptional(request.getEmail());
            if (userOpt.isEmpty()) {
                log.warn("Login failed: User not found for email: {}", request.getEmail());
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid email or password"));
            }
            
            User user = userOpt.get();
            log.info("User found: {}, emailVerified: {}, hasPassword: {}", 
                    user.getEmail(), user.getEmailVerified(), user.getPassword() != null);
            
            // Check if user has password (not OAuth user)
            if (user.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Please use Google login for this account"));
            }
            
            // Check password
            boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
            log.info("Password match result: {}", passwordMatches);
            if (!passwordMatches) {
                log.warn("Login failed: Password mismatch for email: {}", request.getEmail());
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid email or password"));
            }
            
            // Check if email is verified
            if (!user.getEmailVerified()) {
                log.warn("Login failed: Email not verified for user: {}", user.getEmail());
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Please verify your email before logging in"));
            }
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("user", createUserResponse(user));
            
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
            
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/confirm-email")
    public ResponseEntity<ApiResponse<Map<String, Object>>> confirmEmail(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Token is required"));
            }
            
            if (!tokenService.validateToken(token, Token.TokenType.EMAIL_CONFIRMATION)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid or expired token. Please request a new confirmation email."));
            }
            
            String email = tokenService.getEmailFromToken(token);
            Optional<User> userOpt = userService.findByEmailOptional(email);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("User not found"));
            }
            
            User user = userOpt.get();
            
            // Check if email is already verified
            if (user.getEmailVerified()) {
                return ResponseEntity.ok(ApiResponse.success("Email is already verified", createUserResponse(user)));
            }
            
            user.setEmailVerified(true);
            user.setEmailVerificationToken(null);
            userService.save(user);
            
            tokenService.invalidateToken(token);
            
            log.info("Email confirmed successfully for user: {}", email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Email confirmed successfully");
            response.put("user", createUserResponse(user));
            
            return ResponseEntity.ok(ApiResponse.success("Email confirmed successfully", response));
            
        } catch (Exception e) {
            log.error("Error confirming email: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Email confirmation failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/resend-confirmation")
    public ResponseEntity<ApiResponse<String>> resendConfirmation(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email is required"));
            }
            
            Optional<User> userOpt = userService.findByEmailOptional(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("User not found"));
            }
            
            User user = userOpt.get();
            if (user.getEmailVerified()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email is already verified"));
            }
            
            // Generate new confirmation token
            String confirmationToken = tokenService.generateEmailConfirmationToken(email);
            user.setEmailVerificationToken(confirmationToken);
            userService.save(user);
            
            // Send confirmation email
            emailService.sendEmailConfirmation(email, user.getName(), confirmationToken);
            
            return ResponseEntity.ok(ApiResponse.success("Confirmation email sent"));
            
        } catch (Exception e) {
            log.error("Error resending confirmation: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to resend confirmation: " + e.getMessage()));
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email is required"));
            }
            
            Optional<User> userOpt = userService.findByEmailOptional(email);
            if (userOpt.isEmpty()) {
                // Don't reveal if user exists or not
                return ResponseEntity.ok(ApiResponse.success("If the email exists, a password reset link has been sent"));
            }
            
            User user = userOpt.get();
            if (user.getPassword() == null) {
                // OAuth user, don't reveal
                return ResponseEntity.ok(ApiResponse.success("If the email exists, a password reset link has been sent"));
            }
            
            // Generate password reset token
            String resetToken = tokenService.generatePasswordResetToken(email);
            
            // Send password reset email
            emailService.sendPasswordResetEmail(email, user.getName(), resetToken);
            
            return ResponseEntity.ok(ApiResponse.success("If the email exists, a password reset link has been sent"));
            
        } catch (Exception e) {
            log.error("Error processing forgot password: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to process forgot password: " + e.getMessage()));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            if (!tokenService.validateToken(request.getToken(), Token.TokenType.PASSWORD_RESET)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid or expired token. Please request a new password reset."));
            }
            
            String email = tokenService.getEmailFromToken(request.getToken());
            Optional<User> userOpt = userService.findByEmailOptional(email);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("User not found"));
            }
            
            User user = userOpt.get();
            
            // Check if user has a password (not OAuth user)
            if (user.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("This account uses Google login. Please use Google to sign in."));
            }
            
            // Validate password strength
            if (request.getPassword().length() < 8) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Password must be at least 8 characters long"));
            }
            
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userService.save(user);
            
            tokenService.invalidateToken(request.getToken());
            
            log.info("Password reset successfully for user: {}", email);
            
            return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
            
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Password reset failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/login/google")
    public ResponseEntity<ApiResponse<String>> getGoogleLoginUrl() {
        try {
            // This endpoint can be used to get the Google OAuth2 login URL
            // The actual OAuth2 flow is handled by Spring Security
            String loginUrl = "/oauth2/authorization/google";
            return ResponseEntity.ok(ApiResponse.success(loginUrl));
            
        } catch (Exception e) {
            log.error("Error getting Google login URL: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to get login URL: " + e.getMessage()));
        }
    }
    
    // Development endpoint to verify email without going through email system
    @PostMapping("/verify-email-dev")
    public ResponseEntity<ApiResponse<String>> verifyEmailDev(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email is required"));
            }
            
            Optional<User> userOpt = userService.findByEmailOptional(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("User not found"));
            }
            
            User user = userOpt.get();
            if (user.getEmailVerified()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Email is already verified"));
            }
            
            user.setEmailVerified(true);
            user.setEmailVerificationToken(null);
            userService.save(user);
            
            log.info("Email verified for development: {}", email);
            return ResponseEntity.ok(ApiResponse.success("Email verified successfully"));
            
        } catch (Exception e) {
            log.error("Error verifying email: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Email verification failed: " + e.getMessage()));
        }
    }
    
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("email", user.getEmail());
        userInfo.put("name", user.getName());
        userInfo.put("profilePictureUrl", user.getProfilePictureUrl());
        userInfo.put("role", user.getRole());
        userInfo.put("isEnabled", user.getIsEnabled());
        userInfo.put("emailVerified", user.getEmailVerified());
        userInfo.put("createdAt", user.getCreatedAt());
        return userInfo;
    }
    
    // DTOs
    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class LoginRequest {
        private String email;
        private String password;
        
        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class ResetPasswordRequest {
        private String token;
        private String password;
        
        // Getters and setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
