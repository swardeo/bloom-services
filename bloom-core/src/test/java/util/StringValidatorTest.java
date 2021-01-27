package util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringValidatorTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void throwsExceptionWhenNullOrEmptyString(String value) {
        // given
        String name = "myString";

        try {
            // when
            StringValidator.checkNullOrEmpty(value, name);
            shouldHaveThrown(IllegalArgumentException.class);

            // then
        } catch (IllegalArgumentException actual) {
            assertThat(actual).hasMessage(name + " cannot be null or empty");
        }
    }
}
