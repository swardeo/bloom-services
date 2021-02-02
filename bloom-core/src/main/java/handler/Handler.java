package handler;

import model.Subject;

public interface Handler<TRequest, TResponse> {
    TResponse handle(TRequest request, Subject subject);
}
