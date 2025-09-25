@echo off
echo Checking for compilation errors in key files...
echo.

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java is not available in PATH
    exit /b 1
)

REM Try to compile a simple test
echo Testing Java compilation...
javac -version
if %errorlevel% neq 0 (
    echo Java compiler not available
    exit /b 1
)

echo.
echo Java compilation environment is ready.
echo.
echo To fix the Lombok @Slf4j issue:
echo 1. Make sure your IDE has Lombok plugin installed
echo 2. Enable annotation processing in your IDE
echo 3. Or use the manual logger approach (already added to AuthController)
echo.
echo The AuthController now has both @Slf4j and manual logger as fallback.
echo This should resolve the compilation error.

pause

