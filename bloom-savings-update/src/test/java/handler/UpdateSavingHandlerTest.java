package handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import handler.UpdateSavingHandler.UpdateSavingHandlerDelegate;
import java.util.Map;
import model.Name;
import model.Saving;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import service.UpdateSavingService;
import transform.SavingTransformer;

class UpdateSavingHandlerTest {

    Subject mockSubject;
    SavingTransformer mockTransformer;
    UpdateSavingService mockService;
    Saving mockSaving;
    Map mockKey;
    Map mockAttributeMap;

    @BeforeEach
    void beforeEach() {
        mockSubject = mock(Subject.class);
        mockTransformer = mock(SavingTransformer.class);
        mockService = mock(UpdateSavingService.class);
        mockSaving = mock(Saving.class);
        mockKey = mock(Map.class);
        mockAttributeMap = mock(Map.class);

        Name mockName = mock(Name.class);
        when(mockSaving.getName()).thenReturn(mockName);
        when(mockName.getName()).thenReturn("this is a name");

        when(mockSubject.getSubject()).thenReturn("blah");

        when(mockTransformer.toKey(mockSaving.getName(), mockSubject)).thenReturn(mockKey);

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
        sut.handle(mockSaving, mockSubject);

        // then
        verify(mockTransformer, times(1)).toKey(mockSaving.getName(), mockSubject);
    }

    @Test
    void transformerInvokedForAttributeMapWhenDelegateHandled() {
        // given
        UpdateSavingHandlerDelegate sut =
                new UpdateSavingHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(mockSaving, mockSubject);

        // then
        verify(mockTransformer, times(1)).toAttributeMap(mockSaving);
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given
        UpdateSavingHandlerDelegate sut =
                new UpdateSavingHandlerDelegate(mockTransformer, mockService);

        // when
        sut.handle(mockSaving, mockSubject);

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
        sut.handle(mockSaving, mockSubject);

        // then
        verify(mockLogger, times(1))
                .info(
                        "Saving {} updated for subject {}",
                        mockSaving.getName().getName(),
                        mockSubject.getSubject());
    }
}