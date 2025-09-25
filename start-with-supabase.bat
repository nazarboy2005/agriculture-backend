@echo off
echo Starting Agriculture Backend with Supabase Database...
echo.

REM Set environment variables for Supabase
set DATABASE_URL=jdbc:postgresql://aws-1-eu-north-1.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0
set DB_USERNAME=postgres.hlhnquynjylsxojferjc
set DB_PASSWORD=Nazarboy2005

REM Set JWT secret
set JWT_SECRET=mySecretKeyForJWTTokenGenerationThatIsSecureAndLongEnoughForHMAC

REM Set other required environment variables
set WEATHER_API_KEY=your-openweathermap-api-key
set GEMINI_API_KEY=your-gemini-api-key

echo Environment variables set:
echo DATABASE_URL=%DATABASE_URL%
echo DB_USERNAME=%DB_USERNAME%
echo JWT_SECRET=***hidden***
echo.

echo Starting application...
java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar

pause
