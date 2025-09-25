@echo off
echo ========================================
echo FINAL SMTP TIMEOUT FIX - COMPLETE SOLUTION
echo ========================================
echo.

echo Step 1: Cleaning and building application...
call mvn clean package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo Build failed!
    exit /b 1
)

echo.
echo Step 2: Setting all environment variables to disable mail completely...
set SPRING_PROFILES_ACTIVE=prod
set APP_EMAIL_ENABLED=false
set MANAGEMENT_HEALTH_MAIL_ENABLED=false
set SPRING_MAIL_HOST=
set SPRING_MAIL_PORT=
set SPRING_MAIL_USERNAME=
set SPRING_MAIL_PASSWORD=
set SPRING_AUTOCONFIGURE_EXCLUDE=org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration

echo.
echo Step 3: Starting application with complete mail disable...
echo This should eliminate all SMTP-related issues.
echo.

java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar ^
    --spring.profiles.active=prod ^
    --app.email.enabled=false ^
    --management.health.mail.enabled=false ^
    --spring.mail.host= ^
    --spring.mail.port= ^
    --spring.mail.username= ^
    --spring.mail.password= ^
    --spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration

echo.
echo Application started with complete mail disable.
echo Health endpoint should now respond quickly without any SMTP timeouts.
echo.
echo Test the health endpoint: http://localhost:9090/api/actuator/health
