package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.teamcode.helper.Debug;
import org.firstinspires.ftc.teamcode.helper.MotorHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;

@Configurable
@TeleOp
public class MotorLauncherTest extends LinearOpMode {

    private DcMotorEx motor;
    private DcMotorEx motor2;

    public static double motorMaxRPM;
    public static double motorTicksPerRev;

    public static double p;
    public static double i;
    public static double d;

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
        motor = hardwareMap.get(DcMotorEx.class, "launcher");
        motor2 = hardwareMap.get(DcMotorEx.class, "launcher2");
    }

    private void play() {
        // start logic
    }

    private void update() {
        MotorHelper.setRPM(motor, targetRPM, motorTicksPerRev, motorMaxRPM, new PIDCoefficients(p,i,d));
        MotorHelper.setRPM(motor2, targetRPM, motorTicksPerRev, motorMaxRPM, new PIDCoefficients(p,i,d));
        Debug.INSTANCE.addData("RPM", MotorHelper.getCurrentRPM(motor, motorTicksPerRev));
        Debug.INSTANCE.addData("Target RPM", targetRPM);
        Debug.INSTANCE.update();
    }
}