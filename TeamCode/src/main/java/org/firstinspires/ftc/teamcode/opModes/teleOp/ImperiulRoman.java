package org.firstinspires.ftc.teamcode.opModes.teleOp; // <- Location of script/class file

/* \/ Here are the imports/library files references \/ */
import static java.lang.Math.abs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp // Makes the OpMode accessible in the TeleOp panel
public class ImperiulRoman extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor intake;
    private DcMotorEx launcher;
    private double launchSpeed;
    private double max = 0;

    private Servo barrierRight;
    private boolean rightSpinningUp = false;
    private boolean rightShooting = false;
    ElapsedTime rightTimer = new ElapsedTime();

    private Servo barrierLeft;
    private boolean leftSpinningUp = false;
    private boolean leftShooting = false;
    ElapsedTime leftTimer = new ElapsedTime();
    ElapsedTime shootDelay = new ElapsedTime();

    private boolean launcherSpinningUp = false;  // true only during actual spin-up
    private boolean launcherBusy = false;

    private float toggle = -1;
    private float toggle2 = -1;
    private float number;
    private boolean backwards;

    @Override
    public void runOpMode() throws InterruptedException {

        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        intake = hardwareMap.dcMotor.get("intake");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        barrierRight = hardwareMap.servo.get("barrierRight");
        barrierLeft = hardwareMap.servo.get("barrierLeft");

        barrierRight.setPosition(0.6);
        barrierLeft.setPosition(0.4);

        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE );
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);

        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        waitForStart();

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;

            movement(y, x, rx);
            shoot();
            launchSpeed = launcher.getVelocity(AngleUnit.DEGREES);

            if (abs(launchSpeed) > max) {
                max = abs(launchSpeed);
            }

            telemetry.addData("toggle", toggle);

            telemetry.addData("frontRight", frontRight.getPower());
            telemetry.addData("backRight", backRight.getPower());
            telemetry.addData("backLeft", backLeft.getPower());
            telemetry.addData("frontLeft", frontLeft.getPower());

            telemetry.addData("frontRight Encoder Ticks", frontRight.getCurrentPosition());
            telemetry.addData("backRight Encoder Ticks", backRight.getCurrentPosition());
            telemetry.addData("backLeft Encoder Ticks", backLeft.getCurrentPosition());
            telemetry.addData("frontLeft Encoder Ticks", frontLeft.getCurrentPosition());
            telemetry.addData("Launcher Velocity", launchSpeed);
            telemetry.addData("Max Speed Reached", max);

            telemetry.update();
        }
    }
    private void movement(double y, double x, double rx) {
        double denominator = Math.max(abs(y) + abs(x) + abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        frontLeft.setPower(frontLeftPower);
        backLeft.setPower(backLeftPower);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);
    }



    private void shoot() {

        // ===== RIGHT SIDE PRESS =====
        if (gamepad1.rightBumperWasPressed() && !rightSpinningUp && !rightShooting && !launcherSpinningUp && shootDelay.seconds() > 0.1) {
            // Only spin up if motor is below threshold
            if (Math.abs(launchSpeed) < 175) {
                launcher.setPower(1);
                launcherSpinningUp = true;  // lock during spin-up
            }
            rightTimer.reset();
            rightSpinningUp = true;
            launcherBusy = true;
        }

        // Spin-up complete → fire
        if (rightSpinningUp && Math.abs(launchSpeed) >= 175) {
            launcherSpinningUp = false; // allow other side
            barrierRight.setPosition(0.75); // raise barrier
            intake.setPower(1);

            rightSpinningUp = false;
            rightShooting = true;
            rightTimer.reset();
        }

        // Shooting window for right
        if (rightShooting && rightTimer.seconds() > 0.3) {
            barrierRight.setPosition(0.6); // lower barrier
            rightShooting = false;
            shootDelay.reset();
            launcherBusy = false;


            // Stop launcher only if left side is NOT shooting or spinning up
            if (!leftShooting && !leftSpinningUp) launcher.setPower(0);
        }


        // ===== LEFT SIDE PRESS =====
        if (gamepad1.leftBumperWasPressed() && !leftSpinningUp && !leftShooting && !launcherSpinningUp  && shootDelay.seconds() > 0.1) {
            if (Math.abs(launchSpeed) < 175) {
                launcher.setPower(1);
                launcherSpinningUp = true;
            }
            leftTimer.reset();
            leftSpinningUp = true;
            launcherBusy = true;
        }

        // Spin-up complete → fire
        if (leftSpinningUp && Math.abs(launchSpeed) >= 175) {
            launcherSpinningUp = false;
            barrierLeft.setPosition(0.25); // raise barrier
            intake.setPower(1);

            leftSpinningUp = false;
            leftShooting = true;
            leftTimer.reset();
        }

        // Shooting window for left
        if (leftShooting && leftTimer.seconds() > 0.3) {
            barrierLeft.setPosition(0.4); // lower barrier
            leftShooting = false;
            shootDelay.reset();
            launcherBusy = false;


            // Stop launcher only if right side is NOT shooting or spinning up
            if (!rightShooting && !rightSpinningUp) launcher.setPower(0);
        }


        // ===== INTAKE MANUAL REVERSE =====
        if (gamepad1.b || gamepad2.a) {
            intake.setPower(-1);
            backwards = true;
        } else {
            backwards = false;
        }

        // ===== INTAKE TOGGLE =====
        if (!backwards) {
            if (gamepad1.aWasPressed() || gamepad2.aWasPressed()) toggle = -toggle;
            if (toggle == 1) intake.setPower(1);
            else if (toggle == -1 && !leftShooting && !rightShooting) intake.setPower(0);
        }

        if (gamepad1.yWasPressed() || gamepad2.yWasPressed()) toggle2 = -toggle2;
        if (toggle2 == 1) launcher.setPower(1);
        else if (toggle2 == -1 && !launcherBusy) launcher.setPower(0);
    }

}


// cine vede asta, sa stie ca suge pula tare de tot <3
//fac iu :(
//nu sug pula