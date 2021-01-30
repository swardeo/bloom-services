package handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import handler.UpdateSavingHandler.UpdateSavingHandlerDelegate;
import java.util.Map;
import model.Name;
import model.Saving;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import service.UpdateSavingService;
import transform.SavingTransformer;

class UpdateSavingHandlerTest {

    Context mockContext;
    SavingTransformer mockTransformer;
    UpdateSavingService mockService;
    Saving mockSaving;
    Map mockKey;
    Map mockAttributeMap;
    CognitoIdentity mockIdentity;

    @BeforeEach
    void beforeEach() {
        mockContext = mock(Context.class);
        mockTransformer = mock(SavingTransformer.class);
        mockService = mock(UpdateSavingService.class);
        mockSaving = mock(Saving.class);
        mockKey = mock(Map.class);
        mockAttributeMap = mock(Map.class);
        mockIdentity = mock(CognitoIdentity.class);

        Name mockName = mock(Name.class);
        when(mockSaving.getName()).thenReturn(mockName);
        when(mockName.getName()).thenReturn("this is a name");

        when(mockContext.getIdentity()).thenReturn(mockIdentity);
        when(mockIdentity.getIdentityId()).thenReturn("blah");

        when(mockTransformer.toKey(mockSaving.getName(), mockIdentity)).thenReturn(mockKey);

        when(mockTransformer.toAttributeMap(mockSaving)).thenReturn(mockAttributeMap);
    }

    @Test
    void delegateAcceptsCorrectParametersWhenConstructed() {
        // given

        // when
        new UpdateSavingHandlerDelegate(mockTransformer, mockService);

        // then
        // no exception
    }

    @Test
    void transformerInvokedForKeyMapWhenDelegateHandled() {
        // given
        UpdateSavingHandlerDelegate sut =
                new UpdateSavingHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(mockSaving, mockContext);

        // then
        verify(mockTransformer, times(1)).toKey(mockSaving.getName(), mockIdentity);
    }

    @Test
    void transformerInvokedForAttributeMapWhenDelegateHandled() {
        // given
        UpdateSavingHandlerDelegate sut =
                new UpdateSavingHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(mockSaving, mockContext);

        // then
        verify(mockTransformer, times(1)).toAttributeMap(mockSaving);
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given
        UpdateSavingHandlerDelegate sut =
                new UpdateSavingHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(mockSaving, mockContext);

        // then
        verify(mockService, times(1)).updateSaving(mockKey, mockAttributeMap);
    }

    @Test
    void logsWhenSavingUpdated() {
        // given
        Logger mockLogger = mock(Logger.class);
        UpdateSavingHandlerDelegate sut =
                new UpdateSavingHandlerDelegate(mockTransformer, mockService, mockLogger);

        // when
        sut.handle(mockSaving, mockContext);

        // then
        verify(mockLogger, times(1))
                .info(
                        "Saving {} updated for identity {}",
                        mockSaving.getName().getName(),
                        mockContext.getIdentity().getIdentityId());
    }
}
