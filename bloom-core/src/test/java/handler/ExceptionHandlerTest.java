package handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;
import model.HandlerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;

class ExceptionHandlerTest {

    Logger logger;
    ExceptionHandler sut;

    @BeforeEach
    void beforeEach() {
        logger = mock(Logger.class);
        sut = new ExceptionHandler(logger);
    }

    @Test
    void logsErrorWhenInvoked() {
        // given
        String message = "burn";
        RuntimeException exception = new RuntimeException(message);

        // when
        sut.handleException(exception);

        // then
        verify(logger, times(1)).error(message, exception);
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void createsCorrectResponseWhenProvidedException(RuntimeException exception, int statusCode) {
        // given

        // when
        HandlerResponse actual = sut.handleException(exception);

        // then
        assertThat(actual.getStatusCode()).isEqualTo(statusCode);
    }

    @Test
    void returnsDefaultResponseWhenUnknownException() {
        // given
        RuntimeException exception = new TestException("test exception");

        // when
        HandlerResponse actual = sut.handleException(exception);

        // then
        assertThat(actual.getStatusCode()).isEqualTo(500);
    }

    static Stream<Arguments> exceptionProvider() {
        return Stream.of(arguments(new RuntimeException("exception"), 500));
    }

    static class TestException extends RuntimeException {
        TestException(String message) {
            super(message);
        }
    }
}
