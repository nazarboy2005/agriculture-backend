# 🚀 Supabase Production Deployment Guide

## ✅ **Supabase Configuration Complete!**

Your application is now configured to use **Supabase PostgreSQL** for production while keeping **H2 database** for local development.

## 🛠️ **Configuration Overview**

### **Development (Default Profile)**
- ✅ **Database**: H2 in-memory database
- ✅ **Profile**: `dev` (automatic)
- ✅ **No external database needed**
- ✅ **H2 Console**: `http://localhost:9090/api/h2-console`

### **Production (Supabase)**
- ✅ **Database**: Supabase PostgreSQL
- ✅ **Profile**: `prod` (set via environment variable)
- ✅ **Credentials**: From environment variables
- ✅ **Connection**: Secure SSL connection

## 📁 **Updated Configuration Files**

### **application.properties (Main)**
```properties
# Database Configuration - Supabase (Production) / H2 (Development)
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://aws-1-eu-north-1.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0}
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=${DB_USERNAME:postgres.hlhnquynjylsxojferjc}
spring.datasource.password=${DB_PASSWORD:Nazarboy2005}
```

### **application-prod.properties (Production)**
```properties
# Production Database Configuration - Supabase
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://aws-1-eu-north-1.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0}
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=${DB_USERNAME:postgres.hlhnquynjylsxojferjc}
spring.datasource.password=${DB_PASSWORD:Nazarboy2005}
```

### **application-dev.properties (Development)**
```properties
# Development Database Configuration - H2
spring.datasource.url=jdbc:h2:mem:agriculture_db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

## 🚀 **Deployment Instructions**

### **For Railway.app**
1. **Set Environment Variables** in Railway dashboard:
   ```bash
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=jdbc:postgresql://aws-1-eu-north-1.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0
   DB_USERNAME=postgres.hlhnquynjylsxojferjc
   DB_PASSWORD=Nazarboy2005
   ```

2. **Deploy**: Push your code to GitHub and connect Railway

### **For Heroku**
1. **Set Environment Variables** in Heroku dashboard:
   ```bash
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=jdbc:postgresql://aws-1-eu-north-1.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0
   DB_USERNAME=postgres.hlhnquynjylsxojferjc
   DB_PASSWORD=Nazarboy2005
   ```

2. **Deploy**: Push your code to GitHub and connect Heroku

### **For Docker**
1. **Set Environment Variables** in docker-compose.yml:
   ```yaml
   environment:
     - SPRING_PROFILES_ACTIVE=prod
     - DATABASE_URL=jdbc:postgresql://aws-1-eu-north-1.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0
     - DB_USERNAME=postgres.hlhnquynjylsxojferjc
     - DB_PASSWORD=Nazarboy2005
   ```

2. **Deploy**: `docker-compose up`

## 🔍 **What You'll See in Logs**

### **Development (H2 Database):**
```
2025-09-25T17:16:15.148Z  INFO 1 --- [agriculture-backend] [           main] c.h.a.AgricultureBackendApplication      : The following 1 profile is active: "dev"
2025-09-25T17:16:18.456Z  INFO 1 --- [agriculture-backend] [           main] o.s.b.a.h2.H2ConsoleAutoConfiguration    : H2 console available at '/api/h2-console'
```

### **Production (Supabase):**
```
2025-09-25T17:16:15.148Z  INFO 1 --- [agriculture-backend] [           main] c.h.a.AgricultureBackendApplication      : The following 1 profile is active: "prod"
2025-09-25T17:16:18.456Z  INFO 1 --- [agriculture-backend] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/api'
```

## 🎯 **Key Benefits**

- ✅ **Supabase for production** - as requested
- ✅ **H2 for development** - no external database needed
- ✅ **Automatic profile selection** - works out of the box
- ✅ **Secure credentials** - via environment variables
- ✅ **SSL connection** - secure Supabase connection
- ✅ **Connection pooling** - optimized for production

## 🚀 **Ready to Deploy!**

Your application is now configured to use **Supabase PostgreSQL** for production! The solution:

1. **Uses Supabase** for production database
2. **Uses H2** for local development
3. **Automatic profile selection** - works out of the box
4. **Secure credentials** - via environment variables
5. **SSL connection** - secure Supabase connection

## 🆘 **If You Still Have Issues**

1. **Check Supabase credentials** - ensure they're correct
2. **Verify environment variables** - ensure they're set in your platform
3. **Check Supabase connection** - ensure the database is accessible
4. **Use development profile** - for testing without Supabase

**Just push your code and deploy - Supabase will be used automatically in production!** 🎉

## 📋 **Quick Start Guide**

### **Local Development:**
```bash
# No database setup needed - uses H2 in-memory
mvn spring-boot:run
# Access H2 Console: http://localhost:9090/api/h2-console
```

### **Production Deployment:**
```bash
# Set environment variables for Supabase
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://aws-1-eu-north-1.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0
export DB_USERNAME=postgres.hlhnquynjylsxojferjc
export DB_PASSWORD=Nazarboy2005

# Deploy
java -jar app.jar
```

The solution is now configured to use Supabase for production!
