package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class SubjectTest {
    Subject sut;

    @Test
    void acceptsSubjectWhenConstructed() {
        // given

        // when
        sut = new Subject("my subject");

        // then
        // no exception
    }

    @Test
    void returnsSubjectWhenGetInvoked() {
        // given
        String expected = "am subject";
        sut = new Subject(expected);

        // when
        String actual = sut.getSubject();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void subjectCannotBeModifiedOnceConstructed() {
        // given
        String expected = "am subject";
        sut = new Subject(expected);
        expected = "am new subject";

        // when
        String actual = sut.getSubject();

        // then
        assertThat(actual).isEqualTo("am subject");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void throwsExceptionWhenBadSubject(String subject) {
        // given

        try {
            // when
            new Subject(subject);
            shouldHaveThrown(IllegalArgumentException.class);

            // then
        } catch (IllegalArgumentException actual) {
            assertThat(actual).hasMessage("subject cannot be null or empty");
        }
    }
}
