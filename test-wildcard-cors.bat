@echo off
echo ========================================
echo TESTING WILDCARD CORS FIX
echo ========================================

echo.
echo [1/3] Testing preflight OPTIONS request...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method OPTIONS -Headers @{'Origin'='https://agriculture-frontend-two.vercel.app'; 'Access-Control-Request-Method'='POST'} -UseBasicParsing; Write-Host 'CORS Test: Status:' $response.StatusCode; Write-Host 'Headers:' $response.Headers } catch { Write-Host 'CORS Test: FAILED -' $_.Exception.Message }"

echo.
echo [2/3] Testing with different origin...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method OPTIONS -Headers @{'Origin'='https://example.com'; 'Access-Control-Request-Method'='POST'} -UseBasicParsing; Write-Host 'Different Origin Test: Status:' $response.StatusCode; Write-Host 'Headers:' $response.Headers } catch { Write-Host 'Different Origin Test: FAILED -' $_.Exception.Message }"

echo.
echo [3/3] Testing health endpoint...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/actuator/health' -Method GET -Headers @{'Origin'='https://agriculture-frontend-two.vercel.app'} -UseBasicParsing; Write-Host 'Health Test: Status:' $response.StatusCode; Write-Host 'Headers:' $response.Headers } catch { Write-Host 'Health Test: FAILED -' $_.Exception.Message }"

echo.
echo ========================================
echo WILDCARD CORS TEST COMPLETED
echo ========================================
echo.
echo Look for "Access-Control-Allow-Origin: *" in the response headers above.
echo.
pause
