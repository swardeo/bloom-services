package service;

import java.util.Map;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;

public class DeleteSavingService {

    private final DynamoDbClient client;
    private final String tableName;

    public DeleteSavingService(DynamoDbClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    public void deleteSaving(Map<String, AttributeValue> key) {
        DeleteItemRequest request =
                DeleteItemRequest.builder().tableName(tableName).key(key).build();

        client.deleteItem(request);
    }
}
