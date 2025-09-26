@echo off
echo ========================================
echo ULTIMATE CORS FIX DEPLOYMENT
echo ========================================

echo.
echo [1/4] Building with ULTIMATE CORS configuration...
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [2/4] Testing ULTIMATE CORS configuration...
echo Testing preflight request...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method OPTIONS -Headers @{'Origin'='https://agriculture-frontend-two.vercel.app'; 'Access-Control-Request-Method'='POST'} -UseBasicParsing; Write-Host 'CORS Test: Status:' $response.StatusCode; Write-Host 'Headers:' $response.Headers } catch { Write-Host 'CORS Test: FAILED -' $_.Exception.Message }"

echo.
echo [3/4] Deploying ULTIMATE CORS fix to Railway...
railway deploy

echo.
echo [4/4] Testing deployed ULTIMATE CORS fix...
timeout /t 15 /nobreak >nul
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method OPTIONS -Headers @{'Origin'='https://agriculture-frontend-two.vercel.app'; 'Access-Control-Request-Method'='POST'} -UseBasicParsing; Write-Host 'ULTIMATE CORS Test: Status:' $response.StatusCode; Write-Host 'Headers:' $response.Headers } catch { Write-Host 'ULTIMATE CORS Test: FAILED -' $_.Exception.Message }"

echo.
echo ========================================
echo ULTIMATE CORS DEPLOYMENT COMPLETE!
echo ========================================
echo.
echo The ultimate CORS fix should now override Railway's CORS injection.
echo Test your frontend: https://agriculture-frontend-two.vercel.app
echo.
pause