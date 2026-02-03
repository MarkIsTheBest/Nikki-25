package org.firstinspires.ftc.teamcode.helper.control;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.constants.Control;

public class FlywheelPID {

    private final double TICKS_PER_REV = 28.0;

    public void setRPM(DcMotorEx motor, double targetRPM) {
        if (motor == null) return;

        PIDFCoefficients pidf = Control.Flywheel.pidf;

        motor.setPIDFCoefficients(
                DcMotor.RunMode.RUN_USING_ENCODER,
                new com.qualcomm.robotcore.hardware.PIDFCoefficients(pidf.P, pidf.I, pidf.D, pidf.F)
        );

        double targetTicksPerSec = targetRPM * TICKS_PER_REV / 60.0;
        motor.setVelocity(targetTicksPerSec);
    }

    public double getCurrentRPM(DcMotorEx motor) {
        if (motor == null) return 0.0;
        return motor.getVelocity() * 60.0 / TICKS_PER_REV;
    }
}
