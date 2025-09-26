@echo off
echo Deploying with Build Fix
echo ========================

echo.
echo This script will deploy the application with build fixes applied.
echo.

echo 1. Testing local build first...
call fix-build-errors.bat

if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ Local build failed! Please fix the errors above before deploying.
    pause
    exit /b 1
)

echo.
echo 2. Building for deployment...
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ Build failed!
    pause
    exit /b 1
)

echo.
echo 3. Verifying JAR file...
if not exist "target\agriculture-backend-0.0.1-SNAPSHOT.jar" (
    echo ❌ JAR file not created!
    pause
    exit /b 1
)

echo ✅ JAR file created successfully

echo.
echo 4. Deployment options:
echo.
echo Option 1: Deploy to Railway
echo - Push to GitHub
echo - Railway will auto-deploy
echo.
echo Option 2: Deploy to Heroku
echo - git add .
echo - git commit -m "Fix build errors"
echo - git push heroku main
echo.
echo Option 3: Deploy with Docker
echo - docker build -t agriculture-backend .
echo - docker run -p 9090:9090 agriculture-backend
echo.

echo Build fix completed! Ready for deployment.
pause
