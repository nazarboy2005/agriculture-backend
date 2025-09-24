# Gemini AI API Setup Guide

## Overview
This application uses Google's Gemini AI API to generate professional agricultural advice. The system is configured to use real AI responses when the API key is properly configured.

## Setup Instructions

### 1. Get Your Gemini API Key
1. Go to [Google AI Studio](https://aistudio.google.com/)
2. Sign in with your Google account
3. Create a new API key
4. Copy the API key

### 2. Configure the API Key

#### Option A: Environment Variable (Recommended)
```bash
export GEMINI_API_KEY="your-actual-gemini-api-key-here"
```

#### Option B: Application Properties
Update `application.properties`:
```properties
app.gemini.api.key=your-actual-gemini-api-key-here
```

### 3. Verify Configuration
The application will automatically:
- Try to use the real Gemini API first
- Fall back to professional mock responses if API is unavailable
- Log the API status for debugging

## Features

### Real AI Responses
When properly configured, the system provides:
- **Professional Agricultural Advice**: Expert-level recommendations
- **Context-Aware Responses**: Based on farmer's data, location, and crop type
- **Scientific Accuracy**: Evidence-based agricultural science
- **Comprehensive Analysis**: Detailed, actionable advice

### Fallback System
If the API is unavailable, the system provides:
- **Professional Mock Responses**: High-quality, detailed agricultural advice
- **Specialized Content**: Different responses for irrigation, weather, soil, pests, etc.
- **Scientific Terminology**: Professional agricultural language
- **Actionable Recommendations**: Specific, measurable advice

## Response Types

The AI system provides specialized responses for:

1. **Irrigation Management**
   - Water scheduling optimization
   - Efficiency improvements
   - Technology integration

2. **Weather Analysis**
   - Temperature impact assessment
   - Humidity and ET analysis
   - Crop protection measures

3. **Crop Management**
   - Growth stage optimization
   - Nutrient management
   - Harvest planning

4. **Pest & Disease Control**
   - Integrated Pest Management (IPM)
   - Biological controls
   - Economic thresholds

5. **Soil Health**
   - Comprehensive soil analysis
   - Improvement recommendations
   - Sustainable practices

6. **Fertilizer Management**
   - Nutrient requirements
   - Application timing
   - Environmental considerations

## Quality Assurance

All responses include:
- **Professional Formatting**: Structured, easy-to-read advice
- **Scientific Accuracy**: Based on current agricultural research
- **Specific Measurements**: Exact quantities, timing, and methods
- **Actionable Steps**: Clear, implementable recommendations
- **Context Awareness**: Personalized for the farmer's situation

## Troubleshooting

### API Not Working
- Check API key configuration
- Verify internet connection
- Check application logs for errors
- System will automatically use professional fallback responses

### Response Quality
- Ensure API key is valid and active
- Check rate limits and quotas
- Monitor application logs for issues

## Support

For technical support:
1. Check application logs
2. Verify API key configuration
3. Test with different question types
4. Contact development team if issues persist

The system is designed to always provide high-quality, professional agricultural advice regardless of API availability.
