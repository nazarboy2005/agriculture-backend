@echo off
echo 🌱 Quick Plant.id API Setup for Disease Detection
echo ================================================
echo.

REM Check if .env file exists
if not exist .env (
    echo Creating .env file...
    echo # Environment variables for agriculture-backend > .env
)

REM Add Plant.id API keys to .env if not already present
findstr /C:"PLANTID_API_KEYS" .env >nul
if %errorlevel%==0 (
    echo ✅ PLANTID_API_KEYS already exists in .env
    echo Current configuration found.
) else (
    echo Adding Plant.id API configuration to .env...
    echo. >> .env
    echo # Plant.id API Configuration >> .env
    echo PLANTID_API_KEYS=demo-key-1,demo-key-2 >> .env
    echo.
    echo ✅ Added demo Plant.id API keys to .env
    echo.
    echo 📋 To use real Plant.id API:
    echo 1. Go to https://plant.id/
    echo 2. Sign up for a free account
    echo 3. Get your API key from the dashboard
    echo 4. Replace the demo keys in .env with your real API key
    echo.
)

echo 🔧 Disease detection will work in demo mode with sample responses
echo Start your application with: mvn spring-boot:run
echo.
pause
