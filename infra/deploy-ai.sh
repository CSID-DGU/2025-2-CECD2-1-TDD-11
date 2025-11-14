#!/bin/bash

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

echo "Deploying AI Stack..."
npm run build
npx cdk deploy TalkToBook-Base TalkToBook-AI --require-approval never
echo "AI deployment complete!"
