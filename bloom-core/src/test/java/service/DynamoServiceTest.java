package service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

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

    @Test
    void listRequestHasCorrectTableNameWhenInvoked() {
        // given
        String keyConditionExpression = "my string";
        Map<String, AttributeValue> expressionAttributeValues = mock(Map.class);

        // when
        sut.list(keyConditionExpression, expressionAttributeValues);

        // then
        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(mockClient).query(captor.capture());
        QueryRequest actual = captor.getValue();

        assertThat(actual.tableName()).isEqualTo(tableName);
    }

    @Test
    void listRequestHasCorrectKeyConditionExpressionWhenInvoked() {
        // given
        String keyConditionExpression = "my key";
        Map<String, AttributeValue> expressionAttributeValues = mock(Map.class);

        // when
        sut.list(keyConditionExpression, expressionAttributeValues);

        // then
        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(mockClient).query(captor.capture());
        QueryRequest actual = captor.getValue();

        assertThat(actual.keyConditionExpression()).isEqualTo(keyConditionExpression);
    }

    @Test
    void listRequestHasCorrectExpressionAttributeValuesWhenInvoked() {
        // given
        String keyConditionExpression = "my key";
        Map<String, AttributeValue> expressionAttributeValues = mock(Map.class);

        // when
        sut.list(keyConditionExpression, expressionAttributeValues);

        // then
        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(mockClient).query(captor.capture());
        QueryRequest actual = captor.getValue();

        assertThat(actual.expressionAttributeValues()).isEqualTo(expressionAttributeValues);
    }

    @Test
    void listRequestReturnsCorrectResponseWhenInvoked() {
        // given
        String keyConditionExpression = "my key";
        Map<String, AttributeValue> expressionAttributeValues = mock(Map.class);

        QueryRequest request =
                QueryRequest.builder()
                        .tableName(tableName)
                        .keyConditionExpression(keyConditionExpression)
                        .expressionAttributeValues(expressionAttributeValues)
                        .build();

        QueryResponse expected = QueryResponse.builder().build();
        when(mockClient.query(eq(request))).thenReturn(expected);

        // when
        QueryResponse actual = sut.list(keyConditionExpression, expressionAttributeValues);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteRequestHasCorrectTableNameWhenInvoked() {
        // given
        Map key = mock(Map.class);

        // when
        sut.delete(key);

        // then
        ArgumentCaptor<DeleteItemRequest> captor = ArgumentCaptor.forClass(DeleteItemRequest.class);
        verify(mockClient).deleteItem(captor.capture());
        DeleteItemRequest actual = captor.getValue();

        assertThat(actual.tableName()).isEqualTo(tableName);
    }

    @Test
    void deleteRequestHasCorrectKeyWhenInvoked() {
        // given
        Map key = mock(Map.class);

        // when
        sut.delete(key);

        // then
        ArgumentCaptor<DeleteItemRequest> captor = ArgumentCaptor.forClass(DeleteItemRequest.class);
        verify(mockClient).deleteItem(captor.capture());
        DeleteItemRequest actual = captor.getValue();

        assertThat(actual.key()).isEqualTo(key);
    }

    @Test
    void updateRequestHasCorrectTableNameWhenInvoked() {
        // given
        Map<String, AttributeValue> mockKey = mock(Map.class);
        String updateExpression = "my update expression";
        Map<String, String> mockExpressionAttributeNames = mock(Map.class);
        Map<String, AttributeValue> mockAttributeValueMap = mock(Map.class);

        // when
        sut.update(mockKey, updateExpression, mockExpressionAttributeNames, mockAttributeValueMap);

        // then
        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(mockClient, times(1)).updateItem(captor.capture());
        UpdateItemRequest actual = captor.getValue();

        assertThat(actual.tableName()).isEqualTo(tableName);
    }

    @Test
    void updateRequestHasCorrectKeyWhenInvoked() {
        // given
        Map<String, AttributeValue> mockKey = mock(Map.class);
        String updateExpression = "my update expression";
        Map<String, String> mockExpressionAttributeNames = mock(Map.class);
        Map<String, AttributeValue> mockAttributeValueMap = mock(Map.class);

        // when
        sut.update(mockKey, updateExpression, mockExpressionAttributeNames, mockAttributeValueMap);

        // then
        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(mockClient, times(1)).updateItem(captor.capture());
        UpdateItemRequest actual = captor.getValue();

        assertThat(actual.key()).isEqualTo(mockKey);
    }

    @Test
    void updateRequestHasCorrectUpdateExpressionWhenInvoked() {
        // given
        Map<String, AttributeValue> mockKey = mock(Map.class);
        String updateExpression = "my update expression";
        Map<String, String> mockExpressionAttributeNames = mock(Map.class);
        Map<String, AttributeValue> mockAttributeValueMap = mock(Map.class);

        // when
        sut.update(mockKey, updateExpression, mockExpressionAttributeNames, mockAttributeValueMap);

        // then
        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(mockClient, times(1)).updateItem(captor.capture());
        UpdateItemRequest actual = captor.getValue();

        assertThat(actual.updateExpression()).isEqualTo(updateExpression);
    }

    @Test
    void updateRequestHasCorrectExpressionAttributeNamesWhenInvoked() {
        // given
        Map<String, AttributeValue> mockKey = mock(Map.class);
        String updateExpression = "my update expression";
        Map<String, String> mockExpressionAttributeNames = mock(Map.class);
        Map<String, AttributeValue> mockAttributeValueMap = mock(Map.class);

        // when
        sut.update(mockKey, updateExpression, mockExpressionAttributeNames, mockAttributeValueMap);

        // then
        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(mockClient, times(1)).updateItem(captor.capture());
        UpdateItemRequest actual = captor.getValue();

        assertThat(actual.expressionAttributeNames()).isEqualTo(mockExpressionAttributeNames);
    }

    @Test
    void updateRequestHasCorrectAttributeValueMapWhenInvoked() {
        // given
        Map<String, AttributeValue> mockKey = mock(Map.class);
        String updateExpression = "my update expression";
        Map<String, String> mockExpressionAttributeNames = mock(Map.class);
        Map<String, AttributeValue> mockAttributeValueMap = mock(Map.class);

        // when
        sut.update(mockKey, updateExpression, mockExpressionAttributeNames, mockAttributeValueMap);

        // then
        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(mockClient, times(1)).updateItem(captor.capture());
        UpdateItemRequest actual = captor.getValue();

        assertThat(actual.expressionAttributeValues()).isEqualTo(mockAttributeValueMap);
    }
}
