package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TypeTest {

    @ParameterizedTest
    @MethodSource("typeProvider")
    void returnsCorrectHealthValueWhenInvoked(Type sut, String expected) {
        // given

        // when
        String actual = sut.getType();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> typeProvider() {
        return Stream.of(arguments(Type.SAVING, "SAVING"), arguments(Type.DEBT, "DEBT"));
    }
}
