package library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.testng.Assert;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputDescription;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.IpRange;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.DescribeLogStreamsRequest;
import com.amazonaws.services.logs.model.DescribeLogStreamsResult;
import com.amazonaws.services.logs.model.GetLogEventsRequest;
import com.amazonaws.services.logs.model.GetLogEventsResult;
import com.amazonaws.services.logs.model.LogStream;
import com.amazonaws.services.logs.model.OutputLogEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteVersionRequest;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.PurgeQueueResult;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageBatchResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import utilities.reusable.FileOperrations;

public class AwsCore {
  

  private static String AWS_account = "default";
  private static AmazonSQS sqsClient;
  private static AmazonDynamoDB dynamodbClient = null;
  private static AmazonS3 s3Client = null;
  private static AmazonEC2 ec2Client = null;
  private static AWSLambda lambdaClient = null;
  private static AmazonKinesis kinesisClient = null;
  private static AWSSecurityTokenService stsClient = null;
  private static AWSLogs logsClient = null;
  private static ItemCollection<QueryOutcome> dbresult = null;
  private static List<OutputLogEvent> logMessages = new ArrayList<>();
  public static String kinesismilli = null;
  private static BasicSessionCredentials stsCredentials = null;
  private static boolean sts_IND = false;
  private static List<IpPermission> ipPermList = new ArrayList<>();
  private static HashMap<String, Condition> scanFilter = new HashMap<>();
  private static List<Map<String, AttributeValue>> dynamoResult = new ArrayList<>();
  private static SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.dd.HH.mm.ss" );

  // *****************
  // AWS Core Methods
  // *****************
  public static void AWS_setSessionAccount( String account ) {
    if ( account.toLowerCase().contentEquals( "caracaltest" ) )
      AWS_account = "caracaltest";
    else
      if ( account.toLowerCase().contentEquals( "locationtest" ) )
        AWS_account = "locationtest";
      else
        AWS_account = "default";
    sts_IND = false;
  }

  public static void AWS_setSessionCredentials() {
    Properties prop = new Properties();
    try {
      InputStream readProp = AwsCore.class.getClassLoader().getResourceAsStream(
          "configs/common/AWSconfig.properties" );
      prop.load( readProp );
      System.out.println( "Setting AWS credentials for account ---> " + AWS_account.toUpperCase() );
      System.setProperty( "aws.accessKeyId", prop.getProperty( AWS_account + ".accessKey" ) );
      System.setProperty( "aws.secretKey", prop.getProperty( AWS_account + ".secretKey" ) );
      System.setProperty( "aws.region", prop.getProperty( AWS_account + ".region" ) );
      if ( prop.containsKey( AWS_account + ".switchrole.DETAILS" ) == true ) {
        sts_IND = true;
        String[] roleDetails = prop.getProperty( AWS_account + ".switchrole.DETAILS" ).split( "@@" );
        STS_switchRole( roleDetails[ 0 ], roleDetails[ 1 ], System.getProperty( "aws.region" ) );
        System.out.println( "Switched role as defined in config ---> " + roleDetails[ 1 ].toUpperCase() );
      }
    } catch ( IOException e ) {
      Assert.fail( "Unable to set AWS session credentials" );
    }
  }

  public static void AWS_serviceConnect( String service ) {
    if ( sts_IND == false ) {
      switch ( service.toLowerCase() ) {
      case "sqs":
        sqsClient = AmazonSQSClientBuilder.defaultClient();
        break;
      case "dynamodb":
        dynamodbClient = AmazonDynamoDBClientBuilder.defaultClient();
        break;
      case "s3":
        s3Client = AmazonS3ClientBuilder.defaultClient();
        break;
      case "ec2":
        ec2Client = AmazonEC2ClientBuilder.defaultClient();
        break;
      case "lambda":
        lambdaClient = AWSLambdaClientBuilder.defaultClient();
        break;
      case "kinesis":
        kinesisClient = AmazonKinesisClientBuilder.defaultClient();
        break;
      case "logs":
        logsClient = AWSLogsClientBuilder.defaultClient();
        break;
      default:
        Assert.fail( "Service unavailable for testing in framework." );
      }
      System.out.println( "\n" + service + " service started successfully." );
    } else {
      switch ( service.toLowerCase() ) {
      case "sqs":
        sqsClient = AmazonSQSClientBuilder.standard().withCredentials( new AWSStaticCredentialsProvider(
            stsCredentials ) ).build();
        break;
      case "dynamodb":
        dynamodbClient = AmazonDynamoDBClientBuilder.standard().withCredentials( new AWSStaticCredentialsProvider(
            stsCredentials ) ).build();
        break;
      case "s3":
        s3Client = AmazonS3ClientBuilder.standard().withCredentials( new AWSStaticCredentialsProvider(
            stsCredentials ) ).build();
        break;
      case "ec2":
        ec2Client = AmazonEC2ClientBuilder.standard().withCredentials( new AWSStaticCredentialsProvider(
            stsCredentials ) ).build();
        break;
      case "lambda":
        lambdaClient = AWSLambdaClientBuilder.standard().withCredentials( new AWSStaticCredentialsProvider(
            stsCredentials ) ).build();
        break;
      case "kinesis":
        kinesisClient = AmazonKinesisClientBuilder.standard().withCredentials( new AWSStaticCredentialsProvider(
            stsCredentials ) ).build();
        break;
      case "logs":
        logsClient = AWSLogsClientBuilder.standard().withCredentials( new AWSStaticCredentialsProvider(
            stsCredentials ) ).build();
        break;
      default:
        Assert.fail( "Service unavailable for testing in framework." );
      }
      System.out.println( "\n" + service + " service started successfully." );
    }
  }

  public static void AWS_serviceDisconnect( String service ) {
    if ( ( sqsClient != null ) || ( dynamodbClient != null ) || ( s3Client != null ) || ( ec2Client != null )
        || ( lambdaClient != null ) || ( kinesisClient != null ) || ( logsClient ) != null ) {
      // System.out.println( "\nTrying to close " + service + " connection..." );
      switch ( service.toLowerCase() ) {
      case "sqs":
        sqsClient.shutdown();
        break;
      case "dynamodb":
        dynamodbClient.shutdown();
        break;
      case "s3":
        s3Client.shutdown();
        break;
      case "ec2":
        ec2Client.shutdown();
        break;
      case "lambda":
        lambdaClient.shutdown();
        break;
      case "kinesis":
        kinesisClient.shutdown();
        break;
      case "logs":
        logsClient.shutdown();
        break;
      }
      System.out.println( service + " service closed successfully.\n" );
    }
  }

