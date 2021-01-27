package util;

public class StringValidator {

    public static void checkNullOrEmpty(String value, String name) {
        if (null == value || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " cannot be null or empty");
        }
    }
}
