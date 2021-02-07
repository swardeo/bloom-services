package handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import exception.BadRequestException;
import exception.NoItemFoundException;
import exception.NotFoundException;
import handler.GetSavingHandler.GetSavingHandlerDelegate;
import java.util.Map;
import model.RequestDetails;
import model.Saving;
import model.Subject;
import model.request.NameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import service.GetSavingService;

class GetSavingHandlerTest {

    Subject mockSubject;
    RequestDetails mockDetails;
    GetSavingService mockService;
    Map<String, String> pathParamters;

    @BeforeEach
    void beforeEach() {
        mockSubject = mock(Subject.class);
        mockDetails = mock(RequestDetails.class);
        mockService = mock(GetSavingService.class);

        pathParamters = Map.of("name", "savingName");
        when(mockDetails.getPathParameters()).thenReturn(pathParamters);
        when(mockSubject.getSubject()).thenReturn("jsdfds-32423-dsf");
    }

    @Test
    void delegateConstructsCorrectlyWhenInvoked() {
        // given

        // when
        new GetSavingHandlerDelegate(mockService);

        // then
    }

    @Test
    void serviceInvokedWhenDelegateHandled() {
        // given
        GetSavingHandlerDelegate sut = new GetSavingHandlerDelegate(mockService);

        // when
        sut.handle(null, mockSubject, mockDetails);

        // then
        verify(mockService, times(1)).getSaving(eq(mockSubject), any());
    }

    @Test
    void correctNameUsedWhenServiceInvoked() {
        // given
        GetSavingHandlerDelegate sut = new GetSavingHandlerDelegate(mockService);

        // when
        sut.handle(null, mockSubject, mockDetails);

        // then
        ArgumentCaptor<NameRequest> captor = ArgumentCaptor.forClass(NameRequest.class);
        verify(mockService, times(1)).getSaving(eq(mockSubject), captor.capture());
        NameRequest actual = captor.getValue();

        assertThat(actual.getName()).isEqualTo(pathParamters.get("name"));
    }

    @Test
    void spacesParsedInNameWhenServiceInvoked() {
        // given
        pathParamters = Map.of("name", "i%20am%20a%20saving");
        when(mockDetails.getPathParameters()).thenReturn(pathParamters);
        GetSavingHandlerDelegate sut = new GetSavingHandlerDelegate(mockService);

        // when
        sut.handle(null, mockSubject, mockDetails);

        // then
        ArgumentCaptor<NameRequest> captor = ArgumentCaptor.forClass(NameRequest.class);
        verify(mockService, times(1)).getSaving(eq(mockSubject), captor.capture());
        NameRequest actual = captor.getValue();

        assertThat(actual.getName()).isEqualTo("i am a saving");
    }

    @Test
    void logsCorrectlyWhenSavingRetrieved() {
        // given
        Logger mockLogger = mock(Logger.class);
        GetSavingHandlerDelegate sut = new GetSavingHandlerDelegate(mockService, mockLogger);

        // when
        sut.handle(null, mockSubject, mockDetails);

        // then
        verify(mockLogger, times(1))
                .info(
                        "Saving {} retrieved for subject {}",
                        pathParamters.get("name"),
                        mockSubject.getSubject());
    }

    @Test
    void rethrowsItemNotFoundExceptionCorrectly() {
        // given
        NoItemFoundException cause = new NoItemFoundException("exception message");
        when(mockService.getSaving(eq(mockSubject), any())).thenThrow(cause);
        Logger mockLogger = mock(Logger.class);
        GetSavingHandlerDelegate sut = new GetSavingHandlerDelegate(mockService, mockLogger);

        try {
            // when
            sut.handle(null, mockSubject, mockDetails);
            shouldHaveThrown(NotFoundException.class);

        } catch (NotFoundException actual) {
            // then
            assertThat(actual).hasCauseReference(cause);
            assertThat(actual).hasMessage("no saving could be retrieved");
            verify(mockLogger, times(1))
                    .error(
                            "Saving {} could not be retrieved for subject {}",
                            pathParamters.get("name"),
                            mockSubject.getSubject());
        }
    }

    @Test
    void rethrowsIllegalArgumentExceptionCorrectly() {
        // given
        IllegalArgumentException cause = new IllegalArgumentException("exception message");
        when(mockService.getSaving(eq(mockSubject), any())).thenThrow(cause);
        Logger mockLogger = mock(Logger.class);
        GetSavingHandlerDelegate sut = new GetSavingHandlerDelegate(mockService, mockLogger);

        try {
            // when
            sut.handle(null, mockSubject, mockDetails);
            shouldHaveThrown(BadRequestException.class);

        } catch (BadRequestException actual) {
            // then
            assertThat(actual).hasCauseReference(cause);
            assertThat(actual).hasMessage("illegal name provided to get saving");
            verify(mockLogger, times(1))
                    .error(
                            "Saving could not be retrieved for subject {}",
                            mockSubject.getSubject());
        }
    }

    @Test
    void savingReturnedWhenDelegateHandled() {
        // given
        Saving expected = mock(Saving.class);
        when(mockService.getSaving(eq(mockSubject), any())).thenReturn(expected);
        GetSavingHandlerDelegate sut = new GetSavingHandlerDelegate(mockService);

        // when
        Saving actual = sut.handle(null, mockSubject, mockDetails);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
