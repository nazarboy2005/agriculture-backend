@echo off
echo ========================================
echo Deploying Email and Chat Fixes
echo ========================================

echo.
echo [1/4] Building the application...
call mvn clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [2/4] Building Docker image...
docker build -t agriculture-backend .

if %ERRORLEVEL% neq 0 (
    echo ERROR: Docker build failed!
    pause
    exit /b 1
)

echo.
echo [3/4] Deploying to Railway...
railway up --detach

if %ERRORLEVEL% neq 0 (
    echo ERROR: Railway deployment failed!
    pause
    exit /b 1
)

echo.
echo [4/4] Setting environment variables...
railway variables set APP_EMAIL_ENABLED=true
railway variables set MANAGEMENT_HEALTH_MAIL_ENABLED=true
railway variables set SPRING_PROFILES_ACTIVE=prod

echo.
echo ========================================
echo Email and Chat fixes deployed successfully!
echo ========================================
echo.
echo Fixes applied:
echo - Enhanced email debugging with detailed logging
echo - Added SimpleChatService as fallback for chat
echo - Improved chat response reliability
echo - Added /simple-send endpoint for testing
echo.
echo Test endpoints:
echo - Chat: /v1/chat/simple-send
echo - Email: Check logs for EMAIL DEBUG messages
echo.
pause
