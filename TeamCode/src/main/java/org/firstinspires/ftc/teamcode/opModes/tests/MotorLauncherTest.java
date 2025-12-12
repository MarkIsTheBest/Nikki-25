package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
@Configurable
public class MotorLauncherTest extends LinearOpMode {

    private DcMotorEx launcher1, launcher2;

    public static double p = 0.0, i = 0.0, d = 0.0, f = 0.0;
    public static double targetVelocity;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        launcher1 = hardwareMap.get(DcMotorEx.class, "launcher");
        launcher2 = hardwareMap.get(DcMotorEx.class, "launcher2");

        launcher1.setDirection(DcMotorSimple.Direction.REVERSE);
        launcher2.setDirection(DcMotorSimple.Direction.FORWARD);

        launcher1.setVelocity(targetVelocity, AngleUnit.DEGREES);
        launcher2.setVelocity(targetVelocity, AngleUnit.DEGREES);
    }

    private void play() {
        // start logic
    }

    private void update() {
        if(gamepad1.aWasPressed())
        {
            launcher1.setPower(1);
        }

        if(gamepad1.bWasPressed())
        {
            launcher1.setPower(0);
        }

        if(gamepad1.xWasPressed())
        {
            launcher2.setPower(1);
        }

        if(gamepad1.yWasPressed())
        {
            launcher2.setPower(0);
        }


    }
}