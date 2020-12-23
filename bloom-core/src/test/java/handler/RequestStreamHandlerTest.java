package handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import model.HandlerRequest;
import model.HandlerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

class RequestStreamHandlerTest {

    ObjectMapper mapper;
    TestDelegate delegate;
    InputStream input;
    OutputStream output;
    Context context;
    ExceptionHandler exceptionHandler;
    Logger logger;

    HandlerRequest handlerRequest;
    String request, requestBody, response, mappedResponse;

    RequestStreamHandler<String, String> sut;

    @BeforeEach
    void beforeEach() throws IOException {
        mapper = mock(ObjectMapper.class);
        delegate = mock(TestDelegate.class);
        input = mock(InputStream.class);
        output = mock(OutputStream.class);
        context = mock(Context.class);
        exceptionHandler = mock(ExceptionHandler.class);
        logger = mock(Logger.class);

        sut =
                new RequestStreamHandler<>(
                        mapper, delegate, String.class, String.class, exceptionHandler, logger);

        handlerRequest = mock(HandlerRequest.class);
        when(mapper.readValue(input, HandlerRequest.class)).thenReturn(handlerRequest);

        requestBody = "body";
        when(handlerRequest.getBody()).thenReturn(requestBody);

        request = "request";
        when(mapper.readValue(handlerRequest.getBody(), String.class)).thenReturn(request);

        response = "response";
        when(delegate.handle(request, context)).thenReturn(response);

        mappedResponse = "mapped response";
        when(mapper.writeValueAsString(response)).thenReturn(mappedResponse);
    }

    @Test
    void readsRequestWhenInvoked() throws IOException {
        // given

        // when
        sut.handleRequest(input, output, context);

        // then
        verify(mapper, times(1)).readValue(input, HandlerRequest.class);
    }

    @Test
    void readsRequestBodyWhenInvoked() throws IOException {
        // given

        // when
        sut.handleRequest(input, output, context);

        // then
        verify(mapper, times(1)).readValue(handlerRequest.getBody(), String.class);
    }

    @Test
    void handlesRequestWhenInvoked() throws IOException {
        // given

        // when
        sut.handleRequest(input, output, context);

        // then
        verify(delegate, times(1)).handle(request, context);
    }

    @Test
    void writesResponseUsingMapperWhenInvoked() throws IOException {
        // given

        // when
        sut.handleRequest(input, output, context);

        // then
        verify(mapper, times(1)).writeValueAsString(response);
    }

    @Test
    void constructsCorrectResponseWhenInvoked() throws IOException {
        // given

        // when
        sut.handleRequest(input, output, context);

        // then
        ArgumentCaptor<HandlerResponse> captor = ArgumentCaptor.forClass(HandlerResponse.class);
        verify(mapper, times(1)).writeValueAsBytes(captor.capture());
        HandlerResponse actual = captor.getValue();

        assertThat(actual.getStatusCode()).isEqualTo(200);
        assertThat(actual.getBody()).isEqualTo(mappedResponse);
    }

    @Test
    void outputsCorrectResponseWhenCompleted() throws IOException {
        // given

        // when
        sut.handleRequest(input, output, context);

        // then
        ArgumentCaptor<HandlerResponse> captor = ArgumentCaptor.forClass(HandlerResponse.class);
        verify(mapper, times(1)).writeValueAsBytes(captor.capture());
        HandlerResponse actualResponse = captor.getValue();

        verify(output, times(1)).write(mapper.writeValueAsBytes(actualResponse));
    }

    @Test
    void throwsExceptionWhenSerializationFails() throws IOException {
        // given
        when(mapper.readValue(input, HandlerRequest.class)).thenThrow(IOException.class);

        // when
        sut.handleRequest(input, output, context);

        // then
        ArgumentCaptor<HandlerResponse> captor = ArgumentCaptor.forClass(HandlerResponse.class);
        verify(mapper, times(1)).writeValueAsBytes(captor.capture());
        HandlerResponse actual = captor.getValue();

        assertThat(actual.getStatusCode()).isEqualTo(500);
    }

    @Test
    void logsExceptionWhenSerializationFails() throws IOException {
        // given
        String message = "blah";
        IOException exception = new IOException(message);
        when(mapper.readValue(input, HandlerRequest.class)).thenThrow(exception);

        // when
        sut.handleRequest(input, output, context);

        // then
        verify(logger, times(1)).error(message, exception);
    }

    @Test
    void handlesRuntimeExceptionsCorrectlyWhenInvoked() throws IOException {
        // given
        String message = "crash";
        RuntimeException expected = new RuntimeException(message);
        when(mapper.readValue(input, HandlerRequest.class)).thenThrow(expected);

        try {
            // when
            sut.handleRequest(input, output, context);

            // then
        } catch (RuntimeException e) {
            verify(exceptionHandler, times(1)).handleException(expected);
            assertThat(e).hasMessage(message);
        }
    }

    static class TestDelegate implements Handler<String, String> {
        @Override
        public String handle(String s, Context context) {
            return null;
        }
    }
}
