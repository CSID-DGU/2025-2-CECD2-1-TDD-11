import * as cdk from "aws-cdk-lib";
import * as ec2 from "aws-cdk-lib/aws-ec2";
import * as elbv2 from "aws-cdk-lib/aws-elasticloadbalancingv2";
import * as targets from "aws-cdk-lib/aws-elasticloadbalancingv2-targets";
import * as s3 from "aws-cdk-lib/aws-s3";
import { Construct } from "constructs";

interface ServerStackProps extends cdk.StackProps {
  vpc: ec2.IVpc;
  role: cdk.aws_iam.Role;
  deployBucket: s3.Bucket;
  config: {
    region: string;
    account: string;
    ecrRegistry: string;
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

export class ServerStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props: ServerStackProps) {
    super(scope, id, props);

    // Server Security Group
    const serverSG = new ec2.SecurityGroup(this, "ServerSG", {
      vpc: props.vpc,
      allowAllOutbound: true,
      description: "Server security group",
      securityGroupName: "TalkToBook-Server-SG",
    });
    serverSG.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(22), "SSH");
    serverSG.addIngressRule(
      ec2.Peer.anyIpv4(),
      ec2.Port.tcp(8080),
      "Server API"
    );
    serverSG.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(6380), "Redis");

    // Server Instance
    const instance = new ec2.Instance(this, "ServerInstance", {
      instanceType: ec2.InstanceType.of(
        ec2.InstanceClass.T2,
        ec2.InstanceSize.MEDIUM
      ),
      machineImage: ec2.MachineImage.lookup({
        name: 'ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*',
        owners: ['099720109477'], // Canonical
      }),
      vpc: props.vpc,
      vpcSubnets: {
        availabilityZones: ["ap-northeast-2a"],
      },
      keyPair: ec2.KeyPair.fromKeyPairName(this, 'ServerKeyPair', props.config.keyPairs.server),
      role: props.role,
      securityGroup: serverSG,
      userData: this.createUserData(props.config),
    });

    // EC2 이름 설정
    cdk.Tags.of(instance).add("Name", "TalkToBook-Server");

    // 탄력적 IP 할당
    const eip = new ec2.CfnEIP(this, "ServerEIP", {
      domain: "vpc",
      instanceId: instance.instanceId,
    });

    // 기존 ALB 참조 (환경변수 사용)
    const existingALB = props.config.albArn
      ? elbv2.ApplicationLoadBalancer.fromLookup(this, "ExistingALB", {
          loadBalancerArn: props.config.albArn,
        })
      : elbv2.ApplicationLoadBalancer.fromLookup(this, "ExistingALB", {
          loadBalancerTags: { Name: props.config.albTagName || "default-alb" },
        });

    // 타겟 그룹 생성
    const targetGroup = new elbv2.ApplicationTargetGroup(
      this,
      "ServerTargetGroup",
      {
        vpc: props.vpc,
        port: 8080,
        protocol: elbv2.ApplicationProtocol.HTTP,
        targetGroupName: "TalkToBook-Server-TG",
        targets: [new targets.InstanceTarget(instance, 8080)],
        healthCheck: {
          path: "/health",
          healthyHttpCodes: "200",
        },
      }
    );

    // 기존 ALB에 리스너 규칙 추가 (선택사항)
    // const listener = elbv2.ApplicationListener.fromLookup(this, 'ExistingListener', {
    //   listenerArn: 'arn:aws:elasticloadbalancing:...'
    // });
    // listener.addTargetGroups('ServerRule', {
    //   targetGroups: [targetGroup],
    //   conditions: [elbv2.ListenerCondition.pathPatterns(['/api/server/*'])],
    //   priority: 100
    // });

    new cdk.CfnOutput(this, "ServerInstanceIP", {
      value: eip.ref,
      description: "Server Elastic IP",
    });

    new cdk.CfnOutput(this, "ServerTargetGroupArn", {
      value: targetGroup.targetGroupArn,
      description: "Server Target Group ARN",
    });
  }

  private createUserData(config: any): ec2.UserData {
    const userData = ec2.UserData.forLinux();
    userData.addCommands(
      "apt-get update",
      "apt-get install -y awscli docker.io",
      'curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose',
      "chmod +x /usr/local/bin/docker-compose",
      "systemctl start docker",
      "systemctl enable docker",
      "usermod -a -G docker ubuntu",
      "mkdir -p /opt/deploy",
      "chown ubuntu:ubuntu /opt/deploy"
    );
    return userData;
  }
}
