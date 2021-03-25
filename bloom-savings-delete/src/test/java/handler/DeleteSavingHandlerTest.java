package handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import handler.DeleteSavingHandler.DeleteSavingHandlerDelegate;
import model.RequestDetails;
import model.Subject;
import model.Type;
import model.request.NameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import service.DeleteItemService;

class DeleteSavingHandlerTest {

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
        new DeleteSavingHandlerDelegate(mockService);

        // then
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given
        DeleteSavingHandlerDelegate sut = new DeleteSavingHandlerDelegate(mockService);

        // when
        sut.handle(mockNameRequest, mockSubject, mockDetails);

        // then
        verify(mockService).delete(mockSubject, Type.SAVING, mockNameRequest);
    }

    @Test
    void logsCorrectlyWhenSavingDeleted() {
        // given
        Logger mockLogger = mock(Logger.class);
        DeleteSavingHandlerDelegate sut = new DeleteSavingHandlerDelegate(mockService, mockLogger);

        // when
        sut.handle(mockNameRequest, mockSubject, mockDetails);

        // then
        verify(mockLogger, times(1))
                .info(
                        "Saving {} deleted for subject {}",
                        mockNameRequest.getName(),
                        mockSubject.getSubject());
    }
}
