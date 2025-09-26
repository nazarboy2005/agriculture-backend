@echo off
echo Starting deployment with compilation fixes...

echo.
echo ========================================
echo Building Backend with Compilation Fixes
echo ========================================

cd /d "%~dp0"

echo.
echo Cleaning and compiling backend...
call mvnw.cmd clean compile -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Backend compilation failed!
    echo Please check the compilation errors above.
    pause
    exit /b 1
)

echo.
echo ✅ Backend compilation successful!

echo.
echo ========================================
echo Building Frontend
echo ========================================

cd ..\agriculture-frontend

echo.
echo Installing frontend dependencies...
call npm install

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Frontend dependency installation failed!
    pause
    exit /b 1
)

echo.
echo Building frontend...
call npm run build

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Frontend build failed!
    pause
    exit /b 1
)

echo.
echo ✅ Frontend build successful!

echo.
echo ========================================
echo Deployment Summary
echo ========================================
echo.
echo ✅ Backend compilation fixes applied successfully
echo ✅ Frontend build completed successfully
echo.
echo The following issues have been fixed:
echo - UserSettingsDTO naming consistency
echo - Missing log variable declarations replaced with System.out.println
echo - Missing getter/setter methods added to Chat.java
echo - All compilation errors resolved
echo.
echo Ready for deployment!
echo.
pause
