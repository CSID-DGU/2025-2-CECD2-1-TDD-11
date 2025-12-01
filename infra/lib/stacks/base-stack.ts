import * as cdk from "aws-cdk-lib";
import * as ec2 from "aws-cdk-lib/aws-ec2";
import * as iam from "aws-cdk-lib/aws-iam";
import * as s3 from "aws-cdk-lib/aws-s3";
import { Construct } from "constructs";

interface BaseStackProps extends cdk.StackProps {
  config: {
    region: string;
    account: string;
    ecrRegistry: string;
    ecrRepository: {
      ai: string;
      server: string;
      stream: string;
    };
    s3BucketName: string;
    keyPairs: {
      server: string;
      ai: string;
      stream: string;
    };
    database: {
      host: string;
      port: string;
      name: string;
      user: string;
      password: string;
    };
    redis: {
      host: string;
      port: string;
    };
    albArn?: string;
    albTagName?: string;
  };
}

// BaseStack: VPC 및 공통 IAM Role 설정
export class BaseStack extends cdk.Stack {
  public readonly vpc: ec2.IVpc;
  public readonly role: iam.Role;
  public readonly deployBucket: s3.Bucket;

  constructor(scope: Construct, id: string, props: BaseStackProps) {
    super(scope, id, props);

    // VPC
    this.vpc = ec2.Vpc.fromLookup(this, "DefaultVPC", {
      isDefault: true,
    });

    const bucketName = props.config.s3BucketName;

    // S3 Bucket for deployment files
    this.deployBucket = s3.Bucket.fromBucketName(
      this,
      "DeployBucket",
      bucketName
    ) as s3.Bucket;

    // Shared IAM Role
    this.role = new iam.Role(this, "EC2Role", {
      roleName: "TalkToBook-EC2-Role", // IAM Role 이름 지정
      assumedBy: new iam.ServicePrincipal("ec2.amazonaws.com"),
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName(
          "AmazonEC2ContainerRegistryReadOnly"
        ),
        iam.ManagedPolicy.fromAwsManagedPolicyName(
          "CloudWatchAgentServerPolicy"
        ),
        iam.ManagedPolicy.fromAwsManagedPolicyName(
          "AmazonSSMManagedInstanceCore"
        ), // SSM 권한 추가
      ],
      inlinePolicies: {
        S3Access: new iam.PolicyDocument({
          statements: [
            new iam.PolicyStatement({
              effect: iam.Effect.ALLOW,
              actions: ["s3:GetObject"],
              resources: [`${this.deployBucket.bucketArn}/*`],
            }),
          ],
        }),
      },
    });

    // S3 버킷 출력
    new cdk.CfnOutput(this, "DeployBucketName", {
      value: this.deployBucket.bucketName,
      description: "S3 Bucket for deployment files",
    });
  }
}
