@echo off
echo Debugging Chat Functionality...
echo.

echo 1. Testing chat send endpoint...
curl -X POST "http://localhost:9090/api/v1/chat/send" ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "farmerId=1&message=Soil health recommendations?&messageType=GENERAL" ^
  -w "\nHTTP Status: %{http_code}\n" ^
  -s

echo.
echo 2. Testing chat history endpoint...
curl -X GET "http://localhost:9090/api/v1/chat/history/1" ^
  -w "\nHTTP Status: %{http_code}\n" ^
  -s

echo.
echo 3. Testing with non-agriculture question...
curl -X POST "http://localhost:9090/api/v1/chat/send" ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "farmerId=1&message=hello&messageType=GENERAL" ^
  -w "\nHTTP Status: %{http_code}\n" ^
  -s

echo.
echo 4. Testing chat history again...
curl -X GET "http://localhost:9090/api/v1/chat/history/1" ^
  -w "\nHTTP Status: %{http_code}\n" ^
  -s

echo.
echo Debug completed!
pause
