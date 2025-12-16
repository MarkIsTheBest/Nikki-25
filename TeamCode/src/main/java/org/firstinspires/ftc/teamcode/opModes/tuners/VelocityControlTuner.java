package org.firstinspires.ftc.teamcode.opModes.tuners;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.teamcode.helper.Debug;
import org.firstinspires.ftc.teamcode.helper.MotorHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;

@Configurable
@TeleOp(name = "Velocity Control Tuner", group = "Tuners")
public class VelocityControlTuner extends LinearOpMode {

    private DcMotorEx[] motors;

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
        motors = new DcMotorEx[] {
                hardwareMap.get(DcMotorEx.class,"motor")
        };
    }

    private void play() {
        // start logic
    }

    private void update() {
        for (DcMotorEx motor : motors) {
            MotorHelper.setRPM(motor, targetRPM, motorTicksPerRev, motorMaxRPM, new PIDCoefficients(p,i,d));
        }
        Debug.INSTANCE.addData("RPM", MotorHelper.getCurrentRPM(motors[0],motorTicksPerRev));
        Debug.INSTANCE.addData("Power", motors[0].getPower());
        Debug.INSTANCE.update();
    }
}