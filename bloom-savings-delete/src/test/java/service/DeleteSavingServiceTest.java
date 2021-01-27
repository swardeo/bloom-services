package service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;

class DeleteSavingServiceTest {

    DeleteSavingService sut;

    DynamoDbClient mockClient;
    String tableName;
    Map<String, AttributeValue> mockAttributeValueMap;

    @BeforeEach
    void beforeEach() {
        mockClient = mock(DynamoDbClient.class);
        tableName = "MY_TABLE_NAME";
        mockAttributeValueMap = mock(Map.class);

        sut = new DeleteSavingService(mockClient, tableName);
    }

    @Test
    void requestHasCorrectTableNameWhenInvoked() {
        // given

        // when
        sut.deleteSaving(mockAttributeValueMap);

        // then
        ArgumentCaptor<DeleteItemRequest> captor = ArgumentCaptor.forClass(DeleteItemRequest.class);
        verify(mockClient, times(1)).deleteItem(captor.capture());
        DeleteItemRequest actual = captor.getValue();

        assertThat(actual.tableName()).isEqualTo(tableName);
    }

    @Test
    void requestHasCorrectAttributeValueMapWhenInvoked() {
        // given

        // when
        sut.deleteSaving(mockAttributeValueMap);

        // then
        ArgumentCaptor<DeleteItemRequest> captor = ArgumentCaptor.forClass(DeleteItemRequest.class);
        verify(mockClient, times(1)).deleteItem(captor.capture());
        DeleteItemRequest actual = captor.getValue();

        assertThat(actual.key()).isEqualTo(mockAttributeValueMap);
    }
}
