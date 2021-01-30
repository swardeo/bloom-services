package service;

import java.util.Map;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public class UpdateSavingService {

    private final DynamoDbClient client;
    private final String tableName;

    public UpdateSavingService(DynamoDbClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    public void updateSaving(
            Map<String, AttributeValue> key, Map<String, AttributeValue> attributeValueMap) {
        String updateExpression =
                "SET #a = :startAmount, #b = :monthlyAmount, #c = :startDate, #d = :endDate, #e = :yearlyRate, #f = :adjustments, #g = :oneTimePayments";

        Map<String, String> expressionAttributeNames =
                Map.of(
                        "#a", "StartAmount",
                        "#b", "MonthlyAmount",
                        "#c", "StartDate",
                        "#d", "EndDate",
                        "#e", "YearlyRate",
                        "#f", "Adjustments",
                        "#g", "OneTimePayments");

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
