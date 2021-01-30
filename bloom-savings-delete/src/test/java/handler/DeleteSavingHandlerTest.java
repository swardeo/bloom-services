package handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import handler.DeleteSavingHandler.DeleteSavingHandlerDelegate;
import java.util.Map;
import model.NameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import service.DeleteSavingService;
import transform.NameRequestTransformer;

class DeleteSavingHandlerTest {

    Context mockContext;
    NameRequestTransformer mockTransformer;
    DeleteSavingService mockService;
    NameRequest mockNameRequest;
    CognitoIdentity mockIdentity;
    Map mockKey;

    @BeforeEach
    void beforeEach() {
        mockContext = mock(Context.class);
        mockTransformer = mock(NameRequestTransformer.class);
        mockService = mock(DeleteSavingService.class);
        mockNameRequest = mock(NameRequest.class);
        mockIdentity = mock(CognitoIdentity.class);
        mockKey = mock(Map.class);

        when(mockNameRequest.getName()).thenReturn("hello");

        when(mockContext.getIdentity()).thenReturn(mockIdentity);
        when(mockIdentity.getIdentityId()).thenReturn("eu-west-2:jsdfds-32423-dsf");

        when(mockTransformer.toKey(mockNameRequest, mockContext.getIdentity())).thenReturn(mockKey);
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
        sut.handle(mockNameRequest, mockContext);

        // then
        verify(mockTransformer, times(1)).toKey(mockNameRequest, mockContext.getIdentity());
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given
        DeleteSavingHandlerDelegate sut =
                new DeleteSavingHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(mockNameRequest, mockContext);

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
        sut.handle(mockNameRequest, mockContext);

        // then
        verify(mockLogger, times(1))
                .info(
                        "Saving {} deleted for identity {}",
                        mockNameRequest.getName(),
                        mockContext.getIdentity().getIdentityId());
    }
}
