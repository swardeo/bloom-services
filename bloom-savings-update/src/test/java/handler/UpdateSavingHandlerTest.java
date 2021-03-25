package handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import handler.UpdateSavingHandler.UpdateSavingHandlerDelegate;
import java.util.Map;
import model.Name;
import model.RequestDetails;
import model.Saving;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import service.UpdateSavingService;
import transform.SavingTransformer;

class UpdateSavingHandlerTest {

    UpdateSavingHandlerDelegate sut;

    Subject mockSubject;
    RequestDetails mockDetails;
    SavingTransformer mockTransformer;
    UpdateSavingService mockService;
    Saving mockSaving;
    Map mockKey;
    Map mockAttributeMap;
    Logger mockLogger;

    @BeforeEach
    void beforeEach() {
        mockSubject = mock(Subject.class);
        mockDetails = mock(RequestDetails.class);
        mockTransformer = mock(SavingTransformer.class);
        mockService = mock(UpdateSavingService.class);
        mockSaving = mock(Saving.class);
        mockKey = mock(Map.class);
        mockAttributeMap = mock(Map.class);
        mockLogger = mock(Logger.class);

        Name mockName = mock(Name.class);
        when(mockSaving.getName()).thenReturn(mockName);
        when(mockName.getName()).thenReturn("this is a name");
        when(mockSubject.getSubject()).thenReturn("blah");

        when(mockTransformer.toKey(mockSaving.getName(), mockSubject)).thenReturn(mockKey);
        when(mockTransformer.toAttributeMap(mockSaving)).thenReturn(mockAttributeMap);

        sut = new UpdateSavingHandlerDelegate(mockTransformer, mockService, mockLogger);
    }

    @Test
    void transformerInvokedForKeyMapWhenDelegateHandled() {
        // given

        // when
        sut.handle(mockSaving, mockSubject, mockDetails);

        // then
        verify(mockTransformer).toKey(mockSaving.getName(), mockSubject);
    }

    @Test
    void transformerInvokedForAttributeMapWhenDelegateHandled() {
        // given

        // when
        sut.handle(mockSaving, mockSubject, mockDetails);

        // then
        verify(mockTransformer).toAttributeMap(mockSaving);
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given

        // when
        sut.handle(mockSaving, mockSubject, mockDetails);

        // then
        verify(mockService).updateSaving(mockKey, mockAttributeMap);
    }

    @Test
    void logsWhenSavingUpdated() {
        // given

        // when
        sut.handle(mockSaving, mockSubject, mockDetails);

        // then
        verify(mockLogger)
                .info(
                        "Saving {} updated for subject {}",
                        mockSaving.getName().getName(),
                        mockSubject.getSubject());
    }
}
