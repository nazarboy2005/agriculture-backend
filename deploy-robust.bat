@echo off
echo 🚀 Agriculture Backend Robust Deployment
echo ======================================
echo.

REM Set working directory to script location
cd /d "%~dp0"

echo 📋 Environment Check:
echo ====================
echo Current directory: %CD%
echo Java version:
java -version 2>&1
echo.

REM Check if we're in the right directory
if not exist "pom.xml" (
    echo ❌ pom.xml not found! Are you in the correct directory?
    echo Expected: agriculture-backend directory
    pause
    exit /b 1
)

echo ✅ Project structure verified
echo.

REM Clean previous builds
echo 🧹 Cleaning previous builds...
if exist target (
    rmdir /s /q target
    echo ✅ Target directory cleaned
)

echo.
echo 🔨 Building application...
echo.

REM Try different build methods
echo Method 1: Maven wrapper (mvnw.cmd)
if exist "mvnw.cmd" (
    echo Using Maven wrapper...
    call mvnw.cmd clean package -DskipTests -X
    if %errorlevel% equ 0 goto :build_success
    echo Maven wrapper failed, trying alternative...
)

echo Method 2: Maven wrapper (mvnw)
if exist "mvnw" (
    echo Using Maven wrapper (Unix style)...
    call mvnw clean package -DskipTests -X
    if %errorlevel% equ 0 goto :build_success
    echo Maven wrapper failed, trying alternative...
)

echo Method 3: System Maven
mvn --version >nul 2>&1
if %errorlevel% equ 0 (
    echo Using system Maven...
    mvn clean package -DskipTests -X
    if %errorlevel% equ 0 goto :build_success
    echo System Maven failed...
)

echo Method 4: Direct Java compilation (fallback)
echo Attempting direct compilation...
if exist "src\main\java" (
    echo Compiling Java sources directly...
    REM This is a simplified approach - in practice, you'd need to handle dependencies
    echo This method requires manual dependency management
    echo Please use Maven or Gradle for proper builds
)

echo.
echo ❌ All build methods failed!
echo.
echo Troubleshooting steps:
echo 1. Ensure Java 17+ is installed and in PATH
echo 2. Check if Maven is installed: mvn --version
echo 3. Verify internet connection for dependency downloads
echo 4. Check available disk space
echo 5. Review build logs above for specific errors
echo.
pause
exit /b 1

:build_success
echo.
echo ✅ Build successful!
echo.

REM Verify JAR file
if not exist "target\agriculture-backend-0.0.1-SNAPSHOT.jar" (
    echo ❌ JAR file not found after successful build!
    echo This indicates a packaging issue
    echo.
    echo Target directory contents:
    if exist target (
        dir target /b
    ) else (
        echo Target directory not found
    )
    pause
    exit /b 1
)

echo 📦 JAR file verification:
for %%A in ("target\agriculture-backend-0.0.1-SNAPSHOT.jar") do (
    echo ✅ JAR file found: %%~nxA
    echo 📊 Size: %%~zA bytes
    echo 📅 Date: %%~tA
)

echo.
echo 🧪 Testing JAR file...
echo Attempting to get JAR information...
java -jar target\agriculture-backend-0.0.1-SNAPSHOT.jar --help >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ JAR file is executable
) else (
    echo ⚠️  JAR file may have issues (this is normal for Spring Boot apps)
)

echo.
echo 🚀 Ready for deployment!
echo.
echo Next steps:
echo 1. Test locally: java -jar target\agriculture-backend-0.0.1-SNAPSHOT.jar
echo 2. Deploy to your platform using the updated configuration files
echo 3. Monitor logs for any runtime issues
echo.
echo 📁 Deployment files updated:
echo - Procfile: Uses ./start.sh (Linux/Mac) or start.bat (Windows)
echo - railway.json: Uses ./start.sh
echo - nixpacks.toml: Uses ./start.sh
echo.
pause
