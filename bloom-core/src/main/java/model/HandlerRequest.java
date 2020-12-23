package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = HandlerRequest.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HandlerRequest {

    private String httpMethod;
    private String path;
    private Map<String, String> queryStringParameters;
    private Map<String, String> pathParameters;
    private Map<String, String> headers;
    private String body;

    private HandlerRequest(HandlerRequest.Builder builder) {
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

        private Builder() {
            queryStringParameters = new HashMap<>();
            pathParameters = new HashMap<>();
            headers = new HashMap<>();
        }

        public Builder withHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder withPath(String path) {
            this.path = path;
            return this;
        }

        public Builder withQueryStringParameter(String name, String value) {
            queryStringParameters.put(name, value);
            return this;
        }

        public Builder withPathParameter(String name, String value) {
            pathParameters.put(name, value);
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

        public HandlerRequest build() {
            return new HandlerRequest(this);
        }
    }
}
