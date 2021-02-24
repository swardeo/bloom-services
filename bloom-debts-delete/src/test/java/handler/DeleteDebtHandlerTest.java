package handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import handler.DeleteDebtHandler.DeleteDebtHandlerDelegate;
import model.RequestDetails;
import model.Subject;
import model.Type;
import model.request.NameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import service.DeleteItemService;

class DeleteDebtHandlerTest {

    Subject mockSubject;
    RequestDetails mockDetails;
    NameRequest mockNameRequest;
    DeleteItemService mockService;

    @BeforeEach
    void beforeEach() {
        mockSubject = mock(Subject.class);
        mockDetails = mock(RequestDetails.class);
        mockNameRequest = mock(NameRequest.class);
        mockService = mock(DeleteItemService.class);

        when(mockSubject.getSubject()).thenReturn("hsdf-324jds3");
        when(mockNameRequest.getName()).thenReturn("am item");
    }

    @Test
    void delegateConstructsCorrectlyWhenInvoked() {
        // given

        // when
        new DeleteDebtHandlerDelegate(mockService);

        // then
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given
        DeleteDebtHandlerDelegate sut = new DeleteDebtHandlerDelegate(mockService);

        // when
        sut.handle(mockNameRequest, mockSubject, mockDetails);

        // then
        verify(mockService).delete(mockSubject, Type.DEBT, mockNameRequest);
    }

    @Test
    void logsCorrectlyWhenSavingDeleted() {
        // given
        Logger mockLogger = mock(Logger.class);
        DeleteDebtHandlerDelegate sut = new DeleteDebtHandlerDelegate(mockService, mockLogger);

        // when
        sut.handle(mockNameRequest, mockSubject, mockDetails);

        // then
        verify(mockLogger)
                .info(
                        "Debt {} deleted for subject {}",
                        mockNameRequest.getName(),
                        mockSubject.getSubject());
    }
}
