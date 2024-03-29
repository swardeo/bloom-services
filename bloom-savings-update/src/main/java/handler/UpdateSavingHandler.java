package handler;

import static org.slf4j.LoggerFactory.getLogger;
import static provider.DynamoProvider.provideClient;
import static provider.DynamoProvider.provideTableName;
import static provider.MapperProvider.provideMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import model.RequestDetails;
import model.Saving;
import model.Subject;
import org.slf4j.Logger;
import service.DynamoService;
import service.UpdateSavingService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import transform.SavingTransformer;

public class UpdateSavingHandler extends RequestStreamHandler<Saving, Void> {

    public static final ObjectMapper OBJECT_MAPPER = provideMapper();

    public static final UpdateSavingService UPDATE_SAVING_SERVICE =
            new UpdateSavingService(new DynamoService(provideClient(), provideTableName()));

    public static final Logger LOGGER = getLogger(UpdateSavingHandler.class);

    public static final UpdateSavingHandlerDelegate DELEGATE =
            new UpdateSavingHandlerDelegate(new SavingTransformer(), UPDATE_SAVING_SERVICE, LOGGER);

    public UpdateSavingHandler() {
        super(OBJECT_MAPPER, DELEGATE, Saving.class);
    }

    static class UpdateSavingHandlerDelegate implements Handler<Saving, Void> {

        private final SavingTransformer transformer;
        private final UpdateSavingService service;
        private final Logger logger;

        UpdateSavingHandlerDelegate(
                SavingTransformer transformer, UpdateSavingService service, Logger logger) {
            this.transformer = transformer;
            this.service = service;
            this.logger = logger;
        }

        @Override
        public Void handle(Saving saving, Subject subject, RequestDetails details) {
            Map<String, AttributeValue> key = transformer.toKey(saving.getName(), subject);
            Map<String, AttributeValue> attributeValueMap = transformer.toAttributeMap(saving);
            service.updateSaving(key, attributeValueMap);
            logger.info(
                    "Saving {} updated for subject {}",
                    saving.getName().getName(),
                    subject.getSubject());
            return null;
        }
    }
}
