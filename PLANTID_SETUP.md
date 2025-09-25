# Plant.id API Setup Guide

## Overview
The disease detection feature requires Plant.id API keys to function. This guide will help you configure the API keys properly.

## Step 1: Get Plant.id API Keys

1. **Sign up at Plant.id**: Go to [https://plant.id/](https://plant.id/)
2. **Create an account**: Register for a free account
3. **Get API keys**: Navigate to the "API Keys" section in your dashboard
4. **Copy your API key(s)**: You can get multiple keys for better performance

## Step 2: Configure Environment Variables

### Option A: Using .env file (Recommended)
Create a `.env` file in the backend root directory:

```bash
# Plant.id API Configuration
PLANTID_API_KEYS=your-api-key-1,your-api-key-2,your-api-key-3
```

### Option B: Environment Variables
Set the environment variable directly:

```bash
# Windows
set PLANTID_API_KEYS=your-api-key-1,your-api-key-2,your-api-key-3

# Linux/Mac
export PLANTID_API_KEYS=your-api-key-1,your-api-key-2,your-api-key-3
```

## Step 3: Restart the Application

After configuring the API keys, restart your Spring Boot application:

```bash
mvn spring-boot:run
```

## Step 4: Test the Configuration

1. **Start the backend**: Make sure the backend is running on port 9090
2. **Start the frontend**: Make sure the frontend is running on port 3000
3. **Login to the application**: Use your registered account
4. **Navigate to Disease Detection**: Go to the disease detection page
5. **Upload an image**: Try uploading a plant leaf image
6. **Check the results**: You should see real disease detection results

## Troubleshooting

### Error: "Plant.id API keys not configured"
- Make sure you've set the `PLANTID_API_KEYS` environment variable
- Check that the API keys are valid and not expired
- Restart the application after setting the environment variables

### Error: "Service Unavailable"
- Verify your Plant.id API keys are correct
- Check your internet connection
- Ensure you haven't exceeded your API quota

### Error: "User not found"
- Make sure you're logged in to the application
- Check that your user account exists in the database
- Try logging out and logging back in

## API Limits

- **Free tier**: 100 identifications per month
- **Paid tiers**: Higher limits available
- **Multiple keys**: Use comma-separated keys for rotation

## Security Notes

- Never commit API keys to version control
- Use environment variables for production
- Rotate API keys regularly
- Monitor API usage in your Plant.id dashboard

## Support

If you encounter issues:
1. Check the application logs for detailed error messages
2. Verify your Plant.id account status
3. Contact the development team for assistance
