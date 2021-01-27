package provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class MapperProvider {

    public static ObjectMapper provideMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
