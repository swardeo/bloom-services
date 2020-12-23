package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HandlerResponseTest {

    HandlerResponse sut;

    @Test
    void returnsNewBuilderWhenInvoked() {
        // given

        // when
        HandlerResponse.newBuilder();

        // then
        // no exception
    }

    @Test
    void builderAcceptsStatusCodeWhenInvoked() {
        // given
        HandlerResponse.Builder builder = HandlerResponse.newBuilder();

        // when
        builder.withStatusCode(200);

        // then
        // no exception
    }

    @Test
    void builderAcceptsBodyWhenInvoked() {
        // given
        HandlerResponse.Builder builder = HandlerResponse.newBuilder();

        // when
        builder.withBody("blah");

        // then
        // no exception
    }

    @Test
    void builderAcceptsHeadersWhenInvoked() {
        // given
        HandlerResponse.Builder builder = HandlerResponse.newBuilder();

        // when
        builder.withHeader("my header", "boom");

        // then
        // no exception
    }

    @Test
    void builderConstructsCorrectResponseWhenBuilt() {
        // given
        HandlerResponse.Builder builder = HandlerResponse.newBuilder();

        int statusCode = 200;
        String body = "blah";
        String headerName = "my header";
        String headerValue = "crash";

        builder.withStatusCode(statusCode).withBody(body).withHeader(headerName, headerValue);

        // when
        sut = builder.build();

        // then
        assertThat(sut.getStatusCode()).isEqualTo(statusCode);
        assertThat(sut.getBody()).isEqualTo(body);
        assertThat(sut.getHeaders().get(headerName)).isEqualTo(headerValue);
    }

    @Test
    void responseAlwaysContainsContentTypeWhenCreated() {
        // given
        HandlerResponse.Builder builder = HandlerResponse.newBuilder();

        // when
        sut = builder.build();

        // then
        assertThat(sut.getHeaders().get("Content-Type")).isEqualTo("application/json");
    }

    @ParameterizedTest
    @MethodSource("responseProvider")
    void responseSerializesCorrectlyWhenInvoked(
            HandlerResponse.Builder builder, String expectedJson) throws JsonProcessingException {
        // given

        // when
        sut = builder.build();

        // then
        ObjectMapper mapper = new ObjectMapper();

        JsonNode tree1 = mapper.valueToTree(sut);
        JsonNode tree2 = mapper.readTree(expectedJson);

        assertThat(tree1).isEqualTo(tree2);
    }

    static Stream<Arguments> responseProvider() {
        return Stream.of(
                arguments(
                        HandlerResponse.newBuilder().withStatusCode(200),
                        "{\"statusCode\":200,\"headers\":{\"Content-Type\":\"application/json\"}}"),
                arguments(
                        HandlerResponse.newBuilder().withStatusCode(200).withBody("hello"),
                        "{\"statusCode\":200,\"headers\":{\"Content-Type\":\"application/json\"},\"body\":\"hello\"}"),
                arguments(
                        HandlerResponse.newBuilder()
                                .withStatusCode(200)
                                .withBody("hello")
                                .withHeader("my header", "kaboom"),
                        "{\"statusCode\":200,\"headers\":{\"Content-Type\":\"application/json\",\"my header\":\"kaboom\"},\"body\":\"hello\"}"));
    }
}
