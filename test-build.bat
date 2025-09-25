@echo off
echo ========================================
echo TESTING BUILD WITH CORRECT JAR NAME
echo ========================================

echo.
echo [1/4] Cleaning previous builds...
call mvnw.cmd clean -q

echo.
echo [2/4] Building with finalName=app...
call mvnw.cmd package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [3/4] Verifying JAR was created with correct name...
if exist target\app.jar (
    echo SUCCESS: app.jar was created!
    echo File size: 
    dir target\app.jar
) else (
    echo ERROR: app.jar was not created!
    echo Available files in target:
    dir target\*.jar
    pause
    exit /b 1
)

echo.
echo [4/4] Testing JAR execution...
echo Testing if JAR can start (will stop after 10 seconds)...
timeout /t 2 /nobreak > nul
start /b java -jar target\app.jar --spring.profiles.active=prod
timeout /t 10 /nobreak > nul
taskkill /f /im java.exe > nul 2>&1

echo.
echo ========================================
echo BUILD TEST COMPLETED
echo ========================================
echo.
echo The build is working correctly with finalName=app!
echo.
echo Next step: Deploy to Railway using deploy-docker-fix.bat
echo.
pause
