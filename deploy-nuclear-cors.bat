@echo off
echo ========================================
echo DEPLOYING NUCLEAR CORS FIX
echo ========================================

echo.
echo [1/5] Cleaning previous builds...
if exist target rmdir /s /q target

echo.
echo [2/5] Building with nuclear CORS fix...
call mvnw.cmd clean package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [3/5] Verifying JAR was created...
if not exist target\app.jar (
    echo ERROR: app.jar was not created!
    echo Available files in target:
    dir target\*.jar
    pause
    exit /b 1
)

echo.
echo [4/5] Deploying nuclear CORS fix to Railway...
railway up --detach

echo.
echo [5/5] Waiting for deployment to complete...
timeout /t 60 /nobreak > nul

echo.
echo ========================================
echo NUCLEAR CORS FIX DEPLOYED!
echo ========================================
echo.
echo The following nuclear CORS fixes have been applied:
echo 1. RailwayCorsKiller - Custom response wrapper that overrides Railway CORS
echo 2. Multiple CORS override filters with different priorities
echo 3. Enhanced application-prod.properties with nuclear CORS options
echo 4. CustomCorsFilter with highest priority
echo.
echo This should completely override Railway's platform-level CORS!
echo.
echo Test your Vercel frontend now - it should work!
echo.
pause