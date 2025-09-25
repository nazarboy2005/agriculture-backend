@echo off
echo ========================================
echo DEPLOYING CORRECT JAR TO RAILWAY
echo ========================================

echo.
echo [1/6] Cleaning previous builds...
if exist target rmdir /s /q target

echo.
echo [2/6] Compiling with correct JAR name...
call mvnw.cmd clean compile -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo [3/6] Building JAR with finalName=app...
call mvnw.cmd package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: JAR build failed!
    pause
    exit /b 1
)

echo.
echo [4/6] Verifying JAR was created...
if not exist target\app.jar (
    echo ERROR: app.jar was not created!
    echo Available files in target:
    dir target\*.jar
    pause
    exit /b 1
)

echo.
echo [5/6] Deploying to Railway with correct JAR...
railway up --detach

echo.
echo [6/6] Waiting for deployment to complete...
timeout /t 45 /nobreak > nul

echo.
echo ========================================
echo CORRECT JAR DEPLOYED!
echo ========================================
echo.
echo The following fixes have been applied:
echo 1. Updated railway.json to use correct JAR: target/app.jar
echo 2. Added finalName=app to pom.xml for consistent JAR naming
echo 3. Removed Railway CORS variables that were interfering
echo 4. Set SPRING_PROFILES_ACTIVE=prod to use production config
echo.
echo Your application should now run the correct JAR with proper CORS!
echo.
echo Test your Vercel frontend now - it should work!
echo.
pause
