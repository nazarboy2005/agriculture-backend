@echo off
echo Testing Forgot Password Email Functionality
echo ===========================================

echo.
echo This script will help debug the forgot password email issue.
echo.

echo 1. Building the application...
call mvn clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo 2. Starting application with email debugging...
echo.
echo The application will start with detailed email logging.
echo Look for these log messages:
echo - "Attempting to send password reset email to: {email}"
echo - "Email enabled: true, Mail sender present: true"
echo - "Password reset email sent successfully to: {email}"
echo.
echo Once the application is running, you can test the email functionality:
echo.
echo 3. Test the forgot password endpoint:
echo    POST http://localhost:9090/api/v1/auth/forgot-password
echo    Content-Type: application/json
echo    {"email": "your-email@example.com"}
echo.
echo 4. Test the email test endpoint:
echo    POST http://localhost:9090/api/v1/auth/test-email
echo    Content-Type: application/json
echo    {"email": "your-email@example.com"}
echo.
echo Press Ctrl+C to stop the application when done testing.
echo.

java -jar target/app.jar --spring.profiles.active=prod --logging.level.com.hackathon.agriculture_backend.service.EmailService=DEBUG --logging.level.com.hackathon.agriculture_backend.controller.AuthController=DEBUG

pause
