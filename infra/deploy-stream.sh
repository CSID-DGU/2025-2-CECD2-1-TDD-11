#!/bin/bash

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

echo "Deploying Stream Stack..."
npm run build
npx cdk deploy TalkToBook-Base TalkToBook-Stream --require-approval never
echo "Stream deployment complete!"
