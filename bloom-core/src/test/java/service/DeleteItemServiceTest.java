package service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import model.Subject;
import model.Type;
import model.request.NameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

class DeleteItemServiceTest {

    DeleteItemService sut;
    DynamoService mockDynamoService;
    Subject mockSubject;
    NameRequest nameRequest;

    @BeforeEach
    void beforeEach() {
        mockDynamoService = mock(DynamoService.class);
        sut = new DeleteItemService(mockDynamoService);
        nameRequest = new NameRequest("am a name");

        mockSubject = mock(Subject.class);
        when(mockSubject.getSubject()).thenReturn("74sr7f7-j234fd-4385ds");
    }

    @Test
    void requestHasCorrectPrimaryKeyWhenInvoked() {
        // given
        Type type = Type.SAVING;

        // when
        sut.delete(mockSubject, type, nameRequest);

        // then
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(mockDynamoService).delete(captor.capture());
        Map<String, AttributeValue> actual = captor.getValue();

        assertThat(actual.get("PK").s()).isEqualTo("USER#" + mockSubject.getSubject());
    }

    @Test
    void requestHasCorrectNameWhenInvoked() {
        // given
        Type type = Type.SAVING;

        // when
        sut.delete(mockSubject, type, nameRequest);

        // then
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(mockDynamoService).delete(captor.capture());
        Map<String, AttributeValue> actual = captor.getValue();

        assertThat(actual.get("SK").s()).isEqualTo("SAVING#" + nameRequest.getName());
    }

    @ParameterizedTest
    @EnumSource(Type.class)
    void requestHasCorrectTypeWhenInvoked(Type type) {
        // given

        // when
        sut.delete(mockSubject, type, nameRequest);

        // then
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(mockDynamoService).delete(captor.capture());
        Map<String, AttributeValue> actual = captor.getValue();

        assertThat(actual.get("SK").s()).isEqualTo(type.getType() + "#" + nameRequest.getName());
    }
}
