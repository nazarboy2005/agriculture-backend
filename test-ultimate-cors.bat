@echo off
echo ========================================
echo TESTING ULTIMATE CORS FIX
echo ========================================

echo.
echo Testing CORS headers from Vercel frontend...
echo.

echo [1/4] Testing OPTIONS preflight request...
curl -X OPTIONS ^
  -H "Origin: https://agriculture-frontend-two.vercel.app" ^
  -H "Access-Control-Request-Method: POST" ^
  -H "Access-Control-Request-Headers: Content-Type, Authorization" ^
  -v ^
  https://agriculture-backend-production.railway.app/api/v1/auth/register

echo.
echo [2/4] Testing actual POST request...
curl -X POST ^
  -H "Origin: https://agriculture-frontend-two.vercel.app" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"password123\"}" ^
  -v ^
  https://agriculture-backend-production.railway.app/api/v1/auth/register

echo.
echo [3/4] Testing health endpoint...
curl -H "Origin: https://agriculture-frontend-two.vercel.app" ^
  -v ^
  https://agriculture-backend-production.railway.app/api/actuator/health

echo.
echo [4/4] Testing with different Vercel domain...
curl -H "Origin: https://agriculture-frontend.vercel.app" ^
  -v ^
  https://agriculture-backend-production.railway.app/api/actuator/health

echo.
echo ========================================
echo ULTIMATE CORS TEST COMPLETED
echo ========================================
echo.
echo Look for these headers in the responses:
echo - Access-Control-Allow-Origin: https://agriculture-frontend-two.vercel.app
echo - Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
echo - Access-Control-Allow-Credentials: true
echo.
echo If you see these headers, the CORS fix is working!
echo.
pause
