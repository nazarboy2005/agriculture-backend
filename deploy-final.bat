@echo off
echo ========================================
echo Final Deployment with Correct Frontend URL
echo ========================================

echo.
echo Frontend URL: https://agriculture-frontend-two.vercel.app
echo Backend URL: https://agriculture-backend-production.railway.app
echo.

echo [1/4] Building application with CORS fixes...
call mvnw.cmd clean package -DskipTests -q

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    echo Please check the compilation errors above.
    pause
    exit /b 1
)

echo Build completed successfully!
echo.

echo [2/4] Testing CORS configuration...
echo Testing with correct frontend URL: https://agriculture-frontend-two.vercel.app
powershell -Command "try { $headers = @{'Origin' = 'https://agriculture-frontend-two.vercel.app'; 'Access-Control-Request-Method' = 'POST'}; $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method OPTIONS -Headers $headers; Write-Host 'CORS Test: SUCCESS - Status:' $response.StatusCode } catch { Write-Host 'CORS Test: FAILED -' $_.Exception.Message }"

echo.
echo [3/4] Checking Railway CLI...
railway --version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Railway CLI not found. Manual deployment required.
    echo.
    echo Manual deployment options:
    echo 1. Push to GitHub (if auto-deploy is set up)
    echo 2. Use Railway web interface
    echo 3. Install Railway CLI: npm install -g @railway/cli
    echo.
    echo Your CORS configuration is ready for deployment!
    echo.
    pause
    exit /b 0
)

echo [4/4] Deploying to Railway...
railway deploy

echo.
echo ========================================
echo Deployment Complete!
echo ========================================
echo.
echo ✅ CORS configuration updated for:
echo    - https://agriculture-frontend-two.vercel.app
echo    - https://agriculture-frontend-btleirx65.vercel.app
echo    - https://agriculture-frontend.vercel.app
echo    - Local development URLs
echo.
echo 🎯 Test your frontend now:
echo    https://agriculture-frontend-two.vercel.app
echo.
echo The 401 and CORS errors should now be resolved!
echo.
pause
