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
        PIDState() { timer.reset(); }
    }

    private static final Map<DcMotorEx, PIDState> pidStateMap = new HashMap<>();

    // Optimization: Caches for Hardware Writes
    private static final Map<DcMotorEx, Double> velocityCache = new HashMap<>();
    private static final Map<DcMotorEx, PIDFCoefficients> pidfCache = new HashMap<>();

    private MotorHelper() {}

    // ==========================
    // Velocity / RPM Control
    // ==========================

    public static void setRPM(DcMotorEx motor, double targetRPM) {
        if (motor == null) return;

        double maxRPM = 6000.0;
        double ticksPerRev = 28.0;
        PIDFCoefficients pid = Control.FlywheelPID.pidf;
        double f = 32767.0 / (maxRPM * ticksPerRev / 60.0);

        setRPM(motor, targetRPM, ticksPerRev, maxRPM, pid);
    }

    public static void setRPM(DcMotorEx[] motors, double targetRPM) {
        for (DcMotorEx motor : motors) {
            setRPM(motor, targetRPM);
        }
    }

    public static void setRPM(DcMotorEx motor, double targetRPM, double ticksPerRev, double maxRPM, PIDFCoefficients pid) {
        if (motor == null) return;
        // 1. Optimize PIDF Writes (VERY SLOW OPERATION)
        // Only write coefficients if they have changed substantially
        PIDFCoefficients newPIDF = new PIDFCoefficients(pid.p, pid.i, pid.d, pid.f);
        PIDFCoefficients cachedPIDF = pidfCache.get(motor);

        if (cachedPIDF == null ||
                cachedPIDF.p != newPIDF.p ||
                cachedPIDF.i != newPIDF.i ||
                cachedPIDF.d != newPIDF.d ||
                cachedPIDF.f != newPIDF.f) {

            motor.setVelocityPIDFCoefficients(pid.p, pid.i, pid.d, pid.f);
            pidfCache.put(motor, newPIDF);
        }

        // 2. Optimize Velocity Writes
        // Only write velocity if target changed
        double targetVelocityTicks = targetRPM * ticksPerRev / 60.0;
        Double cachedVel = velocityCache.get(motor);

        if (cachedVel == null || Math.abs(cachedVel - targetVelocityTicks) > 0.1) {
            motor.setVelocity(targetVelocityTicks);
            velocityCache.put(motor, targetVelocityTicks);
        }
    }

    public static double getCurrentRPM(DcMotorEx motor, double ticksPerRev) {
        if (motor == null) return 0;
        return motor.getVelocity() * 60.0 / ticksPerRev;
    }

    public static double getCurrentRPM(DcMotorEx motor) {
        return getCurrentRPM(motor, 28.0);
    }

    // ==========================
    // Custom PID Update Logic
    // ==========================

    public static void setSlidePosition(DcMotorEx[] motors, double targetPosition, PIDFCoefficients pidf) {
        for (DcMotorEx motor : motors) {
            if (motor != null) updateSlidePID(motor, targetPosition, pidf);
        }
    }

    public static void setSlidePosition(DcMotorEx motor, double targetPosition, PIDFCoefficients pidf) {
        if (motor != null) updateSlidePID(motor, targetPosition, pidf);
    }

    public static void setArmAngle(DcMotorEx motor, double targetAngleDeg, double motorTicksPerRev, double powerLimit, PIDFCoefficients pidf) {
        if (motor != null) updateArmPID(motor, targetAngleDeg, motorTicksPerRev, powerLimit, pidf);
    }

    private static void updateSlidePID(DcMotorEx motor, double targetPosition, PIDFCoefficients pidf) {
        PIDState state = pidStateMap.computeIfAbsent(motor, k -> new PIDState());

        double dt = state.timer.seconds();
        state.timer.reset();
        if (dt == 0) return; // Prevent division by zero

        double currentPosition = motor.getCurrentPosition();
        double error = targetPosition - currentPosition;

        state.integralSum += error * dt;
        state.integralSum = clamp(state.integralSum, -100, 100);

        double derivative = (error - state.lastError) / dt;
        state.lastError = error;

        double output = (pidf.p * error) + (pidf.i * state.integralSum) + (pidf.d * derivative) + pidf.f;

        // Use cached setPower via Motors class if available, or direct setPower (but carefully)
        // Since this class doesn't see Motors.setPower easily without circular dep,
        // we will just set it directly but check against local cache if we wanted.
        // For now, assuming direct setPower is fine as this PID updates constantly.
        motor.setPower(clamp(output, -1.0, 1.0));
    }

    private static void updateArmPID(DcMotorEx motor, double targetAngle, double ticksPerRev, double powerLimit, PIDFCoefficients pidf) {
        PIDState state = pidStateMap.computeIfAbsent(motor, k -> new PIDState());

        double dt = state.timer.seconds();
        state.timer.reset();
        if (dt == 0) return;

        double currentAngle = motor.getCurrentPosition() * 360.0 / ticksPerRev;
        double error = targetAngle - currentAngle;

        state.integralSum += error * dt;
        state.integralSum = clamp(state.integralSum, -100, 100);

        double derivative = (error - state.lastError) / dt;
        state.lastError = error;

        double gravityComp = pidf.f * Math.cos(Math.toRadians(currentAngle));
        double output = (pidf.p * error) + (pidf.i * state.integralSum) + (pidf.d * derivative) + gravityComp;

        motor.setPower(clamp(output, -powerLimit, powerLimit));
    }

    private static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}