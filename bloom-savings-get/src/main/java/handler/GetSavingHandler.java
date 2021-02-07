package handler;

import static org.slf4j.LoggerFactory.getLogger;
import static provider.DynamoProvider.provideClient;
import static provider.DynamoProvider.provideTableName;
import static provider.MapperProvider.provideMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.BadRequestException;
import exception.NoItemFoundException;
import exception.NotFoundException;
import model.RequestDetails;
import model.Saving;
import model.Subject;
import model.request.NameRequest;
import org.slf4j.Logger;
import service.GetSavingService;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import transform.SavingsDynamoTransformer;

public class GetSavingHandler extends RequestStreamHandler<Void, Saving> {

    public static final ObjectMapper OBJECT_MAPPER = provideMapper();
    public static final DynamoDbClient DYNAMO_DB_CLIENT = provideClient();
    public static final String TABLE_NAME = provideTableName();
    public static final SavingsDynamoTransformer SAVING_TRANSFORMER =
            new SavingsDynamoTransformer();
    public static final GetSavingService GET_SAVING_SERVICE =
            new GetSavingService(DYNAMO_DB_CLIENT, TABLE_NAME, SAVING_TRANSFORMER);

    public GetSavingHandler() {
        super(OBJECT_MAPPER, new GetSavingHandlerDelegate(GET_SAVING_SERVICE), Void.class);
    }

    static class GetSavingHandlerDelegate implements Handler<Void, Saving> {

        private final GetSavingService service;
        private final Logger logger;

        GetSavingHandlerDelegate(GetSavingService service) {
            this(service, getLogger(GetSavingHandler.class));
        }

        GetSavingHandlerDelegate(GetSavingService service, Logger logger) {
            this.service = service;
            this.logger = logger;
        }

        @Override
        public Saving handle(Void request, Subject subject, RequestDetails details) {
            NameRequest name;
            Saving saving;
            try {
                name = new NameRequest(details.getPathParameters().get("name").replace("%20", " "));
                saving = service.getSaving(subject, name);
            } catch (IllegalArgumentException exception) {
                logger.error("Saving could not be retrieved for subject {}", subject.getSubject());
                throw new BadRequestException("illegal name provided to get saving", exception);
            } catch (NoItemFoundException exception) {
                logger.error(
                        "Saving {} could not be retrieved for subject {}",
                        details.getPathParameters().get("name"),
                        subject.getSubject());
                throw new NotFoundException("no saving could be retrieved", exception);
            }

            logger.info("Saving {} retrieved for subject {}", name.getName(), subject.getSubject());
            return saving;
        }
    }
}
