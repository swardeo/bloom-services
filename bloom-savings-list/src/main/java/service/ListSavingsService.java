package service;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class ListSavingsService {

    private final DynamoDbClient client;
    private final String tableName;

    public ListSavingsService(DynamoDbClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    public QueryResponse listSavings(CognitoIdentity identity) {
        String keyConditionExpression = "PK = :user AND begins_with ( SK, :saving )";

        Map<String, AttributeValue> expressionAttributeValues =
                Map.of(
                        ":user",
                                AttributeValue.builder()
                                        .s("USER#" + identity.getIdentityId())
                                        .build(),
                        ":saving", AttributeValue.builder().s("SAVING#").build());

        QueryRequest request =
                QueryRequest.builder()
                        .tableName(tableName)
                        .keyConditionExpression(keyConditionExpression)
                        .expressionAttributeValues(expressionAttributeValues)
                        .build();

        return client.query(request);
    }
}
