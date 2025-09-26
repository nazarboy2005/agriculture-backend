@echo off
echo ========================================
echo DEPLOYING CLEAN CORS SOLUTION
echo ========================================

echo.
echo [1/3] Building application...
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Deploying to Railway...
railway deploy

echo.
echo [3/3] Testing CORS...
timeout /t 20 /nobreak >nul
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method OPTIONS -Headers @{'Origin'='https://agriculture-frontend-two.vercel.app'; 'Access-Control-Request-Method'='POST'} -UseBasicParsing; Write-Host 'CORS Test: Status:' $response.StatusCode; Write-Host 'Headers:' $response.Headers } catch { Write-Host 'CORS Test: FAILED -' $_.Exception.Message }"

echo.
echo ========================================
echo DEPLOYMENT COMPLETE!
echo ========================================
echo.
echo The clean CORS solution should now work.
echo Test your frontend: https://agriculture-frontend-two.vercel.app
echo.
pause