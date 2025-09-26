# Google OAuth Redirect URI Mismatch Fix

## Problem
The error "redirect_uri_mismatch" occurs when the redirect URI in the Google Cloud Console doesn't match the one configured in the application.

## Solution

### 1. Update Google Cloud Console
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Navigate to "APIs & Services" > "Credentials"
3. Find your OAuth 2.0 Client ID
4. Click "Edit"
5. In "Authorized redirect URIs", add:
   ```
   https://agriculture-backend-1077945709935.europe-west1.run.app/api/login/oauth2/code/google
   ```
6. Save the changes

### 2. Environment Variables
Make sure these environment variables are set in your deployment:
```
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

### 3. Application Configuration
The application.properties file has been updated with the correct redirect URI:
```properties
spring.security.oauth2.client.registration.google.redirect-uri=https://agriculture-backend-1077945709935.europe-west1.run.app/api/login/oauth2/code/google
```

### 4. Frontend Configuration
The frontend should handle the OAuth redirect at `/oauth2/redirect` route.

### 5. Testing
1. Deploy the updated backend
2. Test Google OAuth login
3. Verify the redirect works correctly

## Common Issues
- Make sure the redirect URI exactly matches (including https/http)
- Ensure the path includes `/api` prefix
- Check that the Google Cloud Console has the correct URI
- Verify environment variables are set correctly
