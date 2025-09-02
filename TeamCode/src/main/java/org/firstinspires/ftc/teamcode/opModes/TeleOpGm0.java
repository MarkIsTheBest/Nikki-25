package org.firstinspires.ftc.teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.hardware.Motors;

@TeleOp
public class TeleOpGm0 extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        Motors.init(hardwareMap);
        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.1;
            double rx = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            Motors.leftFront.setPower(frontLeftPower);
            Motors.leftRear.setPower(backLeftPower);
            Motors.rightFront.setPower(frontRightPower);
            Motors.rightRear.setPower(backRightPower);
        }
    }
}