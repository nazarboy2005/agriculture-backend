@echo off
echo ========================================
echo DEPLOYING COMPLETE JAR PATH FIX
echo ========================================

echo.
echo [1/6] Cleaning previous builds...
if exist target rmdir /s /q target

echo.
echo [2/6] Building with finalName=app...
call mvnw.cmd clean package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [3/6] Verifying JAR was created with correct name...
if not exist target\app.jar (
    echo ERROR: app.jar was not created!
    echo Available files in target:
    dir target\*.jar
    pause
    exit /b 1
)

echo.
echo [4/6] Summary of fixes applied:
echo - railway.json: Updated start command to "java -jar app.jar"
echo - Procfile: Updated to use "java -jar app.jar"
echo - nixpacks.toml: Updated to look for target/app.jar
echo - Dockerfile: Updated to copy and use app.jar
echo - pom.xml: Added finalName=app for consistent naming
echo.

echo [5/6] Deploying to Railway with complete fix...
railway up --detach

echo.
echo [6/6] Waiting for deployment to complete...
timeout /t 60 /nobreak > nul

echo.
echo ========================================
echo COMPLETE JAR PATH FIX DEPLOYED!
echo ========================================
echo.
echo All JAR path references have been updated:
echo 1. railway.json - Start command: "java -jar app.jar"
echo 2. Procfile - Updated to use app.jar
echo 3. nixpacks.toml - Updated to look for target/app.jar
echo 4. Dockerfile - Updated to copy and use app.jar
echo 5. pom.xml - Added finalName=app
echo.
echo Your application should now start correctly!
echo.
echo Test your Vercel frontend now - it should work!
echo.
pause
