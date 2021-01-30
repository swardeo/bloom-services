package transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import java.util.Map;
import model.request.NameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

class NameRequestTransformerTest {

    NameRequestTransformer sut;
    NameRequest nameRequest;
    CognitoIdentity mockCognitoIdentity;

    @BeforeEach
    void beforeEach() {
        nameRequest = new NameRequest("a name");
        mockCognitoIdentity = mock(CognitoIdentity.class);

        when(mockCognitoIdentity.getIdentityId()).thenReturn("eu-west-2:74sr7f7-j234fd-4385ds");

        sut = new NameRequestTransformer();
    }

    @Test
    void mapContainsRequiredSavingAttributesWhenInvoked() {
        // given

        // when
        Map<String, AttributeValue> actual = sut.toKey(nameRequest, mockCognitoIdentity);

        // then
        assertThat(actual.get("PK").s()).isEqualTo("USER#" + mockCognitoIdentity.getIdentityId());
        assertThat(actual.get("SK").s()).isEqualTo("SAVING#" + nameRequest.getName());
    }
}
