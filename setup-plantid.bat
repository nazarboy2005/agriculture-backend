@echo off
echo 🌱 Plant.id API Setup for Disease Detection
echo ==========================================
echo.

REM Check if PLANTID_API_KEYS is already set
if not "%PLANTID_API_KEYS%"=="" (
    echo ✅ PLANTID_API_KEYS is already configured
    echo Current keys: %PLANTID_API_KEYS:~0,20%...
    echo.
    set /p update_keys="Do you want to update the API keys? (y/n): "
    if not "%update_keys%"=="y" (
        echo Setup cancelled.
        exit /b 0
    )
)

echo 📋 Setup Instructions:
echo 1. Go to https://plant.id/
echo 2. Sign up for a free account
echo 3. Navigate to 'API Keys' section
echo 4. Copy your API key(s)
echo 5. For multiple keys (recommended for rotation), separate them with commas
echo.

set /p api_keys="Enter your Plant.id API key(s) (comma-separated for multiple): "

if "%api_keys%"=="" (
    echo ❌ No API keys provided. Setup cancelled.
    exit /b 1
)

REM Count number of keys
set key_count=0
for %%i in ("%api_keys:,=" "%") do set /a key_count+=1

echo ✅ Found %key_count% API key(s)

REM Set environment variable
set PLANTID_API_KEYS=%api_keys%

REM Create .env file if it doesn't exist
if not exist .env (
    echo # Environment variables for agriculture-backend > .env
)

REM Add or update PLANTID_API_KEYS in .env
findstr /C:"PLANTID_API_KEYS" .env >nul
if %errorlevel%==0 (
    powershell -Command "(Get-Content .env) -replace 'PLANTID_API_KEYS=.*', 'PLANTID_API_KEYS=%api_keys%' | Set-Content .env"
) else (
    echo PLANTID_API_KEYS=%api_keys% >> .env
)

echo.
echo ✅ Plant.id API keys configured successfully!
echo.
echo 🔧 Next steps:
echo 1. Start your application: mvn spring-boot:run
echo 2. Test disease detection at: http://localhost:9090/api/disease/detect
echo 3. View the frontend at: http://localhost:3000/disease-detection
echo.
echo 🔒 Security Note:
echo - API keys are stored in .env file (not committed to git)
echo - Keys are automatically rotated for better performance
echo - Each detection is logged with the API key used
echo.
echo 📚 Documentation:
echo - API Documentation: https://plant.id/api-docs
echo - Free tier includes 100 identifications per month
echo - Upgrade for higher limits and better performance

pause
