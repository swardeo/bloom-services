package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdjustmentTest {

    Adjustment sut;

    Amount amount;
    Date dateFrom;
    Rate rate;

    @BeforeEach
    void beforeEach() {
        amount = new Amount("11.92");
        dateFrom = new Date("2012-07");
        rate = new Rate("1.25");
    }

    @Test
    void acceptsCorrectParametersWhenConstructed() {
        // given

        // when
        sut = new Adjustment(amount, dateFrom, rate);

        // then
        // no exception
    }

    @Test
    void returnsCorrectAmountWhenGetInvoked() {
        // given
        sut = new Adjustment(amount, dateFrom, rate);

        // when
        Amount actual = sut.getAmount();

        // then
        assertThat(actual).isEqualTo(amount);
    }

    @Test
    void returnsCorrectDateFromWhenGetInvoked() {
        // given
        sut = new Adjustment(amount, dateFrom, rate);

        // when
        Date actual = sut.getDateFrom();

        // then
        assertThat(actual).isEqualTo(dateFrom);
    }

    @Test
    void returnsCorrectRateWhenGetInvoked() {
        // given
        sut = new Adjustment(amount, dateFrom, rate);

        // when
        Rate actual = sut.getRate();

        // then
        assertThat(actual).isEqualTo(rate);
    }

    @Test
    void throwsExceptionWhenNullAmountProvided() {
        // given

        try {
            // when
            sut = new Adjustment(null, dateFrom, rate);
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual).hasMessage("amount cannot be null");
        }
    }

    @Test
    void throwsExceptionWhenNullDateFromProvided() {
        // given

        try {
            // when
            sut = new Adjustment(amount, null, rate);
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual).hasMessage("dateFrom cannot be null");
        }
    }

    @Test
    void throwsExceptionWhenNullRateProvided() {
        // given

        try {
            // when
            sut = new Adjustment(amount, dateFrom, null);
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual).hasMessage("rate cannot be null");
        }
    }
}
