@echo off
echo ========================================
echo Testing CORS Configuration
echo ========================================

echo.
echo Testing CORS headers for Vercel frontend...
echo.

echo [1/3] Testing OPTIONS preflight request...
powershell -Command "try { $headers = @{'Origin' = 'https://agriculture-frontend-btleirx65.vercel.app'; 'Access-Control-Request-Method' = 'POST'; 'Access-Control-Request-Headers' = 'Content-Type'}; $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method OPTIONS -Headers $headers; Write-Host 'OPTIONS Request: SUCCESS'; Write-Host 'Status:' $response.StatusCode; Write-Host 'CORS Headers:'; $response.Headers | Where-Object {$_.Key -like '*Access-Control*'} | ForEach-Object { Write-Host '  ' $_.Key ':' $_.Value } } catch { Write-Host 'OPTIONS Request: FAILED -' $_.Exception.Message }"

echo.
echo [2/3] Testing actual POST request...
powershell -Command "try { $headers = @{'Origin' = 'https://agriculture-frontend-btleirx65.vercel.app'; 'Content-Type' = 'application/json'}; $body = '{\"username\":\"test\",\"email\":\"test@example.com\",\"password\":\"password123\"}'; $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method POST -Headers $headers -Body $body; Write-Host 'POST Request: SUCCESS'; Write-Host 'Status:' $response.StatusCode } catch { Write-Host 'POST Request: FAILED -' $_.Exception.Message }"

echo.
echo [3/3] Testing health endpoint...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/actuator/health' -Method GET; Write-Host 'Health Check: SUCCESS'; Write-Host 'Status:' $response.StatusCode; Write-Host 'Response:' $response.Content } catch { Write-Host 'Health Check: FAILED -' $_.Exception.Message }"

echo.
echo ========================================
echo CORS Test Complete!
echo ========================================
echo.
echo If all tests show SUCCESS, the CORS configuration is working.
echo If any test shows FAILED, the backend may need to be redeployed.
echo.
pause
