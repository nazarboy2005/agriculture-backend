# 🌱 Smart Irrigation & Climate Advisory Backend

A comprehensive Spring Boot backend system that provides daily, location-aware irrigation recommendations to farmers based on weather data, crop types, evapotranspiration, and climate alerts. Includes SMS notifications, historical tracking, admin metrics, and open data export capabilities.

## 🚀 Features

### Core Functionality
- **Smart Irrigation Recommendations**: AI-powered daily irrigation advice based on weather, crop type, and evapotranspiration
- **SMS/WhatsApp Alerts**: Real-time notifications via Twilio integration
- **Weather Integration**: OpenWeatherMap API for current weather and forecasts
- **NASA POWER Integration**: Evapotranspiration data from NASA's POWER API
- **Heat Alerts**: Automatic extreme weather notifications
- **Historical Tracking**: Complete recommendation history and water savings analytics

### Mobile & Tablet Optimized
- **RESTful API**: Modern, mobile-friendly endpoints
- **CORS Enabled**: Cross-origin support for web and mobile apps
- **Responsive Design**: Optimized for phones, tablets, and desktops
- **Real-time Data**: Live weather and recommendation updates

### Admin Dashboard
- **Analytics**: Comprehensive metrics and insights
- **Water Savings**: Track environmental impact
- **Farmer Management**: Complete farmer lifecycle management
- **Data Export**: Open data sharing capabilities

## 🛠️ Tech Stack

- **Java 17+** with Spring Boot 3.x
- **Spring Web** for REST APIs
- **Spring Data JPA** for database operations
- **Spring Validation** for input validation
- **PostgreSQL** (production) / H2 (development)
- **Twilio SDK** for SMS notifications
- **WebClient** for external API calls
- **Spring Scheduler** for automated tasks
- **Lombok** for code simplification

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL (for production)
- OpenWeatherMap API key
- Twilio account (for SMS)

## 🚀 Quick Start

### 1. Clone and Setup
```bash
git clone <repository-url>
cd agriculture-backend
```

### 2. Configure Environment Variables
Create a `.env` file or set environment variables:

**Required API Keys:**

1. **Supabase Database Password** (You already have this)
   ```bash
   export DB_PASSWORD=your-supabase-database-password
   ```

2. **OpenWeatherMap API Key** ✅ **ALREADY CONFIGURED!**
   - Your API key: `5d43153239d679121f3c35bbb08ca0bb`
   - One Call API 3.0 is already configured
   - No additional setup needed
   ```bash
   export WEATHER_API_KEY=5d43153239d679121f3c35bbb08ca0bb
   ```

