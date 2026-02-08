package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;

public class Intake {

    private final LinearOpMode opMode;
    private final DcMotorEx intake;
    private final CRServo leftFeeder;
    private final CRServo rightFeeder;
    private boolean isIntaking;
    private boolean isEjecting;

    // Optimization: Cache last set powers to avoid expensive hardware writes
    private double lastIntakePower = 0.0;
    private double lastLeftFeedPower = 0.0;
    private double lastRightFeedPower = 0.0;

    public Intake(LinearOpMode OpMode, Hardware hwMap) {
        opMode = OpMode;
        intake = hwMap.Motors().Intake();
        leftFeeder = hwMap.Servos().FeedLeft();
        rightFeeder = hwMap.Servos().FeedRight();
    }

    public void input() {
        // Standard gamepad access is fast, no optimization needed here
        if(opMode.gamepad1.aWasPressed()) {
            toggleIntake();
        }

        if(opMode.gamepad1.b) {
            setIntakePower(-1);
            setFeederPower(-1);
            isEjecting = true;
        }

        if(opMode.gamepad1.bWasReleased()) {
            isEjecting = false;
            setFeederPower(0);
        }
    }

    public void toggleIntake() {
        isIntaking = !isIntaking || isEjecting; // Simplified toggle logic
        if (isIntaking) isEjecting = false;
    }

    public void setIntake(boolean value) {
        isIntaking = value;
    }

    public void update() {
        // Prioritize Ejecting, then Intaking, then Stop
        if(isEjecting) {
            setIntakePower(-1);
        }
        else if(isIntaking) {
            setIntakePower(1);
        }
        else {
            setIntakePower(0);
        }
    }

    public boolean isIntakeing() {
        return isIntaking;
    }

    // --- OPTIMIZED HARDWARE WRITES ---
    // Only writes to the hub if the value actually changes.
    private void setIntakePower(double power) {
        if (Math.abs(power - lastIntakePower) > 0.01) {
            intake.setPower(power);
            lastIntakePower = power;
        }
    }

    private void setFeederPower(double power) {
        // Assuming left/right always get same power in this context
        if (Math.abs(power - lastLeftFeedPower) > 0.01) {
            leftFeeder.setPower(power);
            lastLeftFeedPower = power;
        }
        if (Math.abs(power - lastRightFeedPower) > 0.01) {
            rightFeeder.setPower(power);
            lastRightFeedPower = power;
        }
    }
}