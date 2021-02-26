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
import service.UpdateDebtService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import transform.DebtTransformer;

public class UpdateDebtHandler extends RequestStreamHandler<Debt, Void> {

    public static final ObjectMapper OBJECT_MAPPER = provideMapper();
    public static final DebtTransformer DEBT_TRANSFORMER = new DebtTransformer();
    public static final UpdateDebtService UPDATE_SERVICE =
            new UpdateDebtService(new DynamoService(provideClient(), provideTableName()));
    public static final Logger LOGGER = getLogger(UpdateDebtHandler.class);

    public UpdateDebtHandler() {
        super(
                OBJECT_MAPPER,
                new UpdateDebtHandlerDelegate(DEBT_TRANSFORMER, UPDATE_SERVICE, LOGGER),
                Debt.class);
    }

    static class UpdateDebtHandlerDelegate implements Handler<Debt, Void> {

        private final DebtTransformer transformer;
        private final UpdateDebtService service;
        private final Logger logger;

        UpdateDebtHandlerDelegate(
                DebtTransformer transformer, UpdateDebtService service, Logger logger) {
            this.transformer = transformer;
            this.service = service;
            this.logger = logger;
        }

        @Override
        public Void handle(Debt request, Subject subject, RequestDetails details) {
            Map<String, AttributeValue> key = transformer.toKey(request.getName(), subject);
            Map<String, AttributeValue> attributeValueMap = transformer.toAttributeMap(request);
            service.update(key, attributeValueMap);
            logger.info(
                    "Debt {} updated for subject {}",
                    request.getName().getName(),
                    subject.getSubject());
            return null;
        }
    }
}
