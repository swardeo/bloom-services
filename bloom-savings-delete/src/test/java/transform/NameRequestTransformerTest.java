package transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import model.Subject;
import model.request.NameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

class NameRequestTransformerTest {

    NameRequestTransformer sut;
    NameRequest nameRequest;
    Subject mockSubject;

    @BeforeEach
    void beforeEach() {
        nameRequest = new NameRequest("a name");
        mockSubject = mock(Subject.class);

        when(mockSubject.getSubject()).thenReturn("74sr7f7-j234fd-4385ds");

        sut = new NameRequestTransformer();
    }

    @Test
    void mapContainsRequiredSavingAttributesWhenInvoked() {
        // given

        // when
        Map<String, AttributeValue> actual = sut.toKey(nameRequest, mockSubject);

        // then
        assertThat(actual.get("PK").s()).isEqualTo("USER#" + mockSubject.getSubject());
        assertThat(actual.get("SK").s()).isEqualTo("SAVING#" + nameRequest.getName());
    }
}
