package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class NameRequestTest {

    NameRequest sut;

    @Test
    void acceptsNameWhenConstructed() {
        // given

        // when
        sut = new NameRequest("hello");

        // then
        // no exception
    }

    @Test
    void returnsNameWhenGetInvoked() {
        // given
        String expected = "hello";
        sut = new NameRequest(expected);

        // when
        String actual = sut.getName();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void throwsExceptionWhenNullName(String name) {
        // given

        try {
            // when
            new NameRequest(name);
            shouldHaveThrown(IllegalArgumentException.class);

            // then
        } catch (IllegalArgumentException actual) {
            assertThat(actual).hasMessage("name cannot be null or empty");
        }
    }

    @ParameterizedTest
    @MethodSource("requestProvider")
    void deserializesCorrectlyWhenInvoked(String fromJson, Name expected)
            throws JsonProcessingException {
        // given
        ObjectMapper mapper = new ObjectMapper();

        // when
        NameRequest actual = mapper.readValue(fromJson, NameRequest.class);

        // then
        assertThat(actual.getName()).isEqualTo(expected.getName());
    }

    static Stream<Arguments> requestProvider() {
        return Stream.of(
                arguments("{\"name\":\"saving name\"}", new Name("saving name")),
                arguments("{\"name\":\"My name 22\"}", new Name("My name 22")),
                arguments("{\"name\":\"22 my name\"}", new Name("22 my name")));
    }
}
