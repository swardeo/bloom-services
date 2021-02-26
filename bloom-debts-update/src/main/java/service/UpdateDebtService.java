package service;

import java.util.Map;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class UpdateDebtService {

    private final DynamoService service;

    public UpdateDebtService(DynamoService service) {
        this.service = service;
    }

    public void update(
            Map<String, AttributeValue> key, Map<String, AttributeValue> attributeValueMap) {
        String updateExpression =
                "SET #a = :startAmount, #b = :monthlyAmount, #c = :startDate, #d = :yearlyRate, #e = :adjustments, #f = :oneTimePayments";

        Map<String, String> expressionAttributeNames =
                Map.of(
                        "#a", "StartAmount",
                        "#b", "MonthlyAmount",
                        "#c", "StartDate",
                        "#d", "YearlyRate",
                        "#e", "Adjustments",
                        "#f", "OneTimePayments");

        service.update(key, updateExpression, expressionAttributeNames, attributeValueMap);
    }
}
