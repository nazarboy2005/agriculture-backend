@echo off
echo 🔍 Agriculture Backend Deployment Debug
echo ======================================
echo.

REM Set working directory to script location
cd /d "%~dp0"

echo 📋 Environment Information:
echo ==========================
echo Current directory: %CD%
echo Java version:
java -version 2>&1
echo.
echo Maven version:
call mvnw.cmd --version 2>&1
echo.

echo 📁 Directory Structure:
echo ======================
echo Project root contents:
dir /b
echo.
echo Target directory contents:
if exist target (
    dir target /b
    echo.
    echo JAR file details:
    if exist "target\agriculture-backend-0.0.1-SNAPSHOT.jar" (
        echo ✅ JAR file found
        for %%A in ("target\agriculture-backend-0.0.1-SNAPSHOT.jar") do (
            echo Size: %%~zA bytes
            echo Date: %%~tA
        )
    ) else (
        echo ❌ JAR file not found
        echo Available files in target:
        dir target /b
    )
) else (
    echo ❌ Target directory not found
)

echo.
echo 🔧 Build Process Test:
echo =====================
echo Attempting clean build...
call mvnw.cmd clean package -DskipTests -X

echo.
echo 📊 Build Results:
echo ================
if exist "target\agriculture-backend-0.0.1-SNAPSHOT.jar" (
    echo ✅ Build successful - JAR file created
    echo JAR location: %CD%\target\agriculture-backend-0.0.1-SNAPSHOT.jar
    echo.
    echo Testing JAR file:
    java -jar target\agriculture-backend-0.0.1-SNAPSHOT.jar --help 2>&1 | findstr /C:"Usage:" >nul
    if %errorlevel% equ 0 (
        echo ✅ JAR file is executable
    ) else (
        echo ⚠️  JAR file may have issues
    )
) else (
    echo ❌ Build failed - JAR file not created
    echo.
    echo Common issues:
    echo 1. Java version mismatch
    echo 2. Maven dependencies not resolved
    echo 3. Compilation errors
    echo 4. Insufficient disk space
)

echo.
echo 🚀 Deployment Configuration Check:
echo ==================================
echo Procfile content:
if exist Procfile (
    type Procfile
) else (
    echo ❌ Procfile not found
)

echo.
echo Railway.json content:
if exist railway.json (
    type railway.json
) else (
    echo ❌ railway.json not found
)

echo.
echo Nixpacks.toml content:
if exist nixpacks.toml (
    type nixpacks.toml
) else (
    echo ❌ nixpacks.toml not found
)

echo.
echo 🔍 Next Steps:
echo ==============
echo 1. If JAR file is missing, check build logs above
echo 2. If JAR file exists but deployment fails, check platform-specific logs
echo 3. Ensure all environment variables are set correctly
echo 4. Verify the deployment platform supports Java 17
echo.
pause
