@echo off
echo ========================================
echo ULTIMATE CORS DEPLOYMENT
echo ========================================
echo.
echo This deploys the ULTIMATE CORS solution:
echo - UltimateCorsConfig (dedicated CORS filter)
echo - CorsHeaderAdvice (ResponseBodyAdvice override)
echo - Multiple fallback mechanisms
echo.

echo [1/2] Committing ultimate CORS changes...
git add .
git commit -m "ULTIMATE CORS: Add UltimateCorsConfig and CorsHeaderAdvice for final CORS override"
if %ERRORLEVEL% neq 0 (
    echo ERROR: Git commit failed!
    pause
    exit /b 1
)

echo [2/2] Pushing ultimate CORS deployment...
git push
if %ERRORLEVEL% neq 0 (
    echo ERROR: Git push failed!
    echo Please check your git configuration and try again.
    pause
    exit /b 1
)

echo.
echo ========================================
echo ULTIMATE CORS DEPLOYMENT TRIGGERED
echo ========================================
echo.
echo This is the FINAL CORS solution with:
echo 1. UltimateCorsConfig - Dedicated CORS filter
echo 2. CorsHeaderAdvice - ResponseBodyAdvice override
echo 3. Multiple fallback mechanisms
echo.
echo Look for these log messages:
echo - "ULTIMATE CORS CONFIG - Creating ultimate CORS filter"
echo - "CORS HEADER ADVICE - FORCING CORS HEADERS FOR"
echo.
echo This WILL override Railway's CORS!
pause
