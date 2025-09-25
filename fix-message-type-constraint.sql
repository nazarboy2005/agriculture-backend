-- Fix the message_type check constraint to include DISEASE_TREATMENT
-- This script should be run on the database to allow the new message type

-- First, drop the existing constraint
ALTER TABLE chats DROP CONSTRAINT IF EXISTS chats_message_type_check;

-- Add the new constraint with all valid message types including DISEASE_TREATMENT
ALTER TABLE chats ADD CONSTRAINT chats_message_type_check 
CHECK (message_type IN (
    'GENERAL',
    'IRRIGATION_ADVICE', 
    'CROP_MANAGEMENT',
    'WEATHER_QUERY',
    'PEST_DISEASE',
    'SOIL_HEALTH',
    'FERTILIZER_ADVICE',
    'HARVEST_PLANNING',
    'MARKET_INFO',
    'TECHNICAL_SUPPORT',
    'DISEASE_TREATMENT'
));

-- Verify the constraint was added
SELECT conname, consrc FROM pg_constraint WHERE conname = 'chats_message_type_check';
