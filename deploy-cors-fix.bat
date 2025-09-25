@echo off
echo ========================================
echo Deploying Backend with CORS Fixes
echo ========================================

echo.
echo [1/4] Building the application...
call mvn clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven build failed!
    pause
    exit /b 1
)

echo.
echo [2/4] Building Docker image...
docker build -t agriculture-backend-cors-fix .

if %ERRORLEVEL% neq 0 (
    echo ERROR: Docker build failed!
    pause
    exit /b 1
)

echo.
echo [3/4] Testing the application locally...
echo Starting application on port 9090...
start /B java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

echo Waiting for application to start...
timeout /t 30 /nobreak > nul

echo.
echo [4/4] Testing CORS configuration...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:9090/api/actuator/health' -Method GET; Write-Host 'Backend Health Check: SUCCESS'; Write-Host 'Status:' $response.StatusCode } catch { Write-Host 'Backend Health Check: FAILED -' $_.Exception.Message }"

echo.
echo ========================================
echo CORS Fix Deployment Complete!
echo ========================================
echo.
echo The backend has been updated with CORS fixes for:
echo - https://agriculture-frontend-btleirx65.vercel.app
echo - https://agriculture-frontend.vercel.app
echo.
echo Next steps:
echo 1. Deploy to Railway with: railway deploy
echo 2. Or push to GitHub to trigger automatic deployment
echo.
echo Press any key to continue...
pause > nul
