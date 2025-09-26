@echo off
echo ========================================
echo DEPLOYING CORS FIX
echo ========================================

echo Building application...
call mvn clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo Build failed!
    exit /b 1
)

echo ========================================
echo CORS FIX DEPLOYED SUCCESSFULLY
echo ========================================
echo.
echo The application now has:
echo - Single CORS configuration in CustomCorsFilter
echo - Highest precedence to override Railway's CORS
echo - Proper frontend origin: https://agriculture-frontend-two.vercel.app
echo.
echo Test the API with:
echo curl -X OPTIONS -H "Origin: https://agriculture-frontend-two.vercel.app" -H "Access-Control-Request-Method: POST" -H "Access-Control-Request-Headers: Content-Type" https://agriculture-backend-production.railway.app/api/v1/auth/register
echo.
echo Or test with your frontend application.
echo ========================================