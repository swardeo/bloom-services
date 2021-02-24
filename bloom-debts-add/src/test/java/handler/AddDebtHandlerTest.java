package handler;

import static org.mockito.Mockito.times;

import handler.AddDebtHandler.AddDebtHandlerDelegate;
import java.util.Map;
import model.Debt;
import model.Name;
import model.RequestDetails;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import service.DynamoService;
import transform.DebtTransformer;

class AddDebtHandlerTest {

    DebtTransformer mockTransformer;
    DynamoService mockService;
    Debt mockDebt;
    Map mockAttributeMap;
    Subject mockSubject;
    RequestDetails mockDetails;

    @BeforeEach
    void beforeEach() {
        mockTransformer = Mockito.mock(DebtTransformer.class);
        mockService = Mockito.mock(DynamoService.class);
        mockDebt = Mockito.mock(Debt.class);
        mockAttributeMap = Mockito.mock(Map.class);
        mockSubject = Mockito.mock(Subject.class);
        mockDetails = Mockito.mock(RequestDetails.class);

        Mockito.when(mockTransformer.toAttributeMap(mockDebt, mockSubject))
                .thenReturn(mockAttributeMap);
        Mockito.when(mockSubject.getSubject()).thenReturn("blah");

        Name mockName = Mockito.mock(Name.class);
        Mockito.when(mockDebt.getName()).thenReturn(mockName);
        Mockito.when(mockName.getName()).thenReturn("this is a name");
    }

    @Test
    void delegateAcceptsCorrectParametersWhenConstructed() {
        // given

        // when
        new AddDebtHandlerDelegate(mockTransformer, mockService);

        // then
        // no exception
    }

    @Test
    void transformerInvokedWhenDelegateHandled() {
        // given
        AddDebtHandlerDelegate sut = new AddDebtHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(mockDebt, mockSubject, mockDetails);

        // then
        Mockito.verify(mockTransformer).toAttributeMap(mockDebt, mockSubject);
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given
        AddDebtHandlerDelegate sut = new AddDebtHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(mockDebt, mockSubject, mockDetails);

        // then
        Mockito.verify(mockService).add(mockAttributeMap);
    }

    @Test
    void logsWhenSavingAdded() {
        // given
        Logger mockLogger = Mockito.mock(Logger.class);
        AddDebtHandlerDelegate sut =
                new AddDebtHandlerDelegate(mockTransformer, mockService, mockLogger);

        // when
        sut.handle(mockDebt, mockSubject, mockDetails);

        // then
        Mockito.verify(mockLogger, times(1))
                .info(
                        "Debt {} added for subject {}",
                        mockDebt.getName().getName(),
                        mockSubject.getSubject());
    }
}
