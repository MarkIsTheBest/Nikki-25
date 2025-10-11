package org.firstinspires.ftc.teamcode.opModes.tuners;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.teamcode.helper.hardware.Motors;

@Configurable
@TeleOp(name = "Velocity Control Tuner", group = "Tuners")
public class VelocityControlTuner extends LinearOpMode {

    private DcMotorEx[] motors;

    public static double motorMaxRPM;
    public static double motorTicksPerRev;

    public static PIDCoefficients pid;
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
                Motors.INSTANCE.Launcher1(),
                Motors.INSTANCE.Launcher2()
        };
    }

    private void play() {
        // start logic
    }

    private void update() {
        for (DcMotorEx motor : motors) {
            Motors.INSTANCE.setRPM(motor, targetRPM, motorTicksPerRev, motorMaxRPM, pid);
        }
    }
}