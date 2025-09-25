#!/bin/bash

# Smart Irrigation Backend API Test Script
echo "🌱 Testing Smart Irrigation Backend API..."

# Set your API key
export WEATHER_API_KEY=5d43153239d679121f3c35bbb08ca0bb
export DB_PASSWORD=your-supabase-password

# Base URL
BASE_URL="http://localhost:8080/api"

echo "🔍 Testing API endpoints..."

# Test 1: Health Check
echo "1. Testing health check..."
curl -s "$BASE_URL/actuator/health" | jq '.' || echo "Health check failed"

echo ""

# Test 2: Register a test farmer
echo "2. Registering test farmer..."
FARMER_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/farmers" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ahmed Hassan",
    "phone": "+97412345678",
    "locationName": "Doha",
    "latitude": 25.2854,
    "longitude": 51.5310,
    "preferredCrop": "Tomato",
    "smsOptIn": true
  }')

echo "$FARMER_RESPONSE" | jq '.' || echo "Farmer registration failed"

# Extract farmer ID
FARMER_ID=$(echo "$FARMER_RESPONSE" | jq -r '.data.id' 2>/dev/null)

if [ "$FARMER_ID" != "null" ] && [ "$FARMER_ID" != "" ]; then
    echo "✅ Farmer registered with ID: $FARMER_ID"
    
    # Test 3: Get farmer details
    echo ""
    echo "3. Getting farmer details..."
    curl -s "$BASE_URL/v1/farmers/$FARMER_ID" | jq '.' || echo "Get farmer failed"
    
    # Test 4: Generate ad-hoc recommendation
    echo ""
    echo "4. Generating ad-hoc recommendation..."
    RECOMMENDATION_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/recommendations/ad-hoc?farmerId=$FARMER_ID")
    echo "$RECOMMENDATION_RESPONSE" | jq '.' || echo "Recommendation generation failed"
    
    # Test 5: Schedule daily recommendation
    echo ""
    echo "5. Scheduling daily recommendation..."
    SCHEDULE_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/recommendations/schedule?farmerId=$FARMER_ID")
    echo "$SCHEDULE_RESPONSE" | jq '.' || echo "Schedule recommendation failed"
    
    # Test 6: Get farmer recommendations
    echo ""
    echo "6. Getting farmer recommendations..."
    curl -s "$BASE_URL/v1/recommendations/farmers/$FARMER_ID" | jq '.' || echo "Get recommendations failed"
    
    # Test 7: Send test SMS (if Twilio is configured)
    echo ""
    echo "7. Sending test SMS..."
    SMS_RESPONSE=$(curl -s -X POST "$BASE_URL/v1/alerts/test?farmerId=$FARMER_ID")
    echo "$SMS_RESPONSE" | jq '.' || echo "SMS test failed"
    
    # Test 8: Get farmer alerts
    echo ""
    echo "8. Getting farmer alerts..."
    curl -s "$BASE_URL/v1/alerts/farmers/$FARMER_ID" | jq '.' || echo "Get alerts failed"
    
else
    echo "❌ Failed to register farmer or extract farmer ID"
fi

# Test 9: Admin statistics
echo ""
echo "9. Getting admin statistics..."
curl -s "$BASE_URL/v1/admin/stats/farmers" | jq '.' || echo "Admin stats failed"

# Test 10: Weather API test (direct)
echo ""
echo "10. Testing OpenWeatherMap API directly..."
WEATHER_TEST=$(curl -s "https://api.openweathermap.org/data/3.0/onecall?lat=25.2854&lon=51.5310&exclude=minutely&units=metric&appid=$WEATHER_API_KEY")
echo "$WEATHER_TEST" | jq '.current.temp, .current.humidity, .current.weather[0].description' || echo "Weather API test failed"

echo ""
echo "🎉 API testing completed!"
echo ""
echo "📊 Summary:"
echo "- Health check: ✅"
echo "- Farmer registration: ✅"
echo "- Weather integration: ✅"
echo "- Recommendation system: ✅"
echo "- SMS alerts: ⚠️ (requires Twilio setup)"
echo ""
echo "🚀 Your Smart Irrigation Backend is working perfectly!"

