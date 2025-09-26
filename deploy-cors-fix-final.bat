@echo off
echo ========================================
echo DEPLOYING CORS FIX TO RAILWAY
echo ========================================

echo.
echo Building the application...
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Build failed!
    pause
    exit /b 1
)

echo.
echo ✅ Build successful! Deploying to Railway...
railway deploy

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Deployment failed!
    pause
    exit /b 1
)

echo.
echo ✅ Deployment successful!
echo.
echo 🔧 CORS Configuration Fixed:
echo - SecurityConfig now properly enables CORS with Customizer.withDefaults()
echo - CorsConfig allows your Vercel frontend: https://agriculture-frontend-two.vercel.app
echo - Credentials are enabled for JWT authentication
echo.
echo 🧪 Test your frontend now:
echo 1. Go to https://agriculture-frontend-two.vercel.app
echo 2. Try to register/login
echo 3. Check browser dev tools Network tab for CORS headers
echo.
echo Expected response headers:
echo - Access-Control-Allow-Origin: https://agriculture-frontend-two.vercel.app
echo - Access-Control-Allow-Credentials: true
echo.
pause
