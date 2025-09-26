#!/bin/bash

# Smart Irrigation Backend Setup Script
echo "🌱 Setting up Smart Irrigation Backend..."

# Check if Java 17+ is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java $JAVA_VERSION detected"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6+."
    exit 1
fi

echo "✅ Maven detected"

# Create environment file if it doesn't exist
if [ ! -f .env ]; then
    echo "📝 Creating .env file from template..."
    cp env.example .env
    echo "⚠️  Please edit .env file with your actual API keys"
fi

# Check if required environment variables are set
echo "🔍 Checking environment variables..."

if [ -z "$DB_PASSWORD" ]; then
    echo "⚠️  DB_PASSWORD not set. Please set your Supabase database password."
fi

if [ -z "$WEATHER_API_KEY" ]; then
    echo "⚠️  WEATHER_API_KEY not set. Please get your OpenWeatherMap API key."
fi

if [ -z "$TWILIO_ACCOUNT_SID" ]; then
    echo "⚠️  TWILIO_ACCOUNT_SID not set. Please get your Twilio credentials."
fi

if [ -z "$TWILIO_AUTH_TOKEN" ]; then
    echo "⚠️  TWILIO_AUTH_TOKEN not set. Please get your Twilio credentials."
fi

if [ -z "$TWILIO_PHONE_NUMBER" ]; then
    echo "⚠️  TWILIO_PHONE_NUMBER not set. Please get your Twilio phone number."
fi

# Build the application
echo "🔨 Building the application..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ Application built successfully"
else
    echo "❌ Build failed"
    exit 1
fi

# Run tests
echo "🧪 Running tests..."
mvn test

if [ $? -eq 0 ]; then
    echo "✅ Tests passed"
else
    echo "⚠️  Some tests failed, but continuing..."
fi

echo ""
echo "🚀 Setup complete! To run the application:"
echo "   mvn spring-boot:run"
echo ""
echo "📱 The application will be available at: http://localhost:8080/api"
echo "📊 Health check: http://localhost:8080/api/actuator/health"
echo ""
echo "🔑 Don't forget to set your environment variables:"
echo "   export DB_PASSWORD=your-supabase-password"
echo "   export WEATHER_API_KEY=your-openweathermap-api-key"
echo "   export TWILIO_ACCOUNT_SID=your-twilio-account-sid"
echo "   export TWILIO_AUTH_TOKEN=your-twilio-auth-token"
echo "   export TWILIO_PHONE_NUMBER=+1234567890"



