@echo off
echo ========================================
echo TESTING NUCLEAR CORS FIX
echo ========================================

echo.
echo [1/3] Testing preflight OPTIONS request...
curl -X OPTIONS ^
  -H "Origin: https://agriculture-frontend-two.vercel.app" ^
  -H "Access-Control-Request-Method: POST" ^
  -H "Access-Control-Request-Headers: Content-Type, Authorization" ^
  -v https://agriculture-backend-production.railway.app/api/v1/auth/register

echo.
echo [2/3] Testing actual POST request...
curl -X POST ^
  -H "Origin: https://agriculture-frontend-two.vercel.app" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"password123\"}" ^
  -v https://agriculture-backend-production.railway.app/api/v1/auth/register

echo.
echo [3/3] Testing health endpoint...
curl -X GET ^
  -H "Origin: https://agriculture-frontend-two.vercel.app" ^
  -v https://agriculture-backend-production.railway.app/api/actuator/health

echo.
echo ========================================
echo NUCLEAR CORS TEST COMPLETED
echo ========================================
echo.
echo Look for "Access-Control-Allow-Origin: https://agriculture-frontend-two.vercel.app"
echo in the response headers above.
echo.
pause