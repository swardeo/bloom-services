package handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.BadRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import model.HandlerRequest;
import model.HandlerResponse;
import model.RequestDetails;
import model.Subject;
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
    Subject subject;
    RequestDetails details;
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

        sut = new RequestStreamHandler<>(mapper, delegate, String.class, exceptionHandler, logger);

        handlerRequest = mock(HandlerRequest.class);
        when(handlerRequest.getHttpMethod()).thenReturn("GET");
        when(handlerRequest.getPath()).thenReturn("/blah");
        when(handlerRequest.getQueryStringParameters()).thenReturn(Map.of());
        when(handlerRequest.getPathParameters()).thenReturn(Map.of());
        when(handlerRequest.getHeaders()).thenReturn(Map.of());
        when(mapper.readValue(input, HandlerRequest.class)).thenReturn(handlerRequest);

        details = RequestDetails.fromHandlerRequest(handlerRequest);

        subject = mock(Subject.class);
        when(handlerRequest.getSubject()).thenReturn(subject);

        requestBody = "body";
        when(handlerRequest.getBody()).thenReturn(requestBody);

        request = "request";
        when(mapper.readValue(handlerRequest.getBody(), String.class)).thenReturn(request);

        response = "response";
        when(delegate.handle(eq(request), eq(subject), any())).thenReturn(response);

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
    void createsRequestDetailsWhenInvoked() throws IOException {
        // given

        // when
        sut.handleRequest(input, output, context);

        // then
        ArgumentCaptor<RequestDetails> captor = ArgumentCaptor.forClass(RequestDetails.class);
        verify(delegate, times(1)).handle(eq(request), eq(subject), captor.capture());
        RequestDetails actual = captor.getValue();

        assertThat(actual).usingRecursiveComparison().isEqualTo(details);
    }

    @Test
    void handlesRequestWhenInvoked() throws IOException {
        // given

        // when
        sut.handleRequest(input, output, context);

        // then
        verify(delegate, times(1)).handle(eq(request), eq(subject), any());
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
    void doesNotWriteResponseWhenNullResponse() throws IOException {
        // given
        String response = null;
        when(delegate.handle(eq(request), eq(subject), any())).thenReturn(response);

        // when
        sut.handleRequest(input, output, context);

        // then
        verify(mapper, never()).writeValueAsString(response);
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

        when(exceptionHandler.handleException(expected))
                .thenReturn(HandlerResponse.newBuilder().withStatusCode(500).build());

        // when
        sut.handleRequest(input, output, context);

        // then
        ArgumentCaptor<RuntimeException> captor = ArgumentCaptor.forClass(RuntimeException.class);
        verify(exceptionHandler, times(1)).handleException(captor.capture());
        RuntimeException actual = captor.getValue();

        assertThat(actual.getMessage()).isEqualTo(message);
    }

    @Test
    void throwsExceptionWhenBadRequestBody() throws IOException {
        // given
        JsonProcessingException exception = mock(JsonProcessingException.class);
        when(mapper.readValue(handlerRequest.getBody(), String.class)).thenThrow(exception);

        when(exceptionHandler.handleException(any()))
                .thenReturn(HandlerResponse.newBuilder().withStatusCode(400).build());

        // when
        sut.handleRequest(input, output, context);

        // then
        ArgumentCaptor<BadRequestException> captor =
                ArgumentCaptor.forClass(BadRequestException.class);
        verify(exceptionHandler, times(1)).handleException(captor.capture());
        BadRequestException actual = captor.getValue();

        assertThat(actual.getCause()).isEqualTo(exception);
        assertThat(actual.getMessage()).isEqualTo("request body contained illegal values");
    }

    @Test
    void logsStatusCodeWhenRequestCompleted() throws IOException {
        // given

        // when
        sut.handleRequest(input, output, context);

        // then
        ArgumentCaptor<HandlerResponse> captor = ArgumentCaptor.forClass(HandlerResponse.class);
        verify(mapper).writeValueAsBytes(captor.capture());
        HandlerResponse actual = captor.getValue();

        verify(logger).info("request completed with status {}", actual.getStatusCode());
    }

    static class TestDelegate implements Handler<String, String> {
        @Override
        public String handle(String s, Subject subject, RequestDetails details) {
            return null;
        }
    }
}
