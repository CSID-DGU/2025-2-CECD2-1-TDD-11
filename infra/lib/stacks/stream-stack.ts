import * as cdk from "aws-cdk-lib";
import * as ec2 from "aws-cdk-lib/aws-ec2";
import * as s3 from "aws-cdk-lib/aws-s3";
import { Construct } from "constructs";

interface StreamStackProps extends cdk.StackProps {
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

export class StreamStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props: StreamStackProps) {
    super(scope, id, props);

    const streamSG = new ec2.SecurityGroup(this, "StreamSG", {
      vpc: props.vpc,
      allowAllOutbound: true,
      description: "Stream service security group",
      securityGroupName: "TalkToBook-Stream-SG",
    });
    streamSG.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(22), "SSH");
    streamSG.addIngressRule(
      ec2.Peer.anyIpv4(),
      ec2.Port.tcp(5672),
      "RabbitMQ AMQP"
    );

    const instance = new ec2.Instance(this, "StreamInstance", {
      instanceType: ec2.InstanceType.of(
        ec2.InstanceClass.T2,
        ec2.InstanceSize.SMALL
      ),
      machineImage: ec2.MachineImage.lookup({
        name: 'ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*',
        owners: ['099720109477'], // Canonical
      }),
      vpc: props.vpc,
      vpcSubnets: {
        availabilityZones: ["ap-northeast-2c"],
      },
      keyPair: ec2.KeyPair.fromKeyPairName(this, 'StreamKeyPair', props.config.keyPairs.stream),
      role: props.role,
      securityGroup: streamSG,
      userData: this.createUserData(props.config),
    });

    // EC2 이름 설정
    cdk.Tags.of(instance).add("Name", "TalkToBook-Stream");

    new cdk.CfnOutput(this, "StreamInstanceIP", {
      value: instance.instancePublicIp,
      description: "Stream EC2 Public IP",
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
