package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.helper.hardware.Hardware2;

public class Slider {

    private static final int STEP_SIZE = 200;
    private static final int STEP_OFFSET = 150;
    private static final int MIN_POSITION = 0;
    private static final int MAX_POSITION = 2450;
    private static final double STEP_POWER = 1;

    // Step indices are clamped separately from raw position, since MAX_POSITION
    // doesn't sit exactly on the step grid (STEP_OFFSET + n*STEP_SIZE).
    // This keeps stepUp()/stepDown() from overshooting and snapping back.
    private static final int MIN_STEP_INDEX = (int) Math.ceil((MIN_POSITION - STEP_OFFSET) / (double) STEP_SIZE);
    private static final int MAX_STEP_INDEX = (int) Math.floor((MAX_POSITION - STEP_OFFSET) / (double) STEP_SIZE);

    private final DcMotorEx left;
    private final DcMotorEx right;

    private int baseTarget = 0;         // step target, no compensation
    private int compensationTicks = 0;  // from Claw, layered on top
    private boolean inPositionMode = false;

    public Slider(Hardware2 hardware) {
        left = hardware.Motors().SliderLeft();
        right = hardware.Motors().SliderRight();
    }

    public void stepUp() {
        int currentStep = nearestStepIndex();
        int nextStep = Math.min(currentStep + 1, MAX_STEP_INDEX);
        baseTarget = positionForStep(nextStep);
        applyTarget();
    }

    public void stepDown() {
        int currentStep = nearestStepIndex();
        int nextStep = Math.max(currentStep - 1, MIN_STEP_INDEX);
        baseTarget = positionForStep(nextStep);
        applyTarget();
    }

    public void setPower(double power) {
        if (inPositionMode) {
            exitPositionMode();
        }

        // Stop driving further past the limits in manual power mode.
        // Power in the "into the limit" direction is zeroed; power pulling
        // back away from the limit is still allowed.
        int currentPosition = getCurrentPosition();
        if (power > 0 && currentPosition >= MAX_POSITION) {
            power = 0;
        } else if (power < 0 && currentPosition <= MIN_POSITION) {
            power = 0;
        }

        left.setPower(power);
        right.setPower(power);
    }

    public void idle() {
        if (!inPositionMode) {
            left.setPower(0.0);
            right.setPower(0.0);
        }
    }

    // Called every loop with Claw.getSliderCompensationTicks()
    public void applyClawCompensation(int ticks) {
        if (compensationTicks == ticks) return; // no change, skip re-sending target
        compensationTicks = ticks;
        if (inPositionMode) {
            applyTarget();
        }
        // if not in position mode (free power control), compensation is
        // remembered but only takes effect once a step is issued
    }

    private int nearestStepIndex() {
        return Math.round((getCurrentPosition() - STEP_OFFSET) / (float) STEP_SIZE);
    }

    private int positionForStep(int stepIndex) {
        return STEP_OFFSET + stepIndex * STEP_SIZE;
    }

    private void applyTarget() {
        if (!inPositionMode) {
            left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            inPositionMode = true;
        }
        // clamp stays here as a safety net: claw compensation can still push
        // the applied target past MAX_POSITION even though baseTarget itself
        // is now always a valid, in-range step.
        int appliedTarget = clamp(baseTarget + compensationTicks);
        left.setTargetPosition(appliedTarget);
        right.setTargetPosition(appliedTarget);
        left.setPower(STEP_POWER);
        right.setPower(STEP_POWER);
    }

    private void exitPositionMode() {
        left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        inPositionMode = false;
    }

    private int getCurrentPosition() {
        return left.getCurrentPosition();
    }

    private int clamp(int position) {
        return Math.max(MIN_POSITION, Math.min(MAX_POSITION, position));
    }
}