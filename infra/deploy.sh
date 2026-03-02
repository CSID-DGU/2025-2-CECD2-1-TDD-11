#!/bin/bash

echo "======================================"
echo "Deploying TalkToBook Infrastructure"
echo "======================================"

# AWS 프로파일 입력 받기
read -p "Enter AWS CLI profile name (default: talktobook): " AWS_PROFILE
AWS_PROFILE=${AWS_PROFILE:-talktobook}

# 자격 증명 유효성 검사
echo "🔍 Validating AWS credentials for profile [$AWS_PROFILE]..."
if ! aws sts get-caller-identity --profile "$AWS_PROFILE" &>/dev/null; then
    echo "Invalid AWS credentials for profile: $AWS_PROFILE"
    exit 1
fi
echo "AWS credentials validated."

# CDK bootstrap (최초 1회만 필요하지만 항상 실행해도 무해함)
echo "Bootstrapping CDK..."
npx cdk bootstrap

# TypeScript 빌드
echo "Building CDK..."
npm run build

# 변경 사항 확인
echo "Showing changes..."
npx cdk diff

# 사용자 확인 후 배포
read -p "Continue with deployment? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Deployment cancelled"
    exit 1
fi

# CDK 배포
echo "Deploying stack..."
npx cdk deploy  --all --require-approval never

echo "Deployment complete!"
echo "Check AWS Console for infrastructure details."