package util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ObjectValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"myObject", "secondObject"})
    void throwsExceptionWhenNull(String name) {
        // given

        try {
            // when
            ObjectValidator.checkNull(null, name);
            shouldHaveThrown(IllegalArgumentException.class);

            // then
        } catch (IllegalArgumentException actual) {
            assertThat(actual).hasMessage(name + " cannot be null");
        }
    }
}
