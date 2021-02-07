package handler;

import static org.slf4j.LoggerFactory.getLogger;
import static provider.DynamoProvider.provideClient;
import static provider.DynamoProvider.provideTableName;
import static provider.MapperProvider.provideMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import model.RequestDetails;
import model.Saving;
import model.Subject;
import org.slf4j.Logger;
import service.ListSavingsService;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import transform.SavingsDynamoTransformer;

public class ListSavingsHandler extends RequestStreamHandler<Void, List<Saving>> {

    public static final ObjectMapper OBJECT_MAPPER = provideMapper();
    public static final DynamoDbClient DYNAMO_DB_CLIENT = provideClient();
    public static final String TABLE_NAME = provideTableName();
    public static final SavingsDynamoTransformer DYNAMO_TRANSFORMER =
            new SavingsDynamoTransformer();
    public static final ListSavingsService LIST_SAVINGS_SERVICE =
            new ListSavingsService(DYNAMO_DB_CLIENT, TABLE_NAME);

    public ListSavingsHandler() {
        super(
                OBJECT_MAPPER,
                new ListSavingsHandlerDelegate(DYNAMO_TRANSFORMER, LIST_SAVINGS_SERVICE),
                Void.class);
    }

    static class ListSavingsHandlerDelegate implements Handler<Void, List<Saving>> {

        private final SavingsDynamoTransformer transformer;
        private final ListSavingsService service;
        private final Logger logger;

        ListSavingsHandlerDelegate(
                SavingsDynamoTransformer transformer, ListSavingsService service) {
            this(transformer, service, getLogger(ListSavingsHandlerDelegate.class));
        }

        ListSavingsHandlerDelegate(
                SavingsDynamoTransformer transformer, ListSavingsService service, Logger logger) {
            this.transformer = transformer;
            this.service = service;
            this.logger = logger;
        }

        @Override
        public List<Saving> handle(Void request, Subject subject, RequestDetails details) {
            QueryResponse queryResponse = service.listSavings(subject);
            List<Saving> savings = transformer.toSavingsList(queryResponse);

            logger.info("{} savings listed for subject {}", savings.size(), subject.getSubject());
            return savings;
        }
    }
}
