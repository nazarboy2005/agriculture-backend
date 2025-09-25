@echo off
echo ========================================
echo DEPLOYING JAR PATH FIX TO RAILWAY
echo ========================================

echo.
echo [1/4] Cleaning previous builds...
if exist target rmdir /s /q target

echo.
echo [2/4] Building with correct JAR name...
call mvnw.cmd clean package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [3/4] Verifying JAR was created...
if not exist target\app.jar (
    echo ERROR: app.jar was not created!
    echo Available files in target:
    dir target\*.jar
    pause
    exit /b 1
)

echo.
echo [4/4] Deploying to Railway with correct JAR path...
railway up --detach

echo.
echo ========================================
echo JAR PATH FIX DEPLOYED!
echo ========================================
echo.
echo The following fixes have been applied:
echo 1. Updated railway.json to use "java -jar app.jar" (not "java -jar target/app.jar")
echo 2. Dockerfile copies JAR to root directory as app.jar
echo 3. Railway start command now matches Dockerfile structure
echo.
echo Your application should now start correctly!
echo.
echo Test your Vercel frontend now - it should work!
echo.
pause
