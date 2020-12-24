package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = HandlerRequest.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HandlerRequest {

    private final String httpMethod;
    private final String path;
    private final Map<String, String> queryStringParameters;
    private final Map<String, String> pathParameters;
    private final Map<String, String> headers;
    private final String body;

    private HandlerRequest(Builder builder) {
        httpMethod = builder.httpMethod;
        path = builder.path;
        queryStringParameters = builder.queryStringParameters;
        pathParameters = builder.pathParameters;
        headers = builder.headers;
        body = builder.body;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryStringParameters() {
        return queryStringParameters;
    }

    public Map<String, String> getPathParameters() {
        return pathParameters;
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
    static final class Builder {
        private String httpMethod;
        private String path;
        private Map<String, String> queryStringParameters;
        private Map<String, String> pathParameters;
        private Map<String, String> headers;
        private String body;

        private Builder() {}

        public Builder withHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder withPath(String path) {
            this.path = path;
            return this;
        }

        public Builder withQueryStringParameters(Map<String, String> queryStringParameters) {
            this.queryStringParameters = queryStringParameters;
            return this;
        }

        public Builder withPathParameters(Map<String, String> pathParameters) {
            this.pathParameters = pathParameters;
            return this;
        }

        public Builder withHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder withBody(String body) {
            this.body = body;
            return this;
        }

        public HandlerRequest build() {
            return new HandlerRequest(this);
        }
    }
}
