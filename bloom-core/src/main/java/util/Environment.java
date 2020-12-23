package util;

public class Environment {

    public static String readVariable(String name) {
        return System.getenv(name);
    }
}
