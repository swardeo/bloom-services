package transform;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import java.util.HashMap;
import java.util.Map;
import model.NameRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class NameRequestTransformer {

    public NameRequestTransformer() {}

    public Map<String, AttributeValue> toKey(NameRequest request, CognitoIdentity identity) {
        Map<String, AttributeValue> key = new HashMap<>();

        key.put("PK", builder().s("USER#" + identity.getIdentityId()).build());
        key.put("SK", builder().s("SAVING#" + request.getName()).build());

        return key;
    }
}
