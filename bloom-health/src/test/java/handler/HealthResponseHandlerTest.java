package handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.amazonaws.services.lambda.runtime.Context;
import handler.HealthHandler.HealthHandlerDelegate;
import model.HealthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

class HealthResponseHandlerTest {

    DynamoDbClient mockClient;
    String tableName;
    Context mockContext;

    @BeforeEach
    void beforeEach() {
        mockClient = mock(DynamoDbClient.class);
        tableName = "mockTable";
        mockContext = mock(Context.class);

        TableDescription table = TableDescription.builder().tableStatus(TableStatus.ACTIVE).build();
        DescribeTableResponse tableResponse = DescribeTableResponse.builder().table(table).build();
        when(mockClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build()))
                .thenReturn(tableResponse);
    }

    @Test
    void delegateAcceptsCorrectParametersWhenConstructed() {
        // given

        // when
        new HealthHandlerDelegate(mockClient, tableName);

        // then
        // no exception
    }

    @Test
    void clientInvokedCorrectlyWhenDelegateHandled() {
        // given
        HealthHandlerDelegate sut = new HealthHandlerDelegate(mockClient, tableName);

        // when
        sut.handle(null, mockContext);

        // then
        verify(mockClient, times(1))
                .describeTable(DescribeTableRequest.builder().tableName(tableName).build());
    }

    @Test
    void clientReturnsHealthyWhenTableActive() {
        // given
        HealthHandlerDelegate sut = new HealthHandlerDelegate(mockClient, tableName);

        HealthResponse expected = HealthResponse.HEALTHY;

        // when
        HealthResponse actual = sut.handle(null, mockContext);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void clientReturnsUnhealthyWhenTableNotActive() {
        // given
        HealthHandlerDelegate sut = new HealthHandlerDelegate(mockClient, tableName);

        TableDescription table =
                TableDescription.builder().tableStatus(TableStatus.ARCHIVED).build();
        DescribeTableResponse tableResponse = DescribeTableResponse.builder().table(table).build();
        when(mockClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build()))
                .thenReturn(tableResponse);

        HealthResponse expected = HealthResponse.UNHEALTHY;

        // when
        HealthResponse actual = sut.handle(null, mockContext);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void logsWhenTableNotActive() {
        // given
        Logger mockLogger = mock(Logger.class);
        HealthHandlerDelegate sut = new HealthHandlerDelegate(mockClient, tableName, mockLogger);

        TableDescription table =
                TableDescription.builder().tableStatus(TableStatus.ARCHIVED).build();
        DescribeTableResponse tableResponse = DescribeTableResponse.builder().table(table).build();
        when(mockClient.describeTable(DescribeTableRequest.builder().tableName(tableName).build()))
                .thenReturn(tableResponse);

        // when
        HealthResponse actual = sut.handle(null, mockContext);

        // then
        verify(mockLogger, times(1)).info("Table {} is not active", tableName);
    }
}
