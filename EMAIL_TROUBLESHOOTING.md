# Email Troubleshooting Guide

## Current Issue: "Failed to send resend email"

### Problem Analysis
The application is configured to use Gmail SMTP for sending emails, but there might be authentication issues.

### Current Email Configuration
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=killiyaezov@gmail.com
spring.mail.password=wmyahtxvziwqsdly
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

### Possible Issues & Solutions

#### 1. Gmail App Password Issues
- **Problem**: Gmail might have disabled the app password or it might be incorrect
- **Solution**: Generate a new Gmail App Password
  1. Go to Google Account settings
  2. Security → 2-Step Verification (must be enabled)
  3. App passwords → Generate new app password
  4. Use the new password in configuration

#### 2. Gmail Security Settings
- **Problem**: Gmail might block "less secure apps"
- **Solution**: 
  1. Enable 2-Factor Authentication
  2. Use App Passwords instead of regular password
  3. Ensure "Less secure app access" is disabled (use App Passwords instead)

#### 3. Network/Firewall Issues
- **Problem**: SMTP port 587 might be blocked
- **Solution**: Try alternative ports (465 for SSL) or check firewall settings

### Alternative Email Services

#### Option 1: Use Resend (Recommended)
```properties
# Resend API Configuration
spring.mail.host=smtp.resend.com
spring.mail.port=587
spring.mail.username=resend
spring.mail.password=YOUR_RESEND_API_KEY
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

#### Option 2: Use SendGrid
```properties
# SendGrid Configuration
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=YOUR_SENDGRID_API_KEY
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

#### Option 3: Use Mailgun
```properties
# Mailgun Configuration
spring.mail.host=smtp.mailgun.org
spring.mail.port=587
spring.mail.username=YOUR_MAILGUN_SMTP_USERNAME
spring.mail.password=YOUR_MAILGUN_SMTP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Quick Fix: Disable Email for Development
If you want to disable email sending temporarily for development:

```properties
# Disable email sending
app.email.enabled=false
```

### Testing Email Configuration
Add this endpoint to test email configuration:

```java
@GetMapping("/test-email")
public ResponseEntity<String> testEmail() {
    try {
        emailService.sendEmailConfirmation("test@example.com", "Test User", "test-token");
        return ResponseEntity.ok("Email sent successfully");
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Email failed: " + e.getMessage());
    }
}
```

### Recommended Action
1. **Immediate**: Try generating a new Gmail App Password
2. **Long-term**: Consider switching to Resend or SendGrid for better reliability
3. **Development**: Disable email sending if not critical for testing
