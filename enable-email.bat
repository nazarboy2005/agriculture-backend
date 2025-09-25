@echo off
echo Enabling email sending...
echo.

REM Update application.properties to enable email
powershell -Command "(Get-Content 'src\main\resources\application.properties') -replace 'app\.email\.enabled=false', 'app.email.enabled=true' | Set-Content 'src\main\resources\application.properties'"

echo Email sending has been enabled.
echo Make sure your Gmail credentials are correct in application.properties
echo.

pause
