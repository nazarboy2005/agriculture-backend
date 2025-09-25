@echo off
echo 🚀 Agriculture Backend Startup Script
echo =====================================
echo.

REM Set working directory to script location
cd /d "%~dp0"

echo 📋 Current directory: %CD%
echo.

REM Check if JAR file exists
if exist "target\agriculture-backend-0.0.1-SNAPSHOT.jar" (
    echo ✅ JAR file found: target\agriculture-backend-0.0.1-SNAPSHOT.jar
    for %%A in ("target\agriculture-backend-0.0.1-SNAPSHOT.jar") do echo 📊 JAR file size: %%~zA bytes
    echo.
) else (
    echo ❌ JAR file not found! Attempting to build...
    echo.
    
    REM Try to build the project
    if exist "mvnw.cmd" (
        echo 🔨 Building with Maven wrapper...
        call mvnw.cmd clean package -DskipTests
    ) else if exist "mvnw" (
        echo 🔨 Building with Maven wrapper...
        call mvnw clean package -DskipTests
    ) else (
        echo 🔨 Building with system Maven...
        mvn clean package -DskipTests
    )
    
    REM Check if build was successful
    if not exist "target\agriculture-backend-0.0.1-SNAPSHOT.jar" (
        echo ❌ Build failed! JAR file not created
        echo Available files in target directory:
        if exist target (
            dir target /b
        ) else (
            echo Target directory not found
        )
        pause
        exit /b 1
    )
    
    echo ✅ Build successful! JAR file created
    echo.
)

REM Set default port if not provided
if "%PORT%"=="" set PORT=9090

echo 🌐 Starting application on port %PORT%
echo 📡 API will be available at: http://localhost:%PORT%/api
echo 🔍 Health check: http://localhost:%PORT%/api/actuator/health
echo.

REM Start the application
echo 🚀 Starting Spring Boot application...
java -jar target\agriculture-backend-0.0.1-SNAPSHOT.jar --server.port=%PORT%

echo.
echo 👋 Application stopped
pause
