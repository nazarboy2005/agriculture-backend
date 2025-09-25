@echo off
echo Building JAR file for agriculture-backend...
echo.

REM Set JAVA_HOME if not set
if "%JAVA_HOME%"=="" (
    echo Setting JAVA_HOME...
    set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    echo JAVA_HOME set to: %JAVA_HOME%
)

REM Verify Java is working
echo Verifying Java installation...
"%JAVA_HOME%\bin\java" -version
if %errorlevel% neq 0 (
    echo Java verification failed!
    pause
    exit /b 1
)

REM Try to build using Maven wrapper
echo Attempting to build with Maven wrapper...
call .\mvnw.cmd clean package -DskipTests

if %errorlevel% neq 0 (
    echo Maven wrapper failed, trying direct Maven approach...
    echo.
    REM Try using Maven directly if available
    mvn --version >nul 2>&1
    if %errorlevel% equ 0 (
        echo Using system Maven...
        mvn clean package -DskipTests
        if %errorlevel% equ 0 goto :success
    )
    
    echo.
    echo Build failed. Please try one of these alternatives:
    echo 1. Install Maven: https://maven.apache.org/download.cgi
    echo 2. Use your IDE: Run -> Maven -> Clean -> Package
    echo 3. Use Docker: docker build -t agriculture-backend .
    echo.
    pause
    exit /b 1
)

:success

echo.
echo ✅ JAR file built successfully!
echo JAR location: target\agriculture-backend-0.0.1-SNAPSHOT.jar
echo.
pause
