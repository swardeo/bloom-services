package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OneTimePaymentTest {

    OneTimePayment sut;

    Amount amount;
    Date date;
    Rate rate;

    @BeforeEach
    void beforeEach() {
        amount = new Amount("13.67");
        date = new Date("2009-08");
    }

    @Test
    void acceptsCorrectParametersWhenConstructed() {
        // given

        // when
        sut = new OneTimePayment(amount, date);

        // then
        // no exception
    }

    @Test
    void returnsCorrectAmountWhenGetInvoked() {
        // given
        sut = new OneTimePayment(amount, date);

        // when
        Amount actual = sut.getAmount();

        // then
        assertThat(actual).isEqualTo(amount);
    }

    @Test
    void returnsCorrectDateWhenGetInvoked() {
        // given
        sut = new OneTimePayment(amount, date);

        // when
        Date actual = sut.getDate();

        // then
        assertThat(actual).isEqualTo(date);
    }

    @Test
    void throwsExceptionWhenNullAmountProvided() {
        // given

        try {
            // when
            sut = new OneTimePayment(null, date);
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual).hasMessage("amount cannot be null");
        }
    }

    @Test
    void throwsExceptionWhenNullDateProvided() {
        // given

        try {
            // when
            sut = new OneTimePayment(amount, null);
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual).hasMessage("date cannot be null");
        }
    }
}
