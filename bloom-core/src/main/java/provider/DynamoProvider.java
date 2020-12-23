package provider;

import static software.amazon.awssdk.regions.Region.EU_WEST_2;
import static util.Environment.readVariable;

import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoProvider {

    public static DynamoDbClient provideClient() {
        return DynamoDbClient.builder()
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .region(EU_WEST_2)
                .build();
    }

    public static String provideTableName() {
        return readVariable("DYNAMO_DB_TABLE_NAME");
    }
}
