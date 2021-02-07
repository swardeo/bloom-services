package handler;

import exception.BadRequestException;
import exception.NotFoundException;
import java.util.HashMap;
import java.util.Map;
import model.HandlerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler {

    private final Logger logger;
    private final Map<Class<? extends RuntimeException>, Integer> exceptionResponseMap;

    public ExceptionHandler() {
        this(LoggerFactory.getLogger(ExceptionHandler.class));
    }

    ExceptionHandler(Logger logger) {
        this.logger = logger;
        this.exceptionResponseMap = new HashMap<>();
        populateExceptionResponseMap();
    }

    public HandlerResponse handleException(RuntimeException exception) {
        logger.error(exception.getMessage(), exception);

        if (exceptionResponseMap.containsKey(exception.getClass())) {
            return generateResponse(exceptionResponseMap.get(exception.getClass()));
        }
        return generateResponse(exceptionResponseMap.get(RuntimeException.class));
    }

    private void populateExceptionResponseMap() {
        exceptionResponseMap.put(BadRequestException.class, 400);
        exceptionResponseMap.put(NotFoundException.class, 404);
        exceptionResponseMap.put(RuntimeException.class, 500);
    }

    private HandlerResponse generateResponse(int statusCode) {
        return HandlerResponse.newBuilder().withStatusCode(statusCode).build();
    }
}
