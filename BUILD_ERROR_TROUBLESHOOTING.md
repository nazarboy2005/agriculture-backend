# Build Error Troubleshooting Guide

## Problem
Maven build is failing during Docker build process with a non-zero exit code.

## Common Causes and Solutions

### 1. Maven Wrapper Issues
**Problem**: Maven wrapper not executable or corrupted
**Solution**:
```bash
# Make wrapper executable
chmod +x ./mvnw

# Or use system Maven
mvn clean package -DskipTests
```

### 2. Java Version Issues
**Problem**: Wrong Java version or JAVA_HOME not set
**Solution**:
```bash
# Check Java version
java -version

# Set JAVA_HOME (Windows)
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Set JAVA_HOME (Linux/Mac)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### 3. Dependency Issues
**Problem**: Missing or conflicting dependencies
**Solution**:
```bash
# Clean and re-download dependencies
mvn clean
mvn dependency:resolve
```

### 4. Memory Issues
**Problem**: Out of memory during build
**Solution**:
```bash
# Increase Maven memory
export MAVEN_OPTS="-Xmx1024m -Xms512m"
mvn clean package -DskipTests
```

### 5. Compilation Errors
**Problem**: Java compilation errors
**Solution**:
```bash
# Compile with verbose output
mvn compile -X

# Check specific files
mvn compile -Dmaven.compiler.showWarnings=true
```

## Step-by-Step Debugging

### 1. Test Local Build
```bash
# Run the build test script
./test-build.bat

# Or manually
mvnw clean compile -DskipTests
```

### 2. Check for Specific Errors
```bash
# Look for compilation errors
mvn compile -X | grep -i error

# Check for missing dependencies
mvn dependency:tree
```

### 3. Verify Configuration
```bash
# Check application properties
grep -n "MAIL_" src/main/resources/application-prod.properties

# Check for syntax errors
find src -name "*.java" -exec javac -cp "$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" {} \;
```

## Docker-Specific Issues

### 1. Dockerfile Problems
**Current Dockerfile**:
```dockerfile
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests -X
```

**Alternative Simple Dockerfile**:
```dockerfile
FROM maven:3.9.4-eclipse-temurin-17
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
CMD ["java", "-jar", "target/agriculture-backend-0.0.1-SNAPSHOT.jar"]
```

### 2. Build Context Issues
**Problem**: Files not copied correctly
**Solution**:
```bash
# Check what's being copied
docker build --no-cache -t agriculture-backend .

# Or use .dockerignore to exclude unnecessary files
```

## Quick Fixes

### Fix 1: Use System Maven
```dockerfile
FROM maven:3.9.4-eclipse-temurin-17
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
CMD ["java", "-jar", "target/agriculture-backend-0.0.1-SNAPSHOT.jar"]
```

### Fix 2: Increase Memory
```dockerfile
FROM maven:3.9.4-eclipse-temurin-17
WORKDIR /app
COPY . .
ENV MAVEN_OPTS="-Xmx1024m -Xms512m"
RUN mvn clean package -DskipTests
CMD ["java", "-jar", "target/agriculture-backend-0.0.1-SNAPSHOT.jar"]
```

### Fix 3: Multi-stage with Debug
```dockerfile
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src src
RUN mvn clean package -DskipTests -X
RUN ls -la target/
RUN find target/ -name "*.jar" -type f
```

## Testing Commands

### Local Testing
```bash
# Test build locally
./test-build.bat

# Fix build errors
./fix-build-errors.bat

# Deploy with fixes
./deploy-with-build-fix.bat
```

### Docker Testing
```bash
# Build with simple Dockerfile
docker build -f Dockerfile.simple -t agriculture-backend .

# Run container
docker run -p 9090:9090 agriculture-backend
```

## Environment Variables Check

Make sure these are set in your deployment environment:
```bash
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=killiyaezov@gmail.com
MAIL_PASSWORD=wmyahtxvziwqsdly
```

## Files Created for Debugging
- `test-build.bat` - Local build testing
- `fix-build-errors.bat` - Build error fixing
- `deploy-with-build-fix.bat` - Deployment with fixes
- `Dockerfile.simple` - Simplified Dockerfile
- `BUILD_ERROR_TROUBLESHOOTING.md` - This guide

## Next Steps
1. **Run local build test**: `./test-build.bat`
2. **Fix any compilation errors**: `./fix-build-errors.bat`
3. **Deploy with fixes**: `./deploy-with-build-fix.bat`
4. **Use simplified Dockerfile** if needed: `Dockerfile.simple`
