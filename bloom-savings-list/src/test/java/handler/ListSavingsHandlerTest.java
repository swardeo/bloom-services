package handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import handler.ListSavingsHandler.ListSavingsHandlerDelegate;
import java.util.List;
import model.Saving;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import service.ListSavingsService;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import transform.DynamoTransformer;

class ListSavingsHandlerTest {

    Context mockContext;
    DynamoTransformer mockTransformer;
    ListSavingsService mockService;
    CognitoIdentity mockIdentity;
    QueryResponse response;
    List mockSavingsList;

    @BeforeEach
    void beforeEach() {
        mockContext = mock(Context.class);
        mockTransformer = mock(DynamoTransformer.class);
        mockService = mock(ListSavingsService.class);
        mockIdentity = mock(CognitoIdentity.class);
        response = QueryResponse.builder().build();
        mockSavingsList = mock(List.class);

        when(mockContext.getIdentity()).thenReturn(mockIdentity);
        when(mockService.listSavings(mockIdentity)).thenReturn(response);
        when(mockTransformer.toSavingsList(response)).thenReturn(mockSavingsList);
    }

    @Test
    void delegateAcceptsCorrectParametersWhenConstructed() {
        // given

        // when
        new ListSavingsHandlerDelegate(mockTransformer, mockService);

        // then
        // no exception
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given
        ListSavingsHandlerDelegate sut =
                new ListSavingsHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(null, mockContext);

        // then
        verify(mockService, times(1)).listSavings(mockIdentity);
    }

    @Test
    void transformerInvokedForAttributeMapWhenDelegateHandled() {
        // given
        ListSavingsHandlerDelegate sut =
                new ListSavingsHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(null, mockContext);

        // then
        ArgumentCaptor<QueryResponse> captor = ArgumentCaptor.forClass(QueryResponse.class);
        verify(mockTransformer, times(1)).toSavingsList(captor.capture());
        QueryResponse actual = captor.getValue();

        assertThat(actual).isEqualTo(response);
    }

    @Test
    void returnsCorrectResponseWhenDelegateInvoked() {
        // given
        ListSavingsHandlerDelegate sut =
                new ListSavingsHandlerDelegate(mockTransformer, mockService);

        // when
        List<Saving> actual = sut.handle(null, mockContext);

        // then
        assertThat(actual).isEqualTo(mockSavingsList);
    }

    @Test
    void logsWhenSavingsListReturned() {
        // given
        Logger mockLogger = mock(Logger.class);
        // given
        ListSavingsHandlerDelegate sut =
                new ListSavingsHandlerDelegate(mockTransformer, mockService, mockLogger);

        // when
        sut.handle(null, mockContext);

        // then
        verify(mockLogger, times(1))
                .info(
                        "{} savings listed for identity {}",
                        mockSavingsList.size(),
                        mockContext.getIdentity().getIdentityId());
    }
}
