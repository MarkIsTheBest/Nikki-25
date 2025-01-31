package tests.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import subsystems.hardware.Motors;

@Autonomous(name = "Auto", group = "Autonomous", preselectTeleOp = "TeleOpTest")

public final class RedSample extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Motors.init(hardwareMap);

        waitForStart();
        Motors.rightFront.setPower(-0.5);
        Motors.leftFront.setPower(0.5);
        Motors.leftRear.setPower(0.5);
        Motors.rightRear.setPower(-0.5);

        sleep(2000);

        Motors.rightFront.setPower(0);
        Motors.leftFront.setPower(0);
        Motors.leftRear.setPower(0);
        Motors.rightRear.setPower(0);
    }
}