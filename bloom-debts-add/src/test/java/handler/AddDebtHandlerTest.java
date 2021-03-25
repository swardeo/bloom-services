package handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import handler.AddDebtHandler.AddDebtHandlerDelegate;
import java.util.Map;
import model.Debt;
import model.Name;
import model.RequestDetails;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import service.DynamoService;
import transform.DebtTransformer;

class AddDebtHandlerTest {

    AddDebtHandlerDelegate sut;

    DebtTransformer mockTransformer;
    DynamoService mockService;
    Debt mockDebt;
    Map mockAttributeMap;
    Subject mockSubject;
    RequestDetails mockDetails;
    Logger mockLogger;

    @BeforeEach
    void beforeEach() {
        mockTransformer = mock(DebtTransformer.class);
        mockService = mock(DynamoService.class);
        mockDebt = mock(Debt.class);
        mockAttributeMap = mock(Map.class);
        mockSubject = mock(Subject.class);
        mockDetails = mock(RequestDetails.class);
        mockLogger = mock(Logger.class);

        when(mockTransformer.toAttributeMap(mockDebt, mockSubject)).thenReturn(mockAttributeMap);
        when(mockSubject.getSubject()).thenReturn("blah");

        Name mockName = mock(Name.class);
        when(mockDebt.getName()).thenReturn(mockName);
        when(mockName.getName()).thenReturn("this is a name");

        sut = new AddDebtHandlerDelegate(mockTransformer, mockService, mockLogger);
    }

    @Test
    void transformerInvokedWhenDelegateHandled() {
        // given

        // when
        sut.handle(mockDebt, mockSubject, mockDetails);

        // then
        verify(mockTransformer).toAttributeMap(mockDebt, mockSubject);
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given

        // when
        sut.handle(mockDebt, mockSubject, mockDetails);

        // then
        verify(mockService).add(mockAttributeMap);
    }

    @Test
    void logsWhenSavingAdded() {
        // given

        // when
        sut.handle(mockDebt, mockSubject, mockDetails);

        // then
        verify(mockLogger)
                .info(
                        "Debt {} added for subject {}",
                        mockDebt.getName().getName(),
                        mockSubject.getSubject());
    }
}
