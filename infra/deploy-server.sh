#!/bin/bash

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

echo "Deploying Server Stack..."
npm run build
npx cdk deploy TalkToBook-Base TalkToBook-Server --require-approval never
echo "Server deployment complete!"
