package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.helper.hardware.Hardware2;

public class Slider {

    // HARDCODED STEP POSITIONS
// Index 0 = Home (0 ticks)
// Index 1 = Step 1 (50 ticks)
// Index 2 = Step 2 (350 ticks)
// Index 3 = Step 3 (650 ticks)
// ... up to max height
    private static final int[] STEP_POSITIONS = {
            50, // Step 1: FIRST STEP IS EXACTLY 50 TICKS
            400, // Step 2
            750, // Step 3
            1100, // Step 4
            1450, // Step 5
            1800, // Step 6
            2150, // Step 7
            2450, // Step 8
    };

    private static final int MIN_POSITION = 0;
    private static final int MAX_POSITION = 2450;
    private static final double STEP_POWER = 1.0;

    private final DcMotorEx left;
    private final DcMotorEx right;

    private int currentStepIndex = 0; // Starts at Step 0 (0 ticks)
    private int compensationTicks = 100;
    private int compoffset = 0;
    private boolean inPositionMode = false;

    public Slider(Hardware2 hardware) {
        left = hardware.Motors().SliderLeft();
        right = hardware.Motors().SliderRight();

// HARD RESET ENCODERS TO ZERO ON INIT
        left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setPosition(int ticks) {
        left.setTargetPosition(ticks);
        right.setTargetPosition(ticks);

        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        left.setPower(STEP_POWER);
        right.setPower(STEP_POWER);
    }

    public void setClaw(boolean value) {
        if (value) {
            compoffset = compensationTicks;
        } else {
            compoffset = 0;
        }
    }

    public void stepUp() {
        inPositionMode = true;

        if (currentStepIndex < STEP_POSITIONS.length - 1) {
            currentStepIndex++;
        }
    }

    public void stepDown() {
        inPositionMode = true;

        if (currentStepIndex > 0) {
            currentStepIndex--;
        }

    }

    public void reset() {
        inPositionMode = true;

        currentStepIndex = 0;
    }

    public void setPower(double power, boolean manual) {
        if (inPositionMode) {
            exitPositionMode();
        }
        double newPower = power;

        int currentPosition = getCurrentPosition();
        if (newPower > 0 && currentPosition >= MAX_POSITION) {
            newPower = 0;
        } else if (newPower < 0 && currentPosition <= MIN_POSITION) {
            newPower = 0;
        }

        left.setPower(manual ? power : newPower);
        right.setPower(manual ? power : newPower);
    }

    public void resetEncoder() {
        left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void idle() {
        if (!inPositionMode) {
            left.setPower(0.0);
            right.setPower(0.0);
        }
        else {
            applyTarget();
        }
    }

    public void applyClawCompensation(int ticks) {
        if (compensationTicks == ticks) return;
        compensationTicks = ticks;
        if (inPositionMode) {
            applyTarget();
        }
    }

    public boolean isBusy() {
        return right.isBusy() || left.isBusy();
    }

    private void applyTarget() {
        int baseTarget = STEP_POSITIONS[currentStepIndex];
        int applyComp = currentStepIndex == 0 ? 0 : 1;

        int appliedTarget = baseTarget + compoffset * applyComp;

        left.setTargetPosition(appliedTarget);
        right.setTargetPosition(appliedTarget);

        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        left.setPower(STEP_POWER);
        right.setPower(STEP_POWER);
    }

    public void goToStep(int stepIndex) {
        currentStepIndex = stepIndex;
        applyTarget();
    }

    private void exitPositionMode() {
        left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        inPositionMode = false;

        syncStepIndexToPosition();
    }

    private void syncStepIndexToPosition() {
        int pos = getCurrentPosition();
        int closestIndex = 0;
        int minDistance = Integer.MAX_VALUE;

        for (int i = 0; i < STEP_POSITIONS.length; i++) {
            int dist = Math.abs(pos - STEP_POSITIONS[i]);
            if (dist < minDistance) {
                minDistance = dist;
                closestIndex = i;
            }
        }
        currentStepIndex = closestIndex;
    }

    private int getCurrentPosition() {
        return left.getCurrentPosition();
    }

    private int clamp(int position) {
        return Math.max(MIN_POSITION, Math.min(MAX_POSITION, position));
    }

    // Telemetry helper method for debugging
    public int getCurrentStepIndex() {
        return currentStepIndex;
    }

    public int getBaseTarget() {
        return STEP_POSITIONS[currentStepIndex];
    }
}