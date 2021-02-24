package handler;

import static org.slf4j.LoggerFactory.getLogger;
import static provider.DynamoProvider.provideClient;
import static provider.DynamoProvider.provideTableName;
import static provider.MapperProvider.provideMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import model.Debt;
import model.RequestDetails;
import model.Subject;
import org.slf4j.Logger;
import service.DynamoService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import transform.DebtTransformer;

public class AddDebtHandler extends RequestStreamHandler<Debt, Void> {

    public static final ObjectMapper OBJECT_MAPPER = provideMapper();
    public static final DebtTransformer DEBT_TRANSFORMER = new DebtTransformer();
    public static final DynamoService DYNAMO_SERVICE =
            new DynamoService(provideClient(), provideTableName());

    public AddDebtHandler() {
        super(
                OBJECT_MAPPER,
                new AddDebtHandlerDelegate(DEBT_TRANSFORMER, DYNAMO_SERVICE),
                Debt.class);
    }

    static class AddDebtHandlerDelegate implements Handler<Debt, Void> {

        private final DebtTransformer transformer;
        private final DynamoService service;
        private final Logger logger;

        AddDebtHandlerDelegate(DebtTransformer transformer, DynamoService service) {
            this(transformer, service, getLogger(AddDebtHandler.class));
        }

        AddDebtHandlerDelegate(DebtTransformer transformer, DynamoService service, Logger logger) {
            this.transformer = transformer;
            this.service = service;
            this.logger = logger;
        }

        @Override
        public Void handle(Debt debt, Subject subject, RequestDetails details) {
            Map<String, AttributeValue> attributeValueMap =
                    transformer.toAttributeMap(debt, subject);
            service.add(attributeValueMap);
            logger.info(
                    "Debt {} added for subject {}", debt.getName().getName(), subject.getSubject());
            return null;
        }
    }
}
