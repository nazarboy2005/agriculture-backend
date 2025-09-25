@echo off
echo Disabling email sending for development...
echo.

REM Update application.properties to disable email
powershell -Command "(Get-Content 'src\main\resources\application.properties') -replace 'app\.email\.enabled=true', 'app.email.enabled=false' | Set-Content 'src\main\resources\application.properties'"

echo Email sending has been disabled.
echo Users can still register, but no confirmation emails will be sent.
echo.

echo To re-enable email sending, run: enable-email.bat
echo.

pause
