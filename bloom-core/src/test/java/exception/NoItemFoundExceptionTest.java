package exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NoItemFoundExceptionTest {
    RuntimeException sut;

    @Test
    void extendsRuntimeExceptionWhenConstructed() {
        // given

        // when
        sut = new NoItemFoundException("blah");

        // then
        // no exception
    }

    @Test
    void usesProvidedMessageWhenThrown() {
        // given
        String expected = "my no item found exception";

        try {
            // when
            throw new NoItemFoundException(expected);

        } catch (NoItemFoundException actual) {
            // then
            assertThat(actual).hasMessage(expected);
        }
    }
}
