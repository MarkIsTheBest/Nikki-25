package org.firstinspires.ftc.teamcode.helper.control;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.helper.general.MathHelper;

@Configurable
public class CustomFlywheelPID {
    public static double kP = 0.0023;
    public static double kI = 0.001;
    public static double kD = 0.0;
    public static double kV = 0.0002;
    public static double kS = 0.11;

    private double integral = 0;
    private double lastError = 0;
    private final ElapsedTime timer = new ElapsedTime();

    public CustomFlywheelPID() {
        timer.reset();
    }

    public double update(double targetRPM, double currentRPM) {
        if (targetRPM == 0) {
            integral = 0;
            lastError = 0;
            timer.reset();
            return 0;
        }

        double dt = timer.seconds();
        timer.reset();
        if (dt <= 0) return 0;

        double error = targetRPM - currentRPM;

        if (Math.abs(error) < 300) {
            integral += error * dt;
            integral = MathHelper.clamp(integral, -500, 500);
        }

        double derivative = (error - lastError) / dt;
        lastError = error;

        double pid = kP * error + kI * integral + kD * derivative;
        double ff = kS * Math.signum(targetRPM) + kV * targetRPM;

        return MathHelper.clamp(ff + pid, 0.0, 1.0);
    }

    public double getCurrentRPM(DcMotorEx motor) {
        if (motor == null) return 0.0;
        return Math.abs(motor.getVelocity() * 60.0 / 28);
    }
}
