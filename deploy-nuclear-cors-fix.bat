@echo off
echo ========================================
echo NUCLEAR CORS FIX DEPLOYMENT
echo ========================================

echo.
echo [1/4] Building with NUCLEAR CORS configuration...
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [2/4] Testing NUCLEAR CORS configuration...
echo Testing preflight request...
curl -X OPTIONS ^
  -H "Origin: https://agriculture-frontend-two.vercel.app" ^
  -H "Access-Control-Request-Method: POST" ^
  -H "Access-Control-Request-Headers: Content-Type, Authorization" ^
  -v https://agriculture-backend-production.railway.app/api/v1/auth/register

echo.
echo [3/4] Deploying NUCLEAR CORS fix to Railway...
railway deploy

echo.
echo [4/4] Testing deployed NUCLEAR CORS fix...
timeout /t 10 /nobreak >nul
curl -X OPTIONS ^
  -H "Origin: https://agriculture-frontend-two.vercel.app" ^
  -H "Access-Control-Request-Method: POST" ^
  -H "Access-Control-Request-Headers: Content-Type, Authorization" ^
  -v https://agriculture-backend-production.railway.app/api/v1/auth/register

echo.
echo ========================================
echo NUCLEAR CORS DEPLOYMENT COMPLETE!
echo ========================================
echo.
echo The nuclear CORS fix should now override Railway's CORS injection.
echo Test your frontend: https://agriculture-frontend-two.vercel.app
echo.
pause
