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
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

class UpdateSavingServiceTest {

    UpdateSavingService sut;

    DynamoDbClient mockClient;
    String tableName;
    Map<String, AttributeValue> mockKey;
    Map<String, AttributeValue> mockAttributeValueMap;

    @BeforeEach
    void beforeEach() {
        mockClient = mock(DynamoDbClient.class);
        tableName = "MY_TABLE_NAME";
        mockKey = mock(Map.class);
        mockAttributeValueMap = mock(Map.class);

        sut = new UpdateSavingService(mockClient, tableName);
    }

    @Test
    void requestHasCorrectTableNameWhenInvoked() {
        // given

        // when
        sut.updateSaving(mockKey, mockAttributeValueMap);

        // then
        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(mockClient, times(1)).updateItem(captor.capture());
        UpdateItemRequest actual = captor.getValue();

        assertThat(actual.tableName()).isEqualTo(tableName);
    }

    @Test
    void requestHasCorrectKeyWhenInvoked() {
        // given

        // when
        sut.updateSaving(mockKey, mockAttributeValueMap);

        // then
        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(mockClient, times(1)).updateItem(captor.capture());
        UpdateItemRequest actual = captor.getValue();

        assertThat(actual.key()).isEqualTo(mockKey);
    }

    @Test
    void requestHasCorrectUpdateExpressionWhenInvoked() {
        // given
        String updateExpression =
                "SET #a = :startAmount, #b = :monthlyAmount, #c = :startDate, #d = :endDate, #e = :yearlyRate, #f = :adjustments, #g = :oneTimePayments";

        // when
        sut.updateSaving(mockKey, mockAttributeValueMap);

        // then
        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(mockClient, times(1)).updateItem(captor.capture());
        UpdateItemRequest actual = captor.getValue();

        assertThat(actual.updateExpression()).isEqualTo(updateExpression);
    }

    @Test
    void requestHasCorrectExpressionAttributeNamesWhenInvoked() {
        // given
        Map<String, String> expressionAttributeNames =
                Map.of(
                        "#a", "StartAmount",
                        "#b", "MonthlyAmount",
                        "#c", "StartDate",
                        "#d", "EndDate",
                        "#e", "YearlyRate",
                        "#f", "Adjustments",
                        "#g", "OneTimePayments");

        // when
        sut.updateSaving(mockKey, mockAttributeValueMap);

        // then
        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(mockClient, times(1)).updateItem(captor.capture());
        UpdateItemRequest actual = captor.getValue();

        assertThat(actual.expressionAttributeNames()).isEqualTo(expressionAttributeNames);
    }

    @Test
    void requestHasCorrectAttributeValueMapWhenInvoked() {
        // given

        // when
        sut.updateSaving(mockKey, mockAttributeValueMap);

        // then
        ArgumentCaptor<UpdateItemRequest> captor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(mockClient, times(1)).updateItem(captor.capture());
        UpdateItemRequest actual = captor.getValue();

        assertThat(actual.expressionAttributeValues()).isEqualTo(mockAttributeValueMap);
    }
}
