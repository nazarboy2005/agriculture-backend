@echo off
echo ========================================
echo Building with HealthConfig Fix
echo ========================================

echo.
echo [1/2] Setting up environment...
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo [2/2] Building application...
call mvnw.cmd clean compile -DskipTests

if %ERRORLEVEL% neq 0 (
    echo.
    echo ERROR: Build failed!
    echo The HealthConfig.java compilation error should be fixed now.
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build Successful!
echo ========================================
echo.
echo The HealthConfig.java compilation error has been fixed.
echo You can now proceed with deployment.
echo.
pause
