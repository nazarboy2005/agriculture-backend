# 🧪 API Testing Guide

This guide provides comprehensive testing instructions for the Smart Irrigation Backend API.

## 🚀 Quick Start Testing

### 1. Start the Application
```bash
# Set environment variables
export DB_PASSWORD=your-supabase-password
export WEATHER_API_KEY=your-openweathermap-api-key
export TWILIO_ACCOUNT_SID=your-twilio-account-sid
export TWILIO_AUTH_TOKEN=your-twilio-auth-token
export TWILIO_PHONE_NUMBER=+1234567890

# Run the application
mvn spring-boot:run
```

### 2. Health Check
```bash
curl http://localhost:8080/api/actuator/health
```

## 📱 Mobile-Optimized API Testing

### Test Farmer Registration (Mobile-Friendly)
```bash
# Register a new farmer
curl -X POST http://localhost:8080/api/v1/farmers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ahmed Hassan",
    "phone": "+97412345678",
    "locationName": "Doha",
    "latitude": 25.2854,
    "longitude": 51.5310,
    "preferredCrop": "Tomato",
    "smsOptIn": true
  }'
```

### Test Recommendation Generation
```bash
# Generate ad-hoc recommendation
curl -X POST "http://localhost:8080/api/v1/recommendations/ad-hoc?farmerId=1"

# Schedule daily recommendation
curl -X POST "http://localhost:8080/api/v1/recommendations/schedule?farmerId=1"
```

### Test SMS Alert
```bash
# Send test SMS
curl -X POST "http://localhost:8080/api/v1/alerts/test?farmerId=1"
```

## 🔍 Complete API Test Suite

### 1. Farmer Management Tests

#### Register Farmer
```bash
curl -X POST http://localhost:8080/api/v1/farmers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Farmer",
    "phone": "+97498765432",
    "locationName": "Al Rayyan",
    "latitude": 25.2919,
    "longitude": 51.4244,
    "preferredCrop": "Wheat",
    "smsOptIn": true
  }'
```

#### Get All Farmers
```bash
curl http://localhost:8080/api/v1/farmers
```

#### Get Farmer by ID
```bash
curl http://localhost:8080/api/v1/farmers/1
```

#### Update Farmer
```bash
curl -X PUT http://localhost:8080/api/v1/farmers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Farmer Name",
    "phone": "+97498765432",
    "locationName": "Al Rayyan",
    "latitude": 25.2919,
    "longitude": 51.4244,
    "preferredCrop": "Corn",
    "smsOptIn": true
  }'
```

### 2. Recommendation Tests

#### Generate Ad-Hoc Recommendation
```bash
curl -X POST "http://localhost:8080/api/v1/recommendations/ad-hoc?farmerId=1"
```

#### Schedule Daily Recommendation
```bash
curl -X POST "http://localhost:8080/api/v1/recommendations/schedule?farmerId=1"
```

#### Get Farmer Recommendations
```bash
curl "http://localhost:8080/api/v1/recommendations/farmers/1"
```

### 3. Alert Tests

#### Send Test SMS
```bash
curl -X POST "http://localhost:8080/api/v1/alerts/test?farmerId=1"
```

#### Get Farmer Alerts
```bash
curl "http://localhost:8080/api/v1/alerts/farmers/1"
```

#### Get Alerts by Status
```bash
curl "http://localhost:8080/api/v1/alerts/status/SENT"
curl "http://localhost:8080/api/v1/alerts/status/FAILED"
```

### 4. Admin & Analytics Tests

#### Get Admin Metrics
```bash
curl "http://localhost:8080/api/v1/admin/metrics"
```

#### Get Water Savings
```bash
curl "http://localhost:8080/api/v1/admin/farmers/1/water-savings?from=2025-01-01&to=2025-01-31"
```

#### Get Statistics
```bash
curl "http://localhost:8080/api/v1/admin/stats/farmers"
curl "http://localhost:8080/api/v1/admin/stats/sms-opt-in"
```

