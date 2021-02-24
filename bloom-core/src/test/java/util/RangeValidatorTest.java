package util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.mockito.Mockito.mock;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;
import model.Adjustment;
import model.Amount;
import model.Date;
import model.OneTimePayment;
import model.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class RangeValidatorTest {

    YearMonth startDate, endDate;

    @BeforeEach
    void beforeEach() {
        startDate = new Date("2000-09").getDate();
        endDate = new Date("2000-09").getDate();
    }

    @ParameterizedTest
    @MethodSource("adjustmentsDateProvider")
    void throwsExceptionWhenAdjustmentDateOutsideRange(List<Adjustment> adjustments) {
        // given
        String message = "adjustment dates should be in range (startDate, endDate)";

        try {
            // when
            RangeValidator.validateAdjustmentDates(startDate, endDate, adjustments, message);
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual).hasMessage(message);
        }
    }

    static Stream<List<Adjustment>> adjustmentsDateProvider() {
        return Stream.of(
                List.of(new Adjustment(mock(Amount.class), new Date("2000-08"), mock(Rate.class))),
                List.of(new Adjustment(mock(Amount.class), new Date("2000-09"), mock(Rate.class))),
                List.of(new Adjustment(mock(Amount.class), new Date("2050-12"), mock(Rate.class))),
                List.of(
                        new Adjustment(mock(Amount.class), new Date("2007-06"), mock(Rate.class)),
                        new Adjustment(mock(Amount.class), new Date("1999-09"), mock(Rate.class)),
                        new Adjustment(mock(Amount.class), new Date("2011-07"), mock(Rate.class))),
                List.of(
                        new Adjustment(mock(Amount.class), new Date("2007-06"), mock(Rate.class)),
                        new Adjustment(mock(Amount.class), new Date("2050-12"), mock(Rate.class)),
                        new Adjustment(mock(Amount.class), new Date("2003-09"), mock(Rate.class))));
    }

    @ParameterizedTest
    @MethodSource("oneTimePaymentDateProvider")
    void throwsExceptionWhenOneTimePaymentDateOutsideRange(List<OneTimePayment> oneTimePayments) {
        // given
        String message = "oneTimePayment dates should be in range (startDate, endDate)";

        try {
            // when
            RangeValidator.validateOneTimePaymentDates(
                    startDate, endDate, oneTimePayments, message);
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual).hasMessage(message);
        }
    }

    static Stream<List<OneTimePayment>> oneTimePaymentDateProvider() {
        return Stream.of(
                List.of(new OneTimePayment(mock(Amount.class), new Date("2000-08"))),
                List.of(new OneTimePayment(mock(Amount.class), new Date("2000-09"))),
                List.of(new OneTimePayment(mock(Amount.class), new Date("2012-08"))),
                List.of(new OneTimePayment(mock(Amount.class), new Date("2012-09"))),
                List.of(
                        new OneTimePayment(mock(Amount.class), new Date("2007-06")),
                        new OneTimePayment(mock(Amount.class), new Date("1999-09")),
                        new OneTimePayment(mock(Amount.class), new Date("2011-07"))),
                List.of(
                        new OneTimePayment(mock(Amount.class), new Date("2007-06")),
                        new OneTimePayment(mock(Amount.class), new Date("2003-09")),
                        new OneTimePayment(mock(Amount.class), new Date("2016-07"))));
    }
}
