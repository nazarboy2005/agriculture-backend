@echo off
echo ========================================
echo TESTING NUCLEAR CORS FIX
echo ========================================

echo.
echo [1/6] Waiting for deployment to complete...
timeout /t 30 /nobreak > nul

echo.
echo [2/6] Testing OPTIONS preflight request...
curl -X OPTIONS ^
  -H "Origin: https://agriculture-frontend-two.vercel.app" ^
  -H "Access-Control-Request-Method: POST" ^
  -H "Access-Control-Request-Headers: Content-Type, Authorization" ^
  -v ^
  https://agriculture-backend-production.railway.app/api/v1/auth/register

echo.
echo [3/6] Testing actual POST request...
curl -X POST ^
  -H "Origin: https://agriculture-frontend-two.vercel.app" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"password123\"}" ^
  -v ^
  https://agriculture-backend-production.railway.app/api/v1/auth/register

echo.
echo [4/6] Testing health endpoint...
curl -H "Origin: https://agriculture-frontend-two.vercel.app" ^
  -v ^
  https://agriculture-backend-production.railway.app/api/actuator/health

echo.
echo [5/6] Testing with different Vercel domain...
curl -H "Origin: https://agriculture-frontend.vercel.app" ^
  -v ^
  https://agriculture-backend-production.railway.app/api/actuator/health

echo.
echo [6/6] Checking Railway logs for CORS override messages...
echo Look for these messages in Railway logs:
echo - "RAILWAY CORS KILLER - KILLING RAILWAY CORS FOR: https://agriculture-frontend-two.vercel.app"
echo - "RAILWAY CORS KILLER - OVERRIDING CORS ORIGIN: https://railway.com -> https://agriculture-frontend-two.vercel.app"
echo - "RAILWAY CORS KILLER - HANDLING PREFLIGHT REQUEST FOR: https://agriculture-frontend-two.vercel.app"
echo.
echo ========================================
echo NUCLEAR CORS TEST COMPLETED
echo ========================================
echo.
echo If you see "Access-Control-Allow-Origin: https://agriculture-frontend-two.vercel.app"
echo in the response headers above, the nuclear CORS fix is working!
echo.
echo If you still see "Access-Control-Allow-Origin: https://railway.com",
echo then Railway is overriding at the platform level and we need a different approach.
echo.
pause
