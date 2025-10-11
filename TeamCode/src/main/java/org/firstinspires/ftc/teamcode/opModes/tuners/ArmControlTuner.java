package org.firstinspires.ftc.teamcode.opModes.tuners;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.helper.hardware.Motors;

@Configurable
@TeleOp(name = "Arm Control Tuner", group = "Tuners")
public class ArmControlTuner extends LinearOpMode {

    private DcMotorEx[] motors;

    public static double maxPower;
    public static double motorTicksPerRev;

    public static PIDFCoefficients pidf;
    public static double targetAngle;

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

        };
    }

    private void play() {
        // start logic
    }

    private void update() {
        for (DcMotorEx motor : motors) {
            Motors.INSTANCE.setArmAngle(motor, targetAngle, motorTicksPerRev, maxPower, pidf);
        }
    }
}