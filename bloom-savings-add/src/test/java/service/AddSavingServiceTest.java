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
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

class AddSavingServiceTest {

    AddSavingService sut;

    DynamoDbClient mockClient;
    String tableName;
    Map<String, AttributeValue> mockAttributeValueMap;

    @BeforeEach
    void beforeEach() {
        mockClient = mock(DynamoDbClient.class);
        tableName = "MY_TABLE_NAME";
        mockAttributeValueMap = mock(Map.class);

        sut = new AddSavingService(mockClient, tableName);
    }

    @Test
    void requestHasCorrectTableNameWhenInvoked() {
        // given

        // when
        sut.addSaving(mockAttributeValueMap);

        // then
        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(mockClient, times(1)).putItem(captor.capture());
        PutItemRequest actual = captor.getValue();

        assertThat(actual.tableName()).isEqualTo(tableName);
    }

    @Test
    void requestHasCorrectAttributeValueMapWhenInvoked() {
        // given

        // when
        sut.addSaving(mockAttributeValueMap);

        // then
        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(mockClient, times(1)).putItem(captor.capture());
        PutItemRequest actual = captor.getValue();

        assertThat(actual.item()).isEqualTo(mockAttributeValueMap);
    }
}
