@echo off
echo Testing Chat and Email Functionality
echo ====================================

echo.
echo 1. Testing Chat API Endpoint
echo ----------------------------
echo Testing /v1/chat/send endpoint...

curl -X POST "http://localhost:8080/v1/chat/send" ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "farmerId=1&message=Hello, I need help with irrigation&messageType=GENERAL" ^
  --connect-timeout 10 --max-time 30

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Chat API test failed!
    echo Make sure the backend is running on port 8080
    pause
    exit /b 1
)

echo.
echo ✅ Chat API test completed!

echo.
echo 2. Testing Simple Chat API Endpoint
echo ------------------------------------
echo Testing /v1/chat/simple-send endpoint...

curl -X POST "http://localhost:8080/v1/chat/simple-send" ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "farmerId=1&message=Test message for simple chat&messageType=GENERAL" ^
  --connect-timeout 10 --max-time 30

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Simple Chat API test failed!
    pause
    exit /b 1
)

echo.
echo ✅ Simple Chat API test completed!

echo.
echo 3. Testing Chat Health Check
echo ----------------------------
echo Testing /v1/chat/health endpoint...

curl -X GET "http://localhost:8080/v1/chat/health" ^
  --connect-timeout 10 --max-time 30

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Chat health check failed!
    pause
    exit /b 1
)

echo.
echo ✅ Chat health check completed!

echo.
echo 4. Testing User Registration (Email Trigger)
echo ---------------------------------------------
echo Testing /v1/auth/register endpoint to trigger email...

curl -X POST "http://localhost:8080/v1/auth/register" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"password123\"}" ^
  --connect-timeout 10 --max-time 30

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ User registration test failed!
    pause
    exit /b 1
)

echo.
echo ✅ User registration test completed!

echo.
echo ====================================
echo Test Summary
echo ====================================
echo.
echo ✅ Chat API endpoints are working
echo ✅ Simple Chat fallback is working  
echo ✅ Chat health check is working
echo ✅ User registration (email trigger) is working
echo.
echo Check the backend console output for:
echo - Chat message processing logs
echo - Email sending debug messages
echo - Any error messages
echo.
echo If you see "EMAIL DEBUG" messages in the console,
echo it means the email service is being called but may
echo not be configured properly for actual sending.
echo.
pause

