package org.firstinspires.ftc.teamcode.helper.general;

import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.constants.MotorConstants;

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

    public static boolean pointInTriangle(Vector2D p, Vector2D a, Vector2D b, Vector2D c) {
        double v0x = c.x - a.x;
        double v0y = c.y - a.y;

        double v1x = b.x - a.x;
        double v1y = b.y - a.y;

        double v2x = p.x - a.x;
        double v2y = p.y - a.y;

        double dot00 = v0x * v0x + v0y * v0y;
        double dot01 = v0x * v1x + v0y * v1y;
        double dot02 = v0x * v2x + v0y * v2y;
        double dot11 = v1x * v1x + v1y * v1y;
        double dot12 = v1x * v2x + v1y * v2y;

        double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        return (u >= 0) && (v >= 0) && (u + v <= 1);
    }

    public static boolean pointInSquare(
            double px, double py,
            double cx, double cy,
            double halfSize
    ) {
        return px >= cx - halfSize && px <= cx + halfSize &&
                py >= cy - halfSize && py <= cy + halfSize;
    }

    public static boolean rotatedSquareInsideSquare(
            Vector2D inner,
            double innerHalfSize,
            double innerHeadingRad,

            Vector2D outer,
            double outerHalfSize
    ) {
        double cos = Math.cos(innerHeadingRad);
        double sin = Math.sin(innerHeadingRad);

        double[][] corners = {
                { innerHalfSize,  innerHalfSize},
                { innerHalfSize, -innerHalfSize},
                {-innerHalfSize, -innerHalfSize},
                {-innerHalfSize,  innerHalfSize}
        };

        for (double[] c : corners) {
            double rx = c[0] * cos - c[1] * sin;
            double ry = c[0] * sin + c[1] * cos;

            double wx = inner.x + rx;
            double wy = inner.y + ry;

            if (!pointInSquare(wx, wy, outer.x, outer.y, outerHalfSize)) {
                return false;
            }
        }

        return true;
    }

    public static boolean rotatedSquareIntersectsSquare(
            Vector2D inner,
            double innerHalfSize,
            double innerHeadingRad,

            Vector2D outer,
            double outerHalfSize
    ) {
        double cos = Math.cos(innerHeadingRad);
        double sin = Math.sin(innerHeadingRad);

        // Rotated square local axes
        double ax1x = cos;
        double ax1y = sin;
        double ax2x = -sin;
        double ax2y = cos;

        // Test all separating axes
        return overlapOnAxis(inner, innerHalfSize, outer, outerHalfSize, 1, 0, ax1x, ax1y, ax2x, ax2y) &&
                overlapOnAxis(inner, innerHalfSize, outer, outerHalfSize, 0, 1, ax1x, ax1y, ax2x, ax2y) &&
                overlapOnAxis(inner, innerHalfSize, outer, outerHalfSize, ax1x, ax1y, ax1x, ax1y, ax2x, ax2y) &&
                overlapOnAxis(inner, innerHalfSize, outer, outerHalfSize, ax2x, ax2y, ax1x, ax1y, ax2x, ax2y);
    }

    private static boolean overlapOnAxis(
            Vector2D inner,
            double innerHalfSize,

            Vector2D outer,
            double outerHalfSize,

            double axisX, double axisY,
            double ax1x, double ax1y,
            double ax2x, double ax2y
    ) {
        double len = Math.hypot(axisX, axisY);
        axisX /= len;
        axisY /= len;

        double innerProj = inner.x * axisX + inner.y * axisY;
        double outerProj = outer.x * axisX + outer.y * axisY;

        double innerRadius =
                innerHalfSize * Math.abs(ax1x * axisX + ax1y * axisY) +
                        innerHalfSize * Math.abs(ax2x * axisX + ax2y * axisY);

        double outerRadius =
                outerHalfSize * (Math.abs(axisX) + Math.abs(axisY));

        return Math.abs(innerProj - outerProj) <= innerRadius + outerRadius;
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int angleToTicks(double angleDeg, double ticksPerRev) {
        return (int) ((angleDeg / 360.0) * ticksPerRev);
    }

    public static int angleToTicks(double angleDeg) {
        return (int) ((angleDeg / 360.0) * MotorConstants.MOTOR_1620_TICKS_PER_REV);
    }

    public static double ticksToAngle(int ticks, double ticksPerRev) {
        return (ticks / ticksPerRev) * 360.0;
    }

    public static double ticksToAngle(int ticks) {
        return (ticks / MotorConstants.MOTOR_1620_TICKS_PER_REV) * 360.0;
    }
}
