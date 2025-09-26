@echo off
echo ========================================
echo QUICK CORS DEPLOYMENT
echo ========================================

echo.
echo CORS Configuration Updated:
echo ✅ application-prod.properties - CORS origins configured
echo ✅ CorsConfig.java - Dynamic CORS with environment variables
echo.

echo Deployment Options:
echo 1. Git Push (if auto-deploy enabled)
echo 2. Railway CLI (if installed)
echo 3. Manual deployment instructions
echo.

set /p choice="Choose deployment method (1-3): "

if "%choice%"=="1" (
    echo.
    echo Git Push Deployment:
    echo 1. git add .
    echo 2. git commit -m "Fix CORS for Vercel frontend - allow agriculture-frontend-two.vercel.app"
    echo 3. git push
    echo.
    echo After push, Railway will auto-deploy the changes.
    echo Wait 2-3 minutes then test from your frontend.
) else if "%choice%"=="2" (
    echo.
    echo Railway CLI Deployment:
    railway deploy
    if %ERRORLEVEL% neq 0 (
        echo Railway CLI not found. Install it first or use option 1/3.
    )
) else (
    echo.
    echo Manual Deployment:
    echo 1. Go to Railway dashboard
    echo 2. Connect your GitHub repo (if not already)
    echo 3. Trigger a new deployment
    echo 4. Or upload the JAR file manually
    echo.
    echo The CORS fix is in the code - just needs to be deployed!
)

echo.
echo ========================================
echo CORS FIX SUMMARY
echo ========================================
echo.
echo Changes Made:
echo - application-prod.properties: Added spring.web.cors.allowed-origins
echo - CorsConfig.java: Enhanced with environment variable support
echo - Both include: https://agriculture-frontend-two.vercel.app
echo.
echo After deployment, your frontend should work!
echo.
pause
