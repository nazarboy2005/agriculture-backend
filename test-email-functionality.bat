@echo off
echo Testing Email Functionality for Forgot Password
echo ================================================

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
echo This will start the application with detailed email logging.
echo Look for these log messages:
echo - "Attempting to send password reset email to: {email}"
echo - "Email enabled: true, Mail sender present: true"
echo - "Password reset email sent successfully to: {email}"
echo.
echo Press Ctrl+C to stop the application when done testing.
echo.

java -jar target/app.jar --spring.profiles.active=prod --logging.level.com.hackathon.agriculture_backend.service.EmailService=DEBUG

pause
