@echo off
echo ========================================
echo Deploying All Fixes - Email, OAuth, UI
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
echo All fixes deployed successfully!
echo ========================================
echo.
echo Fixes applied:
echo - Email functionality enabled
echo - Google OAuth redirect URI fixed
echo - Chat interface width increased
echo - Double spinner issue resolved
echo.
echo Please update your Google Cloud Console with the new redirect URI:
echo https://agriculture-backend-1077945709935.europe-west1.run.app/api/login/oauth2/code/google
echo.
pause
