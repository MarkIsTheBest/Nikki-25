package org.firstinspires.ftc.teamcode.helper.general;

import com.pedropathing.geometry.Pose;

public class MathHelper {
    public static boolean inInterval(Object value, Object min, Object max) {
        return ((Comparable) value).compareTo(min) >= 0 && ((Comparable) value).compareTo(max) <= 0;
    }

    public static double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public static double dist(Pose a, Pose b) {
        return Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2));
    }
}
