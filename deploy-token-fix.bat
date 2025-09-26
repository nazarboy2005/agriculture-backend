@echo off
echo ========================================
echo Deploying Token Service Fix
echo ========================================

echo.
echo [1/3] Building the application...
call mvn clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Building Docker image...
docker build -t agriculture-backend .

if %ERRORLEVEL% neq 0 (
    echo ERROR: Docker build failed!
    pause
    exit /b 1
)

echo.
echo [3/3] Deploying to Railway...
railway up --detach

if %ERRORLEVEL% neq 0 (
    echo ERROR: Railway deployment failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Token service fix deployed successfully!
echo ========================================
echo.
echo Fixes applied:
echo - Fixed Token entity constructor usage
echo - Replaced log statements with System.out.println
echo - Removed dependency on Lombok setters
echo.
pause
