@echo off
echo Testing Plant.id API configuration...
echo.

REM Test if the application is running
echo Checking if application is running on port 9090...
netstat -an | findstr :9090
if %errorlevel% neq 0 (
    echo ERROR: Application is not running on port 9090
    echo Please start the application first with: mvn spring-boot:run
    pause
    exit /b 1
)

echo.
echo Application is running. Testing Plant.id configuration...
echo.

REM Test the disease detection endpoint with a simple request
echo Making test request to disease detection endpoint...
curl -X POST "http://localhost:9090/api/disease/detect" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer test-token" ^
  -d "{\"imageBase64\":\"test\"}"

echo.
echo Test completed. Check the output above for any errors.
pause