3. **Twilio SMS Configuration** (Get from https://console.twilio.com/)
   ```bash
   export TWILIO_ACCOUNT_SID=your-twilio-account-sid
   export TWILIO_AUTH_TOKEN=your-twilio-auth-token
   export TWILIO_PHONE_NUMBER=+1234567890
   ```

**Complete Environment Setup:**
```bash
# Database
export DB_PASSWORD=your-supabase-database-password

# Weather API (Already configured!)
export WEATHER_API_KEY=5d43153239d679121f3c35bbb08ca0bb

# Twilio SMS
export TWILIO_ACCOUNT_SID=your-twilio-account-sid
export TWILIO_AUTH_TOKEN=your-twilio-auth-token
export TWILIO_PHONE_NUMBER=+1234567890
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

### 4. Database Setup
Your Supabase PostgreSQL database is already configured. The application will automatically create the required tables on first startup.

## 🔑 API Keys Setup Guide

### 1. OpenWeatherMap API Key
1. Go to [OpenWeatherMap](https://openweathermap.org/api)
2. Sign up for a free account
3. Navigate to "API Keys" section
4. Copy your API key
5. Set environment variable: `export WEATHER_API_KEY=your-api-key`

### 2. Twilio SMS Setup
1. Go to [Twilio Console](https://console.twilio.com/)
2. Sign up for a free account
3. Get your Account SID and Auth Token from the dashboard
4. Purchase a phone number from Twilio (required for SMS)
5. Set environment variables:
   ```bash
   export TWILIO_ACCOUNT_SID=your-account-sid
   export TWILIO_AUTH_TOKEN=your-auth-token
   export TWILIO_PHONE_NUMBER=+1234567890
   ```

### 3. Supabase Database (Already Configured)
Your Supabase database is already set up with the following connection:
- **Host**: `db.hlhnquynjylsxojferjc.supabase.co`
- **Port**: `5432`
- **Database**: `postgres`
- **Username**: `postgres`
- **Password**: `[YOUR-PASSWORD]` (you have this)

Just set: `export DB_PASSWORD=your-supabase-password`

## 📱 API Endpoints

### Farmer Management
```
POST   /api/v1/farmers                    # Register farmer
GET    /api/v1/farmers/{id}               # Get farmer by ID
GET    /api/v1/farmers/phone/{phone}      # Get farmer by phone
GET    /api/v1/farmers                    # Get all farmers
PUT    /api/v1/farmers/{id}               # Update farmer
DELETE /api/v1/farmers/{id}               # Delete farmer
GET    /api/v1/farmers/sms-opt-in         # Get farmers with SMS opt-in
GET    /api/v1/farmers/location/{location} # Get farmers by location
GET    /api/v1/farmers/crop/{crop}        # Get farmers by crop
```

### Recommendations
```
POST   /api/v1/recommendations/ad-hoc     # Generate on-demand recommendation
POST   /api/v1/recommendations/schedule   # Schedule daily recommendation
GET    /api/v1/recommendations/farmers/{id} # Get farmer's recommendations
GET    /api/v1/recommendations/farmers/{id}/latest # Get latest recommendation
```

### Alerts
```
POST   /api/v1/alerts/test                # Send test SMS
GET    /api/v1/alerts/farmers/{id}       # Get farmer's alerts
GET    /api/v1/alerts/status/{status}     # Get alerts by status
GET    /api/v1/alerts/type/{type}         # Get alerts by type
```

### Admin & Analytics
```
GET    /api/v1/admin/metrics              # Get admin metrics
GET    /api/v1/admin/farmers/{id}/water-savings # Get water savings
GET    /api/v1/admin/data/export          # Export data
GET    /api/v1/admin/stats/farmers         # Get farmer statistics
GET    /api/v1/admin/stats/sms-opt-in     # Get SMS opt-in statistics
```

## 🔧 Configuration

### Application Properties
```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Database (H2 for development)
spring.datasource.url=jdbc:h2:mem:irrigationdb
spring.datasource.username=sa
spring.datasource.password=password

# External APIs
app.weather.api.key=${WEATHER_API_KEY}
app.weather.api.url=https://api.openweathermap.org/data/2.5/onecall
app.nasa.api.url=https://power.larc.nasa.gov/api/temporal/daily/point

# Twilio SMS
app.twilio.account.sid=${TWILIO_ACCOUNT_SID}
app.twilio.auth.token=${TWILIO_AUTH_TOKEN}
app.twilio.phone.number=${TWILIO_PHONE_NUMBER}

# Scheduler
app.scheduler.daily-recommendation.cron=0 0 6 * * ?
app.scheduler.enabled=true
```

## 📊 Data Models

### Farmer
```json
{
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
```

### Irrigation Recommendation
```json
{
  "id": 1,
  "farmerId": 1,
  "date": "2025-01-27",
  "cropType": "Tomato",
  "locationName": "Doha",
  "tempC": 32.5,
  "humidity": 45.0,
  "rainfallMm": 0.0,
  "evapotranspiration": 5.2,
  "recommendation": "HIGH",
  "explanation": "High temperature (32.5°C) requires more irrigation...",
  "waterSavedLiters": 1200.0
}
```

## 🤖 Automated Scheduling

The system includes several scheduled tasks:

1. **Daily Recommendations** (6:00 AM): Sends irrigation recommendations to all opted-in farmers
2. **Heat Alerts** (12:00 PM): Monitors for extreme temperatures and sends alerts
3. **Evening Reminders** (6:00 PM): Sends reminders to check daily recommendations

## 📱 Mobile Optimization

### Responsive Design
- **Mobile-First**: Optimized for smartphones
- **Tablet Support**: Enhanced experience for tablets
- **Touch-Friendly**: Large buttons and intuitive navigation
- **Offline Capability**: Cached data for poor connectivity

### API Features
- **CORS Enabled**: Cross-origin requests supported
- **JSON Responses**: Lightweight, mobile-friendly data format
- **Error Handling**: Comprehensive error responses
- **Rate Limiting**: Built-in protection against abuse

## 🔒 Security Features

- **Input Validation**: Comprehensive validation on all inputs
- **SQL Injection Protection**: JPA/Hibernate parameterized queries
- **XSS Protection**: Input sanitization
- **CORS Configuration**: Controlled cross-origin access
- **Error Handling**: Secure error messages without sensitive data

## 📈 Monitoring & Analytics

### Health Endpoints
```
GET /api/actuator/health     # Application health
GET /api/actuator/info       # Application info
GET /api/actuator/metrics    # Application metrics
```

### Admin Metrics
- Total farmers registered
- Active farmers with SMS opt-in
- Total water saved (liters)
- Recommendation distribution
- Alert success/failure rates
- Crop distribution analytics

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
- Unit tests for all services
- Integration tests for controllers
- Repository tests with test data
- Scheduled task tests

## 🚀 Deployment

### Production Setup
1. **Database**: Configure PostgreSQL connection
2. **Environment Variables**: Set all required API keys
3. **Twilio**: Configure SMS service
4. **Monitoring**: Set up application monitoring

### Docker Support
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/agriculture-backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 📞 Support

For technical support or questions:
- **Documentation**: Check this README
- **API Docs**: Available at `/api/actuator/info`
- **Health Check**: Available at `/api/actuator/health`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Built with ❤️ for sustainable agriculture and smart farming**
