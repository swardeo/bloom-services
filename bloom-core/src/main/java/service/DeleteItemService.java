package service;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import java.util.Map;
import model.Subject;
import model.Type;
import model.request.NameRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DeleteItemService {

    private final DynamoService service;

    public DeleteItemService(DynamoService service) {
        this.service = service;
    }

    public void delete(Subject subject, Type type, NameRequest request) {
        Map<String, AttributeValue> key =
                Map.of(
                        "PK", builder().s("USER#" + subject.getSubject()).build(),
                        "SK", builder().s(type.getType() + "#" + request.getName()).build());
        service.delete(key);
    }
}
