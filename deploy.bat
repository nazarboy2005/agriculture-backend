@echo off
echo 🚀 Agriculture Backend Deployment Script
echo ======================================
echo.

REM Set working directory to script location
cd /d "%~dp0"

echo 📋 Checking prerequisites...
echo.

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

REM Check if Maven wrapper exists
if not exist mvnw.cmd (
    echo ❌ Maven wrapper not found
    echo Please ensure mvnw.cmd exists in the project directory
    pause
    exit /b 1
)

echo ✅ Prerequisites check passed
echo.

echo 🔨 Building application...
echo.

REM Clean and build the project
call mvnw.cmd clean package -DskipTests

if %errorlevel% neq 0 (
    echo ❌ Build failed!
    echo Please check the error messages above
    pause
    exit /b 1
)

echo.
echo ✅ Build successful!
echo.

REM Check if JAR file exists
if not exist "target\agriculture-backend-0.0.1-SNAPSHOT.jar" (
    echo ❌ JAR file not found after build!
    echo Expected location: target\agriculture-backend-0.0.1-SNAPSHOT.jar
    echo.
    echo Listing target directory contents:
    dir target
    pause
    exit /b 1
)

echo 📦 JAR file found: target\agriculture-backend-0.0.1-SNAPSHOT.jar
echo.

REM Get JAR file size
for %%A in ("target\agriculture-backend-0.0.1-SNAPSHOT.jar") do set JAR_SIZE=%%~zA
echo 📊 JAR file size: %JAR_SIZE% bytes

if %JAR_SIZE% LSS 1000000 (
    echo ⚠️  Warning: JAR file seems unusually small (%JAR_SIZE% bytes)
    echo This might indicate a build issue
    echo.
)

echo.
echo 🚀 Starting application...
echo.

REM Set default port if not provided
if "%PORT%"=="" set PORT=9090

echo 🌐 Starting on port %PORT%
echo 📡 API will be available at: http://localhost:%PORT%/api
echo 🔍 Health check: http://localhost:%PORT%/api/actuator/health
echo.

REM Start the application
java -jar target\agriculture-backend-0.0.1-SNAPSHOT.jar --server.port=%PORT%

echo.
echo 👋 Application stopped
pause
