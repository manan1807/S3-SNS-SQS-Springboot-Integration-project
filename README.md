# S3-SNS-SQS-Springboot-Integration-project
This is a rective spring project, created with aws services integration.

### SNS to SQS
Here message/data is read from the request and with the help of SNS message is published to the AWS SNS Topic.
An AWS SQS standard queue is created in the aws console, and make it subscribe to the SNS topic.
Now for every published to the SNS topic it gets consumed by Amazon SQS by polling and can retrieved with the help of AmazonSQSAsync class
and then extract the message and start processing.

### S3 to SQS
Here we need to create a bucket in aws s3 console and enable the bucket versioning and in the properties section we need to also attach the event notification 
and select the SQS queue created to handle the S3 events.
After uploading the file to the S3 bucket its metadata sent as event to the configured event listener i.e. Aws SQS Queue.
Then we can read the metadata and extract file name and versionId corresponds to the S3 file uploaded in the bucket.
Now the file can be retrieved from the S3 object and then deserialize it into Java objects for further processing.

### Dependencies required to configured the AmazonS3, AmazonSNS and AmazonSQS client 
	implementation 'org.springframework.cloud:spring-cloud-starter-aws-messaging:2.2.6.RELEASE' // Use the appropriate version
	implementation 'com.amazonaws:aws-java-sdk-sqs:1.12.472' // Use the appropriate version
	implementation 'com.amazonaws:aws-java-sdk-core:1.12.472' // Use the appropriate version
	implementation 'com.amazonaws:aws-java-sdk-sns:1.12.472' // Use the appropriate version
	implementation 'com.amazonaws:aws-java-sdk-s3:1.12.472' // Use the appropriate version
