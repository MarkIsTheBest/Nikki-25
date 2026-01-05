package org.firstinspires.ftc.teamcode.helper.general;

public class MathHelper {
    public static boolean inInterval(Object value, Object min, Object max) {
        return ((Comparable) value).compareTo(min) >= 0 && ((Comparable) value).compareTo(max) <= 0;
    }
}
