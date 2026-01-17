package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;

@Configurable
@TeleOp(name = "VelocityControl", group = "TestsPID")
public class VelocityControl extends LinearOpMode {

    private final Debug debug = new Debug(telemetry);

    private final double MOTOR_RPM = 6000;
    private final double TICKS_PER_REV = 28;
    private final double MAX_TICKS_PER_SECOND = MOTOR_RPM * TICKS_PER_REV / 60.0;
    public final double f = 12 / MAX_TICKS_PER_SECOND;

    private DcMotorEx[] flywheelMotor;

    public static double p = 0.00;
    public static double i = 0.00;
    public static double d = 0.00;

    public static double targetRPM;
    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        Motors.init();
        flywheelMotor = Motors.Launchers();
        for (DcMotorEx motor : flywheelMotor) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    private void play() {
        // start logic
    }

    private void update() {
        double targetTicksPerSec = targetRPM * TICKS_PER_REV / 60.0;
        for (DcMotorEx motor : flywheelMotor) {

            motor.setVelocityPIDFCoefficients(p,i,d,f);
            motor.setVelocity(targetTicksPerSec);
        }
        debug();
    }

    private void debug()
    {
        double currentRPM = flywheelMotor[1].getVelocity() * 60.0 / TICKS_PER_REV;
        debug.addData("Target RPM", targetRPM);
        debug.addData("Current RPM", currentRPM);
        debug.addData("Error", targetRPM - currentRPM);
        debug.addData("Power", flywheelMotor[1].getPower());
        debug.update();
    }
}