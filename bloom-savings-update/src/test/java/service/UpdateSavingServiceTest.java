package service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

class UpdateSavingServiceTest {

    UpdateSavingService sut;
    DynamoService mockDynamoService;

    Map<String, AttributeValue> mockKey;
    Map<String, AttributeValue> mockAttributeValueMap;

    @BeforeEach
    void beforeEach() {
        mockDynamoService = mock(DynamoService.class);
        sut = new UpdateSavingService(mockDynamoService);

        mockKey = mock(Map.class);
        mockAttributeValueMap = mock(Map.class);
    }

    @Test
    void requestHasCorrectKeyWhenInvoked() {
        // given

        // when
        sut.updateSaving(mockKey, mockAttributeValueMap);

        // then
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(mockDynamoService).update(captor.capture(), any(), any(), any());
        Map<String, AttributeValue> actual = captor.getValue();

        assertThat(actual).isEqualTo(mockKey);
    }

    @Test
    void requestHasCorrectUpdateExpressionWhenInvoked() {
        // given
        String updateExpression =
                "SET #a = :startAmount, #b = :monthlyAmount, #c = :startDate, #d = :endDate, #e = :yearlyRate, #f = :adjustments, #g = :oneTimePayments";

        // when
        sut.updateSaving(mockKey, mockAttributeValueMap);

        // then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockDynamoService).update(any(), captor.capture(), any(), any());
        String actual = captor.getValue();

        assertThat(actual).isEqualTo(updateExpression);
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
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(mockDynamoService).update(any(), any(), captor.capture(), any());
        Map<String, String> actual = captor.getValue();

        assertThat(actual).isEqualTo(expressionAttributeNames);
    }

    @Test
    void requestHasCorrectAttributeValueMapWhenInvoked() {
        // given

        // when
        sut.updateSaving(mockKey, mockAttributeValueMap);

        // then
        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(mockDynamoService).update(any(), any(), any(), captor.capture());
        Map<String, AttributeValue> actual = captor.getValue();

        assertThat(actual).isEqualTo(mockAttributeValueMap);
    }
}
