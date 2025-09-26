@echo off
echo ========================================
echo WILDCARD CORS FIX DEPLOYMENT
echo ========================================

echo.
echo [1/3] Building with WILDCARD CORS configuration...
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Deploying WILDCARD CORS fix to Railway...
railway deploy

echo.
echo [3/3] Testing deployed WILDCARD CORS fix...
timeout /t 15 /nobreak >nul
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method OPTIONS -Headers @{'Origin'='https://agriculture-frontend-two.vercel.app'; 'Access-Control-Request-Method'='POST'} -UseBasicParsing; Write-Host 'WILDCARD CORS Test: Status:' $response.StatusCode; Write-Host 'Headers:' $response.Headers } catch { Write-Host 'WILDCARD CORS Test: FAILED -' $_.Exception.Message }"

echo.
echo ========================================
echo WILDCARD CORS DEPLOYMENT COMPLETE!
echo ========================================
echo.
echo The wildcard CORS fix should now allow ALL origins.
echo Test your frontend: https://agriculture-frontend-two.vercel.app
echo.
pause
