package exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {
    RuntimeException sut;

    @Test
    void extendsRuntimeExceptionWhenConstructed() {
        // given

        // when
        sut = new NotFoundException("blah", null);

        // then
        // no exception
    }

    @Test
    void usesProvidedMessageWhenThrown() {
        // given
        String expected = "my not found exception";

        try {
            // when
            throw new NotFoundException(expected, null);

        } catch (NotFoundException actual) {
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
            throw new NotFoundException("my not found exception", expected);

        } catch (NotFoundException actual) {
            // then
            assertThat(actual).hasCauseReference(expected);
        }
    }
}
