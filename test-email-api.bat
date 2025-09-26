@echo off
echo Testing Email API Endpoints
echo ============================

echo.
echo This script will test the email functionality using curl.
echo Make sure the application is running on localhost:9090
echo.

set BASE_URL=http://localhost:9090/api/v1/auth

echo.
echo 1. Testing forgot password endpoint...
echo POST %BASE_URL%/forgot-password
echo Content-Type: application/json
echo {"email": "test@example.com"}
echo.

curl -X POST "%BASE_URL%/forgot-password" ^
  -H "Content-Type: application/json" ^
  -d "{\"email\": \"test@example.com\"}" ^
  -v

echo.
echo.
echo 2. Testing email test endpoint...
echo POST %BASE_URL%/test-email
echo Content-Type: application/json
echo {"email": "test@example.com"}
echo.

curl -X POST "%BASE_URL%/test-email" ^
  -H "Content-Type: application/json" ^
  -d "{\"email\": \"test@example.com\"}" ^
  -v

echo.
echo.
echo 3. Testing with a real email address (replace with your email)...
echo POST %BASE_URL%/test-email
echo Content-Type: application/json
echo {"email": "your-email@example.com"}
echo.

set /p REAL_EMAIL="Enter your email address to test: "

curl -X POST "%BASE_URL%/test-email" ^
  -H "Content-Type: application/json" ^
  -d "{\"email\": \"%REAL_EMAIL%\"}" ^
  -v

echo.
echo.
echo Test completed! Check the application logs for email sending status.
echo.

pause
