@echo off
echo Testing health endpoint after SMTP timeout fix...
echo.

echo Building application...
call mvn clean package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo Build failed!
    exit /b 1
)

echo.
echo Starting application in background...
start /b java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

echo Waiting for application to start...
timeout /t 30 /nobreak > nul

echo.
echo Testing health endpoint...
curl -s http://localhost:9090/api/actuator/health
echo.

echo.
echo If you see a 200 response with health status, the fix worked!
echo If you still see 503 or timeout errors, check the logs.

echo.
echo Press any key to stop the application...
pause > nul

echo Stopping application...
taskkill /f /im java.exe > nul 2>&1
echo Done!
