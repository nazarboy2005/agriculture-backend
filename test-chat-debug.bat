@echo off
echo Testing Chat API Debug...
echo.

echo 1. Testing backend health...
curl -X GET "http://localhost:9090/api/v1/chat/health" ^
  -w "\nHTTP Status: %{http_code}\n" ^
  -s

echo.
echo 2. Testing farmer endpoint...
curl -X GET "http://localhost:9090/api/v1/farmers/1" ^
  -w "\nHTTP Status: %{http_code}\n" ^
  -s

echo.
echo 3. Testing chat send with simple message...
curl -X POST "http://localhost:9090/api/v1/chat/send" ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "farmerId=1&message=test&messageType=GENERAL" ^
  -w "\nHTTP Status: %{http_code}\n" ^
  -s

echo.
echo 4. Testing chat history...
curl -X GET "http://localhost:9090/api/v1/chat/history/1" ^
  -w "\nHTTP Status: %{http_code}\n" ^
  -s

echo.
echo Debug completed!
echo.
echo If you see connection errors, the backend might not be running.
echo If you see 500 errors, there might be a backend issue.
echo If you see 200 responses, the backend is working.
pause
