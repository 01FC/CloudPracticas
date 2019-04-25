import boto3
import json
import urllib.parse

s3 = boto3.client('s3')
client = boto3.client('ecs')

def lambda_handler(event, context):
    key = urllib.parse.unquote_plus(event['Records'][0]['s3']['object']['key'], encoding='utf-8')
    bucket = event['Records'][0]['s3']['bucket']['name']
    
    try:
        response = s3.get_object(Bucket=bucket, Key=key)
        if not key.startswith( 'SMALLER' ): 
            response = client.run_task(
                cluster='cluster-p2',
                taskDefinition='task-p2:1',
                overrides={
                    'containerOverrides': [
                        {
                            'name': 'container-p2-ex',
                            'command': [
                                '/app/resize.sh',
                                str(key)
                            ]
                        },
                    ]
                },
                
                launchType='FARGATE',
                networkConfiguration={
                    'awsvpcConfiguration': {
                        'subnets': [
                            'subnet-0674be26b3bd048d4',
                        ],
                        'assignPublicIp': 'ENABLED'
                    }
                }
            )
            return 0
        else:
            return -1
    except Exception as e:
        raise e
        
        
        
        
