@echo off
echo ========================================
echo Quick Deploy with CORS Fixes
echo ========================================

echo.
echo [1/3] Building application with CORS fixes...
call mvn clean package -DskipTests -q

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo Build completed successfully!
echo.

echo [2/3] Checking if Railway CLI is available...
railway --version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Railway CLI not found. Please install it or use manual deployment.
    echo.
    echo Manual deployment options:
    echo 1. Push to GitHub (if auto-deploy is set up)
    echo 2. Use Railway web interface
    echo 3. Install Railway CLI: npm install -g @railway/cli
    echo.
    pause
    exit /b 0
)

echo [3/3] Deploying to Railway...
railway deploy

echo.
echo ========================================
echo Deployment Complete!
echo ========================================
echo.
echo Your CORS fixes are now deployed:
echo - Added https://agriculture-frontend-btleirx65.vercel.app to allowed origins
echo - Updated SecurityConfig.java and WebConfig.java
echo - Updated application-prod.properties
echo.
echo Test your frontend now!
echo.
pause
