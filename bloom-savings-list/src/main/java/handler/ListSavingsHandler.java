package handler;

import static org.slf4j.LoggerFactory.getLogger;
import static provider.DynamoProvider.provideClient;
import static provider.DynamoProvider.provideTableName;
import static provider.MapperProvider.provideMapper;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import model.Saving;
import org.slf4j.Logger;
import service.ListSavingsService;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import transform.DynamoTransformer;

public class ListSavingsHandler extends RequestStreamHandler<Void, List<Saving>> {

    public static final ObjectMapper OBJECT_MAPPER = provideMapper();
    public static final DynamoDbClient DYNAMO_DB_CLIENT = provideClient();
    public static final String TABLE_NAME = provideTableName();
    public static final DynamoTransformer DYNAMO_TRANSFORMER = new DynamoTransformer();
    public static final ListSavingsService LIST_SAVINGS_SERVICE =
            new ListSavingsService(DYNAMO_DB_CLIENT, TABLE_NAME);

    public ListSavingsHandler() {
        super(
                OBJECT_MAPPER,
                new ListSavingsHandlerDelegate(DYNAMO_TRANSFORMER, LIST_SAVINGS_SERVICE),
                Void.class);
    }

    static class ListSavingsHandlerDelegate implements Handler<Void, List<Saving>> {

        private final DynamoTransformer transformer;
        private final ListSavingsService service;
        private final Logger logger;

        ListSavingsHandlerDelegate(DynamoTransformer transformer, ListSavingsService service) {
            this(transformer, service, getLogger(ListSavingsHandlerDelegate.class));
        }

        ListSavingsHandlerDelegate(
                DynamoTransformer transformer, ListSavingsService service, Logger logger) {
            this.transformer = transformer;
            this.service = service;
            this.logger = logger;
        }

        @Override
        public List<Saving> handle(Void request, Context context) {
            QueryResponse queryResponse = service.listSavings(context.getIdentity());
            List<Saving> savings = transformer.toSavingsList(queryResponse);

            logger.info(
                    "{} savings listed for identity {}",
                    savings.size(),
                    context.getIdentity().getIdentityId());
            return savings;
        }
    }
}
