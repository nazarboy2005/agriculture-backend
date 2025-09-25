@echo off
echo 🚀 Agriculture Backend Setup
echo ===========================
echo.

echo 📋 Setting up backend configuration...
echo.

REM Create .env file if it doesn't exist
if not exist .env (
    echo Creating .env file...
    echo # Environment variables for agriculture-backend > .env
    echo. >> .env
)

REM Add JWT secret if not present
findstr /C:"JWT_SECRET" .env >nul
if %errorlevel%==1 (
    echo Adding JWT secret to .env...
    echo JWT_SECRET=mySecretKeyForJWTTokenGenerationThatIsSecureAndLongEnoughForHMAC >> .env
)

REM Add Google OAuth configuration (disabled by default)
findstr /C:"GOOGLE_OAUTH_ENABLED" .env >nul
if %errorlevel%==1 (
    echo Adding Google OAuth configuration to .env...
    echo GOOGLE_OAUTH_ENABLED=false >> .env
    echo GOOGLE_CLIENT_ID= >> .env
    echo GOOGLE_CLIENT_SECRET= >> .env
)

REM Add Plant.id API configuration
findstr /C:"PLANTID_API_KEYS" .env >nul
if %errorlevel%==1 (
    echo Adding Plant.id API configuration to .env...
    echo PLANTID_API_KEYS= >> .env
)

echo.
echo ✅ Backend configuration completed!
echo.
echo 🔧 Next steps:
echo 1. Start the backend: mvn spring-boot:run
echo 2. Create test user: POST http://localhost:9090/api/v1/auth/create-test-user
echo 3. Test login with: mbappezu@gmail.com / password123
echo.
echo 📝 Optional configurations:
echo - Set GOOGLE_OAUTH_ENABLED=true and add Google OAuth credentials for Google login
echo - Add PLANTID_API_KEYS for disease detection functionality
echo.
pause
