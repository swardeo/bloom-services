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
import org.mockito.Mockito;
import org.slf4j.Logger;
import service.ListTypeService;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import transform.DebtsDynamoTransformer;

class ListDebtsHandlerTest {

    Subject mockSubject;
    RequestDetails mockDetails;
    DebtsDynamoTransformer mockTransformer;
    ListTypeService mockService;
    QueryResponse response;
    List mockDebtsList;

    @BeforeEach
    void beforeEach() {
        mockSubject = Mockito.mock(Subject.class);
        mockDetails = Mockito.mock(RequestDetails.class);
        mockTransformer = Mockito.mock(DebtsDynamoTransformer.class);
        mockService = Mockito.mock(ListTypeService.class);
        response = QueryResponse.builder().build();
        mockDebtsList = mock(List.class);

        when(mockSubject.getSubject()).thenReturn("hsdf-324jds3");
        when(mockService.list(mockSubject, Type.DEBT)).thenReturn(response);
        when(mockTransformer.toDebtsList(response)).thenReturn(mockDebtsList);
    }

    @Test
    void delegateAcceptsCorrectParametersWhenConstructed() {
        // given

        // when
        new ListDebtsHandlerDelegate(mockTransformer, mockService);

        // then
        // no exception
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given
        ListDebtsHandlerDelegate sut = new ListDebtsHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(null, mockSubject, mockDetails);

        // then
        verify(mockService).list(mockSubject, Type.DEBT);
    }

    @Test
    void transformerInvokedForQueryResponseWhenDelegateHandled() {
        // given
        ListDebtsHandlerDelegate sut = new ListDebtsHandlerDelegate(mockTransformer, mockService);

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
        ListDebtsHandlerDelegate sut = new ListDebtsHandlerDelegate(mockTransformer, mockService);

        // when
        List<Debt> actual = sut.handle(null, mockSubject, mockDetails);

        // then
        assertThat(actual).isEqualTo(mockDebtsList);
    }

    @Test
    void logsWhenSavingsListReturned() {
        // given
        Logger mockLogger = mock(Logger.class);
        ListDebtsHandlerDelegate sut =
                new ListDebtsHandlerDelegate(mockTransformer, mockService, mockLogger);

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
