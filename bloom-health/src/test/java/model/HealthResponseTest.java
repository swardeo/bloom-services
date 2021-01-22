package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HealthResponseTest {

    @ParameterizedTest
    @MethodSource("healthProvider")
    void returnsCorrectHealthValueWhenInvoked(HealthResponse sut, String expected) {
        // given

        // when
        String actual = sut.getHealth();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> healthProvider() {
        return Stream.of(
                arguments(HealthResponse.HEALTHY, "healthy"),
                arguments(HealthResponse.UNHEALTHY, "unhealthy"));
    }

    @ParameterizedTest
    @MethodSource("responseProvider")
    void healthSerializesCorrectlyWhenInvoked(HealthResponse sut, String expected)
            throws JsonProcessingException {
        // given
        ObjectMapper mapper = new ObjectMapper();

        // when
        String actual = mapper.writeValueAsString(sut);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> responseProvider() {
        return Stream.of(
                arguments(HealthResponse.HEALTHY, "{\"health\":\"healthy\"}"),
                arguments(HealthResponse.UNHEALTHY, "{\"health\":\"unhealthy\"}"));
    }
}
