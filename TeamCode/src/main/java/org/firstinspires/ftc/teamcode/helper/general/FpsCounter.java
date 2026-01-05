package org.firstinspires.ftc.teamcode.helper.general;

public class FpsCounter {

    private long lastTime = System.nanoTime();
    private double fps = 0.0;

    public void update() {
        long now = System.nanoTime();
        long delta = now - lastTime;

        if (delta > 0) {
            fps = 1e9 / delta;
        }

        lastTime = now;
    }

    public double getFps() {
        return fps;
    }
}
