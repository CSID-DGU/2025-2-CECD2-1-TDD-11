#!/usr/bin/env node
import * as dotenv from "dotenv";
dotenv.config();

import * as cdk from "aws-cdk-lib";
import { BaseStack } from "../lib/stacks/base-stack";
import { ServerStack } from "../lib/stacks/server-stack";
import { AIStack } from "../lib/stacks/ai-stack";
import { StreamStack } from "../lib/stacks/stream-stack";

const app = new cdk.App();

// 필수 환경변수 검증 함수
function getRequiredEnv(name: string): string {
  const value = process.env[name];
  if (!value) {
    throw new Error(`Required environment variable ${name} is not set`);
  }
  return value;
}

// 환경변수 중앙 관리
const config = {
  region: getRequiredEnv("AWS_REGION"),
  account: getRequiredEnv("AWS_ACCOUNT_ID"),
  ecrRegistry: getRequiredEnv("ECR_REGISTRY"),
  ecrRepository: {
    ai: getRequiredEnv("ECR_REPOSITORY_AI"),
    server: getRequiredEnv("ECR_REPOSITORY_SERVER"),
    stream: getRequiredEnv("ECR_REPOSITORY_STREAM"),
  },
  s3BucketName: getRequiredEnv("S3_DEPLOY_BUCKET"),
  keyPairs: {
    server: getRequiredEnv("KEY_PAIR_SERVER"),
    ai: getRequiredEnv("KEY_PAIR_AI"),
    stream: getRequiredEnv("KEY_PAIR_STREAM"),
  },
  database: {
    host: getRequiredEnv("DB_HOST"),
    port: getRequiredEnv("DB_PORT"),
    name: getRequiredEnv("DB_NAME"),
    user: getRequiredEnv("DB_USER"),
    password: getRequiredEnv("DB_PASSWORD"),
  },
  redis: {
    host: getRequiredEnv("REDIS_HOST"),
    port: getRequiredEnv("REDIS_PORT"),
  },
  albArn: getRequiredEnv("ALB_ARN"),
  albTagName: getRequiredEnv("ALB_TAG_NAME"),
};

// Base resources
const baseStack = new BaseStack(app, "TalkToBook-Base", {
  env: {
    account: config.account,
    region: config.region,
  },
  config,
});

// Service stacks
new ServerStack(app, "TalkToBook-Server", {
  vpc: baseStack.vpc,
  role: baseStack.role,
  deployBucket: baseStack.deployBucket,
  env: {
    account: config.account,
    region: config.region,
  },
  config,
});

new AIStack(app, "TalkToBook-AI", {
  vpc: baseStack.vpc,
  role: baseStack.role,
  deployBucket: baseStack.deployBucket,
  env: {
    account: config.account,
    region: config.region,
  },
  config,
});

new StreamStack(app, "TalkToBook-Stream", {
  vpc: baseStack.vpc,
  role: baseStack.role,
  deployBucket: baseStack.deployBucket,
  env: {
    account: config.account,
    region: config.region,
  },
  config,
});

new MonitoringStack(app, "TalkToBook-Monitoring", {
  vpc: baseStack.vpc,
  role: baseStack.role,
  deployBucket: baseStack.deployBucket,
  env: {
    account: config.account,
    region: config.region,
  },
  config,
});
