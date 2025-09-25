@echo off
echo ========================================
echo Verifying Deployment
echo ========================================

echo.
echo Testing backend endpoints...
echo.

echo [1/4] Testing health endpoint...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/actuator/health' -Method GET; Write-Host 'Health Check: SUCCESS'; Write-Host 'Status:' $response.StatusCode; Write-Host 'Response:' $response.Content } catch { Write-Host 'Health Check: FAILED -' $_.Exception.Message }"

echo.
echo [2/4] Testing CORS preflight...
powershell -Command "try { $headers = @{'Origin' = 'https://agriculture-frontend-btleirx65.vercel.app'; 'Access-Control-Request-Method' = 'POST'}; $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method OPTIONS -Headers $headers; Write-Host 'CORS Preflight: SUCCESS'; Write-Host 'Status:' $response.StatusCode } catch { Write-Host 'CORS Preflight: FAILED -' $_.Exception.Message }"

echo.
echo [3/4] Testing auth endpoint...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/v1/auth/register' -Method GET; Write-Host 'Auth Endpoint: SUCCESS'; Write-Host 'Status:' $response.StatusCode } catch { Write-Host 'Auth Endpoint: FAILED -' $_.Exception.Message }"

echo.
echo [4/4] Testing CORS headers...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'https://agriculture-backend-production.railway.app/api/actuator/health' -Method GET; Write-Host 'CORS Headers Check:'; $response.Headers | Where-Object {$_.Key -like '*Access-Control*'} | ForEach-Object { Write-Host '  ' $_.Key ':' $_.Value } } catch { Write-Host 'CORS Headers: FAILED -' $_.Exception.Message }"

echo.
echo ========================================
echo Verification Complete!
echo ========================================
echo.
echo If all tests show SUCCESS, your deployment is working correctly.
echo You can now test your frontend at: https://agriculture-frontend-btleirx65.vercel.app
echo.
pause
