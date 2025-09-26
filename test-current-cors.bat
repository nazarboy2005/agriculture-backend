@echo off
echo ========================================
echo TESTING CURRENT CORS STATUS
echo ========================================

echo.
echo [1/3] Testing preflight OPTIONS request...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method OPTIONS -Headers @{'Origin'='https://agriculture-frontend-two.vercel.app'; 'Access-Control-Request-Method'='POST'} -UseBasicParsing; Write-Host 'CORS Test: Status:' $response.StatusCode; Write-Host 'Headers:' $response.Headers } catch { Write-Host 'CORS Test: FAILED -' $_.Exception.Message }"

echo.
echo [2/3] Testing actual POST request...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method POST -Headers @{'Origin'='https://agriculture-frontend-two.vercel.app'; 'Content-Type'='application/json'} -Body '{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"password123\"}' -UseBasicParsing; Write-Host 'POST Test: Status:' $response.StatusCode; Write-Host 'Headers:' $response.Headers } catch { Write-Host 'POST Test: FAILED -' $_.Exception.Message }"

echo.
echo [3/3] Testing health endpoint...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/actuator/health' -Method GET -Headers @{'Origin'='https://agriculture-frontend-two.vercel.app'} -UseBasicParsing; Write-Host 'Health Test: Status:' $response.StatusCode; Write-Host 'Headers:' $response.Headers } catch { Write-Host 'Health Test: FAILED -' $_.Exception.Message }"

echo.
echo ========================================
echo CURRENT CORS TEST COMPLETED
echo ========================================
echo.
echo Look for "Access-Control-Allow-Origin: https://agriculture-frontend-two.vercel.app"
echo in the response headers above.
echo.
pause
