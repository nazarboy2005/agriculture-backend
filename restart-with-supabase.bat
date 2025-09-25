@echo off
echo ========================================
echo Restarting Agriculture Backend with Supabase
echo ========================================
echo.

REM Load environment variables from env.development
echo Loading environment variables...
for /f "usebackq tokens=1,2 delims==" %%a in ("env.development") do (
    if not "%%a"=="" if not "%%a:~0,1%"=="#" (
        set "%%a=%%b"
    )
)

echo Environment loaded:
echo DATABASE_URL=%DATABASE_URL%
echo DB_USERNAME=%DB_USERNAME%
echo JWT_SECRET=***hidden***
echo.

echo Stopping any running instances...
taskkill /f /im java.exe 2>nul

echo.
echo Starting application with Supabase database...
echo.

REM Start the application with environment variables
java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar

pause
