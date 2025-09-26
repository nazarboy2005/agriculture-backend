# Email Environment Variables Fix

## Problem Identified
Your deployment environment uses `MAIL_*` environment variables, but the Spring Boot application was configured to use `SMTP_*` variables. This mismatch was preventing the email configuration from being read correctly.

## Environment Variables in Your Deployment
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=killiyaezov@gmail.com
MAIL_PASSWORD=wmyahtxvziwqsdly
```

## Fix Applied

### Updated `application-prod.properties`
**Before:**
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${SMTP_USERNAME:killiyaezov@gmail.com}
spring.mail.password=${SMTP_PASSWORD:wmyahtxvziwqsdly}
app.email.from=${SMTP_USERNAME:killiyaezov@gmail.com}
```

**After:**
```properties
spring.mail.host=${MAIL_HOST:smtp.gmail.com}
spring.mail.port=${MAIL_PORT:587}
spring.mail.username=${MAIL_USERNAME:killiyaezov@gmail.com}
spring.mail.password=${MAIL_PASSWORD:wmyahtxvziwqsdly}
app.email.from=${MAIL_USERNAME:killiyaezov@gmail.com}
```

## How It Works Now

1. **Environment Variables**: Your deployment platform reads the `MAIL_*` variables
2. **Spring Configuration**: The application now correctly maps these to Spring Mail properties
3. **Email Service**: The EmailService will now receive the correct configuration
4. **Email Sending**: Forgot password emails should now be sent successfully

## Testing the Fix

### 1. Deploy the Updated Configuration
```bash
# Run the deployment script
./deploy-email-fix.bat
```

### 2. Test the Forgot Password Endpoint
```bash
curl -X POST "https://your-backend-url/api/v1/auth/forgot-password" \
  -H "Content-Type: application/json" \
  -d '{"email": "your-email@example.com"}'
```

### 3. Test the Email Test Endpoint
```bash
curl -X POST "https://your-backend-url/api/v1/auth/test-email" \
  -H "Content-Type: application/json" \
  -d '{"email": "your-email@example.com"}'
```

## Expected Results

### Success Logs:
```
INFO  - Attempting to send password reset email to: user@example.com
INFO  - Email enabled: true, Mail sender present: true
INFO  - Password reset email sent successfully to: user@example.com
```

### API Response:
```json
{
  "success": true,
  "message": "If the email exists, a password reset link has been sent",
  "data": null
}
```

## Troubleshooting

### If emails still don't send:

#### 1. Check Environment Variables
Ensure your deployment platform has these variables set:
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=killiyaezov@gmail.com
MAIL_PASSWORD=wmyahtxvziwqsdly
```

#### 2. Check Gmail Credentials
The password `wmyahtxvziwqsdly` might be expired. To fix:
1. Go to Google Account settings
2. Enable 2-Factor Authentication
3. Generate new App Password
4. Update `MAIL_PASSWORD` environment variable

#### 3. Check Application Logs
Look for these error messages:
```
WARN  - Email sending is disabled or mail sender not available
ERROR - Failed to send password reset email - Error: Authentication failed
ERROR - Failed to send password reset email - Error: Connection timeout
```

## Alternative Solutions

If Gmail continues to have issues, consider using a dedicated email service:

### SendGrid
```bash
MAIL_HOST=smtp.sendgrid.net
MAIL_PORT=587
MAIL_USERNAME=apikey
MAIL_PASSWORD=your_sendgrid_api_key
```

### AWS SES
```bash
MAIL_HOST=email-smtp.us-east-1.amazonaws.com
MAIL_PORT=587
MAIL_USERNAME=your_aws_ses_username
MAIL_PASSWORD=your_aws_ses_password
```

### Mailgun
```bash
MAIL_HOST=smtp.mailgun.org
MAIL_PORT=587
MAIL_USERNAME=your_mailgun_username
MAIL_PASSWORD=your_mailgun_password
```

## Files Modified
- `src/main/resources/application-prod.properties` - Updated to use MAIL_* environment variables
- `deploy-email-fix.bat` - Deployment script
- `EMAIL_ENVIRONMENT_VARIABLES_FIX.md` - This documentation

## Next Steps
1. **Deploy the updated configuration**
2. **Test the forgot password functionality**
3. **Check application logs for email sending status**
4. **Update Gmail credentials if needed**
5. **Consider using a dedicated email service for production**
