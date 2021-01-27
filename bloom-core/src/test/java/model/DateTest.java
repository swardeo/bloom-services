package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DateTest {

    Date sut;

    @Test
    void acceptsCorrectParametersWhenConstructed() {
        // given

        // when
        sut = new Date("2015-12");

        // then
        // no exception
    }

    @Test
    void returnsValidObjectWhenGetInvoked() {
        // given
        String expectedDate = "2015-12";

        sut = new Date(expectedDate);

        // when
        YearMonth actual = sut.getDate();

        // then
        assertThat(actual.toString()).isEqualTo(expectedDate);
    }

    @Test
    void returnsValidObjectWhenPublicConstructorUsed() {
        // given
        String date = "2015-12";

        sut = new Date(date);

        // when
        YearMonth actual = sut.getDate();

        // then
        assertThat(actual.getYear()).isEqualTo(2015);
        assertThat(actual.getMonthValue()).isEqualTo(12);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2015-00", "2015-13"})
    void throwsExceptionWhenInvalidMonth(String date) {
        // given

        try {
            // when
            new Date(date);
            shouldHaveThrown(IllegalArgumentException.class);

            // then
        } catch (IllegalArgumentException actual) {
            assertThat(actual).hasMessage("month should be in range [1, 12]");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"1915-06", "1939-06", "2051-06", "2075-06"})
    void throwsExceptionWhenInappropriateYear(String date) {
        // given

        try {
            // when
            new Date(date);
            shouldHaveThrown(IllegalArgumentException.class);

            // then
        } catch (IllegalArgumentException actual) {
            assertThat(actual).hasMessage("year should be in range [1940, 2050]");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"1980-06-bang", "crash-burn", "blah-06", "2030-06-245"})
    void throwsExceptionWhenBadlyFormattedDate(String date) {
        // given

        try {
            // when
            new Date(date);
            shouldHaveThrown(IllegalArgumentException.class);

            // then
        } catch (IllegalArgumentException actual) {
            assertThat(actual).hasMessage("date should be formatted like yyyy-MM");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"1940-06", "2050-06", "2000-01", "2000-12"})
    void noExceptionWhenValidDatesProvided(String date) {
        // given

        // when
        sut = new Date(date);

        // then
        // no exception
    }

    @ParameterizedTest
    @ValueSource(strings = {"2015-12", "2012-01", "1997-07"})
    void returnsCorrectValueWhenToStringInvoked(String expected) {
        // given
        sut = new Date(expected);

        // when
        String actual = sut.toString();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
