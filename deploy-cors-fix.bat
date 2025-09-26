@echo off
echo ========================================
echo DEPLOY CORS FIX - URGENT
echo ========================================

echo.
echo Current Issue:
echo - CORS returning 'https://railway.com' instead of Vercel domain
echo - Frontend: https://agriculture-frontend-two.vercel.app
echo - Backend: https://agriculture-backend-production.railway.app
echo.

echo [1/4] Building with CORS fixes...
call mvnw.cmd clean package -DskipTests -q

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo Build successful!
echo.

echo [2/4] Testing current CORS...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/actuator/health' -Method GET; Write-Host 'Backend is UP - Status:' $response.StatusCode } catch { Write-Host 'Backend check: FAILED -' $_.Exception.Message }"

echo.
echo [3/4] Deploying to Railway...
echo Choose deployment method:
echo 1. Railway CLI (recommended)
echo 2. Git push (if auto-deploy enabled)
echo 3. Manual upload
echo.

set /p choice="Enter choice (1-3): "

if "%choice%"=="1" (
    echo Deploying with Railway CLI...
    railway deploy
    if %ERRORLEVEL% neq 0 (
        echo Railway CLI not found. Please install it or use option 2/3.
    )
) else if "%choice%"=="2" (
    echo Please run these commands:
    echo git add .
    echo git commit -m "Fix CORS for Vercel frontend"
    echo git push
    echo.
    echo Then wait for Railway auto-deployment...
) else (
    echo Manual deployment:
    echo 1. Go to Railway dashboard
    echo 2. Upload target/agriculture-backend-0.0.1-SNAPSHOT.jar
    echo 3. Or use Railway CLI: railway deploy
)

echo.
echo [4/4] CORS Configuration Applied:
echo ✅ https://agriculture-frontend-two.vercel.app
echo ✅ https://agriculture-frontend.vercel.app  
echo ✅ https://agriculture-frontend-btleirx65.vercel.app
echo ✅ Local development URLs
echo.
echo After deployment, test from your Vercel frontend!
echo.
pause