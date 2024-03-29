package handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import handler.UpdateDebtHandler.UpdateDebtHandlerDelegate;
import java.util.Map;
import model.Debt;
import model.Name;
import model.RequestDetails;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import service.UpdateDebtService;
import transform.DebtTransformer;

class UpdateDebtHandlerTest {
    UpdateDebtHandlerDelegate sut;

    Subject mockSubject;
    RequestDetails mockDetails;
    DebtTransformer mockTransformer;
    UpdateDebtService mockService;
    Debt mockDebt;
    Map mockKey;
    Map mockAttributeMap;
    Logger mockLogger;

    @BeforeEach
    void beforeEach() {
        mockSubject = mock(Subject.class);
        mockDetails = mock(RequestDetails.class);
        mockTransformer = mock(DebtTransformer.class);
        mockService = mock(UpdateDebtService.class);
        mockDebt = mock(Debt.class);
        mockLogger = mock(Logger.class);

        Name mockName = mock(Name.class);
        when(mockDebt.getName()).thenReturn(mockName);
        when(mockName.getName()).thenReturn("the name");
        when(mockSubject.getSubject()).thenReturn("hsdf-324jds3");

        when(mockTransformer.toKey(mockDebt.getName(), mockSubject)).thenReturn(mockKey);
        when(mockTransformer.toAttributeMap(mockDebt)).thenReturn(mockAttributeMap);

        sut = new UpdateDebtHandlerDelegate(mockTransformer, mockService, mockLogger);
    }

    @Test
    void transformerInvokedForKeyWhenDelegateHandled() {
        // given

        // when
        sut.handle(mockDebt, mockSubject, mockDetails);

        // then
        verify(mockTransformer).toKey(mockDebt.getName(), mockSubject);
    }

    @Test
    void transformerInvokedForAttributeValueMapWhenDelegateHandled() {
        // given

        // when
        sut.handle(mockDebt, mockSubject, mockDetails);

        // then
        verify(mockTransformer).toAttributeMap(mockDebt);
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given

        // when
        sut.handle(mockDebt, mockSubject, mockDetails);

        // then
        verify(mockService).update(mockKey, mockAttributeMap);
    }

    @Test
    void logsWhenDebtUpdated() {
        // given

        // when
        sut.handle(mockDebt, mockSubject, mockDetails);

        // then
        verify(mockLogger)
                .info(
                        "Debt {} updated for subject {}",
                        mockDebt.getName().getName(),
                        mockSubject.getSubject());
    }
}
