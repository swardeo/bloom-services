package util;

public class ObjectValidator {

    public static void checkNull(Object value, String name) {
        if (null == value) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }
}
