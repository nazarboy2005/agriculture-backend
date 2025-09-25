@echo off
echo ========================================
echo VERIFYING RAILWAY DEPLOYMENT
echo ========================================

echo.
echo [1/4] Checking if backend is running...
curl -s -o nul -w "%%{http_code}" https://agriculture-backend-production.railway.app/api/actuator/health
if %ERRORLEVEL% neq 0 (
    echo ERROR: Backend is not responding!
    pause
    exit /b 1
)

echo.
echo [2/4] Testing CORS headers from Vercel origin...
curl -X OPTIONS ^
  -H "Origin: https://agriculture-frontend-two.vercel.app" ^
  -H "Access-Control-Request-Method: POST" ^
  -H "Access-Control-Request-Headers: Content-Type, Authorization" ^
  -v ^
  https://agriculture-backend-production.railway.app/api/v1/auth/register

echo.
echo [3/4] Testing actual POST request...
curl -X POST ^
  -H "Origin: https://agriculture-frontend-two.vercel.app" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"password123\"}" ^
  -v ^
  https://agriculture-backend-production.railway.app/api/v1/auth/register

echo.
echo [4/4] Checking backend logs for CORS configuration...
echo Look for these messages in Railway logs:
echo - "Setting up CORS configuration..."
echo - "Allowed CORS origins: [..., https://agriculture-frontend-two.vercel.app, ...]"
echo - "CORS configuration completed successfully"
echo.
echo ========================================
echo VERIFICATION COMPLETED
echo ========================================
echo.
echo If you see "Access-Control-Allow-Origin: https://agriculture-frontend-two.vercel.app"
echo in the response headers above, the CORS fix is working!
echo.
pause