package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class AmountTest {

    Amount sut;

    @Test
    void acceptsAmountWhenConstructed() {
        // given

        // when
        sut = new Amount("10.00");

        // then
        // no exception
    }

    @ParameterizedTest
    @ValueSource(strings = {"133.27", "0.52", "1.98"})
    void returnsAmountWhenGetInvoked(String expected) {
        // given
        sut = new Amount(expected);

        // when
        BigDecimal actual = sut.getAmount();

        // then
        assertThat(actual.toString()).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void throwsExceptionWhenInvalidAmount(String value) {
        // given

        try {
            // when
            sut = new Amount(value);
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual).hasMessage("amount cannot be null or empty");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"133.27", "0.52", "1.98"})
    void returnsCorrectValueWhenToStringInvoked(String expected) {
        // given
        sut = new Amount(expected);

        // when
        String actual = sut.toString();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
