@echo off
echo Testing Maven Build Process
echo ============================

echo.
echo This script will test the Maven build to identify any compilation errors.
echo.

echo 1. Cleaning previous build...
call mvnw.cmd clean

echo.
echo 2. Compiling source code...
call mvnw.cmd compile -DskipTests

if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ Compilation failed! Check the errors above.
    echo.
    echo Common issues:
    echo - Missing imports
    echo - Syntax errors
    echo - Missing dependencies
    echo.
    pause
    exit /b 1
)

echo.
echo 3. Running tests (skipped)...
call mvnw.cmd test -DskipTests

echo.
echo 4. Packaging application...
call mvnw.cmd package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ Packaging failed! Check the errors above.
    echo.
    pause
    exit /b 1
)

echo.
echo ✅ Build successful!
echo.
echo Checking JAR file...
if exist "target\agriculture-backend-0.0.1-SNAPSHOT.jar" (
    echo ✅ JAR file created: target\agriculture-backend-0.0.1-SNAPSHOT.jar
    dir target\*.jar
) else (
    echo ❌ JAR file not found!
    echo.
    echo Checking target directory:
    dir target\
)

echo.
echo Build test completed!
pause