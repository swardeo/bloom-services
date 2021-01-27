package exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BadRequestExceptionTest {
    RuntimeException sut;

    @Test
    void extendsRuntimeExceptionWhenConstructed() {
        // given

        // when
        sut = new BadRequestException("blah", null);

        // then
        // no exception
    }

    @Test
    void usesProvidedMessageWhenThrown() {
        // given
        String expected = "my bad request exception";

        try {
            // when
            throw new BadRequestException(expected, null);

        } catch (BadRequestException actual) {
            // then
            assertThat(actual).hasMessage(expected);
        }
    }

    @Test
    void hasCorrectCauseWhenThrown() {
        // given
        Throwable expected = new RuntimeException("cause");

        try {
            // when
            throw new BadRequestException("my bad request exception", expected);

        } catch (BadRequestException actual) {
            // then
            assertThat(actual).hasCauseReference(expected);
        }
    }
}
