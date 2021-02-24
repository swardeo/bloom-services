package service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import model.Subject;
import model.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

class ListTypeServiceTest {

    ListTypeService sut;
    DynamoService mockDynamoService;
    Subject mockSubject;

    @BeforeEach
    void beforeEach() {
        mockDynamoService = mock(DynamoService.class);
        sut = new ListTypeService(mockDynamoService);

        mockSubject = mock(Subject.class);
        when(mockSubject.getSubject()).thenReturn("74sr7f7-j234fd-4385ds");
    }

    @Test
    void requestHasCorrectKeyConditionExpressionWhenInvoked() {
        // given
        Type type = Type.SAVING;
        String keyConditionExpression = "PK = :user AND begins_with ( SK, :type )";

        // when
        sut.list(mockSubject, type);

        // then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockDynamoService).list(captor.capture(), any());
        String actual = captor.getValue();

        assertThat(actual).isEqualTo(keyConditionExpression);
    }

    @ParameterizedTest
    @EnumSource(Type.class)
    void requestHasCorrectExpressionAttributeValuesWhenInvoked(Type type) {
        // given
        Map<String, AttributeValue> expected =
                Map.of(
                        ":user",
                        AttributeValue.builder().s("USER#" + mockSubject.getSubject()).build(),
                        ":type",
                        AttributeValue.builder().s(type.getType() + "#").build());

        // when
        sut.list(mockSubject, type);

        // then
        ArgumentCaptor<Map<String, AttributeValue>> captor = ArgumentCaptor.forClass(Map.class);
        verify(mockDynamoService).list(any(), captor.capture());
        Map<String, AttributeValue> actual = captor.getValue();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void requestReturnsResponseFromServiceWhenInvoked() {
        // given
        Type type = Type.SAVING;
        QueryResponse expected = QueryResponse.builder().build();
        when(mockDynamoService.list(any(), any())).thenReturn(expected);

        // when
        QueryResponse actual = sut.list(mockSubject, type);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
