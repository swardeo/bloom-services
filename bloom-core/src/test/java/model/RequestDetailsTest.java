package model;

import static model.RequestDetails.fromHandlerRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestDetailsTest {

    RequestDetails sut;
    HandlerRequest.Builder handlerRequestBuilder;

    String httpMethod;
    String path;
    Map<String, String> queryStringParameters;
    Map<String, String> pathParameters;
    Map<String, String> headers;

    @BeforeEach
    void beforeEach() {
        httpMethod = "POST";
        path = "/blah";
        queryStringParameters = new HashMap<>(Map.of());
        pathParameters = new HashMap<>(Map.of());
        headers = new HashMap<>(Map.of());

        handlerRequestBuilder =
                HandlerRequest.newBuilder()
                        .withHttpMethod(httpMethod)
                        .withPath(path)
                        .withQueryStringParameters(queryStringParameters)
                        .withPathParameters(pathParameters)
                        .withHeaders(headers);
    }

    @Test
    void acceptsCorrectParametersWhenStaticFactoryInvoked() {
        // given
        HandlerRequest handlerRequest = handlerRequestBuilder.build();

        // when
        sut = fromHandlerRequest(handlerRequest);

        // then
        // no exception
    }

    @Test
    void retrievesCorrectHttpMethodWhenInvoked() {
        // given
        HandlerRequest handlerRequest = handlerRequestBuilder.build();
        sut = fromHandlerRequest(handlerRequest);

        // when
        String actual = sut.getHttpMethod();

        // then
        assertThat(actual).isEqualTo(httpMethod);
    }

    @Test
    void retrievesCorrectPathWhenInvoked() {
        // given
        HandlerRequest handlerRequest = handlerRequestBuilder.build();
        sut = fromHandlerRequest(handlerRequest);

        // when
        String actual = sut.getPath();

        // then
        assertThat(actual).isEqualTo(path);
    }

    @Test
    void retrievesCorrectQueryStringParametersWhenInvoked() {
        // given
        HandlerRequest handlerRequest = handlerRequestBuilder.build();
        sut = fromHandlerRequest(handlerRequest);

        // when
        Map<String, String> actual = sut.getQueryStringParameters();

        // then
        assertThat(actual).isEqualTo(queryStringParameters);
    }

    @Test
    void retrievesCorrectPathParametersWhenInvoked() {
        // given
        HandlerRequest handlerRequest = handlerRequestBuilder.build();
        sut = fromHandlerRequest(handlerRequest);

        // when
        Map<String, String> actual = sut.getPathParameters();

        // then
        assertThat(actual).isEqualTo(pathParameters);
    }

    @Test
    void retrievesCorrectHeadersWhenInvoked() {
        // given
        HandlerRequest handlerRequest = handlerRequestBuilder.build();
        sut = fromHandlerRequest(handlerRequest);

        // when
        Map<String, String> actual = sut.getHeaders();

        // then
        assertThat(actual).isEqualTo(headers);
    }

    @Test
    void cannotModifyQueryStringParametersOnceConstructed() {
        // given
        HandlerRequest handlerRequest = handlerRequestBuilder.build();
        sut = fromHandlerRequest(handlerRequest);
        queryStringParameters.put("parameter", "value");

        // when
        Map<String, String> actual = sut.getQueryStringParameters();

        // then
        assertThat(actual).hasSize(0);
    }

    @Test
    void cannotModifyPathParametersOnceConstructed() {
        // given
        HandlerRequest handlerRequest = handlerRequestBuilder.build();
        sut = fromHandlerRequest(handlerRequest);
        pathParameters.put("parameter", "value");

        // when
        Map<String, String> actual = sut.getPathParameters();

        // then
        assertThat(actual).hasSize(0);
    }

    @Test
    void cannotModifyHeadersOnceConstructed() {
        // given
        HandlerRequest handlerRequest = handlerRequestBuilder.build();
        sut = fromHandlerRequest(handlerRequest);
        headers.put("header", "value");

        // when
        Map<String, String> actual = sut.getHeaders();

        // then
        assertThat(actual).hasSize(0);
    }

    @Test
    void noExceptionThrownWhenQueryStringParametersNull() {
        // given
        HandlerRequest handlerRequest =
                handlerRequestBuilder.withQueryStringParameters(null).build();

        // when
        sut = fromHandlerRequest(handlerRequest);

        // then
        // no exception
    }

    @Test
    void noExceptionThrownWhenPathParametersNull() {
        // given
        HandlerRequest handlerRequest = handlerRequestBuilder.withPathParameters(null).build();

        // when
        sut = fromHandlerRequest(handlerRequest);

        // then
        // no exception
    }

    @Test
    void noExceptionThrownWhenHeadersNull() {
        // given
        HandlerRequest handlerRequest = handlerRequestBuilder.withHeaders(null).build();

        // when
        sut = fromHandlerRequest(handlerRequest);

        // then
        // no exception
    }
}
