package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.helper.Debug;

@Configurable
@TeleOp(name = "ArmControl", group = "TestsPID")
public class ArmControl extends LinearOpMode {

    private final int MOTOR_RPM = 312;
    private final double TICKS_PER_REV = 537.7;

    public static double targetAngleDeg = 45;
    public static double p = 0.015;
    public static double i = 0.0;
    public static double d = 0.0005;
    public static double kG = 0.05;

    public static double motorPowerLimit = 1.0;

    private DcMotorEx motor;
    private double integralSum = 0;
    private double lastError = 0;
    private ElapsedTime timer = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();
        timer.reset();

        while (opModeIsActive()) {
            double currentAngleDeg = getArmAngleDeg();
            double power = PIDControl(targetAngleDeg, currentAngleDeg);

            // Clip to [-1, 1]
            power = Math.max(-motorPowerLimit, Math.min(motorPowerLimit, power));

            motor.setPower(power);

            Debug.INSTANCE.addData("Target Angle", targetAngleDeg);
            Debug.INSTANCE.addData("Current Angle", currentAngleDeg);
            Debug.INSTANCE.addData("Output Power", power);
            Debug.INSTANCE.update();
        }
    }

    private double PIDControl(double referenceDeg, double stateDeg) {
        double error = referenceDeg - stateDeg;

        double dt = timer.seconds();
        timer.reset();

        integralSum += error * dt;
        integralSum = Math.max(-100, Math.min(100, integralSum));
        double derivative = (error - lastError) / dt;
        lastError = error;

        double gravityComp = kG * Math.cos(Math.toRadians(stateDeg));

        return (p * error) + (i * integralSum) + (d * derivative) + gravityComp;
    }

    private double getArmAngleDeg() {
        double ticks = motor.getCurrentPosition();
        double revolutions = ticks / TICKS_PER_REV;
        return revolutions * 360.0;
    }
}
