package handler;

import static org.slf4j.LoggerFactory.getLogger;
import static provider.DynamoProvider.provideClient;
import static provider.DynamoProvider.provideTableName;
import static provider.MapperProvider.provideMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.RequestDetails;
import model.Subject;
import model.Type;
import model.request.NameRequest;
import org.slf4j.Logger;
import service.DeleteItemService;
import service.DynamoService;

public class DeleteSavingHandler extends RequestStreamHandler<NameRequest, Void> {

    public static final ObjectMapper OBJECT_MAPPER = provideMapper();
    public static final DeleteItemService DELETE_SERVICE =
            new DeleteItemService(new DynamoService(provideClient(), provideTableName()));

    public DeleteSavingHandler() {
        super(OBJECT_MAPPER, new DeleteSavingHandlerDelegate(DELETE_SERVICE), NameRequest.class);
    }

    static class DeleteSavingHandlerDelegate implements Handler<NameRequest, Void> {

        private final DeleteItemService service;
        private final Logger logger;

        DeleteSavingHandlerDelegate(DeleteItemService service) {
            this(service, getLogger(DeleteSavingHandler.class));
        }

        DeleteSavingHandlerDelegate(DeleteItemService service, Logger logger) {
            this.service = service;
            this.logger = logger;
        }

        @Override
        public Void handle(NameRequest request, Subject subject, RequestDetails details) {
            service.delete(subject, Type.SAVING, request);
            logger.info(
                    "Saving {} deleted for subject {}", request.getName(), subject.getSubject());
            return null;
        }
    }
}
