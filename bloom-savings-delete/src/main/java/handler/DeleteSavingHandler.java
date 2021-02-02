package handler;

import static org.slf4j.LoggerFactory.getLogger;
import static provider.DynamoProvider.provideClient;
import static provider.DynamoProvider.provideTableName;
import static provider.MapperProvider.provideMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import model.NameRequest;
import model.Subject;
import org.slf4j.Logger;
import service.DeleteSavingService;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import transform.NameRequestTransformer;

public class DeleteSavingHandler extends RequestStreamHandler<NameRequest, Void> {

    public static final ObjectMapper OBJECT_MAPPER = provideMapper();
    public static final DynamoDbClient DYNAMO_DB_CLIENT = provideClient();
    public static final String TABLE_NAME = provideTableName();
    public static final NameRequestTransformer SAVING_NAME_TRANSFORMER =
            new NameRequestTransformer();
    public static final DeleteSavingService DELETE_SAVING_SERVICE =
            new DeleteSavingService(DYNAMO_DB_CLIENT, TABLE_NAME);

    public DeleteSavingHandler() {
        super(
                OBJECT_MAPPER,
                new DeleteSavingHandlerDelegate(SAVING_NAME_TRANSFORMER, DELETE_SAVING_SERVICE),
                NameRequest.class);
    }

    static class DeleteSavingHandlerDelegate implements Handler<NameRequest, Void> {

        private final NameRequestTransformer transformer;
        private final DeleteSavingService service;
        private final Logger logger;

        DeleteSavingHandlerDelegate(
                NameRequestTransformer transformer, DeleteSavingService service) {
            this(transformer, service, getLogger(DeleteSavingHandler.class));
        }

        DeleteSavingHandlerDelegate(
                NameRequestTransformer transformer, DeleteSavingService service, Logger logger) {
            this.transformer = transformer;
            this.service = service;
            this.logger = logger;
        }

        @Override
        public Void handle(NameRequest request, Subject subject) {
            Map<String, AttributeValue> key = transformer.toKey(request, subject);
            service.deleteSaving(key);
            logger.info(
                    "Saving {} deleted for subject {}", request.getName(), subject.getSubject());
            return null;
        }
    }
}
