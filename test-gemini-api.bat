@echo off
echo Testing Gemini API Integration...
echo.

echo Testing chat endpoint with soil health question...
curl -X POST "http://localhost:9090/api/v1/chat/send" ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "farmerId=1&message=Soil health recommendations?&messageType=GENERAL"

echo.
echo.
echo Testing chat endpoint with non-agriculture question...
curl -X POST "http://localhost:9090/api/v1/chat/send" ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "farmerId=1&message=hello&messageType=GENERAL"

echo.
echo.
echo Testing chat endpoint with irrigation question...
curl -X POST "http://localhost:9090/api/v1/chat/send" ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "farmerId=1&message=What irrigation schedule should I use?&messageType=GENERAL"

echo.
echo Test completed!
pause
