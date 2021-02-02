package handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import handler.ListSavingsHandler.ListSavingsHandlerDelegate;
import java.util.List;
import model.Saving;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import service.ListSavingsService;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import transform.DynamoTransformer;

class ListSavingsHandlerTest {

    Subject mockSubject;
    DynamoTransformer mockTransformer;
    ListSavingsService mockService;
    QueryResponse response;
    List mockSavingsList;

    @BeforeEach
    void beforeEach() {
        mockSubject = mock(Subject.class);
        mockTransformer = mock(DynamoTransformer.class);
        mockService = mock(ListSavingsService.class);
        response = QueryResponse.builder().build();
        mockSavingsList = mock(List.class);

        when(mockSubject.getSubject()).thenReturn("hsdf-324jds3");
        when(mockService.listSavings(mockSubject)).thenReturn(response);
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
        sut.handle(null, mockSubject);

        // then
        verify(mockService, times(1)).listSavings(mockSubject);
    }

    @Test
    void transformerInvokedForAttributeMapWhenDelegateHandled() {
        // given
        ListSavingsHandlerDelegate sut =
                new ListSavingsHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(null, mockSubject);

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
        List<Saving> actual = sut.handle(null, mockSubject);

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
        sut.handle(null, mockSubject);

        // then
        verify(mockLogger, times(1))
                .info(
                        "{} savings listed for subject {}",
                        mockSavingsList.size(),
                        mockSubject.getSubject());
    }
}
