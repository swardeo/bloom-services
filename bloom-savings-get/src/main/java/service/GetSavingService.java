package service;

import exception.NoItemFoundException;
import java.util.Map;
import model.Saving;
import model.Subject;
import model.request.NameRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import transform.SavingsDynamoTransformer;

public class GetSavingService {

    private final DynamoDbClient client;
    private final String tableName;
    private final SavingsDynamoTransformer transformer;

    public GetSavingService(
            DynamoDbClient client, String tableName, SavingsDynamoTransformer transformer) {
        this.client = client;
        this.tableName = tableName;
        this.transformer = transformer;
    }

    public Saving getSaving(Subject subject, NameRequest nameRequest) {
        String keyConditionExpression = "PK = :user AND SK = :saving";

        Map<String, AttributeValue> expressionAttributeValues =
                Map.of(
                        ":user", AttributeValue.builder().s("USER#" + subject.getSubject()).build(),
                        ":saving",
                                AttributeValue.builder()
                                        .s("SAVING#" + nameRequest.getName())
                                        .build());

        QueryRequest request =
                QueryRequest.builder()
                        .tableName(tableName)
                        .keyConditionExpression(keyConditionExpression)
                        .expressionAttributeValues(expressionAttributeValues)
                        .build();

        QueryResponse response = client.query(request);
        if (1 != response.count()) {
            throw new NoItemFoundException("no item found matching query");
        }

        return transformer.toSaving(response.items().get(0));
    }
}
