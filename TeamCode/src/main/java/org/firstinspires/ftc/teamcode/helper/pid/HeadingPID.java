package org.firstinspires.ftc.teamcode.helper.pid;

import org.firstinspires.ftc.teamcode.constants.Control;

public class HeadingPID {

    private PIDF k;

    private double integral = 0;
    private double lastError = 0;
    private long lastTime = 0;

    public HeadingPID() {
        k = new PIDF(Control.HeadingPID.p, Control.HeadingPID.i, Control.HeadingPID.d, 0);
    }

    /** Call when enabling heading lock */
    public void reset() {
        integral = 0;
        lastError = 0;
        lastTime = System.nanoTime();
    }

    /** Update PID and return turn power (-1 to 1) */
    public double update(double targetHeading, double currentHeading) {
        double error = angleWrap(targetHeading - currentHeading);

        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9;
        lastTime = now;

        if (dt <= 0) return 0;

        integral += error * dt;
        double derivative = (error - lastError) / dt;
        lastError = error;

        double output = (k.p * error) + (k.i * integral) + (k.d * derivative);

        return clamp(output, -1.0, 1.0);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /** Wrap angle to [-π, π] */
    private double angleWrap(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }
}
