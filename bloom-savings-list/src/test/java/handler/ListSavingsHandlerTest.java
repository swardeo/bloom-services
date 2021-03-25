package handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import handler.ListSavingsHandler.ListSavingsHandlerDelegate;
import java.util.List;
import model.RequestDetails;
import model.Saving;
import model.Subject;
import model.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import service.ListTypeService;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import transform.SavingsDynamoTransformer;

class ListSavingsHandlerTest {

    ListSavingsHandlerDelegate sut;

    Subject mockSubject;
    RequestDetails mockDetails;
    SavingsDynamoTransformer mockTransformer;
    ListTypeService mockService;
    QueryResponse response;
    List mockSavingsList;
    Logger mockLogger;

    @BeforeEach
    void beforeEach() {
        mockSubject = mock(Subject.class);
        mockDetails = mock(RequestDetails.class);
        mockTransformer = mock(SavingsDynamoTransformer.class);
        mockService = mock(ListTypeService.class);
        response = QueryResponse.builder().build();
        mockSavingsList = mock(List.class);
        mockLogger = mock(Logger.class);

        when(mockSubject.getSubject()).thenReturn("hsdf-324jds3");
        when(mockService.list(mockSubject, Type.SAVING)).thenReturn(response);
        when(mockTransformer.toSavingsList(response)).thenReturn(mockSavingsList);

        sut = new ListSavingsHandlerDelegate(mockTransformer, mockService, mockLogger);
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given

        // when
        sut.handle(null, mockSubject, mockDetails);

        // then
        verify(mockService).list(mockSubject, Type.SAVING);
    }

    @Test
    void transformerInvokedForAttributeMapWhenDelegateHandled() {
        // given

        // when
        sut.handle(null, mockSubject, mockDetails);

        // then
        ArgumentCaptor<QueryResponse> captor = ArgumentCaptor.forClass(QueryResponse.class);
        verify(mockTransformer, times(1)).toSavingsList(captor.capture());
        QueryResponse actual = captor.getValue();

        assertThat(actual).isEqualTo(response);
    }

    @Test
    void returnsCorrectResponseWhenDelegateInvoked() {
        // given

        // when
        List<Saving> actual = sut.handle(null, mockSubject, mockDetails);

        // then
        assertThat(actual).isEqualTo(mockSavingsList);
    }

    @Test
    void logsWhenSavingsListReturned() {
        // given

        // when
        sut.handle(null, mockSubject, mockDetails);

        // then
        verify(mockLogger, times(1))
                .info(
                        "{} savings listed for subject {}",
                        mockSavingsList.size(),
                        mockSubject.getSubject());
    }
}