  public static String AWS_getRequestId( String service, AmazonWebServiceRequest request ) {
    String requestId = null;
    switch ( service.toLowerCase() ) {
    case "sqs":
      requestId = sqsClient.getCachedResponseMetadata( request ).getRequestId().toString();
      break;
    case "dynamodb":
      requestId = dynamodbClient.getCachedResponseMetadata( request ).getRequestId().toString();
      break;
    case "s3":
      requestId = s3Client.getCachedResponseMetadata( request ).getRequestId().toString();
      break;
    case "ec2":
      requestId = ec2Client.getCachedResponseMetadata( request ).getRequestId().toString();
      break;
    case "lambda":
      requestId = lambdaClient.getCachedResponseMetadata( request ).getRequestId().toString();
      break;
    case "kinesis":
      requestId = kinesisClient.getCachedResponseMetadata( request ).getRequestId().toString();
      break;
    case "logs":
      requestId = logsClient.getCachedResponseMetadata( request ).getRequestId().toString();
      break;
    }
    if ( requestId != null )
      return requestId;
    else {
      Assert.fail( "Request Id not found. Please check service name and request " );
      return requestId;
    }
  }

  // **************************************
  // AWS Security Token Service (STS) Methods
  // **************************************
  public static void STS_switchRole( String roleARN, String roleSessionName, String region ) {
    try {
      stsClient = AWSSecurityTokenServiceClientBuilder.defaultClient();
      System.out.println( "Using STS client to switch role as defined in configs." );
      AssumeRoleRequest request = new AssumeRoleRequest().withRoleArn( roleARN ).withRoleSessionName( roleSessionName );
      AssumeRoleResult response = stsClient.assumeRole( request );
      Credentials STS_creds = response.getCredentials();
      stsCredentials = new BasicSessionCredentials( STS_creds.getAccessKeyId(), STS_creds.getSecretAccessKey(),
          STS_creds.getSessionToken() );
      stsClient.shutdown();
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "sqs" );
      Assert.fail(
          "Your request made it to Amazon STS, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  // **************************************
  // AWS Simple Queue Service (SQS) Methods
  // **************************************
  public static void SQS_listAllQueues() {
    try {
      ListQueuesResult lq_result = sqsClient.listQueues();
      System.out.println( "SQS Queue are:" );
      for ( String url: lq_result.getQueueUrls() )
        System.out.println( url );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "sqs" );
      Assert.fail(
          "Your request made it to Amazon SQS, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static String SQS_returnQueueURL( String QueueName ) {
    String QURL = null;
    try {
      ListQueuesResult lq_result = sqsClient.listQueues();
      for ( String url: lq_result.getQueueUrls() )
        if ( !url.toLowerCase().contains( QueueName.toLowerCase() ) )
          continue;
        else
          QURL = url;
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please validate SQS service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "sqs" );
      Assert.fail(
          "Your request made it to Amazon SQS, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    if ( QURL == null ) {
      Assert.fail( "Queue URL not found for given Queue name" );
      return QURL;
    } else
      return QURL;
  }

  public static void SQS_purgeQueueMessages( String QueueName ) {
    String purgeQURL = SQS_returnQueueURL( QueueName );
    try {
      PurgeQueueResult result = sqsClient.purgeQueue( new PurgeQueueRequest().withQueueUrl( purgeQURL ) );
      if ( result.toString().contentEquals( "{}" ) )
        System.out.println( "Purge queue request submitted successfully." );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please validate SQS service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "sqs" );
      Assert.fail(
          "Your request made it to Amazon SQS, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void SQS_receiveAllQueueMessages( String QueueName ) {
    String QURL = SQS_returnQueueURL( QueueName );
    List<Message> messages = null;
    try {
      ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest( QURL ).withMaxNumberOfMessages( 10 )
          .withWaitTimeSeconds( 20 );
      messages = sqsClient.receiveMessage( receiveMessageRequest ).getMessages();
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please validate SQS service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "sqs" );
      Assert.fail(
          "Your request made it to Amazon SQS, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    if ( messages.isEmpty() )
      System.out.println( "Message list empty" );
    else {
      System.out.println( "\nMessages recieved from queue '" + QueueName + "' are:" );
      System.out.println( "INDEX\tMESSAGEID\t\t\t\tMESSAGEBODY" );
      int i = 0;
      for ( Message message: messages ) {
        System.out.println( ( i + 1 ) + "\t" + message.getMessageId() + "\t" + message.getBody() );
        i++;
      }
    }
  }

  public static List<Message> SQS_returnAllQueueMessages( String QueueName ) {
    String QURL = SQS_returnQueueURL( QueueName );
    List<Message> messages = null;
    try {
      ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest( QURL ).withMaxNumberOfMessages( 10 )
          .withWaitTimeSeconds( 20 );
      messages = sqsClient.receiveMessage( receiveMessageRequest ).getMessages();
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please validate SQS service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "sqs" );
      Assert.fail(
          "Your request made it to Amazon SQS, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    if ( messages.isEmpty() ) {
      Assert.fail( "Message list empty" );
      return messages;
    } else
      return messages;
  }

  public static String SQS_getSendMessageGroupID() {
    Properties prop = new Properties();
    try {
      InputStream readProp = AwsCore.class.getClassLoader().getResourceAsStream(
          "configs/common/AWSconfig.properties" );
      prop.load( readProp );
    } catch ( IOException e ) {
      Assert.fail( "I/O Exception found while trying to read AWS configurations." );
    }
    return prop.getProperty( AWS_account + ".SQSmessageGroupID" );
  }

  public static void SQS_sendQueueSingleMessage( String QueueName, String messagebody ) {
    String QURL = SQS_returnQueueURL( QueueName );
    SendMessageResult sendMessageResult = null;
    try {
      SendMessageRequest sendMessageRequest = new SendMessageRequest( QURL, messagebody ).withMessageDeduplicationId(
          String.valueOf( utilities.reusable.RandomNumberGenerator.generateRandomDigits( 7 ) ) ).withMessageGroupId(
              SQS_getSendMessageGroupID() );
      sendMessageResult = sqsClient.sendMessage( sendMessageRequest );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please validate SQS service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "sqs" );
      Assert.fail(
          "Your request made it to Amazon SQS, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    if ( sendMessageResult.getMessageId() != null )
      System.out.println( "Message sent successfully to queue '" + QueueName + "' with message ID as '"
          + sendMessageResult.getMessageId() + "'" );
    else
      Assert.fail( "Message not sent to given given queue." );
  }

  // Split message using delimiter and return list of batch request entries.
  public static List<SendMessageBatchRequestEntry> SQS_setBatchRequestEntries( String multimessagebody ) {
    List<SendMessageBatchRequestEntry> entries = new ArrayList<>();
    String[] textmessagearray = multimessagebody.split( "//" );
    int i = 1;
    for ( String textmessage: textmessagearray ) {
      entries.add( new SendMessageBatchRequestEntry().withId( "BatchRequestID_" + ( i ) ).withMessageDeduplicationId(
          String.valueOf( utilities.reusable.RandomNumberGenerator.generateRandomDigits( 7 ) ) ).withMessageBody(
              textmessage ).withMessageGroupId( SQS_getSendMessageGroupID() ) );
      i++;
    }
    return entries;
  }

  public static void SQS_sendQueueMultipleMessage( String QueueName, String multimessagebody ) {
    try {
      String QURL = SQS_returnQueueURL( QueueName );
      SendMessageBatchRequest sendMessageBatchRequest = new SendMessageBatchRequest( QURL ).withEntries(
          SQS_setBatchRequestEntries( multimessagebody ) );
      SendMessageBatchResult sendMessageBatchResult = sqsClient.sendMessageBatch( sendMessageBatchRequest );
      if ( !sendMessageBatchResult.getFailed().isEmpty() )
        Assert.fail( "Batch messages request not successful." );
      else
        System.out.println( "Batch messages request submitted successfully." );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please validate SQS service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "sqs" );
      Assert.fail(
          "Your request made it to Amazon SQS, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void SQS_deleteQueueMessage( String QueueName, String messagebody ) {
    String receiptHandle = null;
    String QURL = SQS_returnQueueURL( QueueName );
    List<Message> messages = SQS_returnAllQueueMessages( QueueName );
    for ( Message message: messages )
      if ( message.getBody().toLowerCase().contains( messagebody.toLowerCase() ) )
        receiptHandle = message.getReceiptHandle();
    try {
      if ( receiptHandle != null )
        sqsClient.deleteMessage( new DeleteMessageRequest( QURL, receiptHandle ) );
      else
        Assert.fail( "Message not found matching given message body." );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please validate SQS service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "sqs" );
      Assert.fail(
          "Your request made it to Amazon SQS, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void SQS_validateDeadLetterQueue( String SourceQueueName, String DeadQueueName ) {
    String SURL = SQS_returnQueueURL( SourceQueueName );
    String DURL = SQS_returnQueueURL( DeadQueueName );
    String redrivePolicy = null;
    String queueArn = null;
    try {
      GetQueueAttributesResult sourceQattrs = sqsClient.getQueueAttributes( new GetQueueAttributesRequest( SURL )
          .withAttributeNames( "RedrivePolicy" ) );
      redrivePolicy = sourceQattrs.getAttributes().get( "RedrivePolicy" );
      GetQueueAttributesResult deadQattrs = sqsClient.getQueueAttributes( new GetQueueAttributesRequest( DURL )
          .withAttributeNames( "QueueArn" ) );
      queueArn = deadQattrs.getAttributes().get( "QueueArn" );
      if ( !redrivePolicy.contains( queueArn ) )
        Assert.fail( "Source queue redrivePolicy does not contain dead queue Arn" );
      else {
        // pending code here
      }
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please validate SQS service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "sqs" );
      Assert.fail(
          "Your request made it to Amazon SQS, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }
  // **************************************
  // AWS Simple Storage Service (S3) Methods
  // **************************************

  public static void S3_createBucket( String bucketName ) {
    try {
      if ( s3Client.doesBucketExistV2( bucketName ) ) {
        Assert.fail( "Bucket name is not available." + " Try again with a different Bucket name." );
        return;
      }
      s3Client.createBucket( bucketName );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "s3" );
      Assert.fail(
          "Your request made it to Amazon S3, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void S3_listAllBuckets() {
    try {
      List<Bucket> buckets = s3Client.listBuckets();
      System.out.println( "All available S3 buckets are:" );
      for ( Bucket bucket: buckets )
        System.out.println( bucket.getName() );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "s3" );
      Assert.fail(
          "Your request made it to Amazon S3, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static List<Bucket> S3_returnBucketList() {
    List<Bucket> buckets = new ArrayList<>();
    try {
      buckets = s3Client.listBuckets();
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "s3" );
      Assert.fail(
          "Your request made it to Amazon S3, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    if ( buckets.isEmpty() ) {
      Assert.fail( "Bucket list is empty" );
      return null;
    } else
      return buckets;
  }

  public static Bucket S3_returnGivenBucket( String bucketName ) {
    List<Bucket> buckets = new ArrayList<>();
    Bucket result = null;
    try {
      buckets = s3Client.listBuckets();
      for ( Bucket bucket: buckets )
        if ( bucket.getName().toLowerCase().contains( bucketName.toLowerCase() ) )
          result = bucket;
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "s3" );
      Assert.fail(
          "Your request made it to Amazon S3, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    if ( result == null ) {
      Assert.fail( "Bucket not found with given name." );
      return null;
    } else
      return result;
  }

  public static void S3_deleteNonVersionedBucket( String bucketName ) {
    String actualBucketNamme = S3_returnGivenBucket( bucketName ).getName();
    try {
      List<Bucket> buckets = s3Client.listBuckets();
      Boolean ValidateOwner = false;
      Boolean BucketExist = false;
      if ( buckets.isEmpty() )
        Assert.fail( "Bucket list is empty" );
      for ( Bucket bucket: buckets )
        if ( bucket.getName().toLowerCase().contains( actualBucketNamme.toLowerCase() ) ) {
          System.out.println( "Bucket found with given name." );
          BucketExist = true;
          if ( s3Client.getS3AccountOwner() == bucket.getOwner() ) {
            System.out.println( "S3client owner and Bucker owner are same." );
            ValidateOwner = true;
            ObjectListing objectListing = s3Client.listObjects( actualBucketNamme );
            while ( true ) {
              Iterator<S3ObjectSummary> objIter = objectListing.getObjectSummaries().iterator();
              while ( objIter.hasNext() )
                s3Client.deleteObject( actualBucketNamme, objIter.next().getKey() );
              if ( objectListing.isTruncated() )
                objectListing = s3Client.listNextBatchOfObjects( objectListing );
              else
                break;
            }
            s3Client.deleteBucket( bucket.getName() );
          }
        }
      if ( BucketExist == false )
        Assert.fail( "Bucket not found with given name." );
      if ( ValidateOwner == false )
        Assert.fail( "S3 client owner and bucket owner are different. Bucket cannot be deleted." );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "s3" );
      Assert.fail(
          "Your request made it to Amazon S3, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void S3_deleteVersionedBucket( String bucketName ) {
    String actualBucketNamme = S3_returnGivenBucket( bucketName ).getName();
    try {
      List<Bucket> buckets = s3Client.listBuckets();
      Boolean ValidateOwner = false;
      Boolean BucketExist = false;
      if ( buckets.isEmpty() )
        Assert.fail( "Bucket list is empty" );
      for ( Bucket bucket: buckets )
        if ( bucket.getName().toLowerCase().contains( actualBucketNamme.toLowerCase() ) ) {
          System.out.println( "Bucket found with given name." );
          BucketExist = true;
          if ( s3Client.getS3AccountOwner() == bucket.getOwner() ) {
            System.out.println( "S3client owner and Bucker owner are same." );
            ValidateOwner = true;
            VersionListing versionList = s3Client.listVersions( new ListVersionsRequest().withBucketName(
                actualBucketNamme ) );
            while ( true ) {
              Iterator<S3VersionSummary> versionIter = versionList.getVersionSummaries().iterator();
              while ( versionIter.hasNext() ) {
                S3VersionSummary vs = versionIter.next();
                s3Client.deleteVersion( actualBucketNamme, vs.getKey(), vs.getVersionId() );
              }
              if ( versionList.isTruncated() )
                versionList = s3Client.listNextBatchOfVersions( versionList );
              else
                break;
            }
            s3Client.deleteBucket( bucket.getName() );
          }
        }
      if ( BucketExist == false )
        Assert.fail( "Bucket not found with given name." );
      if ( ValidateOwner == false )
        Assert.fail( "S3 client owner and bucket owner are different. Bucket cannot be deleted." );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "s3" );
      Assert.fail(
          "Your request made it to Amazon S3, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void S3_listObjectsGivenBucket( String bucketName ) {
    String actualBucketName = S3_returnGivenBucket( bucketName ).getName();
    try {
      ObjectListing objectListing = s3Client.listObjects( actualBucketName );
      System.out.println( "Object of given bucket are:" );
      for ( S3ObjectSummary objectsummary: objectListing.getObjectSummaries() )
        System.out.println( objectsummary.getKey() );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "s3" );
      Assert.fail(
          "Your request made it to Amazon S3, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void S3_uloadObjectGivenBucket( String bucketName, String uploadPath, String localfilePath ) {
    String uploadBucketNamme = S3_returnGivenBucket( bucketName ).getName();
    String fileName = FileOperrations.returnNamefromPath( localfilePath );
    String destinationPath = uploadPath.concat( fileName );
    File uploadFile = FileOperrations.getFileFromDir( localfilePath );
    try {
      PutObjectRequest putObjectRequest = new PutObjectRequest( uploadBucketNamme, destinationPath, uploadFile );
      PutObjectResult putObjectResult = s3Client.putObject( putObjectRequest );
      if ( !putObjectResult.getETag().isEmpty() )
        System.out.println( "Object upload to given bucket successfully." );
      else
        Assert.fail( "Object not uploaded to given bucket." );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "s3" );
      Assert.fail(
          "Your request made it to Amazon S3, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void S3_copyObjectGivenBucket( String SourcebucketName, String SourceObjectKey, String TargetBucketName,
      String TargetObjectKey ) {
    String actualSourcebucketName = S3_returnGivenBucket( SourcebucketName ).getName();
    String actualSourceObjectKey = null;
    String actualTargetbucketName = S3_returnGivenBucket( TargetBucketName ).getName();
    try {
      ObjectListing objectListing = s3Client.listObjects( actualSourcebucketName );
      for ( S3ObjectSummary objectsummary: objectListing.getObjectSummaries() )
        if ( !objectsummary.getKey().toLowerCase().contentEquals( SourceObjectKey ) )
          Assert.fail( "''" + SourcebucketName + "' bucket does not contain any object with given Objectkey" );
        else
          actualSourceObjectKey = objectsummary.getKey();
      if ( actualSourceObjectKey != null ) {
        CopyObjectRequest copyObjRequest = new CopyObjectRequest( actualSourcebucketName, actualSourceObjectKey,
            actualTargetbucketName, TargetObjectKey );
        s3Client.copyObject( copyObjRequest );
      }
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "s3" );
      Assert.fail(
          "Your request made it to Amazon S3, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static boolean S3_deleteObjectNonVersionedBucket( String bucketName, String objectKey ) {
    boolean status = false;
    String actualBucketName = S3_returnGivenBucket( bucketName ).getName();
    try {
      s3Client.deleteObject( new DeleteObjectRequest( actualBucketName, objectKey ) );
      status = true;
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "s3" );
      Assert.fail(
          "Your request made it to Amazon S3, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    return status;
  }

  public static boolean S3_deleteObjectVersionedBucket( String bucketName, String objectKey, String versionID ) {
    boolean status = false;
    String actualBucketName = S3_returnGivenBucket( bucketName ).getName();
    try {
      String bucketVersionStatus = s3Client.getBucketVersioningConfiguration( actualBucketName ).getStatus();
      if ( !bucketVersionStatus.equals( BucketVersioningConfiguration.ENABLED ) )
        Assert.fail( "Bucket is not versioning-enabled." );
      else {
        s3Client.deleteVersion( new DeleteVersionRequest( actualBucketName, objectKey, versionID ) );
        System.out.printf( "Object %s, version %s deleted\n", objectKey, versionID );
        status = true;
      }
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "s3" );
      Assert.fail(
          "Your request made it to Amazon S3, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    return status;
  }
  // **************************************
  // AWS Elastic Cloud Compute(EC2) Methods
  // **************************************

  public static void EC2_createSecurityGroup( String groupName, String groupDesc ) {
    try {
      CreateSecurityGroupRequest request = new CreateSecurityGroupRequest().withGroupName( groupName ).withDescription(
          groupDesc );
      CreateSecurityGroupResult response = ec2Client.createSecurityGroup( request );
      if ( !response.getGroupId().isEmpty() )
        System.out.println( "Security group created successfully." );
      else
        Assert.fail( "Unable to create security group." );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "ec2" );
      Assert.fail(
          "Your request made it to Amazon EC2, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void EC2_createSecurityGroupwithVPC( String groupName, String groupDesc, String vpcID ) {
    try {
      CreateSecurityGroupRequest request = new CreateSecurityGroupRequest().withGroupName( groupName ).withDescription(
          groupDesc ).withVpcId( vpcID );
      CreateSecurityGroupResult response = ec2Client.createSecurityGroup( request );
      if ( !response.getGroupId().isEmpty() )
        System.out.println( "Security group created successfully." );
      else
        Assert.fail( "Unable to create security group." );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "ec2" );
      Assert.fail(
          "Your request made it to Amazon EC2, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void EC2_addIpPermList( String ipRange, String protocolType, String toPort, String fromPort ) {
    try {
      IpRange ip_range = new IpRange().withCidrIp( ipRange );
      IpPermission ip_perm = new IpPermission().withIpProtocol( protocolType ).withToPort( Integer.getInteger(
          toPort ) ).withFromPort( Integer.getInteger( fromPort ) ).withIpv4Ranges( ip_range );
      ipPermList.add( ip_perm );
      System.out.println( "Ip permission list updated successfully" );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "ec2" );
      Assert.fail(
          "Your request made it to Amazon EC2, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void EC2_configureSecuritygroup( String groupName ) {
    try {
      AuthorizeSecurityGroupIngressRequest request = new AuthorizeSecurityGroupIngressRequest().withGroupName(
          groupName ).withIpPermissions( ipPermList );
      ec2Client.authorizeSecurityGroupIngress( request );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "ec2" );
      Assert.fail(
          "Your request made it to Amazon EC2, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    } finally {
      ipPermList.clear();
    }
  }

  public static boolean EC2_createKeyPair( String keyName ) {
    boolean status = false;
    try {
      CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest().withKeyName( "baeldung-key-pair" );
      CreateKeyPairResult createKeyPairResult = ec2Client.createKeyPair( createKeyPairRequest );
      String material = createKeyPairResult.getKeyPair().getKeyMaterial();
      Timestamp timestamp = new Timestamp( System.currentTimeMillis() );
      FileOperrations.createFileWithContent( "/AWS/EC2/generatedKeys", "key_" + sdf.format( timestamp ), material );
      status = true;
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "ec2" );
      Assert.fail(
          "Your request made it to Amazon EC2, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    return status;
  }

  public static boolean EC2_deleteKeyPair( String keyName ) {
    boolean status = false;
    try {
      DeleteKeyPairRequest request = new DeleteKeyPairRequest().withKeyName( keyName );
      ec2Client.deleteKeyPair( request );
      status = true;
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "ec2" );
      Assert.fail(
          "Your request made it to Amazon EC2, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    return status;
  }

  public static String EC2_createInstance( String imageID, String instanceType, String keyName, String securityGroup ) {
    String yourInstanceId = null;
    try {
      RunInstancesRequest runInstancesRequest = new RunInstancesRequest().withImageId( imageID ).withInstanceType(
          instanceType ).withKeyName( keyName ).withMinCount( 1 ).withMaxCount( 1 ).withSecurityGroups( securityGroup );
      yourInstanceId = ec2Client.runInstances( runInstancesRequest ).getReservation().getInstances().get( 0 )
          .getInstanceId();
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "ec2" );
      Assert.fail(
          "Your request made it to Amazon EC2, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    if ( yourInstanceId == null ) {
      Assert.fail( "Unable to get instace ID from EC2 client." );
      return null;
    } else
      return yourInstanceId;
  }

  public static boolean EC2_startInstance( String instanceID ) {
    boolean status = false;
    try {
      StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds( instanceID );
      ec2Client.startInstances( startInstancesRequest );
      status = true;
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "ec2" );
      Assert.fail(
          "Your request made it to Amazon EC2, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    return status;
  }

  public static boolean EC2_stopInstance( String instanceID ) {
    boolean status = false;
    try {
      StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds( instanceID );
      ec2Client.stopInstances( stopInstancesRequest );
      status = true;
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "ec2" );
      Assert.fail(
          "Your request made it to Amazon EC2, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    return status;
  }

  public static boolean EC2_rebootInstance( String instanceID ) {
    boolean status = false;
    try {
      RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds( instanceID );
      ec2Client.rebootInstances( request );
      status = true;
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "EC2" );
      Assert.fail(
          "Your request made it to Amazon EC2, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    return status;
  }

  public static void EC2_getAllInstanceDetails() {
    try {
      boolean done = false;
      DescribeInstancesRequest request = new DescribeInstancesRequest();
      while ( !done ) {
        DescribeInstancesResult response = ec2Client.describeInstances( request );
        for ( Reservation reservation: response.getReservations() ) {
          for ( Instance instance: reservation.getInstances() ) {
            System.out.printf( "Found instance with id %s, " + "AMI %s, " + "type %s, " + "state %s "
                + "and monitoring state %s", instance.getInstanceId(), instance.getImageId(), instance
                    .getInstanceType(), instance.getState().getName(), instance.getMonitoring().getState() );
          }
        }
        request.setNextToken( response.getNextToken() );
        if ( response.getNextToken() == null ) {
          done = true;
        }
      }
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "ec2" );
      Assert.fail(
          "Your request made it to Amazon EC2, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void EC2_getInstanceDetails( String instanceID ) {
    try {
      boolean status = false;
      boolean done = false;
      DescribeInstancesRequest request = new DescribeInstancesRequest();
      while ( !done ) {
        DescribeInstancesResult response = ec2Client.describeInstances( request );
        for ( Reservation reservation: response.getReservations() ) {
          for ( Instance instance: reservation.getInstances() ) {
            if ( instance.getInstanceId().toLowerCase().contentEquals( instanceID.toLowerCase() ) ) {
              System.out.printf( "Found instance with id %s, " + "AMI %s, " + "type %s, " + "state %s "
                  + "and monitoring state %s", instance.getInstanceId(), instance.getImageId(), instance
                      .getInstanceType(), instance.getState().getName(), instance.getMonitoring().getState() );
              status = true;
            }
          }
        }
        request.setNextToken( response.getNextToken() );
        if ( response.getNextToken() == null ) {
          done = true;
        }
      }
      if ( status == false )
        Assert.fail( "Instance not found with given ID." );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "ec2" );
      Assert.fail(
          "Your request made it to Amazon EC2, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  // ********************
  // AWS DynamoDB Methods
  // ********************
  public static void Dynamo_listAllTables() {
    ListTablesRequest request;
    boolean more_tables = true;
    String last_name = null;
    try {
      while ( more_tables ) {
        if ( last_name == null ) {
          request = new ListTablesRequest().withLimit( 10 );
        } else {
          request = new ListTablesRequest().withLimit( 10 ).withExclusiveStartTableName( last_name );
        }
        ListTablesResult table_list = dynamodbClient.listTables( request );
        List<String> table_names = table_list.getTableNames();
        if ( table_names.size() > 0 ) {
          for ( String table_name: table_names ) {
            System.out.format( "* %s\n", table_name );
          }
        } else
          Assert.fail( "No tables found!" );
        last_name = table_list.getLastEvaluatedTableName();
        if ( last_name == null ) {
          more_tables = false;
        }
      }
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "dynamodb" );
      Assert.fail(
          "Your request made it to Amazon DynamoDB, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void Dynamo_getTableDetails( String tableName ) {
    try {
      TableDescription table_info = dynamodbClient.describeTable( tableName ).getTable();
      if ( table_info != null ) {
        System.out.format( "Table name  : %s\n", table_info.getTableName() );
        System.out.format( "Table ARN   : %s\n", table_info.getTableArn() );
        System.out.format( "Status      : %s\n", table_info.getTableStatus() );
        System.out.format( "Item count  : %d\n", table_info.getItemCount().longValue() );
        System.out.format( "Size (bytes): %d\n", table_info.getTableSizeBytes().longValue() );
        ProvisionedThroughputDescription throughput_info = table_info.getProvisionedThroughput();
        System.out.println( "Throughput" );
        System.out.format( "  Read Capacity : %d\n", throughput_info.getReadCapacityUnits().longValue() );
        System.out.format( "  Write Capacity: %d\n", throughput_info.getWriteCapacityUnits().longValue() );
        List<AttributeDefinition> attributes = table_info.getAttributeDefinitions();
        System.out.println( "Attributes" );
        for ( AttributeDefinition a: attributes ) {
          System.out.format( "  %s (%s)\n", a.getAttributeName(), a.getAttributeType() );
        }
      }
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "dynamodb" );
      Assert.fail(
          "Your request made it to Amazon DynamoDB, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void Dynamo_scanAllItems( String tableName ) {
    try {
      ScanRequest scanRequest = new ScanRequest( tableName );
      ScanResult scanResult = dynamodbClient.scan( scanRequest );
      dynamoResult = scanResult.getItems();
      Dynamo_printQueryResult();
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "dynamodb" );
      Assert.fail(
          "Your request made it to Amazon DynamoDB, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void Dynamo_setQueryCondition( String key, String value, String DataType ) {
    if ( DataType.toLowerCase().contains( "null" ) ) {
      Condition condition = new Condition().withComparisonOperator( ComparisonOperator.EQ.toString() )
          .withAttributeValueList( new AttributeValue().withNULL( Boolean.valueOf( value.toLowerCase() ) ) );
      scanFilter.put( key, condition );
    } else
      if ( DataType.toLowerCase().contentEquals( "s" ) || DataType.toLowerCase().contentEquals( "string" ) ) {
        Condition condition = new Condition().withComparisonOperator( ComparisonOperator.EQ.toString() )
            .withAttributeValueList( new AttributeValue().withS( value ) );
        scanFilter.put( key, condition );
      } else
        if ( DataType.toLowerCase().contentEquals( "i" ) || DataType.toLowerCase().contentEquals( "integer" )
            || DataType.toLowerCase().contentEquals( "int" ) ) {
          Condition condition = new Condition().withComparisonOperator( ComparisonOperator.EQ.toString() )
              .withAttributeValueList( new AttributeValue().withN( value ) );
          scanFilter.put( key, condition );
        }
  }

  public static void Dynamo_executeScan( String tableName ) {
    if ( scanFilter.size() != 0 ) {
      try {
        ScanRequest scanRequest = new ScanRequest( tableName ).withScanFilter( scanFilter );
        ScanResult scanResult = dynamodbClient.scan( scanRequest );
        dynamoResult = scanResult.getItems();
      } catch ( NullPointerException e ) {
        Assert.fail( "NullPointerException found. Please verify service connection." );
      } catch ( AmazonServiceException ase ) {
        AWS_serviceDisconnect( "dynamodb" );
        Assert.fail(
            "Your request made it to Amazon DynamoDB, but was rejected with an error response for some reason.\n Error Message:    "
                + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
      } catch ( AmazonClientException ace ) {
        Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
            .getMessage() );
      }
    } else
      Assert.fail( "Query filer empty." );
  }

  public static void Dynamo_executeQueryPartAndSort( String tableName, String partitionKey, String partitionKeyVal,
      String sortKey, String sortKeyVal ) {
    QuerySpec querySpec = null;
    DynamoDB dynamoDB = new DynamoDB( dynamodbClient );
    Table table = dynamoDB.getTable( tableName );
    try {
      querySpec = new QuerySpec().withKeyConditionExpression( "#n_pKey = :v_pKey and #n_sKey = :v_sKey" ).withNameMap(
          new NameMap().with( "#n_pKey", partitionKey ).with( "#n_sKey", sortKey ) ).withValueMap( new ValueMap()
              .withString( ":v_pKey", partitionKeyVal ).withString( ":v_sKey", sortKeyVal ) ).withConsistentRead(
                  true );
      dbresult = table.query( querySpec );
      System.out.println( "Query " + tableName.toLowerCase() + " table where " + partitionKey + "=" + partitionKeyVal
          + " and " + sortKey + "=" + sortKeyVal );
      Iterator<Item> iterator = dbresult.iterator();
      while ( iterator.hasNext() ) {
        System.out.println( iterator.next().toJSONPretty() );
      }
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "dynamodb" );
      Assert.fail(
          "Your request made it to Amazon DynamoDB, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void Dynamo_executeQueryPartOnly( String tableName, String partitionKey, String partitionKeyVal ) {
    int recordCount = 0;
    QuerySpec querySpec = null;
    DynamoDB dynamoDB = new DynamoDB( dynamodbClient );
    Table table = dynamoDB.getTable( tableName );
    try {
      querySpec = new QuerySpec().withKeyConditionExpression( partitionKey + " = :v_pKey " ).withValueMap(
          new ValueMap().withString( ":v_pKey", partitionKeyVal ) ).withConsistentRead( true );
      dbresult = table.query( querySpec );
      Iterator<Item> iterator = dbresult.iterator();
      System.out.println( "Result for query to " + tableName.toLowerCase() + " table where " + partitionKey + "="
          + partitionKeyVal );
      while ( iterator.hasNext() ) {
        recordCount = dbresult.getAccumulatedItemCount();
        System.out.println( iterator.next().toJSONPretty() );
      }
      System.out.println( "Total records returned in result = " + recordCount );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "dynamodb" );
      Assert.fail(
          "Your request made it to Amazon DynamoDB, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static boolean Dynamo_validateDbResult( String key, String expectedValue ) {
    System.out.println( "Looking for results where " + key + "=" + expectedValue );
    boolean status = false;
    Iterator<Item> iterator = dbresult.iterator();
    while ( iterator.hasNext() ) {
      if ( iterator.next().getString( key ).toLowerCase().contains( expectedValue.toLowerCase() ) ) {
        status = true;
        break;
      }
    }
    return status;
  }

  public static void Dynamo_printQueryResult() {
    int i = 1;
    System.out.println( "Total number of items = " + dynamoResult.size() );
    for ( Map<String, AttributeValue> object: dynamoResult ) {
      System.out.println( "\nItem " + i + ":" );
      object.entrySet().forEach( entry -> {
        System.out.println( entry.getKey() + "\t\t" + entry.getValue() );
      } );
      i++;
    }
  }

  public static void Dynamo_clearScanFilter() {
    scanFilter.clear();
  }

  // ***********
  // AWS Lambda
  // ***********
  public static void Lambda_InvokeFunction( String payload ) {
  }

  public static void Lambda_InvokeFunction( String functionName, String payloadpath ) {
    String payload = null;
    InvokeResult invokeResult = null;
    try {
      payload = FileOperrations.readFile( FileOperrations.getExpectedFilePath( payloadpath ) );
      if ( payload.length() < 1 ) {
        Assert.fail( "Payload empty." );
      }
      InvokeRequest invokeRequest = new InvokeRequest().withFunctionName( functionName ).withPayload( payload );
      invokeResult = lambdaClient.invoke( invokeRequest );
      System.out.println( "Response from '" + functionName + "' lambda:\n" + invokeResult.toString() );
      System.out.println( "Request ID: " + AWS_getRequestId( "lambda", invokeRequest ) );
    } catch ( NullPointerException e ) {
      Assert.fail( "NullPointerException found. Please verify service connection." );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "lambda" );
      Assert.fail(
          "Your request made it to Amazon lambda, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    if ( invokeResult != null && invokeResult.getStatusCode() == 200 )
      System.out.println( "Given lambda invoked successfully." );
    else
      Assert.fail( "Error recieved while invoking lambda. " + invokeResult.getStatusCode() );
  }
  // ************************
  // AWS Kinesis Data Stream
  // ***********************

  public static void Kinesis_getStreamDetails( String streamName ) {
    System.out.println( "Details of given stream name --->" + streamName );
    try {
      System.out.println( kinesisClient.describeStream( streamName ).toString() );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "Kinesis" );
      Assert.fail(
          "Your request made it to Amazon Kinesis, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static GetShardIteratorResult Kinesis_getShardIterator( String streamName, String shardId,
      String iteratorType ) {
    GetShardIteratorResult iterator = null;
    try {
      iterator = kinesisClient.getShardIterator( streamName, shardId, iteratorType );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "Kinesis" );
      Assert.fail(
          "Your request made it to Amazon Kinesis, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    if ( iterator != null )
      return iterator;
    else {
      Assert.fail( "Unable to get shard iterator" );
      return null;
    }
  }

  public static void Kinesis_putRecordStream( String streamName, String dataPath, String partitionKey ) {
    try {
      PutRecordRequest putRecordRequest = new PutRecordRequest();
      putRecordRequest.setStreamName( streamName );
      putRecordRequest.setData( ByteBuffer.wrap( FileOperrations.readFile( FileOperrations.getExpectedFilePath(
          dataPath ) ).getBytes() ) );
      putRecordRequest.setPartitionKey( partitionKey );
      PutRecordResult putRecordResult = kinesisClient.putRecord( putRecordRequest );
      kinesismilli = String.valueOf( ( System.currentTimeMillis() ) );
      System.out.println( "Record successfully processed to given stream with:\n Request Id: " + AWS_getRequestId(
          "kinesis", putRecordRequest ) + "\n Result: " + putRecordResult.toString() );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "Kinesis" );
      Assert.fail(
          "Your request made it to Amazon Kinesis, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void Kinesis_putStrDataRecord( String streamName, String dataStr, String partitionKey ) {
    try {
      PutRecordRequest putRecordRequest = new PutRecordRequest();
      putRecordRequest.setStreamName( streamName );
      putRecordRequest.setData( ByteBuffer.wrap( dataStr.getBytes() ) );
      putRecordRequest.setPartitionKey( partitionKey );
      PutRecordResult putRecordResult = kinesisClient.putRecord( putRecordRequest );
      kinesismilli = String.valueOf( ( System.currentTimeMillis() ) );
      System.out.println( "Record successfully processed to given stream with:\n Request Id: " + AWS_getRequestId(
          "kinesis", putRecordRequest ) + "\n Result: " + putRecordResult.toString() );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "Kinesis" );
      Assert.fail(
          "Your request made it to Amazon Kinesis, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  public static void Kinesis_putRecordsStream( String streamName, String dataPath, String partitionKey ) {
    try {
      String[] arrOfStr = FileOperrations.readFile( FileOperrations.getExpectedFilePath( dataPath ) ).split( "@@@" );
      PutRecordsRequest putRecordsRequest = new PutRecordsRequest();
      putRecordsRequest.setStreamName( streamName );
      List<PutRecordsRequestEntry> putRecordsRequestEntryList = new ArrayList<>();
      for ( int i = 0; i < arrOfStr.length; i++ ) {
        PutRecordsRequestEntry putRecordsRequestEntry = new PutRecordsRequestEntry();
        putRecordsRequestEntry.setData( ByteBuffer.wrap( arrOfStr[ i ].getBytes() ) );
        putRecordsRequestEntry.setPartitionKey( partitionKey );
        putRecordsRequestEntryList.add( putRecordsRequestEntry );
      }
      putRecordsRequest.setRecords( putRecordsRequestEntryList );
      PutRecordsResult putRecordsResult = kinesisClient.putRecords( putRecordsRequest );
      kinesismilli = String.valueOf( ( System.currentTimeMillis() ) );
      System.out.println( putRecordsRequestEntryList.size()
          + " records successfully processed to given stream with:\nRequest Id: " + AWS_getRequestId( "kinesis",
              putRecordsRequest ) + "\n Result: " + putRecordsResult.toString() );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "Kinesis" );
      Assert.fail(
          "Your request made it to Amazon Kinesis, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
  }

  // ********
  // AWS Logs
  // ********
  public static List<OutputLogEvent> Logs_getEventMessages( String LogGroupName, String limitStreams ) {
    String nextToken = null;
    GetLogEventsResult response;
    try {
      DescribeLogStreamsRequest describeLogStreamsRequest = new DescribeLogStreamsRequest().withLogGroupName(
          LogGroupName ).withDescending( true ).withLimit( Integer.valueOf( limitStreams ) );
      DescribeLogStreamsResult describeLogStreamsResult = logsClient.describeLogStreams( describeLogStreamsRequest );
      for ( LogStream logStream: describeLogStreamsResult.getLogStreams() ) {
        do {
          GetLogEventsRequest request = new GetLogEventsRequest().withLogGroupName( LogGroupName ).withLogStreamName(
              logStream.getLogStreamName() );
          if ( nextToken != null )
            request = request.withNextToken( nextToken );
          response = logsClient.getLogEvents( request );
          System.out.println( "Received logs from stream --> " + logStream.getLogStreamName().toString() );
          logMessages.addAll( response.getEvents() );
          // check if token is the same
          if ( response.getNextForwardToken().equals( nextToken ) )
            break;
          nextToken = response.getNextForwardToken();
        } while ( true );
      }
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "dynamodb" );
      Assert.fail(
          "Your request made it to Amazon dynamodb, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    if ( logMessages.isEmpty() == true )
      Assert.fail( "Logs result empty." );
    return logMessages;
  }

  public static List<OutputLogEvent> Logs_getEventMessagesWithStreamName( String LogGroupName, String streamName ) {
    String nextToken = null;
    GetLogEventsResult response;
    try {
      do {
        GetLogEventsRequest request = new GetLogEventsRequest().withLogGroupName( LogGroupName ).withLogStreamName(
            streamName );
        if ( nextToken != null )
          request = request.withNextToken( nextToken );
        response = logsClient.getLogEvents( request );
        System.out.println( "Reading from given stream --> " + streamName );
        logMessages.addAll( response.getEvents() );
        // check if token is the same
        if ( response.getNextForwardToken().equals( nextToken ) )
          break;
        nextToken = response.getNextForwardToken();
      } while ( true );
    } catch ( AmazonServiceException ase ) {
      AWS_serviceDisconnect( "dynamodb" );
      Assert.fail(
          "Your request made it to Amazon dynamodb, but was rejected with an error response for some reason.\n Error Message:    "
              + ase.getMessage() + "\nRequest ID:       " + ase.getRequestId() );
    } catch ( AmazonClientException ace ) {
      Assert.fail( "The client encountered internal problem, please verify service connection.\nError Message: " + ace
          .getMessage() );
    }
    if ( logMessages.isEmpty() == true )
      Assert.fail( "Logs result empty." );
    return logMessages;
  }

  public static boolean Logs_validateUserString( String expectedString ) {
    boolean status = false;
    int i = 0;
    if ( logMessages.isEmpty() ) {
      return status;
    } else
      for ( OutputLogEvent temp: logMessages ) {
        if ( temp.toString().toLowerCase().contains( expectedString.toLowerCase() ) ) {
          status = true;
          i = logMessages.indexOf( temp );
          break;
        }
      }
    if ( status == true )
      System.out.println( "Excepted string found in log messages.\n" + logMessages.get( i ).getMessage() );
    return status;
  }

  // ************************
  // AWS Main
  // ***********************
  public static void main( String[] args ) throws InterruptedException {
    // AWS_setSessionAccount( "abcd" );
    AWS_setSessionCredentials();
    AWS_serviceConnect( "logs" );
    System.out.println( Logs_getEventMessagesWithStreamName( "/aws/lambda/CaracalPackageUpdateProducer",
        "2020/12/15/[$LATEST]fb4e809cb6824029992c04c596011f55" ) );
    // Dynamo_executeQueryPartAndSort( "caracal-package", "ItemHash",
    // "Package|ID:TestKinesis01", "ItemRange",
    // "Activated:2018-01-01 01:10:01" );
    // Kinesis_getStreamDetails( "rsn_tigrillo_package_tables_replica_stream" );
    // Kinesis_putRecordStream( "rsn_tigrillo_package_tables_replica_stream",
    // "/Caracal/Kinesis/tigrilloPackageStream/Package_TestKinesis01.json", "a" );
    // AWS_serviceConnect( "logs" );
    // Logs_getEventMessagesWithStartTime( "/aws/lambda/CaracalPackageSync",
    // "1602774545730" );
    // Kinesis_putRecordsStream( "rsn_tigrillo_package_tables_replica_stream",
    // "/Caracal/Kinesis/tigrilloPackageStream/test2.json", "a" );
    // System.out.println( Kinesis_getShardIterator(
    // "rsn_tigrillo_package_tables_replica_stream", "shardId-000000000000",
    // "LATEST" ) );
    // Lambda_InvokeFunction( "lookup_mast_lambda", "{\r\n" + " \"type\":
    // \"mast\"\r\n" + "}" );
    // SQS_listAllQueues();
    // System.out.println( SQS_returnQueueURL( "failurequeue" ) );
    // SQS_purgeQueueMessages( "readerQueue" );
    // SQS_receiveAllQueueMessages( "resultQueue" );
    // SQS_getSendMessageGroupID();
    // SQS_sendQueueSingleMessage( "readerQueue", "Test message sent via
    // Automation." );
    // SQS_sendQueueMultipleMessage( "readerQueue",
    // "Test message number 1//Test message number 2//Test message number 3" );
    // SQS_setBatchRequestEntries( "Test message number 1//Test message number
    // 2//Test message number 3" );
    // SQS_receiveAllQueueMessages( "readerQueue" );
    // SQS_validateDeadLetterQueue( "reader", "failure" );
    // AWS_serviceDisconnect( "kinesis" );
    AWS_serviceDisconnect( "logs" );
  }
}
