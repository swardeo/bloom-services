package handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import handler.AddSavingHandler.AddSavingHandlerDelegate;
import java.util.Map;
import model.Name;
import model.RequestDetails;
import model.Saving;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import service.DynamoService;
import transform.SavingTransformer;

class AddSavingHandlerTest {

    AddSavingHandlerDelegate sut;

    SavingTransformer mockTransformer;
    DynamoService mockService;
    Saving mockSaving;
    Map mockAttributeMap;
    Subject mockSubject;
    RequestDetails mockDetails;
    Logger mockLogger;

    @BeforeEach
    void beforeEach() {
        mockTransformer = mock(SavingTransformer.class);
        mockService = mock(DynamoService.class);
        mockSaving = mock(Saving.class);
        mockAttributeMap = mock(Map.class);
        mockSubject = mock(Subject.class);
        mockDetails = mock(RequestDetails.class);
        mockLogger = mock(Logger.class);

        when(mockTransformer.toAttributeMap(mockSaving, mockSubject)).thenReturn(mockAttributeMap);

        when(mockSubject.getSubject()).thenReturn("blah");

        Name mockName = mock(Name.class);
        when(mockSaving.getName()).thenReturn(mockName);
        when(mockName.getName()).thenReturn("this is a name");

        sut = new AddSavingHandlerDelegate(mockTransformer, mockService, mockLogger);
    }

    @Test
    void transformerInvokedWhenDelegateHandled() {
        // given

        // when
        sut.handle(mockSaving, mockSubject, mockDetails);

        // then
        verify(mockTransformer).toAttributeMap(mockSaving, mockSubject);
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given

        // when
        sut.handle(mockSaving, mockSubject, mockDetails);

        // then
        verify(mockService).add(mockAttributeMap);
    }

    @Test
    void logsWhenSavingAdded() {
        // given

        // when
        sut.handle(mockSaving, mockSubject, mockDetails);

        // then
        verify(mockLogger)
                .info(
                        "Saving {} added for subject {}",
                        mockSaving.getName().getName(),
                        mockSubject.getSubject());
    }
}
