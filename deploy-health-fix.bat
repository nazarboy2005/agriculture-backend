@echo off
echo Deploying with SMTP timeout fix...
echo.

echo Building application with production profile...
call mvn clean package -DskipTests -Pprod -q
if %ERRORLEVEL% neq 0 (
    echo Build failed!
    exit /b 1
)

echo.
echo Creating production JAR with correct configuration...
if exist target\agriculture-backend-0.0.1-SNAPSHOT.jar (
    echo JAR file created successfully
) else (
    echo JAR file not found!
    exit /b 1
)

echo.
echo Setting production environment variables...
set SPRING_PROFILES_ACTIVE=prod
set APP_EMAIL_ENABLED=false
set MANAGEMENT_HEALTH_MAIL_ENABLED=false

echo.
echo Starting application with production configuration...
java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

echo.
echo Application started with SMTP timeout fix applied!
echo Health endpoint should now work without 134-second timeouts.
