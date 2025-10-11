package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.helper.Debug;

@Configurable
@TeleOp(name = "VelocityControl", group = "TestsPID")
public class VelocityControl extends LinearOpMode {

    private final double MOTOR_RPM = 6000;
    private final double TICKS_PER_REV = 28;
    private final double MAX_TICKS_PER_SECOND = MOTOR_RPM * TICKS_PER_REV / 60.0;
    public final double f = 12 / MAX_TICKS_PER_SECOND;

    private DcMotorEx flywheelMotor;

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
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "motor");
        flywheelMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheelMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void play() {
        // start logic
    }

    private void update() {
        flywheelMotor.setVelocityPIDFCoefficients(p,i,d,f);
        double targetTicksPerSec = targetRPM * TICKS_PER_REV / 60.0;
        flywheelMotor.setVelocity(targetTicksPerSec);
    }
    
    private void debug()
    {
        double currentRPM = flywheelMotor.getVelocity() * 60.0 / TICKS_PER_REV;
        Debug.INSTANCE.addData("Target RPM", targetRPM);
        Debug.INSTANCE.addData("Current RPM", currentRPM);
        Debug.INSTANCE.addData("Error", targetRPM - currentRPM);
        Debug.INSTANCE.update();

    }
}