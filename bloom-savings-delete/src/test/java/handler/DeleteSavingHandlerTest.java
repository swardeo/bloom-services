package handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import handler.DeleteSavingHandler.DeleteSavingHandlerDelegate;
import java.util.Map;
import model.NameRequest;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import service.DeleteSavingService;
import transform.NameRequestTransformer;

class DeleteSavingHandlerTest {

    Subject mockSubject;
    NameRequestTransformer mockTransformer;
    DeleteSavingService mockService;
    NameRequest mockNameRequest;
    CognitoIdentity mockIdentity;
    Map mockKey;

    @BeforeEach
    void beforeEach() {
        mockSubject = mock(Subject.class);
        mockTransformer = mock(NameRequestTransformer.class);
        mockService = mock(DeleteSavingService.class);
        mockNameRequest = mock(NameRequest.class);
        mockKey = mock(Map.class);

        when(mockNameRequest.getName()).thenReturn("hello");
        when(mockSubject.getSubject()).thenReturn("jsdfds-32423-dsf");

        when(mockTransformer.toKey(mockNameRequest, mockSubject)).thenReturn(mockKey);
    }

    @Test
    void delegateConstructsCorrectlyWhenInvoked() {
        // given

        // when
        new DeleteSavingHandlerDelegate(mockTransformer, mockService);

        // then
    }

    @Test
    void transformerInvokedWhenDelegateHandled() {
        // given
        DeleteSavingHandlerDelegate sut =
                new DeleteSavingHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(mockNameRequest, mockSubject);

        // then
        verify(mockTransformer, times(1)).toKey(mockNameRequest, mockSubject);
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given
        DeleteSavingHandlerDelegate sut =
                new DeleteSavingHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(mockNameRequest, mockSubject);

        // then
        verify(mockService, times(1)).deleteSaving(mockKey);
    }

    @Test
    void logsCorrectlyWhenSavingDeleted() {
        // given
        Logger mockLogger = mock(Logger.class);
        DeleteSavingHandlerDelegate sut =
                new DeleteSavingHandlerDelegate(mockTransformer, mockService, mockLogger);

        // when
        sut.handle(mockNameRequest, mockSubject);

        // then
        verify(mockLogger, times(1))
                .info(
                        "Saving {} deleted for subject {}",
                        mockNameRequest.getName(),
                        mockSubject.getSubject());
    }
}
