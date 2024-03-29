package handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import handler.HealthHandler.HealthHandlerDelegate;
import model.HealthResponse;
import model.RequestDetails;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

class HealthHandlerTest {

    HealthHandlerDelegate sut;

    DynamoDbClient mockClient;
    String tableName;
    Subject mockSubject;
    RequestDetails mockDetails;
    Logger mockLogger;

    @BeforeEach
    void beforeEach() {
        mockClient = mock(DynamoDbClient.class);
        tableName = "mockTable";
        mockSubject = mock(Subject.class);
        mockDetails = mock(RequestDetails.class);
        mockLogger = mock(Logger.class);

        TableDescription table = TableDescription.builder().tableStatus(TableStatus.ACTIVE).build();
        DescribeTableResponse tableResponse = DescribeTableResponse.builder().table(table).build();
        when(mockClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build()))
                .thenReturn(tableResponse);

        sut = new HealthHandlerDelegate(mockClient, tableName, mockLogger);
    }

    @Test
    void clientInvokedCorrectlyWhenDelegateHandled() {
        // given

        // when
        sut.handle(null, mockSubject, mockDetails);

        // then
        verify(mockClient)
                .describeTable(DescribeTableRequest.builder().tableName(tableName).build());
    }

    @Test
    void clientReturnsHealthyWhenTableActive() {
        // given
        HealthResponse expected = HealthResponse.HEALTHY;

        // when
        HealthResponse actual = sut.handle(null, mockSubject, mockDetails);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void clientReturnsUnhealthyWhenTableNotActive() {
        // given
        TableDescription table =
                TableDescription.builder().tableStatus(TableStatus.ARCHIVED).build();
        DescribeTableResponse tableResponse = DescribeTableResponse.builder().table(table).build();
        when(mockClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build()))
                .thenReturn(tableResponse);

        HealthResponse expected = HealthResponse.UNHEALTHY;

        // when
        HealthResponse actual = sut.handle(null, mockSubject, mockDetails);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void logsWhenTableNotActive() {
        // given
        TableDescription table =
                TableDescription.builder().tableStatus(TableStatus.ARCHIVED).build();
        DescribeTableResponse tableResponse = DescribeTableResponse.builder().table(table).build();
        when(mockClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build()))
                .thenReturn(tableResponse);

        // when
        sut.handle(null, mockSubject, mockDetails);

        // then
        verify(mockLogger).error("Table {} is not active", tableName);
    }
}
