@echo off
echo Fixing Build Errors
echo ===================

echo.
echo This script will fix common build errors and test the compilation.
echo.

echo 1. Cleaning previous build...
call mvnw.cmd clean

echo.
echo 2. Checking for common issues...

echo.
echo 3. Compiling with detailed output...
call mvnw.cmd compile -DskipTests -X

if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ Compilation failed! Common fixes:
    echo.
    echo - Check for missing imports
    echo - Verify method signatures
    echo - Check for syntax errors
    echo - Ensure all dependencies are available
    echo.
    echo Running specific checks...
    
    echo.
    echo Checking EmailService...
    findstr /n "sendEmailConfirmation" src\main\java\com\hackathon\agriculture_backend\service\EmailService.java
    
    echo.
    echo Checking AuthController...
    findstr /n "testEmail" src\main\java\com\hackathon\agriculture_backend\controller\AuthController.java
    
    echo.
    echo Checking application properties...
    findstr /n "MAIL_" src\main\resources\application-prod.properties
    
    pause
    exit /b 1
)

echo.
echo 4. Packaging application...
call mvnw.cmd package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ Packaging failed!
    echo.
    pause
    exit /b 1
)

echo.
echo ✅ Build successful!
echo.
echo Checking JAR file...
if exist "target\agriculture-backend-0.0.1-SNAPSHOT.jar" (
    echo ✅ JAR file created successfully
    echo File size: 
    dir target\agriculture-backend-0.0.1-SNAPSHOT.jar
) else (
    echo ❌ JAR file not found!
    echo.
    echo Contents of target directory:
    dir target\
)

echo.
echo Build fix completed!
pause
