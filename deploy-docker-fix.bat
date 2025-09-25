@echo off
echo ========================================
echo DEPLOYING DOCKER FIX TO RAILWAY
echo ========================================

echo.
echo [1/5] Cleaning previous builds...
if exist target rmdir /s /q target

echo.
echo [2/5] Building with correct JAR name...
call mvnw.cmd clean package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [3/5] Verifying JAR was created with correct name...
if not exist target\app.jar (
    echo ERROR: app.jar was not created!
    echo Available files in target:
    dir target\*.jar
    pause
    exit /b 1
)

echo.
echo [4/5] Deploying to Railway with Docker fix...
railway up --detach

echo.
echo [5/5] Waiting for deployment to complete...
timeout /t 60 /nobreak > nul

echo.
echo ========================================
echo DOCKER FIX DEPLOYED!
echo ========================================
echo.
echo The following fixes have been applied:
echo 1. Updated Dockerfile to look for target/app.jar (not agriculture-backend-0.0.1-SNAPSHOT.jar)
echo 2. Updated COPY command to use correct JAR name
echo 3. Added finalName=app to pom.xml for consistent JAR naming
echo 4. Updated railway.json to use correct start command
echo.
echo Your application should now build and run correctly!
echo.
echo Test your Vercel frontend now - it should work!
echo.
pause