## 📊 Expected Responses

### Successful Farmer Registration
```json
{
  "success": true,
  "message": "Farmer created successfully",
  "data": {
    "id": 1,
    "name": "Ahmed Hassan",
    "phone": "+97412345678",
    "locationName": "Doha",
    "latitude": 25.2854,
    "longitude": 51.5310,
    "preferredCrop": "Tomato",
    "smsOptIn": true,
    "createdAt": "2025-01-27T10:00:00Z"
  }
}
```

### Successful Recommendation
```json
{
  "success": true,
  "data": {
    "recommendation": "HIGH",
    "explanation": "High temperature (32.5°C) requires more irrigation. Low humidity (45%) increases water demand. No significant rainfall (0.0mm) requires irrigation.",
    "waterSavedLiters": 1200.0,
    "score": 5
  }
}
```

### Successful SMS Alert
```json
{
  "success": true,
  "message": "Test alert sent successfully. Status: SENT"
}
```

## 🚨 Error Testing

### Test Validation Errors
```bash
# Invalid phone number
curl -X POST http://localhost:8080/api/v1/farmers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test",
    "phone": "invalid-phone",
    "locationName": "Test",
    "latitude": 25.0,
    "longitude": 51.0,
    "preferredCrop": "Test"
  }'
```

### Test Missing Farmer
```bash
curl -X POST "http://localhost:8080/api/v1/recommendations/ad-hoc?farmerId=999"
```

## 📱 Mobile Testing Tips

### 1. Use Mobile-Friendly Tools
- **Postman Mobile**: Test APIs on your phone
- **Insomnia**: Lightweight API client
- **curl**: Command-line testing

### 2. Test CORS Support
```bash
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS \
     http://localhost:8080/api/v1/farmers
```

### 3. Test Response Times
```bash
time curl http://localhost:8080/api/v1/farmers
```

## 🔧 Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Check Supabase credentials
   - Verify network connectivity
   - Check SSL mode

2. **Weather API Error**
   - Verify OpenWeatherMap API key
   - Check API quota limits
   - Test with valid coordinates

3. **Twilio SMS Error**
   - Verify Twilio credentials
   - Check phone number format
   - Verify account balance

4. **CORS Issues**
   - Check CORS configuration
   - Verify origin headers
   - Test with different browsers

### Debug Commands

```bash
# Check application logs
tail -f logs/application.log

# Test database connection
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:postgresql://db.hlhnquynjylsxojferjc.supabase.co:5432/postgres"

# Test with debug logging
mvn spring-boot:run -Dlogging.level.com.hackathon.agriculture_backend=DEBUG
```

## 📈 Performance Testing

### Load Testing
```bash
# Test with multiple concurrent requests
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/v1/farmers \
    -H "Content-Type: application/json" \
    -d "{\"name\":\"Farmer $i\",\"phone\":\"+9741234567$i\",\"locationName\":\"Test\",\"latitude\":25.0,\"longitude\":51.0,\"preferredCrop\":\"Test\"}" &
done
```

### Memory Testing
```bash
# Monitor memory usage
curl http://localhost:8080/api/actuator/metrics/jvm.memory.used
```

## ✅ Testing Checklist

- [ ] Application starts successfully
- [ ] Database connection works
- [ ] Farmer registration works
- [ ] Recommendation generation works
- [ ] SMS alerts work (with valid Twilio credentials)
- [ ] Admin endpoints work
- [ ] CORS is properly configured
- [ ] Error handling works
- [ ] Validation works
- [ ] Scheduled tasks work (if enabled)

## 🎯 Next Steps

1. **Set up your API keys** following the main README
2. **Test all endpoints** using this guide
3. **Deploy to production** when ready
4. **Monitor performance** using actuator endpoints
5. **Set up monitoring** for production use

---

**Happy Testing! 🚀**

