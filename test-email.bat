@echo off
echo Testing Email Configuration...
echo.

echo 1. Checking email configuration in application.properties
findstr "app.email.enabled" src\main\resources\application.properties
findstr "spring.mail" src\main\resources\application.properties
echo.

echo 2. Checking if email is enabled
findstr "app.email.enabled=true" src\main\resources\application.properties
if %errorlevel% equ 0 (
    echo ✅ Email is enabled
) else (
    echo ❌ Email is disabled - run enable-email.bat
)
echo.

echo 3. Checking SMTP configuration
findstr "spring.mail.host=smtp.gmail.com" src\main\resources\application.properties
if %errorlevel% equ 0 (
    echo ✅ Gmail SMTP configured
) else (
    echo ❌ Gmail SMTP not configured
)
echo.

echo 4. Checking frontend URL
findstr "app.frontend.url" src\main\resources\application.properties
echo.

echo 5. Testing registration flow:
echo    - Register a new user
echo    - Check if redirected to confirmation page
echo    - Check application logs for email sending attempts
echo    - Check email inbox (including spam folder)
echo.

echo 6. Common issues to check:
echo    - Gmail credentials are correct
echo    - 2FA is enabled on Gmail account
echo    - App Password is used instead of regular password
echo    - Network allows outbound connections on port 587
echo.

echo 7. To enable debug logging, add these to application.properties:
echo    logging.level.com.hackathon.agriculture_backend.service.EmailService=DEBUG
echo    spring.mail.properties.mail.debug=true
echo.

pause
