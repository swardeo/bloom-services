package service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

class DynamoServiceTest {

    DynamoService sut;

    DynamoDbClient mockClient;
    String tableName;

    @BeforeEach
    void beforeEach() {
        mockClient = mock(DynamoDbClient.class);
        tableName = "MY_TABLE_NAME";
        sut = new DynamoService(mockClient, tableName);
    }

    @Test
    void addRequestHasCorrectTableNameWhenInvoked() {
        // given
        Map<String, AttributeValue> mockAttributeValueMap = mock(Map.class);

        // when
        sut.add(mockAttributeValueMap);

        // then
        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(mockClient).putItem(captor.capture());
        PutItemRequest actual = captor.getValue();

        assertThat(actual.tableName()).isEqualTo(tableName);
    }

    @Test
    void addRequestHasCorrectAttributeValueMapWhenInvoked() {
        // given
        Map<String, AttributeValue> mockAttributeValueMap = mock(Map.class);

        // when
        sut.add(mockAttributeValueMap);

        // then
        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(mockClient).putItem(captor.capture());
        PutItemRequest actual = captor.getValue();

        assertThat(actual.item()).isEqualTo(mockAttributeValueMap);
    }
}
