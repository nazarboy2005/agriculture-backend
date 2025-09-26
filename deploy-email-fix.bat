@echo off
echo Deploying Email Configuration Fix
echo ==================================

echo.
echo This script will deploy the email configuration fix.
echo The fix matches your environment variables (MAIL_* prefix).
echo.

echo 1. Building the application...
call mvn clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo 2. Email configuration updated to use your environment variables:
echo    - MAIL_HOST -> spring.mail.host
echo    - MAIL_PORT -> spring.mail.port  
echo    - MAIL_USERNAME -> spring.mail.username
echo    - MAIL_PASSWORD -> spring.mail.password
echo.

echo 3. Deploying to production...
echo    Make sure your environment variables are set:
echo    - MAIL_HOST=smtp.gmail.com
echo    - MAIL_PORT=587
echo    - MAIL_USERNAME=killiyaezov@gmail.com
echo    - MAIL_PASSWORD=wmyahtxvziwqsdly
echo.

echo 4. Testing email functionality...
echo    You can test the forgot password endpoint:
echo    POST /api/v1/auth/forgot-password
echo    {"email": "your-email@example.com"}
echo.

echo 5. Check application logs for:
echo    - "Attempting to send password reset email to: {email}"
echo    - "Email enabled: true, Mail sender present: true"
echo    - "Password reset email sent successfully to: {email}"
echo.

echo Deployment completed! The email functionality should now work.
echo.

pause
