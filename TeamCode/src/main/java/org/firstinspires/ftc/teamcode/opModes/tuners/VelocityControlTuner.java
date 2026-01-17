package org.firstinspires.ftc.teamcode.opModes.tuners;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.MotorHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;

@Configurable
@TeleOp(name = "Velocity Control Tuner", group = "Tuners")
public class VelocityControlTuner extends LinearOpMode {

    Debug debug = new Debug(telemetry);
    private DcMotorEx[] motors;

    public static double motorMaxRPM;
    public static double motorTicksPerRev;

    public static double p;
    public static double i;
    public static double d;
    public static double f;

    public  double lastp;
    public  double lasti;
    public  double lastd;
    public  double lastf;

    public static double targetRPM;
    private double lastTargetRPM;

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
        motors = Motors.Launchers();
    }

    private void play() {
        // start logic
    }

    private void update() {
        if(lastTargetRPM != targetRPM || lastp != p || lasti != i || lastd != d || lastf != f) {
            for (DcMotorEx motor : motors) {
                MotorHelper.setRPM(motor, targetRPM, motorTicksPerRev, motorMaxRPM, new PIDFCoefficients(p,i,d,f));
            }
        }
        lastTargetRPM = targetRPM;
        lastp = p;
        lastd = d;
        lasti = i;
        lastf = f;

        debug.addData("CurrentRPM", MotorHelper.getCurrentRPM(motors[1]));
        debug.addData("TargetRPM", targetRPM);
        debug.update();
    }
}