package handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import handler.AddSavingHandler.AddSavingHandlerDelegate;
import java.util.Map;
import model.Name;
import model.Saving;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import service.AddSavingService;
import transform.SavingTransformer;

class AddSavingHandlerTest {

    Context mockContext;
    SavingTransformer mockTransformer;
    AddSavingService mockService;
    Saving mockSaving;
    Map mockAttributeMap;
    CognitoIdentity mockIdentity;

    @BeforeEach
    void beforeEach() {
        mockContext = mock(Context.class);
        mockTransformer = mock(SavingTransformer.class);
        mockService = mock(AddSavingService.class);
        mockSaving = mock(Saving.class);
        mockAttributeMap = mock(Map.class);
        mockIdentity = mock(CognitoIdentity.class);

        when(mockContext.getIdentity()).thenReturn(mockIdentity);
        when(mockTransformer.toAttributeMap(mockSaving, mockContext.getIdentity()))
                .thenReturn(mockAttributeMap);

        when(mockIdentity.getIdentityId()).thenReturn("blah");

        Name mockName = mock(Name.class);
        when(mockSaving.getName()).thenReturn(mockName);
        when(mockName.getName()).thenReturn("this is a name");
    }

    @Test
    void delegateAcceptsCorrectParametersWhenConstructed() {
        // given

        // when
        new AddSavingHandlerDelegate(mockTransformer, mockService);

        // then
        // no exception
    }

    @Test
    void transformerInvokedWhenDelegateHandled() {
        // given
        AddSavingHandlerDelegate sut = new AddSavingHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(mockSaving, mockContext);

        // then
        verify(mockTransformer, times(1)).toAttributeMap(mockSaving, mockContext.getIdentity());
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given
        AddSavingHandlerDelegate sut = new AddSavingHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(mockSaving, mockContext);

        // then
        verify(mockService, times(1)).addSaving(mockAttributeMap);
    }

    @Test
    void logsWhenSavingAdded() {
        // given
        Logger mockLogger = mock(Logger.class);
        AddSavingHandlerDelegate sut =
                new AddSavingHandlerDelegate(mockTransformer, mockService, mockLogger);

        // when
        sut.handle(mockSaving, mockContext);

        // then
        verify(mockLogger, times(1))
                .info(
                        "Saving {} added for identity {}",
                        mockSaving.getName().getName(),
                        mockContext.getIdentity().getIdentityId());
    }
}
