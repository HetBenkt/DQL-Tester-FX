package nl.bos.utils;

import java.util.HashMap;
import java.util.Map;

public final class Controllers {
    private static final Map<String, Object> registeredControllers = new HashMap<>();

    private Controllers() {
    }

    public static Object get(String controllerName) {
        return registeredControllers.get(controllerName);
    }

    public static void put(String controllerName, Object controller) {
        registeredControllers.put(controllerName, controller);
    }
}
