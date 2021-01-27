package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class NameTest {

    Name sut;

    @Test
    void acceptsNameWhenConstructed() {
        // given

        // when
        sut = new Name("hello");

        // then
        // no exception
    }

    @Test
    void returnsNameWhenGetInvoked() {
        // given
        String expected = "hello";
        sut = new Name(expected);

        // when
        String actual = sut.getName();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void nameCannotBeModifiedOnceConstructed() {
        // given
        String expected = "hello";
        sut = new Name(expected);
        expected = "changed";

        // when
        String actual = sut.getName();

        // then
        assertThat(actual).isEqualTo("hello");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void throwsExceptionWhenNullName(String name) {
        // given

        try {
            // when
            new Name(name);
            shouldHaveThrown(IllegalArgumentException.class);

            // then
        } catch (IllegalArgumentException actual) {
            assertThat(actual).hasMessage("name cannot be null or empty");
        }
    }
}
