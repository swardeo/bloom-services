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
import model.Type;
import org.slf4j.Logger;
import service.DynamoService;
import service.ListTypeService;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import transform.SavingsDynamoTransformer;

public class ListSavingsHandler extends RequestStreamHandler<Void, List<Saving>> {

    public static final ObjectMapper OBJECT_MAPPER = provideMapper();
    public static final SavingsDynamoTransformer DYNAMO_TRANSFORMER =
            new SavingsDynamoTransformer();
    public static final ListTypeService LIST_SERVICE =
            new ListTypeService(new DynamoService(provideClient(), provideTableName()));

    public ListSavingsHandler() {
        super(
                OBJECT_MAPPER,
                new ListSavingsHandlerDelegate(DYNAMO_TRANSFORMER, LIST_SERVICE),
                Void.class);
    }

    static class ListSavingsHandlerDelegate implements Handler<Void, List<Saving>> {

        private final SavingsDynamoTransformer transformer;
        private final ListTypeService service;
        private final Logger logger;

        ListSavingsHandlerDelegate(SavingsDynamoTransformer transformer, ListTypeService service) {
            this(transformer, service, getLogger(ListSavingsHandlerDelegate.class));
        }

        ListSavingsHandlerDelegate(
                SavingsDynamoTransformer transformer, ListTypeService service, Logger logger) {
            this.transformer = transformer;
            this.service = service;
            this.logger = logger;
        }

        @Override
        public List<Saving> handle(Void request, Subject subject, RequestDetails details) {
            QueryResponse queryResponse = service.list(subject, Type.SAVING);
            List<Saving> savings = transformer.toSavingsList(queryResponse);

            logger.info("{} savings listed for subject {}", savings.size(), subject.getSubject());
            return savings;
        }
    }
}
