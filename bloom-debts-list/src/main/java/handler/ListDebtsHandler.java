package handler;

import static org.slf4j.LoggerFactory.getLogger;
import static provider.DynamoProvider.provideClient;
import static provider.DynamoProvider.provideTableName;
import static provider.MapperProvider.provideMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import model.Debt;
import model.RequestDetails;
import model.Subject;
import model.Type;
import org.slf4j.Logger;
import service.DynamoService;
import service.ListTypeService;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import transform.DebtsDynamoTransformer;

public class ListDebtsHandler extends RequestStreamHandler<Void, List<Debt>> {

    public static final ObjectMapper OBJECT_MAPPER = provideMapper();
    public static final DebtsDynamoTransformer DYNAMO_TRANSFORMER = new DebtsDynamoTransformer();
    public static final ListTypeService LIST_SERVICE =
            new ListTypeService(new DynamoService(provideClient(), provideTableName()));

    public ListDebtsHandler() {
        super(
                OBJECT_MAPPER,
                new ListDebtsHandlerDelegate(DYNAMO_TRANSFORMER, LIST_SERVICE),
                Void.class);
    }

    static class ListDebtsHandlerDelegate implements Handler<Void, List<Debt>> {

        private final DebtsDynamoTransformer transformer;
        private final ListTypeService service;
        private final Logger logger;

        ListDebtsHandlerDelegate(DebtsDynamoTransformer transformer, ListTypeService service) {
            this(transformer, service, getLogger(ListDebtsHandler.class));
        }

        ListDebtsHandlerDelegate(
                DebtsDynamoTransformer transformer, ListTypeService service, Logger logger) {
            this.transformer = transformer;
            this.service = service;
            this.logger = logger;
        }

        @Override
        public List<Debt> handle(Void request, Subject subject, RequestDetails details) {
            QueryResponse response = service.list(subject, Type.DEBT);
            List<Debt> debts = transformer.toDebtsList(response);

            logger.info("{} debts listed for subject {}", debts.size(), subject.getSubject());
            return debts;
        }
    }
}
