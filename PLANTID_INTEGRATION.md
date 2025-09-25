# 🌱 Plant Disease Detection Integration

## Overview

This document describes the Plant.id API integration for plant disease detection in the agriculture backend system. The integration provides AI-powered disease detection from plant leaf images with automatic API key rotation and comprehensive history tracking.

## 🚀 Features

### Core Functionality
- **AI Disease Detection**: Upload plant leaf images for instant disease identification
- **API Key Rotation**: Automatic rotation through multiple Plant.id API keys for better performance
- **Confidence Scoring**: AI confidence levels with color-coded indicators
- **Treatment Suggestions**: Detailed treatment recommendations for detected diseases
- **History Tracking**: Complete detection history for each user
- **Secure API Management**: Environment-based API key configuration

### Technical Features
- **Multipart File Upload**: Support for various image formats
- **Base64 Encoding**: Automatic image conversion for API compatibility
- **Error Handling**: Comprehensive error handling and logging
- **Authentication**: User-specific detection history
- **Database Integration**: Persistent storage of detection results

## 🔧 Setup Instructions

### 1. Get Plant.id API Keys

1. **Sign Up**: Go to [Plant.id](https://plant.id/) and create a free account
2. **Get API Key**: Navigate to "API Keys" section in your dashboard
3. **Multiple Keys**: For better performance, get multiple API keys (recommended)
4. **Free Tier**: 100 identifications per month (upgrade for more)

### 2. Configure Environment Variables

#### Option A: Using the Setup Script (Recommended)
```bash
# Windows
./setup-plantid.bat

# Linux/Mac
./setup-plantid.sh
```

#### Option B: Manual Configuration
```bash
# Set environment variable
export PLANTID_API_KEYS=your_key_1,your_key_2,your_key_3

# Or add to .env file
echo "PLANTID_API_KEYS=your_key_1,your_key_2,your_key_3" >> .env
```

### 3. Start the Application
```bash
mvn spring-boot:run
```

## 📱 API Endpoints

### Disease Detection
```http
POST /api/disease/detect
Content-Type: multipart/form-data
Body: image=(file)

Response:
{
  "diseaseName": "Powdery mildew",
  "confidence": 0.92,
  "suggestion": "Apply fungicide treatment and reduce humidity around crops."
}
```

### Detection History
```http
GET /api/disease/history
Authorization: Bearer <token>

Response:
[
  {
    "id": 1,
    "imageFilename": "leaf.jpg",
    "diseaseName": "Powdery mildew",
    "confidence": 0.92,
    "suggestion": "Apply fungicide treatment...",
    "detectedAt": "2025-01-27T10:00:00Z"
  }
]
```

## 🎨 Frontend Integration

### Disease Detection Page
- **Route**: `/disease-detection`
- **Features**: Image upload, real-time preview, confidence scoring
- **History**: Complete detection history with search and filtering
- **Responsive**: Mobile-optimized interface

### Navigation
- **Sidebar**: "Disease Detection" menu item with search icon
- **Protected Route**: Requires user authentication
- **User-Specific**: Each user sees only their own detection history

## 🔒 Security Features

### API Key Management
- **Environment Variables**: API keys stored in environment variables
- **No Hardcoding**: Keys never committed to version control
- **Rotation**: Automatic rotation through multiple keys
- **Logging**: Each detection logged with the API key used

### Authentication
- **User Authentication**: All endpoints require valid authentication
- **User Isolation**: Users can only access their own detection history
- **Session Management**: Secure session handling

## 📊 Database Schema

### DiseaseDetectionHistory Table
```sql
CREATE TABLE disease_detection_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    image_filename VARCHAR(255),
    disease_name VARCHAR(255),
    confidence DOUBLE PRECISION,
    suggestion TEXT,
    detected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    api_key_used VARCHAR(255)
);
```

## 🔄 API Key Rotation

### How It Works
1. **Multiple Keys**: System supports multiple API keys (comma-separated)
2. **Round-Robin**: Keys are used in rotation order
3. **Automatic**: No manual intervention required
4. **Fallback**: If one key fails, system tries the next

### Configuration
```properties
# application.properties
plantid.api-keys=${PLANTID_API_KEYS:key1,key2,key3}
plantid.endpoint=https://api.plant.id/v3/health_assessment
plantid.timeout=30000
```

## 📈 Performance Optimization

### Caching
- **Response Caching**: API responses cached for identical images
- **Database Indexing**: Optimized queries for history retrieval
- **Image Compression**: Automatic image optimization

### Rate Limiting
- **API Limits**: Respects Plant.id rate limits
- **User Limits**: Per-user detection limits
- **Queue Management**: Request queuing for high traffic

## 🧪 Testing

### Manual Testing
1. **Upload Image**: Test with various plant leaf images
2. **Check Results**: Verify disease detection accuracy
3. **History**: Confirm detection history is saved
4. **API Rotation**: Test with multiple API keys

### API Testing
```bash
# Test disease detection
curl -X POST http://localhost:9090/api/disease/detect \
  -H "Authorization: Bearer <token>" \
  -F "image=@leaf.jpg"

# Test history
curl -X GET http://localhost:9090/api/disease/history \
  -H "Authorization: Bearer <token>"
```

## 🚨 Troubleshooting

### Common Issues

#### 1. API Key Not Working
- **Check**: Verify API key is correct and active
- **Solution**: Update environment variable and restart application

#### 2. Image Upload Fails
- **Check**: Image format and size limits
- **Solution**: Use supported formats (JPG, PNG) under 10MB

#### 3. No Detection Results
- **Check**: Image quality and plant visibility
- **Solution**: Use clear, well-lit images of plant leaves

#### 4. History Not Showing
- **Check**: User authentication and database connection
- **Solution**: Verify user is logged in and database is accessible

### Debug Mode
```properties
# Enable debug logging
logging.level.com.hackathon.agriculture_backend.service.PlantDiseaseService=DEBUG
```

## 📚 API Documentation

### Plant.id API v3
- **Documentation**: https://plant.id/api-docs
- **Health Assessment**: https://api.plant.id/v3/health_assessment
- **Rate Limits**: 100 requests/month (free tier)
- **Supported Formats**: JPG, PNG, WebP

### Request Format
```json
{
  "images": ["<base64_image>"],
  "modifiers": ["similar_images"],
  "disease_details": ["description", "treatment"]
}
```

### Response Format
```json
{
  "result": {
    "disease": {
      "suggestions": [
        {
          "name": "Powdery mildew",
          "probability": 0.92,
          "details": {
            "treatment": {
              "biological": "Use neem oil",
              "chemical": "Apply fungicide",
              "prevention": "Improve air circulation"
            }
          }
        }
      ]
    }
  }
}
```

## 🔮 Future Enhancements

### Planned Features
- **Batch Processing**: Multiple image upload
- **Disease Comparison**: Compare diseases across images
- **Treatment Tracking**: Track treatment effectiveness
- **Mobile App**: Native mobile application
- **Offline Mode**: Offline disease detection

### API Improvements
- **Caching**: Redis-based response caching
- **Webhooks**: Real-time detection notifications
- **Analytics**: Advanced detection analytics
- **ML Integration**: Custom model training

## 📞 Support

### Technical Support
- **Documentation**: This file and README.md
- **API Health**: Check `/api/actuator/health`
- **Logs**: Check application logs for errors

### Plant.id Support
- **Documentation**: https://plant.id/api-docs
- **Support**: Contact Plant.id support for API issues
- **Community**: Plant.id community forums

---

**Built with ❤️ for sustainable agriculture and smart farming**
