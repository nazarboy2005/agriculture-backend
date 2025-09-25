#!/bin/bash

# Find the JAR file in the target directory
JAR_FILE=$(find target -name "agriculture-backend-*.jar" | head -1)

if [ -z "$JAR_FILE" ]; then
    echo "Error: No JAR file found in target directory"
    ls -la target/
    exit 1
fi

echo "Starting application with JAR: $JAR_FILE"
java -jar "$JAR_FILE"
