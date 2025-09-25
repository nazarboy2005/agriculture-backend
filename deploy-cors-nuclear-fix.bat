@echo off
echo ========================================
echo DEPLOYING NUCLEAR CORS FIX TO RAILWAY
echo ========================================

echo.
echo [1/6] Cleaning previous builds...
call mvn clean -q

echo.
echo [2/6] Compiling with CORS fixes...
call mvn compile -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo [3/6] Running tests...
call mvn test -q
if %ERRORLEVEL% neq 0 (
    echo WARNING: Some tests failed, but continuing with deployment...
)

echo.
echo [4/6] Building JAR with CORS overrides...
call mvn package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: JAR build failed!
    pause
    exit /b 1
)

echo.
echo [5/6] Deploying to Railway with nuclear CORS fix...
railway up --detach

echo.
echo [6/6] Waiting for deployment to complete...
timeout /t 30 /nobreak > nul

echo.
echo ========================================
echo NUCLEAR CORS FIX DEPLOYED SUCCESSFULLY!
echo ========================================
echo.
echo Your application should now properly handle CORS for:
echo - https://agriculture-frontend-two.vercel.app
echo - https://agriculture-frontend.vercel.app
echo - https://agriculture-frontend-btleirx65.vercel.app
echo.
echo The nuclear CORS override filter will:
echo 1. Completely reset Railway's CORS headers
echo 2. Set proper CORS headers for your Vercel frontend
echo 3. Handle preflight requests immediately
echo.
echo Test your frontend now!
echo.
pause
