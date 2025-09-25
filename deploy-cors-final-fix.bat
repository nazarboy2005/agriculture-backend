@echo off
echo ========================================
echo DEPLOYING FINAL CORS FIX TO RAILWAY
echo ========================================

echo.
echo [1/4] Cleaning previous builds...
if exist target rmdir /s /q target

echo.
echo [2/4] Compiling with final CORS fixes...
call mvnw.cmd compile -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo [3/4] Building JAR with final CORS fixes...
call mvnw.cmd package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: JAR build failed!
    pause
    exit /b 1
)

echo.
echo [4/4] Deploying to Railway with final CORS fix...
railway up --detach

echo.
echo ========================================
echo FINAL CORS FIX DEPLOYED!
echo ========================================
echo.
echo The following CORS fixes have been applied:
echo 1. SimpleCorsFixFilter - Forces correct CORS headers for Vercel
echo 2. CorsHeaderAdvice - Enhanced to detect Vercel origins from Referer
echo 3. Multiple CORS override filters with different priorities
echo.
echo Your Vercel frontend should now work properly!
echo.
pause
