package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import model.HandlerRequest;
import model.HandlerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestStreamHandler<TRequest, TResponse>
        implements com.amazonaws.services.lambda.runtime.RequestStreamHandler {
    private final ObjectMapper mapper;
    private final Handler<TRequest, TResponse> delegate;
    private final Class<TRequest> requestClazz;
    private final Class<TResponse> responseClazz;
    private final ExceptionHandler exceptionHandler;
    private final Logger logger;

    public RequestStreamHandler(
            ObjectMapper mapper,
            Handler<TRequest, TResponse> delegate,
            Class<TRequest> requestClazz,
            Class<TResponse> responseClazz) {
        this(
                mapper,
                delegate,
                requestClazz,
                responseClazz,
                new ExceptionHandler(),
                LoggerFactory.getLogger(RequestStreamHandler.class));
    }

    RequestStreamHandler(
            ObjectMapper mapper,
            Handler<TRequest, TResponse> delegate,
            Class<TRequest> requestClazz,
            Class<TResponse> responseClazz,
            ExceptionHandler exceptionHandler,
            Logger logger) {
        this.mapper = mapper;
        this.delegate = delegate;
        this.requestClazz = requestClazz;
        this.responseClazz = responseClazz;
        this.exceptionHandler = exceptionHandler;
        this.logger = logger;
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context)
            throws IOException {
        HandlerResponse response;
        try {
            HandlerRequest request = mapper.readValue(input, HandlerRequest.class);
            TRequest req = mapper.readValue(request.getBody(), requestClazz);
            TResponse res = delegate.handle(req, context);

            response =
                    HandlerResponse.newBuilder()
                            .withBody(mapper.writeValueAsString(res))
                            .withStatusCode(200)
                            .build();

        } catch (IOException exception) {
            logger.error(exception.getMessage(), exception);
            response = HandlerResponse.newBuilder().withStatusCode(500).build();
        } catch (RuntimeException exception) {
            response = exceptionHandler.handleException(exception);
        }
        output.write(mapper.writeValueAsBytes(response));
    }
}
