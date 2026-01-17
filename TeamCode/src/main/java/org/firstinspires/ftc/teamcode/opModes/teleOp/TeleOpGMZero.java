package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.constants.Positions;

@TeleOp(name = "TeleOp GM Zero (Improved)")
public class TeleOpGMZero extends LinearOpMode {
    // Deadzone for joysticks
    private static final double DEADZONE = 0.05;
    @Override
    public void runOpMode() {

        Servo holder1 = hardwareMap.servo.get("holder1");
        Servo holder2 = hardwareMap.servo.get("holder2");
        Servo holder3 = hardwareMap.servo.get("holder3");
        Servo door1   = hardwareMap.servo.get("door1");
        Servo door2   = hardwareMap.servo.get("door2");

        /*holder1.setPosition(Positions.Servo.H_PREPARE);
        holder2.setPosition(Positions.Servo.H_PREPARE);
        holder3.setPosition(Positions.Servo.H_PREPARE);

        door1.setPosition(Positions.Servo.C_D1_PREPARE);
        door2.setPosition(Positions.Servo.C_D2_PREPARE);*/

        // Motors
        DcMotor frontLeft  = hardwareMap.dcMotor.get("leftFront");
        DcMotor backLeft   = hardwareMap.dcMotor.get("leftRear");
        DcMotor frontRight = hardwareMap.dcMotor.get("rightFront");
        DcMotor backRight  = hardwareMap.dcMotor.get("rightRear");

        // Reverse right side (typical mecanum setup)
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        // Brake for better control
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // No motor PID – smoother mecanum control
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addLine("Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Read joystick values
            double y  = -gamepad1.left_stick_y;  // forward/back
            double x  = gamepad1.left_stick_x;   // strafe
            double rx = gamepad1.right_stick_x;  // rotation

            // Deadzone
            if (Math.abs(y)  < DEADZONE) y  = 0;
            if (Math.abs(x)  < DEADZONE) x  = 0;
            if (Math.abs(rx) < DEADZONE) rx = 0;

            // Square inputs for finer control
            y  = Math.copySign(y * y, y);
            x  = Math.copySign(x * x, x);
            rx = Math.copySign(rx * rx, rx);

            // Slow mode (hold left bumper)
            double speedMultiplier = gamepad1.left_bumper ? 0.4 : 1.0;

            // Mecanum math
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0);

            double frontLeftPower  = (y + x + rx) / denominator;
            double backLeftPower   = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower  = (y + x - rx) / denominator;

            // Apply power
            frontLeft.setPower(frontLeftPower * speedMultiplier);
            backLeft.setPower(backLeftPower * speedMultiplier);
            frontRight.setPower(frontRightPower * speedMultiplier);
            backRight.setPower(backRightPower * speedMultiplier);

            telemetry.addData("Speed Mode", gamepad1.left_bumper ? "SLOW" : "NORMAL");
            telemetry.update();
        }
    }
}