package service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import exception.NoItemFoundException;
import java.util.Map;
import model.Saving;
import model.Subject;
import model.request.NameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import transform.SavingsDynamoTransformer;

class GetSavingServiceTest {

    GetSavingService sut;

    DynamoDbClient mockClient;
    String tableName;
    SavingsDynamoTransformer mockTransformer;
    Subject mockSubject;
    NameRequest mockNameRequest;

    @BeforeEach
    void beforeEach() {
        mockClient = mock(DynamoDbClient.class);
        tableName = "MY_TABLE_NAME";
        mockTransformer = mock(SavingsDynamoTransformer.class);

        mockSubject = mock(Subject.class);
        when(mockSubject.getSubject()).thenReturn("74sr7f7-j234fd-4385ds");

        mockNameRequest = mock(NameRequest.class);
        when(mockNameRequest.getName()).thenReturn("am a saving");

        sut = new GetSavingService(mockClient, tableName, mockTransformer);

        QueryResponse expectedResponse =
                QueryResponse.builder().items(mock(Map.class)).count(1).build();
        when(mockClient.query(any(QueryRequest.class))).thenReturn(expectedResponse);
        when(mockTransformer.toSaving(any(Map.class))).thenReturn(mock(Saving.class));
    }

    @Test
    void requestHasCorrectTableNameWhenInvoked() {
        // given

        // when
        sut.getSaving(mockSubject, mockNameRequest);

        // then
        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(mockClient, times(1)).query(captor.capture());
        QueryRequest actual = captor.getValue();

        assertThat(actual.tableName()).isEqualTo(tableName);
    }

    @Test
    void requestHasCorrectKeyConditionExpressionWhenInvoked() {
        // given
        String keyConditionExpression = "PK = :user AND SK = :saving";

        // when
        sut.getSaving(mockSubject, mockNameRequest);

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
                        AttributeValue.builder().s("USER#" + mockSubject.getSubject()).build(),
                        ":saving",
                        AttributeValue.builder().s("SAVING#" + mockNameRequest.getName()).build());

        // when
        sut.getSaving(mockSubject, mockNameRequest);

        // then
        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(mockClient, times(1)).query(captor.capture());
        QueryRequest actual = captor.getValue();

        assertThat(actual.expressionAttributeValues()).isEqualTo(expressionAttributeValues);
    }

    @Test
    void throwsExceptionWhenNoSavingFound() {
        // given
        QueryResponse expectedResponse = QueryResponse.builder().count(0).build();
        when(mockClient.query(any(QueryRequest.class))).thenReturn(expectedResponse);

        try {
            // when
            sut.getSaving(mockSubject, mockNameRequest);
            shouldHaveThrown(NoItemFoundException.class);

        } catch (NoItemFoundException actual) {
            // then
            assertThat(actual).hasMessage("no item found matching query");
        }
    }

    @Test
    void returnsSavingWhenInvoked() {
        // given
        Saving expected = mock(Saving.class);
        when(mockTransformer.toSaving(any(Map.class))).thenReturn(expected);

        // when
        Saving actual = sut.getSaving(mockSubject, mockNameRequest);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
