#!/bin/bash

# Plant.id API Setup Script
# This script helps you securely configure Plant.id API keys

echo "🌱 Plant.id API Setup for Disease Detection"
echo "=========================================="
echo ""

# Check if PLANTID_API_KEYS is already set
if [ ! -z "$PLANTID_API_KEYS" ]; then
    echo "✅ PLANTID_API_KEYS is already configured"
    echo "Current keys: ${PLANTID_API_KEYS:0:20}..."
    echo ""
    read -p "Do you want to update the API keys? (y/n): " update_keys
    if [ "$update_keys" != "y" ]; then
        echo "Setup cancelled."
        exit 0
    fi
fi

echo "📋 Setup Instructions:"
echo "1. Go to https://plant.id/"
echo "2. Sign up for a free account"
echo "3. Navigate to 'API Keys' section"
echo "4. Copy your API key(s)"
echo "5. For multiple keys (recommended for rotation), separate them with commas"
echo ""

# Get API keys from user
read -p "Enter your Plant.id API key(s) (comma-separated for multiple): " api_keys

if [ -z "$api_keys" ]; then
    echo "❌ No API keys provided. Setup cancelled."
    exit 1
fi

# Validate API key format (basic check)
if [[ ! "$api_keys" =~ ^[a-zA-Z0-9,]+$ ]]; then
    echo "❌ Invalid API key format. Please check your keys."
    exit 1
fi

# Count number of keys
key_count=$(echo "$api_keys" | tr ',' '\n' | wc -l)
echo "✅ Found $key_count API key(s)"

# Set environment variable
export PLANTID_API_KEYS="$api_keys"

# Create .env file if it doesn't exist
if [ ! -f .env ]; then
    touch .env
    echo "# Environment variables for agriculture-backend" >> .env
fi

# Add or update PLANTID_API_KEYS in .env
if grep -q "PLANTID_API_KEYS" .env; then
    sed -i "s/PLANTID_API_KEYS=.*/PLANTID_API_KEYS=$api_keys/" .env
else
    echo "PLANTID_API_KEYS=$api_keys" >> .env
fi

echo ""
echo "✅ Plant.id API keys configured successfully!"
echo ""
echo "🔧 Next steps:"
echo "1. Start your application: mvn spring-boot:run"
echo "2. Test disease detection at: http://localhost:9090/api/disease/detect"
echo "3. View the frontend at: http://localhost:3000/disease-detection"
echo ""
echo "🔒 Security Note:"
echo "- API keys are stored in .env file (not committed to git)"
echo "- Keys are automatically rotated for better performance"
echo "- Each detection is logged with the API key used"
echo ""
echo "📚 Documentation:"
echo "- API Documentation: https://plant.id/api-docs"
echo "- Free tier includes 100 identifications per month"
echo "- Upgrade for higher limits and better performance"
