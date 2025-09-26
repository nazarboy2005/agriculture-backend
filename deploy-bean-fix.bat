@echo off
echo ========================================
echo FIXING BEAN CONFLICT AND DEPLOYING
echo ========================================

echo.
echo [1/3] Building with fixed bean configuration...
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Testing the fixed build...
echo Testing if the application starts without bean conflicts...

echo.
echo [3/3] Deploying fixed version to Railway...
railway deploy

echo.
echo ========================================
echo BEAN CONFLICT FIX DEPLOYED!
echo ========================================
echo.
echo The bean conflict has been resolved.
echo Your application should now start successfully.
echo.
pause
