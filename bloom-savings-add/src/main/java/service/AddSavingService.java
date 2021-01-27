package service;

import java.util.Map;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class AddSavingService {

    private final DynamoDbClient client;
    private final String tableName;

    public AddSavingService(DynamoDbClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    public void addSaving(Map<String, AttributeValue> attributeValueMap) {
        PutItemRequest request =
                PutItemRequest.builder().tableName(tableName).item(attributeValueMap).build();

        client.putItem(request);
    }
}
