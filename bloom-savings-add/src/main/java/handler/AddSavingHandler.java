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
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import transform.SavingTransformer;

public class AddSavingHandler extends RequestStreamHandler<Saving, Void> {

    public static final ObjectMapper OBJECT_MAPPER = provideMapper();

    public static final DynamoService DYNAMO_SERVICE =
            new DynamoService(provideClient(), provideTableName());

    public static final Logger LOGGER = getLogger(AddSavingHandler.class);

    public static final AddSavingHandlerDelegate DELEGATE =
            new AddSavingHandlerDelegate(new SavingTransformer(), DYNAMO_SERVICE, LOGGER);

    public AddSavingHandler() {
        super(OBJECT_MAPPER, DELEGATE, Saving.class);
    }

    static class AddSavingHandlerDelegate implements Handler<Saving, Void> {

        private final SavingTransformer transformer;
        private final DynamoService service;
        private final Logger logger;

        AddSavingHandlerDelegate(
                SavingTransformer transformer, DynamoService service, Logger logger) {
            this.transformer = transformer;
            this.service = service;
            this.logger = logger;
        }

        @Override
        public Void handle(Saving saving, Subject subject, RequestDetails details) {
            Map<String, AttributeValue> attributeValueMap =
                    transformer.toAttributeMap(saving, subject);
            service.add(attributeValueMap);
            logger.info(
                    "Saving {} added for subject {}",
                    saving.getName().getName(),
                    subject.getSubject());
            return null;
        }
    }
}
