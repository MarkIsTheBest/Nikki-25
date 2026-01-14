package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.helper.IntakeHelper;
import org.firstinspires.ftc.teamcode.helper.LaunchHelper;
import org.firstinspires.ftc.teamcode.helper.Launchers;
import org.firstinspires.ftc.teamcode.helper.general.FpsCounter;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;

@TeleOp(name = "TeleOp GM Zero (Improved)")
public class TeleOpGMZero extends LinearOpMode {

    FpsCounter fps = new FpsCounter();

    // Deadzone for joysticks
    private static final double DEADZONE = 0.05;

    IntakeHelper intakeHelper;
    Launchers launcherHelper;


    @Override
    public void runOpMode() {

        Hardware.init();
        Launchers.INSTANCE = new Launchers();
        launcherHelper = Launchers.INSTANCE;

        IntakeHelper.INSTANCE = new IntakeHelper();
        intakeHelper = IntakeHelper.INSTANCE;

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
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

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

            fps.update();
            intakeHelper.update();
            intakeHelper.HandleIntakeSpin();

            if(gamepad1.aWasPressed()) {
                intakeHelper.spinIntake(true);
            }
            if(gamepad1.bWasPressed()) {
                intakeHelper.spinIntake(false);
            }

            telemetry.addData("Fps", fps.getFps());
            telemetry.addData("Speed Mode", gamepad1.left_bumper ? "SLOW" : "NORMAL");
            telemetry.update();
        }
    }
}