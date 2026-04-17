package utility;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.Map;

public class DynamoDBUtil {

    private static final String TABLE_NAME = "AUTOMATION_TESTDATA";
    private static final String TABLE_NAME_EXECUTION = "AUTOMATION_TEST_RESULTS";

    public static Map<String, String> getTestData(String testCaseId) {

        DynamoDbClient dynamoDBClient = DynamoDbClient.builder()
                .region(Region.AP_SOUTHEAST_2)
                .credentialsProvider(software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider.create()) // Use default credentials provider
                .build();

        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(TABLE_NAME)
                .build();

        ScanResponse scanResponse = dynamoDBClient.scan(scanRequest);

       List<Map<String, String>> testDataList = new ArrayList<>();
        for (Map<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> item : scanResponse.items()) {
            if (item.get("TestCaseId").s().equals(testCaseId)) {
                Map<String, String> testData = new java.util.HashMap<>();
                for (Map.Entry<String, software.amazon.awssdk.services.dynamodb.model.AttributeValue> entry : item.entrySet()) {
                    testData.put(entry.getKey(), entry.getValue().s());
                }
                testDataList.add(testData);
            }
        }

        return testDataList.isEmpty() ? null : testDataList.get(0);
    }

    public static void insertTestResult(String testCaseId, String result) {

        DynamoDbClient client = DynamoDbClient.builder()
                .region(Region.AP_SOUTHEAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        String timestamp = Instant.now().toString(); // ISO format

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("TestCaseId", AttributeValue.builder().s(testCaseId).build());
        item.put("ExecutionTime", AttributeValue.builder().s(timestamp).build());
        item.put("ExecutionResult", AttributeValue.builder().s(result).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME_EXECUTION)
                .item(item)
                .build();

        client.putItem(request);

        System.out.println("Inserted result for " + testCaseId + " at " + timestamp);
    }


}
