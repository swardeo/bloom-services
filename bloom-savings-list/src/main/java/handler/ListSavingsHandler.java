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

    public static final ListTypeService LIST_SERVICE =
            new ListTypeService(new DynamoService(provideClient(), provideTableName()));

    public static final Logger LOGGER = getLogger(ListSavingsHandler.class);

    public static final ListSavingsHandlerDelegate DELEGATE =
            new ListSavingsHandlerDelegate(new SavingsDynamoTransformer(), LIST_SERVICE, LOGGER);

    public ListSavingsHandler() {
        super(OBJECT_MAPPER, DELEGATE, Void.class);
    }

    static class ListSavingsHandlerDelegate implements Handler<Void, List<Saving>> {

        private final SavingsDynamoTransformer transformer;
        private final ListTypeService service;
        private final Logger logger;

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
