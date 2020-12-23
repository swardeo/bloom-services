package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = HandlerResponse.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HandlerResponse {

    private final int statusCode;
    private final Map<String, String> headers;
    private final String body;

    private HandlerResponse(HandlerResponse.Builder builder) {
        statusCode = builder.statusCode;
        headers = builder.headers;
        body = builder.body;

        headers.put("Content-Type", "application/json");
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonPOJOBuilder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private int statusCode;
        private Map<String, String> headers;
        private String body;

        private Builder() {
            headers = new HashMap<>();
        }

        public Builder withStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder withHeader(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public Builder withBody(String body) {
            this.body = body;
            return this;
        }

        public HandlerResponse build() {
            return new HandlerResponse(this);
        }
    }
}
