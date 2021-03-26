package handler;

import static model.RequestDetails.fromHandlerRequest;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.BadRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import model.HandlerRequest;
import model.HandlerResponse;
import model.RequestDetails;
import model.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestStreamHandler<TRequest, TResponse>
        implements com.amazonaws.services.lambda.runtime.RequestStreamHandler {

    private final ObjectMapper mapper;
    private final Handler<TRequest, TResponse> delegate;
    private final Class<TRequest> requestClazz;
    private final ExceptionHandler exceptionHandler;
    private final Logger logger;

    public RequestStreamHandler(
            ObjectMapper mapper,
            Handler<TRequest, TResponse> delegate,
            Class<TRequest> requestClazz) {
        this(
                mapper,
                delegate,
                requestClazz,
                new ExceptionHandler(),
                LoggerFactory.getLogger(RequestStreamHandler.class));
    }

    RequestStreamHandler(
            ObjectMapper mapper,
            Handler<TRequest, TResponse> delegate,
            Class<TRequest> requestClazz,
            ExceptionHandler exceptionHandler,
            Logger logger) {
        this.mapper = mapper;
        this.delegate = delegate;
        this.requestClazz = requestClazz;
        this.exceptionHandler = exceptionHandler;
        this.logger = logger;
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context)
            throws IOException {
        HandlerResponse response;
        try {
            HandlerRequest request = mapper.readValue(input, HandlerRequest.class);
            Subject subject = request.getSubject();

            TRequest req = null;
            try {
                if (Void.class != requestClazz) {
                    req = mapper.readValue(request.getBody(), requestClazz);
                }
            } catch (JsonProcessingException exception) {
                throw new BadRequestException("request body contained illegal values", exception);
            }
            RequestDetails details = fromHandlerRequest(request);
            TResponse res = delegate.handle(req, subject, details);

            response =
                    null != res
                            ? HandlerResponse.newBuilder()
                                    .withBody(mapper.writeValueAsString(res))
                                    .withStatusCode(200)
                                    .build()
                            : HandlerResponse.newBuilder().withStatusCode(200).build();

        } catch (IOException exception) {
            logger.error(exception.getMessage(), exception);
            response = HandlerResponse.newBuilder().withStatusCode(500).build();
        } catch (RuntimeException exception) {
            response = exceptionHandler.handleException(exception);
        }
        logger.info("request completed with status {}", response.getStatusCode());
        output.write(mapper.writeValueAsBytes(response));
    }
}
