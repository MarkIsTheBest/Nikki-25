package subsystems;

import java.util.HashMap;
import java.util.Map;

public class Input {
    private static final Map<String, Boolean> previousStates = new HashMap<>();

    public static boolean onKeyDown(String buttonName, boolean currentState) {
        boolean previousState = Boolean.TRUE.equals(previousStates.getOrDefault(buttonName, false));

        if (currentState && !previousState) {
            previousStates.put(buttonName, true);
            return true;
        } else if (!currentState) {
            previousStates.put(buttonName, false);
        }

        return false;
    }

    public static boolean onKeyUp(String buttonName, boolean currentState) {
        boolean previousState = Boolean.TRUE.equals(previousStates.getOrDefault(buttonName, false));

        if (!currentState && previousState) {
            previousStates.put(buttonName, false);
            return true;
        }

        return false;
    }

    public static boolean isDown(String buttonName, boolean currentState) {
        return Boolean.TRUE.equals(previousStates.getOrDefault(buttonName, currentState));
    }
}
