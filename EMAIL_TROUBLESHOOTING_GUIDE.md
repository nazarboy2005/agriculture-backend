# Email Troubleshooting Guide

## Issues Fixed

### 1. Registration Flow Issue ✅ FIXED
**Problem**: After clicking "Create Account", users only saw a toast message but weren't redirected to a confirmation page.

**Solution**: 
- Updated `Register.tsx` to redirect users to `/auth/confirm-email?email={email}&pending=true` after successful registration
- Enhanced `ConfirmEmail.tsx` to show a pending confirmation state with instructions
- Added resend confirmation functionality

### 2. Email Sending Issues ✅ IMPROVED
**Problem**: Emails weren't being sent due to configuration and error handling issues.

**Solutions Applied**:
- Enhanced error logging in `EmailService.java` with detailed exception information
- Improved email configuration validation
- Added better debugging information for email sending failures

## Current Email Configuration

### Backend Configuration
```properties
# Email Settings (application.properties)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=killiyaezov@gmail.com
spring.mail.password=wmyahtxvziwqsdly
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com

# Application Settings
app.email.enabled=true
app.email.from=killiyaezov@gmail.com
app.frontend.url=https://agriculture-frontend-two.vercel.app
```

### Production Configuration
```properties
# Production Email Settings (application-prod.properties)
spring.mail.username=${SMTP_USERNAME:killiyaezov@gmail.com}
spring.mail.password=${SMTP_PASSWORD:wmyahtxvziwqsdly}
app.email.enabled=true
```

## Email Flow

### 1. Registration Flow
1. User fills registration form
2. Backend creates user account
3. Backend generates confirmation token
4. Backend sends confirmation email
5. Frontend redirects to confirmation page
6. User clicks email link
7. Frontend confirms email with backend
8. User account is activated

### 2. Password Reset Flow
1. User requests password reset
2. Backend generates reset token
3. Backend sends reset email
4. User clicks email link
5. Frontend shows reset form
6. User submits new password
7. Backend updates password

## Troubleshooting Steps

### 1. Check Email Configuration
```bash
# Check if email is enabled
grep "app.email.enabled" src/main/resources/application.properties

# Check SMTP settings
grep "spring.mail" src/main/resources/application.properties
```

### 2. Test Email Sending
```bash
# Check application logs for email sending attempts
# Look for these log messages:
# - "Attempting to send email confirmation to: {email}"
# - "Email enabled: true, Mail sender present: true"
# - "Email confirmation sent successfully to: {email}"
```

### 3. Common Issues and Solutions

#### Issue: "Email sending is disabled or mail sender not available"
**Cause**: Email service is disabled or JavaMailSender bean is not available
**Solution**: 
- Check `app.email.enabled=true` in application.properties
- Verify Spring Boot mail auto-configuration is not excluded
- Check if `JavaMailSender` bean is properly configured

#### Issue: "Failed to send email confirmation - Authentication failed"
**Cause**: SMTP credentials are incorrect or Gmail security settings
**Solution**:
- Verify Gmail credentials are correct
- Enable "Less secure app access" or use App Passwords
- Check if 2FA is enabled (requires App Password)

#### Issue: "Connection timeout"
**Cause**: Network issues or firewall blocking SMTP
**Solution**:
- Check network connectivity
- Verify firewall allows outbound connections on port 587
- Test with different SMTP settings

### 4. Gmail Configuration Requirements

#### For Gmail SMTP:
1. **Enable 2-Factor Authentication** on Gmail account
2. **Generate App Password**:
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate password for "Mail"
3. **Use App Password** instead of regular password in configuration

#### Alternative: Use OAuth2 (Recommended for Production)
```properties
# OAuth2 Configuration (more secure)
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.oauth2.enable=true
spring.mail.properties.mail.smtp.oauth2.client-id=${GMAIL_CLIENT_ID}
spring.mail.properties.mail.smtp.oauth2.client-secret=${GMAIL_CLIENT_SECRET}
spring.mail.properties.mail.smtp.oauth2.refresh-token=${GMAIL_REFRESH_TOKEN}
```

### 5. Testing Email Functionality

#### Manual Testing:
1. Register a new user
2. Check application logs for email sending attempts
3. Check email inbox (including spam folder)
4. Click confirmation link
5. Verify account is activated

#### Debug Mode:
```properties
# Enable debug logging
logging.level.com.hackathon.agriculture_backend.service.EmailService=DEBUG
logging.level.org.springframework.mail=DEBUG
spring.mail.properties.mail.debug=true
```

### 6. Production Deployment

#### Environment Variables:
```bash
# Set these in your production environment
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password
```

#### Railway/Render Deployment:
```bash
# Add to environment variables
SMTP_USERNAME=killiyaezov@gmail.com
SMTP_PASSWORD=wmyahtxvziwqsdly
```

## Email Templates

### Confirmation Email Features:
- Professional HTML design
- Responsive layout
- Clear call-to-action button
- Security warnings about link expiration
- Fallback text link
- Branded styling

### Password Reset Email Features:
- Security-focused design
- Clear reset instructions
- Security tips
- Link expiration warnings
- Professional appearance

## Monitoring and Logs

### Key Log Messages to Monitor:
```
INFO  - Attempting to send email confirmation to: {email}
INFO  - Email enabled: true, Mail sender present: true
INFO  - Email confirmation sent successfully to: {email}
ERROR - Failed to send email confirmation to: {email} - Error: {error}
WARN  - Email sending is disabled or mail sender not available
```

### Health Check:
```bash
# Check email service health
curl -X GET "https://your-backend-url/actuator/health"
```

## Next Steps

1. **Test the registration flow** with a real email address
2. **Monitor application logs** for email sending attempts
3. **Check spam folders** if emails don't arrive
4. **Verify Gmail credentials** if authentication fails
5. **Consider using a dedicated email service** (SendGrid, Mailgun) for production

## Support

If email issues persist:
1. Check application logs for detailed error messages
2. Verify SMTP configuration matches Gmail requirements
3. Test with a different email provider
4. Consider using a dedicated email service for production use
