package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.util.Timer;

import org.firstinspires.ftc.teamcode.helper.hardware.Hardware2;

public class Claw {

    private enum State { OPEN, CLOSING, CAUGHT }

    private static final double OPEN_POSITION = 1.0;
    private static final double CLOSE_POSITION = 0.0;
    private static final double MOVEMENT_THRESHOLD = 0.01;
    private static final long STALL_TIME_MS = 500;
    private static final double FEEDBACK_MAX_VOLTAGE = 3.3;

    private final Hardware2 hardware;

    private State state = State.OPEN;
    private double lastCheckedPosition;
    private final Timer stallTimer = new Timer();

    public Claw(Hardware2 hardware) {
        this.hardware = hardware;
        hardware.Servos().Claw().setPosition(OPEN_POSITION);
        lastCheckedPosition = getCurrentPosition();
        stallTimer.resetTimer();
    }

    public void open() {
        state = State.OPEN;
        hardware.Servos().Claw().setPosition(OPEN_POSITION);
    }

    public void close() {
        if (state == State.CAUGHT) return;
        state = State.CLOSING;
        hardware.Servos().Claw().setPosition(CLOSE_POSITION);
        lastCheckedPosition = getCurrentPosition();
        stallTimer.resetTimer();
    }

    public void toggle() {
        if (state == State.OPEN) {
            close();
        } else {
            open();
        }
    }

    public void update() {
        if (state != State.CLOSING) return;

        double currentPosition = getCurrentPosition();

        if (Math.abs(currentPosition - lastCheckedPosition) > MOVEMENT_THRESHOLD) {
            lastCheckedPosition = currentPosition;
            stallTimer.resetTimer();
            return;
        }

        if (stallTimer.getElapsedTimeSeconds() * 1000 >= STALL_TIME_MS) {
            state = State.CAUGHT;
            hardware.Servos().Claw().setPosition(currentPosition);
        }
    }

    private double getCurrentPosition() {
        return hardware.Servos().ClawFeedback().getVoltage() / FEEDBACK_MAX_VOLTAGE;
    }

    public boolean isOpen() {
        return state == State.OPEN;
    }

    public boolean hasCaught() {
        return state == State.CAUGHT;
    }
}