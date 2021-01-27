package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class RateTest {

    Rate sut;

    @Test
    void acceptsRateWhenConstructed() {
        // given

        // when
        sut = new Rate("2.22");

        // then
        // no exception
    }

    @ParameterizedTest
    @ValueSource(strings = {"-3.5", "-0.25", "0.00", "0.52", "1.98"})
    void returnsCorrectRateWhenGetInvoked(String expected) {
        // given
        sut = new Rate(expected);

        // when
        BigDecimal actual = sut.getRate();

        // then
        assertThat(actual.toString()).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void throwsExceptionWhenInvalidRate(String rate) {
        // given

        try {
            // when
            sut = new Rate(rate);
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual).hasMessage("rate cannot be null or empty");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"-3.5", "-0.25", "0.00", "0.52", "1.98"})
    void returnsCorrectValueWhenToStringInvoked(String expected) {
        // given
        sut = new Rate(expected);

        // when
        String actual = sut.toString();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
