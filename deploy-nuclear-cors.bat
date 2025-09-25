@echo off
echo ========================================
echo NUCLEAR CORS DEPLOYMENT
echo ========================================
echo.
echo This deploys MULTIPLE CORS override mechanisms:
echo - NuclearCorsFilter (highest priority filter)
echo - CorsOverrideController (controller-level CORS)
echo - CorsResponseInterceptor (response interceptor)
echo.

echo [1/2] Committing nuclear CORS changes...
git add .
git commit -m "NUCLEAR CORS: Add multiple override mechanisms - filter, controller, interceptor"
if %ERRORLEVEL% neq 0 (
    echo ERROR: Git commit failed!
    pause
    exit /b 1
)

echo [2/2] Pushing nuclear CORS deployment...
git push
if %ERRORLEVEL% neq 0 (
    echo ERROR: Git push failed!
    echo Please check your git configuration and try again.
    pause
    exit /b 1
)

echo.
echo ========================================
echo NUCLEAR CORS DEPLOYMENT TRIGGERED
echo ========================================
echo.
echo Multiple CORS override mechanisms deployed:
echo 1. NuclearCorsFilter - Highest priority filter
echo 2. CorsOverrideController - Controller-level CORS
echo 3. CorsResponseInterceptor - Response interceptor
echo.
echo Look for these log messages:
echo - "NUCLEAR CORS FILTER - DETECTED ORIGIN"
echo - "CORS OVERRIDE CONTROLLER - Origin"
echo - "CORS RESPONSE INTERCEPTOR - Origin"
echo.
echo This should DEFINITELY override Railway's CORS!
pause
