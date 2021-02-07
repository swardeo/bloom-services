package model;

import java.util.Map;

public class RequestDetails {

    private final String httpMethod;
    private final String path;
    private final Map<String, String> queryStringParameters;
    private final Map<String, String> pathParameters;
    private final Map<String, String> headers;

    private RequestDetails(
            String httpMethod,
            String path,
            Map<String, String> queryStringParameters,
            Map<String, String> pathParameters,
            Map<String, String> headers) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryStringParameters =
                null != queryStringParameters ? Map.copyOf(queryStringParameters) : Map.of();
        this.pathParameters = null != pathParameters ? Map.copyOf(pathParameters) : Map.of();
        this.headers = null != headers ? Map.copyOf(headers) : Map.of();
    }

    public static RequestDetails fromHandlerRequest(HandlerRequest request) {
        return new RequestDetails(
                request.getHttpMethod(),
                request.getPath(),
                request.getQueryStringParameters(),
                request.getPathParameters(),
                request.getHeaders());
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
}
