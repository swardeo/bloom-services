package exception;

public class NoItemFoundException extends RuntimeException {

    public NoItemFoundException(String message) {
        super(message);
    }
}
