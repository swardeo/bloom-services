package exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InvalidDateExceptionTest {

    RuntimeException sut;

    @Test
    void extendsRuntimeExceptionWhenConstructed() {
        // given

        // when
        sut = new InvalidDateException("blah");

        // then
        // no exception
    }

    @Test
    void usesProvidedMessageWhenThrown() {
        // given
        String expected = "my invalid date exception";

        try {
            // when
            throw new InvalidDateException(expected);

        } catch (InvalidDateException actual) {
            // then
            assertThat(actual).hasMessage(expected);
        }
    }
}
