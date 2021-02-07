package transform;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import java.util.HashMap;
import java.util.Map;
import model.Subject;
import model.request.NameRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class NameRequestTransformer {

    public NameRequestTransformer() {}

    public Map<String, AttributeValue> toKey(NameRequest request, Subject subject) {
        Map<String, AttributeValue> key = new HashMap<>();

        key.put("PK", builder().s("USER#" + subject.getSubject()).build());
        key.put("SK", builder().s("SAVING#" + request.getName()).build());

        return key;
    }
}
