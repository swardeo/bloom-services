package service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

class ListSavingsServiceTest {

    ListSavingsService sut;

    DynamoDbClient mockClient;
    String tableName;
    CognitoIdentity mockCognitoIdentity;

    @BeforeEach
    void beforeEach() {
        mockClient = mock(DynamoDbClient.class);
        tableName = "MY_TABLE_NAME";

        mockCognitoIdentity = mock(CognitoIdentity.class);
        when(mockCognitoIdentity.getIdentityId()).thenReturn("eu-west-2:74sr7f7-j234fd-4385ds");

        sut = new ListSavingsService(mockClient, tableName);
    }

    @Test
    void requestHasCorrectTableNameWhenInvoked() {
        // given

        // when
        sut.listSavings(mockCognitoIdentity);

        // then
        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(mockClient, times(1)).query(captor.capture());
        QueryRequest actual = captor.getValue();

        assertThat(actual.tableName()).isEqualTo(tableName);
    }

    @Test
    void requestHasCorrectKeyConditionExpressionWhenInvoked() {
        // given
        String keyConditionExpression = "PK = :user AND begins_with ( SK, :saving )";

        // when
        sut.listSavings(mockCognitoIdentity);

        // then
        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(mockClient, times(1)).query(captor.capture());
        QueryRequest actual = captor.getValue();

        assertThat(actual.keyConditionExpression()).isEqualTo(keyConditionExpression);
    }

    @Test
    void requestHasCorrectExpressionAttributeValuesWhenInvoked() {
        // given
        Map<String, AttributeValue> expressionAttributeValues =
                Map.of(
                        ":user",
                                AttributeValue.builder()
                                        .s("USER#" + mockCognitoIdentity.getIdentityId())
                                        .build(),
                        ":saving", AttributeValue.builder().s("SAVING#").build());

        // when
        sut.listSavings(mockCognitoIdentity);

        // then
        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(mockClient, times(1)).query(captor.capture());
        QueryRequest actual = captor.getValue();

        assertThat(actual.expressionAttributeValues()).isEqualTo(expressionAttributeValues);
    }

    @Test
    void methodReturnsCorrectResponseWhenInvoked() {
        // given
        QueryResponse expectedResponse = QueryResponse.builder().build();
        when(mockClient.query(any(QueryRequest.class))).thenReturn(expectedResponse);

        // when
        QueryResponse actual = sut.listSavings(mockCognitoIdentity);

        // then
        assertThat(actual).isEqualTo(expectedResponse);
    }
}
