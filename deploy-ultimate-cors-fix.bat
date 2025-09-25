@echo off
echo ========================================
echo DEPLOYING ULTIMATE CORS FIX TO RAILWAY
echo ========================================

echo.
echo [1/5] Cleaning previous builds...
if exist target rmdir /s /q target

echo.
echo [2/5] Compiling with ultimate CORS fixes...
call mvnw.cmd compile -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo [3/5] Building JAR with ultimate CORS fixes...
call mvnw.cmd package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: JAR build failed!
    pause
    exit /b 1
)

echo.
echo [4/5] Deploying to Railway with ultimate CORS fix...
railway up --detach

echo.
echo [5/5] Waiting for deployment to complete...
timeout /t 45 /nobreak > nul

echo.
echo ========================================
echo ULTIMATE CORS FIX DEPLOYED!
echo ========================================
echo.
echo The following ultimate CORS fixes have been applied:
echo 1. CustomCorsFilter - Highest priority CorsFilter bean
echo 2. Multiple CORS override filters with different priorities
echo 3. Enhanced CorsHeaderAdvice with better origin detection
echo 4. Updated application-prod.properties to disable Railway CORS
echo.
echo This should completely override Railway's platform-level CORS!
echo.
echo Test your Vercel frontend now - it should work!
echo.
pause
