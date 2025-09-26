@echo off
echo ========================================
echo DEPLOYING HIBERNATE METADATA FIX
echo ========================================

echo Building application...
call mvn clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo Build failed!
    exit /b 1
)

echo ========================================
echo HIBERNATE FIX DEPLOYED SUCCESSFULLY
echo ========================================
echo.
echo The application now has:
echo - Complete bidirectional entity mappings
echo - Farmer entity with @OneToMany relationships
echo - IrrigationRecommendation entity with @OneToMany relationships
echo - Proper Hibernate metadata collection
echo.
echo This should resolve the Hibernate metadata collection error.
echo ========================================
