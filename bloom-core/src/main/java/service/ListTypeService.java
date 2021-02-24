package service;

import java.util.Map;
import model.Subject;
import model.Type;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class ListTypeService {

    private final DynamoService service;

    public ListTypeService(DynamoService service) {
        this.service = service;
    }

    public QueryResponse list(Subject subject, Type type) {
        String keyConditionExpression = "PK = :user AND begins_with ( SK, :type )";

        Map<String, AttributeValue> expressionAttributeValues =
                Map.of(
                        ":user", AttributeValue.builder().s("USER#" + subject.getSubject()).build(),
                        ":type", AttributeValue.builder().s(type.getType() + "#").build());

        return service.list(keyConditionExpression, expressionAttributeValues);
    }
}
