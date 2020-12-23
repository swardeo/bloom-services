package handler;

import com.amazonaws.services.lambda.runtime.Context;

public interface Handler<TRequest, TResponse> {
    TResponse handle(TRequest request, Context context);
}
