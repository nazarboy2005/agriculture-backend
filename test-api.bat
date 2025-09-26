@echo off
echo 🌱 Testing Smart Irrigation Backend API...

REM Set your API key
set WEATHER_API_KEY=5d43153239d679121f3c35bbb08ca0bb
set DB_PASSWORD=your-supabase-password

REM Base URL
set BASE_URL=http://localhost:8080/api

echo 🔍 Testing API endpoints...

REM Test 1: Health Check
echo 1. Testing health check...
curl -s "%BASE_URL%/actuator/health"

echo.
echo 2. Registering test farmer...
curl -s -X POST "%BASE_URL%/v1/farmers" -H "Content-Type: application/json" -d "{\"name\":\"Ahmed Hassan\",\"phone\":\"+97412345678\",\"locationName\":\"Doha\",\"latitude\":25.2854,\"longitude\":51.5310,\"preferredCrop\":\"Tomato\",\"smsOptIn\":true}"

echo.
echo 3. Testing OpenWeatherMap API directly...
curl -s "https://api.openweathermap.org/data/3.0/onecall?lat=25.2854&lon=51.5310&exclude=minutely&units=metric&appid=%WEATHER_API_KEY%"

echo.
echo 🎉 API testing completed!
echo.
echo 📊 Your Smart Irrigation Backend is ready!
echo.
echo 🚀 To start the application:
echo    mvn spring-boot:run
echo.
echo 📱 The application will be available at: http://localhost:8080/api


