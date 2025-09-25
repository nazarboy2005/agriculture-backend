# Agriculture Backend Deployment Troubleshooting

## Common Issues and Solutions

### 1. "Unable to access jarfile" Error

**Problem**: `Error: Unable to access jarfile target/agriculture-backend-0.0.1-SNAPSHOT.jar`

**Causes**:
- JAR file not built during deployment
- Working directory is incorrect
- Build process failed silently
- File permissions issue

**Solutions**:

#### A. Verify Build Process
```bash
# Run the debug script
./debug-deployment.bat

# Or manually check
mvn clean package -DskipTests
ls -la target/
```

#### B. Check Working Directory
```bash
# Ensure you're in the correct directory
pwd
ls -la target/agriculture-backend-0.0.1-SNAPSHOT.jar
```

#### C. Use Absolute Paths
Update your deployment configuration to use absolute paths:
```bash
# Instead of: java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar
# Use: java -jar /app/target/agriculture-backend-0.0.1-SNAPSHOT.jar
```

### 2. Platform-Specific Fixes

#### Railway.app
```json
{
  "deploy": {
    "startCommand": "cd /app && java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar --server.port=$PORT"
  }
}
```

#### Heroku
```bash
# Procfile
web: java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar --server.port=$PORT
```

#### Docker
```dockerfile
# Ensure JAR file exists before copying
RUN ls -la target/agriculture-backend-0.0.1-SNAPSHOT.jar
COPY --from=build /app/target/agriculture-backend-0.0.1-SNAPSHOT.jar app.jar
```

### 3. Build Process Verification

#### Check Maven Build
```bash
# Clean build with verbose output
mvn clean package -DskipTests -X

# Check if JAR was created
ls -la target/agriculture-backend-0.0.1-SNAPSHOT.jar

# Verify JAR contents
jar -tf target/agriculture-backend-0.0.1-SNAPSHOT.jar | head -20
```

#### Test JAR File
```bash
# Test if JAR is executable
java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar --help

# Check JAR file size (should be > 1MB)
ls -lh target/agriculture-backend-0.0.1-SNAPSHOT.jar
```

### 4. Environment Variables

Ensure these are set in your deployment platform:

```bash
# Required
JAVA_HOME=/usr/lib/jvm/java-17-openjdk
PORT=9090

# Optional but recommended
SPRING_PROFILES_ACTIVE=prod
```

### 5. Platform-Specific Commands

#### For Railway.app
```bash
# Use the updated railway.json
# The startCommand should be:
"startCommand": "java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar --server.port=$PORT"
```

#### For Heroku
```bash
# Use the updated Procfile
web: java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar --server.port=$PORT
```

#### For Docker
```bash
# Build the image
docker build -t agriculture-backend .

# Run the container
docker run -p 9090:9090 agriculture-backend
```

### 6. Debugging Steps

1. **Run the debug script**:
   ```bash
   ./debug-deployment.bat
   ```

2. **Check build logs**:
   - Look for compilation errors
   - Verify dependencies are resolved
   - Check for memory issues

3. **Verify file permissions**:
   ```bash
   ls -la target/agriculture-backend-0.0.1-SNAPSHOT.jar
   chmod +x target/agriculture-backend-0.0.1-SNAPSHOT.jar
   ```

4. **Test locally first**:
   ```bash
   java -jar target/agriculture-backend-0.0.1-SNAPSHOT.jar
   ```

### 7. Alternative Deployment Methods

#### Method 1: Use Maven Spring Boot Plugin
```bash
# Instead of JAR file, use Maven directly
mvn spring-boot:run
```

#### Method 2: Use Gradle (if you switch)
```bash
./gradlew bootRun
```

#### Method 3: Use Docker Compose
```yaml
version: '3.8'
services:
  agriculture-backend:
    build: .
    ports:
      - "9090:9090"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
```

### 8. Quick Fixes

#### Fix 1: Update all deployment configs
```bash
# Run this to update all deployment files
./deploy.bat
```

#### Fix 2: Use absolute paths
Update your deployment platform to use:
```bash
java -jar /app/target/agriculture-backend-0.0.1-SNAPSHOT.jar --server.port=$PORT
```

#### Fix 3: Add build verification
Add this to your deployment script:
```bash
# Verify JAR exists before starting
if [ ! -f "target/agriculture-backend-0.0.1-SNAPSHOT.jar" ]; then
  echo "JAR file not found! Build failed."
  exit 1
fi
```

## Still Having Issues?

1. Check the platform-specific logs
2. Verify Java version compatibility
3. Ensure sufficient memory allocation
4. Check network connectivity for dependencies
5. Verify environment variables are set correctly

## Support

If you're still experiencing issues:
1. Run `./debug-deployment.bat` and share the output
2. Check your deployment platform's logs
3. Verify your Java and Maven versions
4. Ensure all dependencies are available
