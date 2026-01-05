package org.firstinspires.ftc.teamcode.helper;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.constants.Control;

import java.util.HashMap;
import java.util.Map;

public class MotorHelper {
    private static class PIDState {
        double integralSum = 0;
        double lastError = 0;
        ElapsedTime timer = new ElapsedTime();

        PIDState() {
            timer.reset();
        }
    }
    private static final Map<DcMotorEx, PIDState> pidStateMap = new HashMap<>();

    private MotorHelper() {} // Prevent instantiation

    // ==========================
    // Velocity / RPM Control
    // ==========================

    public static void setRPM(DcMotorEx motor, double targetRPM, double ticksPerRev, double maxRPM, PIDCoefficients pid) {
        // **FIXED:** Correctly calculate the F coefficient for the Rev Hub internal controller.
        double f = 32767.0 / (maxRPM * ticksPerRev / 60.0);
        motor.setVelocityPIDFCoefficients(pid.p, pid.i, pid.d, f);
        motor.setVelocity(targetRPM * ticksPerRev / 60.0); // Convert RPM to Ticks per Second
    }

    public static void setRPM(DcMotorEx motor, double targetRPM) {
        double maxRPM = 6000.0;
        double ticksPerRev = 28.0;
        PIDCoefficients pid = Control.FlywheelPID.pid;

        double f = 32767.0 / (maxRPM * ticksPerRev / 60.0);
        motor.setVelocityPIDFCoefficients(pid.p, pid.i, pid.d, f);
        motor.setVelocity(targetRPM * ticksPerRev / 60.0);
    }

    public static void setRPM(DcMotorEx[] motors, double targetRPM) {
        double maxRPM = 6000.0;
        double ticksPerRev = 28.0;
        PIDCoefficients pid = Control.FlywheelPID.pid;

        double f = 32767.0 / (maxRPM * ticksPerRev / 60.0);

        for (DcMotorEx motor : motors) {
            motor.setVelocityPIDFCoefficients(pid.p, pid.i, pid.d, f);
            motor.setVelocity(targetRPM * ticksPerRev / 60.0);
        }
    }

    public static double getCurrentRPM(DcMotorEx motor, double ticksPerRev) {
        return motor.getVelocity() * 60.0 / ticksPerRev;
    }

    public static double getCurrentRPM(DcMotorEx motor) {
        return motor.getVelocity() * 60.0 / 28.0;
    }

    public static void setSlidePosition(DcMotorEx[] motors, double targetPosition, PIDFCoefficients pidf) {
        for (DcMotorEx motor : motors) {
            if (motor != null) {
                updateSlidePID(motor, targetPosition, pidf);
            }
        }
    }

    public static void setSlidePosition(DcMotorEx motor, double targetPosition, PIDFCoefficients pidf) {
        updateSlidePID(motor, targetPosition, pidf);
    }

    public static void setArmAngle(DcMotorEx motor, double targetAngleDeg, double motorTicksPerRev, double powerLimit, PIDFCoefficients pidf) {
        updateArmPID(motor, targetAngleDeg, motorTicksPerRev, powerLimit, pidf);
    }

    // ==========================
    // Core PID Update Logic (Refactored)
    // ==========================

    private static void updateSlidePID(DcMotorEx motor, double targetPosition, PIDFCoefficients pidf) {
        // Get or create the state for this motor
        PIDState state = pidStateMap.computeIfAbsent(motor, k -> new PIDState());

        double dt = state.timer.seconds();
        state.timer.reset();
        // **FIXED:** Prevent division by zero on the first loop.
        if (dt == 0) return;

        double currentPosition = motor.getCurrentPosition();
        double error = targetPosition - currentPosition;

        // Update integral sum with clamping to prevent windup
        state.integralSum += error * dt;
        state.integralSum = clamp(state.integralSum, -100, 100); // This range may need tuning

        // Update derivative
        double derivative = (error - state.lastError) / dt;
        state.lastError = error;

        // The 'f' term for a slide can be a constant feedforward to counteract gravity if vertical
        double output = (pidf.p * error) + (pidf.i * state.integralSum) + (pidf.d * derivative) + pidf.f;

        motor.setPower(clamp(output, -1.0, 1.0));
    }

    private static void updateArmPID(DcMotorEx motor, double targetAngle, double ticksPerRev, double powerLimit, PIDFCoefficients pidf) {
        PIDState state = pidStateMap.computeIfAbsent(motor, k -> new PIDState());

        double dt = state.timer.seconds();
        state.timer.reset();
        // **FIXED:** Prevent division by zero on the first loop.
        if (dt == 0) return;

        double currentAngle = motor.getCurrentPosition() * 360.0 / ticksPerRev;
        double error = targetAngle - currentAngle;

        state.integralSum += error * dt;
        state.integralSum = clamp(state.integralSum, -100, 100);

        double derivative = (error - state.lastError) / dt;
        state.lastError = error;

        // The 'f' term for an arm is gravity compensation based on the angle (cosine term)
        double gravityComp = pidf.f * Math.cos(Math.toRadians(currentAngle));
        double output = (pidf.p * error) + (pidf.i * state.integralSum) + (pidf.d * derivative) + gravityComp;

        motor.setPower(clamp(output, -powerLimit, powerLimit));
    }

    private static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}