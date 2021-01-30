package handler;

import static org.slf4j.LoggerFactory.getLogger;
import static provider.DynamoProvider.provideClient;
import static provider.DynamoProvider.provideTableName;
import static provider.MapperProvider.provideMapper;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import model.Saving;
import org.slf4j.Logger;
import service.UpdateSavingService;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import transform.SavingTransformer;

public class UpdateSavingHandler extends RequestStreamHandler<Saving, Void> {

    public static final ObjectMapper OBJECT_MAPPER = provideMapper();
    public static final DynamoDbClient DYNAMO_DB_CLIENT = provideClient();
    public static final String TABLE_NAME = provideTableName();
    public static final SavingTransformer SAVING_TRANSFORMER = new SavingTransformer();
    public static final UpdateSavingService UPDATE_SAVING_SERVICE =
            new UpdateSavingService(DYNAMO_DB_CLIENT, TABLE_NAME);

    public UpdateSavingHandler() {
        super(
                OBJECT_MAPPER,
                new UpdateSavingHandlerDelegate(SAVING_TRANSFORMER, UPDATE_SAVING_SERVICE),
                Saving.class);
    }

    static class UpdateSavingHandlerDelegate implements Handler<Saving, Void> {

        private final SavingTransformer transformer;
        private final UpdateSavingService service;
        private final Logger logger;

        UpdateSavingHandlerDelegate(SavingTransformer transformer, UpdateSavingService service) {
            this(transformer, service, getLogger(UpdateSavingHandler.class));
        }

        UpdateSavingHandlerDelegate(
                SavingTransformer transformer, UpdateSavingService service, Logger logger) {
            this.transformer = transformer;
            this.service = service;
            this.logger = logger;
        }

        @Override
        public Void handle(Saving saving, Context context) {
            Map<String, AttributeValue> key =
                    transformer.toKey(saving.getName(), context.getIdentity());
            Map<String, AttributeValue> attributeValueMap = transformer.toAttributeMap(saving);
            service.updateSaving(key, attributeValueMap);
            logger.info(
                    "Saving {} updated for identity {}",
                    saving.getName().getName(),
                    context.getIdentity().getIdentityId());
            return null;
        }
    }
}
