package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HandlerRequestTest {

    HandlerRequest sut;

    @Test
    void returnsNewBuilderWhenInvoked() {
        // given

        // when
        HandlerRequest.newBuilder();

        // then
        // no exception
    }

    @Test
    void builderAcceptsHttpMethodWhenInvoked() {
        // given
        HandlerRequest.Builder builder = HandlerRequest.newBuilder();

        // when
        builder.withHttpMethod("POST");

        // then
        // no exception
    }

    @Test
    void builderAcceptsPathWhenInvoked() {
        // given
        HandlerRequest.Builder builder = HandlerRequest.newBuilder();

        // when
        builder.withPath("/blah/blah");

        // then
        // no exception
    }

    @Test
    void builderAcceptsQueryStringParametersWhenInvoked() {
        // given
        HandlerRequest.Builder builder = HandlerRequest.newBuilder();

        // when
        builder.withQueryStringParameter("blah", "crash");

        // then
        // no exception
    }

    @Test
    void builderAcceptsPathParametersWhenInvoked() {
        // given
        HandlerRequest.Builder builder = HandlerRequest.newBuilder();

        // when
        builder.withPathParameter("proxy", "hello/world");

        // then
        // no exception
    }

    @Test
    void builderAcceptsHeadersWhenInvoked() {
        // given
        HandlerRequest.Builder builder = HandlerRequest.newBuilder();

        // when
        builder.withHeader("blah", "crash");

        // then
        // no exception
    }

    @Test
    void builderAcceptsBodyWhenInvoked() {
        // given
        HandlerRequest.Builder builder = HandlerRequest.newBuilder();

        // when
        builder.withBody("hello");

        // then
        // no exception
    }

    @Test
    void builderConstructsCorrectResponseWhenBuilt() {
        // given
        String httpMethod = "Post";
        String path = "/hello/world";
        String queryStringParameterName = "name";
        String queryStringParameterValue = "me";
        String pathParameterName = "proxy";
        String pathParameterValue = "/hello/world";
        String headerName = "Content-Type";
        String headerValue = "application/json";
        String body = "test";

        HandlerRequest.Builder builder = HandlerRequest.newBuilder();

        builder.withHttpMethod(httpMethod)
                .withPath(path)
                .withQueryStringParameter(queryStringParameterName, queryStringParameterValue)
                .withPathParameter(pathParameterName, pathParameterValue)
                .withHeader(headerName, headerValue)
                .withBody(body);

        // when
        sut = builder.build();

        // then
        assertThat(sut.getHttpMethod()).isEqualTo(httpMethod);
        assertThat(sut.getPath()).isEqualTo(path);
        assertThat(sut.getBody()).isEqualTo(body);
        assertThat(sut.getQueryStringParameters().get(queryStringParameterName))
                .isEqualTo(queryStringParameterValue);
        assertThat(sut.getPathParameters().get(pathParameterName)).isEqualTo(pathParameterValue);
        assertThat(sut.getHeaders().get(headerName)).isEqualTo(headerValue);
    }

    @ParameterizedTest
    @MethodSource("requestProvider")
    void requestDeserializesCorrectlyWhenInvoked(String fromJson, HandlerRequest expected)
            throws JsonProcessingException {
        // given

        // when

        // then
        ObjectMapper mapper = new ObjectMapper();
        HandlerRequest request = mapper.readValue(fromJson, HandlerRequest.class);

        assertThat(request.getHttpMethod()).isEqualTo(expected.getHttpMethod());
    }

    static Stream<Arguments> requestProvider() {
        return Stream.of(
                arguments(
                        ("{\"httpMethod\":\"POST\",\"headers\":{\"Content-Type\":\"application/json\"}}"),
                        HandlerRequest.newBuilder()
                                .withHttpMethod("POST")
                                .withHeader("Content-Type", "application/json")
                                .build(),
                        ("{\"httpMethod\":\"POST\",\"path\":\"/hello/world\",\"pathParameters\":{\"proxy\":\"/hello/world\"},\"headers\":{\"Content-Type\":\"application/json\"}}"),
                        HandlerRequest.newBuilder()
                                .withHttpMethod("POST")
                                .withHeader("Content-Type", "application/json")
                                .withPath("/hello/world")
                                .withPathParameter("proxy", "/hello/world")
                                .build(),
                        ("{\"httpMethod\":\"POST\",\"path\":\"/hello/world\",\"body\":\"hello\",\"headers\":{\"Content-Type\":\"application/json\"},\"pathParameters\":{\"proxy\":\"/hello/world\"},\"queryStringParameters\":{\"name\":\"me\"}}"),
                        HandlerRequest.newBuilder()
                                .withHttpMethod("POST")
                                .withPath("/hello/world")
                                .withPathParameter("proxy", "/hello/world")
                                .withBody("hello")
                                .withQueryStringParameter("name", "me")
                                .withHeader("Content-Type", "application/json")
                                .build()));
    }
}
