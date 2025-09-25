@echo off
echo ========================================
echo FORCE CORS DEPLOYMENT
echo ========================================
echo.
echo This will deploy the ForceCorsFilter to override Railway's CORS
echo.

echo [1/2] Committing CORS force changes...
git add .
git commit -m "Force CORS override for Railway - add ForceCorsFilter and production config"
if %ERRORLEVEL% neq 0 (
    echo ERROR: Git commit failed!
    pause
    exit /b 1
)

echo [2/2] Pushing to trigger Railway deployment...
git push
if %ERRORLEVEL% neq 0 (
    echo ERROR: Git push failed!
    echo Please check your git configuration and try again.
    pause
    exit /b 1
)

echo.
echo ========================================
echo DEPLOYMENT TRIGGERED
echo ========================================
echo.
echo The ForceCorsFilter should now override Railway's CORS headers.
echo Check Railway logs for: "ForceCorsFilter - FORCING CORS headers"
echo.
echo Test your frontend connection in a few minutes.
pause
