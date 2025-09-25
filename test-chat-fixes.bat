@echo off
echo Testing Chat Fixes...
echo.

echo 1. Testing chat with agriculture question...
curl -X POST "http://localhost:9090/api/v1/chat/send" ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "farmerId=1&message=Harvest timing advice?&messageType=GENERAL" ^
  -w "\nHTTP Status: %{http_code}\n" ^
  -s

echo.
echo 2. Testing chat with non-agriculture question...
curl -X POST "http://localhost:9090/api/v1/chat/send" ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "farmerId=1&message=hello&messageType=GENERAL" ^
  -w "\nHTTP Status: %{http_code}\n" ^
  -s

echo.
echo 3. Testing farmer endpoint to verify farmer exists...
curl -X GET "http://localhost:9090/api/v1/farmers/1" ^
  -w "\nHTTP Status: %{http_code}\n" ^
  -s

echo.
echo Test completed!
echo.
echo Expected results:
echo - Agriculture questions should get AI responses
echo - Non-agriculture questions should get filtered responses
echo - Farmer endpoint should return real farmer data (not mock)
echo - No more compilation errors
pause
