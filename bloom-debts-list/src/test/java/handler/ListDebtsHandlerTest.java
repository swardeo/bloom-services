package handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import handler.ListDebtsHandler.ListDebtsHandlerDelegate;
import java.util.List;
import model.Debt;
import model.RequestDetails;
import model.Subject;
import model.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import service.ListTypeService;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import transform.DebtsDynamoTransformer;

class ListDebtsHandlerTest {

    ListDebtsHandlerDelegate sut;

    Subject mockSubject;
    RequestDetails mockDetails;
    DebtsDynamoTransformer mockTransformer;
    ListTypeService mockService;
    QueryResponse response;
    List mockDebtsList;
    Logger mockLogger;

    @BeforeEach
    void beforeEach() {
        mockSubject = mock(Subject.class);
        mockDetails = mock(RequestDetails.class);
        mockTransformer = mock(DebtsDynamoTransformer.class);
        mockService = mock(ListTypeService.class);
        mockDebtsList = mock(List.class);
        mockLogger = mock(Logger.class);

        response = QueryResponse.builder().build();

        when(mockSubject.getSubject()).thenReturn("hsdf-324jds3");
        when(mockService.list(mockSubject, Type.DEBT)).thenReturn(response);
        when(mockTransformer.toDebtsList(response)).thenReturn(mockDebtsList);

        sut = new ListDebtsHandlerDelegate(mockTransformer, mockService, mockLogger);
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given

        // when
        sut.handle(null, mockSubject, mockDetails);

        // then
        verify(mockService).list(mockSubject, Type.DEBT);
    }

    @Test
    void transformerInvokedForQueryResponseWhenDelegateHandled() {
        // given

        // when
        sut.handle(null, mockSubject, mockDetails);

        // then
        ArgumentCaptor<QueryResponse> captor = ArgumentCaptor.forClass(QueryResponse.class);
        verify(mockTransformer).toDebtsList(captor.capture());
        QueryResponse actual = captor.getValue();

        assertThat(actual).isEqualTo(response);
    }

    @Test
    void returnsCorrectResponseWhenDelegateInvoked() {
        // given

        // when
        List<Debt> actual = sut.handle(null, mockSubject, mockDetails);

        // then
        assertThat(actual).isEqualTo(mockDebtsList);
    }

    @Test
    void logsWhenSavingsListReturned() {
        // given

        // when
        sut.handle(null, mockSubject, mockDetails);

        // then
        verify(mockLogger)
                .info(
                        "{} debts listed for subject {}",
                        mockDebtsList.size(),
                        mockSubject.getSubject());
    }
}
