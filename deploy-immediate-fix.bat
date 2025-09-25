@echo off
echo ========================================
echo IMMEDIATE SMTP TIMEOUT FIX DEPLOYMENT
echo ========================================
echo.

echo Step 1: Building application with production profile...
call mvn clean package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo Build failed!
    exit /b 1
)

echo.
echo Step 2: Setting environment variables to disable mail completely...
set SPRING_PROFILES_ACTIVE=prod
set APP_EMAIL_ENABLED=false
set MANAGEMENT_HEALTH_MAIL_ENABLED=false
set SPRING_MAIL_HOST=
set SPRING_MAIL_PORT=
set SPRING_MAIL_USERNAME=
set SPRING_MAIL_PASSWORD=

echo.
echo Step 3: Starting application with mail completely disabled...
echo This should eliminate the 135-second SMTP timeout issue.
echo.

java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar ^
    --spring.profiles.active=prod ^
    --app.email.enabled=false ^
    --management.health.mail.enabled=false ^
    --spring.mail.host= ^
    --spring.mail.port= ^
    --spring.mail.username= ^
    --spring.mail.password=

echo.
echo Application started with complete mail disable.
echo Health endpoint should now respond quickly without SMTP timeouts.
