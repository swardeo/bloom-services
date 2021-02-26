package handler;

import static org.mockito.Mockito.*;

import handler.UpdateDebtHandler.UpdateDebtHandlerDelegate;
import java.util.Map;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import service.UpdateDebtService;
import transform.DebtTransformer;

class UpdateDebtHandlerTest {

    Subject mockSubject;
    RequestDetails mockDetails;
    DebtTransformer mockTransformer;
    UpdateDebtService mockService;
    Debt mockDebt;
    Map mockKey;
    Map mockAttributeMap;

    @BeforeEach
    void beforeEach() {
        mockSubject = mock(Subject.class);
        mockDetails = mock(RequestDetails.class);
        mockTransformer = mock(DebtTransformer.class);
        mockService = mock(UpdateDebtService.class);
        mockDebt = mock(Debt.class);

        Name mockName = mock(Name.class);
        when(mockDebt.getName()).thenReturn(mockName);
        when(mockName.getName()).thenReturn("the name");
        when(mockSubject.getSubject()).thenReturn("hsdf-324jds3");

        when(mockTransformer.toKey(mockDebt.getName(), mockSubject)).thenReturn(mockKey);
        when(mockTransformer.toAttributeMap(mockDebt)).thenReturn(mockAttributeMap);
    }

    @Test
    void delegateAcceptsCorrectParametersWhenConstructed() {
        // given

        // when
        Logger mockLogger = mock(Logger.class);
        UpdateDebtHandlerDelegate sut =
                new UpdateDebtHandlerDelegate(mockTransformer, mockService, mockLogger);

        // then
        // no exception
    }

    @Test
    void transformerInvokedForKeyWhenDelegateHandled() {
        // given
        Logger mockLogger = mock(Logger.class);
        UpdateDebtHandlerDelegate sut =
                new UpdateDebtHandlerDelegate(mockTransformer, mockService, mockLogger);

        // when
        sut.handle(mockDebt, mockSubject, mockDetails);

        // then
        verify(mockTransformer).toKey(mockDebt.getName(), mockSubject);
    }

    @Test
    void transformerInvokedForAttributeValueMapWhenDelegateHandled() {
        // given
        Logger mockLogger = mock(Logger.class);
        UpdateDebtHandlerDelegate sut =
                new UpdateDebtHandlerDelegate(mockTransformer, mockService, mockLogger);

        // when
        sut.handle(mockDebt, mockSubject, mockDetails);

        // then
        verify(mockTransformer).toAttributeMap(mockDebt);
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given
        Logger mockLogger = mock(Logger.class);
        UpdateDebtHandlerDelegate sut =
                new UpdateDebtHandlerDelegate(mockTransformer, mockService, mockLogger);

        // when
        sut.handle(mockDebt, mockSubject, mockDetails);

        // then
        verify(mockService).update(mockKey, mockAttributeMap);
    }

    @Test
    void logsWhenDebtUpdated() {
        // given
        Logger mockLogger = mock(Logger.class);
        UpdateDebtHandlerDelegate sut =
                new UpdateDebtHandlerDelegate(mockTransformer, mockService, mockLogger);

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
