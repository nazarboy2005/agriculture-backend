#!/bin/bash

echo "🚀 Agriculture Backend Startup Script"
echo "====================================="
echo

# Set working directory to script location
cd "$(dirname "$0")"

echo "📋 Current directory: $(pwd)"
echo

# Check if JAR file exists
if [ -f "target/agriculture-backend-0.0.1-SNAPSHOT.jar" ]; then
    echo "✅ JAR file found: target/agriculture-backend-0.0.1-SNAPSHOT.jar"
    echo "📊 JAR file size: $(ls -lh target/agriculture-backend-0.0.1-SNAPSHOT.jar | awk '{print $5}')"
    echo
else
    echo "❌ JAR file not found! Attempting to build..."
    echo
    
    # Try to build the project
    if [ -f "mvnw" ]; then
        echo "🔨 Building with Maven wrapper..."
        chmod +x mvnw
        ./mvnw clean package -DskipTests
    elif command -v mvn &> /dev/null; then
        echo "🔨 Building with system Maven..."
        mvn clean package -DskipTests
    else
        echo "❌ Neither Maven wrapper nor system Maven found!"
        echo "Please ensure Maven is installed or mvnw is available"
        exit 1
    fi
    
    # Check if build was successful
    if [ ! -f "target/agriculture-backend-0.0.1-SNAPSHOT.jar" ]; then
        echo "❌ Build failed! JAR file not created"
        echo "Available files in target directory:"
        ls -la target/ 2>/dev/null || echo "Target directory not found"
        exit 1
    fi
    
    echo "✅ Build successful! JAR file created"
    echo
fi

# Set default port if not provided
if [ -z "$PORT" ]; then
    PORT=9090
fi

echo "🌐 Starting application on port $PORT"
echo "📡 API will be available at: http://localhost:$PORT/api"
echo "🔍 Health check: http://localhost:$PORT/api/actuator/health"
echo

# Start the application
echo "🚀 Starting Spring Boot application..."
java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar --server.port=$PORT

echo
echo "👋 Application stopped"
