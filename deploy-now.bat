@echo off
echo ========================================
echo URGENT: Deploying CORS Fixes NOW
echo ========================================

echo.
echo [1/3] Building with CORS fixes...
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Testing CORS with correct frontend URL...
powershell -Command "try { $headers = @{'Origin' = 'https://agriculture-frontend-two.vercel.app'}; $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method OPTIONS -Headers $headers; Write-Host 'CORS Test: SUCCESS - Status:' $response.StatusCode } catch { Write-Host 'CORS Test: FAILED -' $_.Exception.Message }"

echo.
echo [3/3] Deploying to Railway...
railway deploy

echo.
echo ========================================
echo DEPLOYMENT COMPLETE!
echo ========================================
echo.
echo Your CORS fixes are now live!
echo Test: https://agriculture-frontend-two.vercel.app
echo.
