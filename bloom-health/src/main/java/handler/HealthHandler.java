package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.HealthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import provider.DynamoProvider;
import provider.MapperProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

public class HealthHandler extends RequestStreamHandler<Void, HealthResponse> {

    public static final ObjectMapper OBJECT_MAPPER = MapperProvider.provideMapper();
    public static final DynamoDbClient DYNAMO_DB_CLIENT = DynamoProvider.provideClient();
    public static final String TABLE_NAME = DynamoProvider.provideTableName();

    public HealthHandler() {
        super(OBJECT_MAPPER, new HealthHandlerDelegate(DYNAMO_DB_CLIENT, TABLE_NAME), Void.class);
    }

    static class HealthHandlerDelegate implements Handler<Void, HealthResponse> {

        private final DynamoDbClient dynamoDbClient;
        private final String tableName;
        private final Logger logger;

        HealthHandlerDelegate(DynamoDbClient dynamoDbClient, String tableName) {
            this(dynamoDbClient, tableName, LoggerFactory.getLogger(HealthHandler.class));
        }

        HealthHandlerDelegate(DynamoDbClient dynamoDbClient, String tableName, Logger logger) {
            this.dynamoDbClient = dynamoDbClient;
            this.tableName = tableName;
            this.logger = logger;
        }

        @Override
        public HealthResponse handle(Void request, Context context) {
            DescribeTableResponse response =
                    dynamoDbClient.describeTable(
                            DescribeTableRequest.builder().tableName(tableName).build());

            if (TableStatus.ACTIVE != response.table().tableStatus()) {
                logger.info("Table {} is not active", tableName);
                return HealthResponse.UNHEALTHY;
            }
            return HealthResponse.HEALTHY;
        }
    }
}
