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
import service.AddSavingService;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import transform.SavingTransformer;

public class AddSavingHandler extends RequestStreamHandler<Saving, Void> {

    public static final ObjectMapper OBJECT_MAPPER = provideMapper();
    public static final DynamoDbClient DYNAMO_DB_CLIENT = provideClient();
    public static final String TABLE_NAME = provideTableName();
    public static final SavingTransformer SAVING_TRANSFORMER = new SavingTransformer();
    public static final AddSavingService ADD_SAVING_SERVICE =
            new AddSavingService(DYNAMO_DB_CLIENT, TABLE_NAME);

    public AddSavingHandler() {
        super(
                OBJECT_MAPPER,
                new AddSavingHandlerDelegate(SAVING_TRANSFORMER, ADD_SAVING_SERVICE),
                Saving.class);
    }

    static class AddSavingHandlerDelegate implements Handler<Saving, Void> {

        private final SavingTransformer transformer;
        private final AddSavingService service;
        private final Logger logger;

        AddSavingHandlerDelegate(SavingTransformer transformer, AddSavingService service) {
            this(transformer, service, getLogger(AddSavingHandler.class));
        }

        AddSavingHandlerDelegate(
                SavingTransformer transformer, AddSavingService service, Logger logger) {
            this.transformer = transformer;
            this.service = service;
            this.logger = logger;
        }

        @Override
        public Void handle(Saving saving, Context context) {
            Map<String, AttributeValue> attributeValueMap =
                    transformer.toAttributeMap(saving, context.getIdentity());
            service.addSaving(attributeValueMap);
            logger.info(
                    "Saving {} added for identity {}",
                    saving.getName().getName(),
                    context.getIdentity().getIdentityId());
            return null;
        }
    }
}
