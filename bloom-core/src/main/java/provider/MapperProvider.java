package provider;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MapperProvider {

    public static ObjectMapper provideMapper() {
        return new ObjectMapper();
    }
}
