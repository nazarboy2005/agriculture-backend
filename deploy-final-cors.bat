@echo off
echo ========================================
echo FINAL CORS FIX DEPLOYMENT
echo ========================================

echo.
echo [1/3] Building with FINAL CORS configuration...
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Deploying FINAL CORS fix to Railway...
railway deploy

echo.
echo [3/3] Testing deployed FINAL CORS fix...
timeout /t 20 /nobreak >nul
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method OPTIONS -Headers @{'Origin'='https://agriculture-frontend-two.vercel.app'; 'Access-Control-Request-Method'='POST'} -UseBasicParsing; Write-Host 'FINAL CORS Test: Status:' $response.StatusCode; Write-Host 'Headers:' $response.Headers } catch { Write-Host 'FINAL CORS Test: FAILED -' $_.Exception.Message }"

echo.
echo ========================================
echo FINAL CORS DEPLOYMENT COMPLETE!
echo ========================================
echo.
echo The final CORS fix should now work with wildcard (*) for all origins.
echo Test your frontend: https://agriculture-frontend-two.vercel.app
echo.
pause
