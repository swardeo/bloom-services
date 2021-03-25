package service;

import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class UpdateSavingService {

    private final DynamoService service;

    public UpdateSavingService(DynamoService service) {
        this.service = service;
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

        service.update(key, updateExpression, expressionAttributeNames, attributeValueMap);
    }
}
