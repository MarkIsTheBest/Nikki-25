package org.firstinspires.ftc.teamcode.helper.control;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.util.Timer;
import org.firstinspires.ftc.teamcode.constants.Control;
import org.firstinspires.ftc.teamcode.helper.general.MathHelper;

public class TurretPID {

    private double integralSum = 0;
    private double lastError = 0;
    private double lastDerivative = 0;

    // Smooths the derivative: 0 is no smoothing, 0.9 is heavy smoothing
    private final double lowPassGain = 0.5;
    // Stops micro-adjustments within this many ticks
    private final double deadbandTicks = 1;

    private final Timer timer = new Timer();
    private final PIDFCoefficients pid;

    public TurretPID(PIDFCoefficients pidf) {
        this.pid = pidf;
    }

    public TurretPID() {
        this(Control.Turret.pidf);
    }

    public double getPower(int reference, int state, PIDFCoefficients pidf) {
        double error = reference - state;

        // 1. Deadband: If we are close enough, just stop.
        if (Math.abs(error) <= deadbandTicks) {
            return 0;
        }

        double dt = timer.getElapsedTimeSeconds();
        timer.resetTimer();

        // Prevent division by zero or massive spikes from long pauses
        if (dt <= 0 || dt > 0.1) dt = 0.02;

        // 2. Filtered Derivative
        double currentDerivative = (error - lastError) / dt;
        // Low-pass filter formula: (gain * previous) + ((1 - gain) * current)
        double filteredDerivative = (lowPassGain * lastDerivative) + ((1 - lowPassGain) * currentDerivative);

        lastError = error;
        lastDerivative = filteredDerivative;

        // 3. Integral Logic
        if (Math.abs(MathHelper.ticksToAngle((int)error)) < 15.0) {
            integralSum += error * dt;
        } else {
            integralSum = 0;
        }

        // Anti-windup
        double iLimit = 0.25 / (pidf.I + 1e-6);
        integralSum = MathHelper.clamp(integralSum, -iLimit, iLimit);

        // 4. Feedforward (Static Friction Compensation)
        // Only apply if error is significant to prevent humming at rest
        double feedforward = 0;
        if (Math.abs(error) > deadbandTicks) {
            feedforward = pidf.F * Math.signum(error);
        }

        double output = (pidf.P * error) + (pidf.I * integralSum) + (pidf.D * filteredDerivative) + feedforward;

        return MathHelper.clamp(output, -1.0, 1.0);
    }

    // Overload to use internal coefficients if none provided
    public double getPower(int reference, int state) {
        return getPower(reference, state, this.pid);
    }
}