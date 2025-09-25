@echo off
echo ========================================
echo URGENT: Deploy CORS Fixes Now
echo ========================================

echo.
echo Current Issue:
echo - Backend is running but CORS is still returning 'https://railway.com'
echo - Frontend URL: https://agriculture-frontend-two.vercel.app
echo - Need to deploy the CORS configuration fixes
echo.

echo [1/3] Building with CORS fixes...
call mvnw.cmd clean package -DskipTests -q

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    echo Please check compilation errors.
    pause
    exit /b 1
)

echo Build successful!
echo.

echo [2/3] Testing current CORS (should show railway.com)...
powershell -Command "try { $headers = @{'Origin' = 'https://agriculture-frontend-two.vercel.app'; 'Access-Control-Request-Method' = 'POST'}; $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method OPTIONS -Headers $headers; Write-Host 'Current CORS Headers:'; $response.Headers | Where-Object {$_.Key -like '*Access-Control*'} | ForEach-Object { Write-Host '  ' $_.Key ':' $_.Value } } catch { Write-Host 'CORS Test: FAILED -' $_.Exception.Message }"

echo.
echo [3/3] Deploying CORS fixes...
echo.
echo Choose deployment method:
echo 1. Railway CLI (if installed)
echo 2. Git push (if auto-deploy is set up)
echo 3. Manual deployment
echo.

set /p choice="Enter choice (1-3): "

if "%choice%"=="1" (
    echo Deploying with Railway CLI...
    railway deploy
) else if "%choice%"=="2" (
    echo Please push to GitHub to trigger auto-deployment
    echo git add .
    echo git commit -m "Fix CORS configuration for frontend"
    echo git push
) else (
    echo Manual deployment required:
    echo 1. Go to Railway dashboard
    echo 2. Upload the built JAR file
    echo 3. Or use Railway CLI: railway deploy
)

echo.
echo ========================================
echo CORS Fix Deployment Instructions
echo ========================================
echo.
echo The CORS configuration has been updated to allow:
echo ✅ https://agriculture-frontend-two.vercel.app
echo ✅ https://agriculture-frontend-btleirx65.vercel.app
echo ✅ https://agriculture-frontend.vercel.app
echo ✅ Local development URLs
echo.
echo After deployment, the CORS error should be resolved!
echo.
pause
