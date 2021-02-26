package service;

import java.util.Map;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public class DynamoService {

    private final DynamoDbClient client;
    private final String tableName;

    public DynamoService(DynamoDbClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    public void add(Map<String, AttributeValue> attributeValueMap) {
        PutItemRequest request =
                PutItemRequest.builder().tableName(tableName).item(attributeValueMap).build();

        client.putItem(request);
    }

    public QueryResponse list(
            String keyConditionExpression, Map<String, AttributeValue> expressionAttributeValues) {
        QueryRequest request =
                QueryRequest.builder()
                        .tableName(tableName)
                        .keyConditionExpression(keyConditionExpression)
                        .expressionAttributeValues(expressionAttributeValues)
                        .build();

        return client.query(request);
    }

    public void delete(Map<String, AttributeValue> key) {
        DeleteItemRequest request =
                DeleteItemRequest.builder().tableName(tableName).key(key).build();

        client.deleteItem(request);
    }

    public void update(
            Map<String, AttributeValue> key,
            String updateExpression,
            Map<String, String> expressionAttributeNames,
            Map<String, AttributeValue> attributeValueMap) {
        UpdateItemRequest request =
                UpdateItemRequest.builder()
                        .tableName(tableName)
                        .key(key)
                        .updateExpression(updateExpression)
                        .expressionAttributeNames(expressionAttributeNames)
                        .expressionAttributeValues(attributeValueMap)
                        .build();

        client.updateItem(request);
    }
}
