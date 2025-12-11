package org.firstinspires.ftc.teamcode.opModes.teleOp; // <- Location of script/class file

/* \/ Here are the imports/library files references \/ */
import static java.lang.Math.abs;
import static java.lang.Math.pow;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.opModes.helper.AprilTagHelper;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TeleOp // Makes the OpMode accessible in the TeleOp panel
public class ImperiulRoman extends LinearOpMode {

    private BNO055IMU imu;
    private double headingOffset = 0;
    private ElapsedTime headingTimer = new ElapsedTime();
    private ColorSensor sensorRight;
    private ColorSensor sensorLeft;

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor intake;
    private DcMotorEx launcher;
    private DcMotorEx launcher2;
    private double launchSpeed;
    private double variableSpeed = 175;

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
    private float slower;
    private boolean backwards;

    private AprilTagHelper aprilTags = new AprilTagHelper();



    @Override
    public void runOpMode() throws InterruptedException
    {
        initialize();
        waitForStart();

        while (opModeIsActive()) update();
    }

    private void initialize()
    {
        aprilTags.init(hardwareMap, "Webcam");
        aprilTags.allowedIDs(new ArrayList<>(Arrays.asList(20,24)));

        initializeHardware();
    }

    private void update()
    {
        aprilTags.update();

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        movement(y, x, rx);
        shoot();
        telemetry();


    }

    private void initializeHardware()
    {
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        intake = hardwareMap.dcMotor.get("intake");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        launcher2 = hardwareMap.get(DcMotorEx.class, "launcher2");
        barrierRight = hardwareMap.servo.get("barrierRight");
        barrierLeft = hardwareMap.servo.get("barrierLeft");

        sensorRight = hardwareMap.colorSensor.get("rightSensor");
        sensorLeft = hardwareMap.colorSensor.get("leftSensor");

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

        launcher.setDirection(DcMotorSimple.Direction.FORWARD);
        launcher2.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    private void movement(double y, double x, double rx)
    {
        if (gamepad1.dpadUpWasPressed()) {
            slower += 0.1;
        }
        else if (gamepad1.dpadDownWasPressed()) {
            slower -= 0.1;
        }

        double frontLeftPower  = (y + x + rx);
        double backLeftPower   = (y - x + rx);
        double frontRightPower = (y - x - rx);
        double backRightPower  = (y + x - rx);

        // --- SEND POWER TO MOTORS ---
        frontLeft.setPower(frontLeftPower / 1.15);
        backLeft.setPower(backLeftPower / 1.15);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);


    }

    private void shoot()
    {
        launchSpeed = launcher.getVelocity(AngleUnit.DEGREES);
        if (aprilTags.detectedDistance != -1) {
            variableSpeed = 31948090 + (118.5061 - 31948090)/(1 + Math.pow((aprilTags.detectedDistance/993163.5), 1.325604));
        }

        //y = 17181860 + (141.7382 - 17181860)/(1 + (x/293105.9)^1.444734)
        else variableSpeed = 175;

        // ===== RIGHT SIDE PRESS =====
        if (gamepad1.rightBumperWasPressed() && !rightSpinningUp && !rightShooting && !launcherSpinningUp && shootDelay.seconds() > 0.1) {
            // Only spin up if motor is below threshold
            if (Math.abs(launchSpeed) < variableSpeed) {
                launcher.setPower(1);
                launcher2.setPower(1);
                intake.setPower(1);
                launcherSpinningUp = true;  // lock during spin-up
            }
            rightTimer.reset();
            rightSpinningUp = true;
            launcherBusy = true;
        }

        // Spin-up complete → fire
        if (rightSpinningUp && Math.abs(launchSpeed) >= variableSpeed) {
            launcherSpinningUp = false; // allow other side
            barrierRight.setPosition(0.75); // raise barrier

            rightSpinningUp = false;
            rightShooting = true;
            rightTimer.reset();
        }

        // Shooting window for right
        if (rightShooting && rightTimer.seconds() > 0.25) {
            barrierRight.setPosition(0.6); // lower barrier
            rightShooting = false;
            shootDelay.reset();
            launcherBusy = false;


            // Stop launcher only if left side is NOT shooting or spinning up
            if (!leftShooting && !leftSpinningUp) {
                launcher.setPower(0);
                launcher2.setPower(0);
            }
        }


        // ===== LEFT SIDE PRESS =====
        if (gamepad1.leftBumperWasPressed() && !leftSpinningUp && !leftShooting && !launcherSpinningUp  && shootDelay.seconds() > 0.1) {
            if (Math.abs(launchSpeed) < variableSpeed) {
                launcher.setPower(1);
                launcher2.setPower(1);
                intake.setPower(1);
                launcherSpinningUp = true;
            }
            leftTimer.reset();
            leftSpinningUp = true;
            launcherBusy = true;
        }

        // Spin-up complete → fire
        if (leftSpinningUp && Math.abs(launchSpeed) >= variableSpeed) {
            launcherSpinningUp = false;
            barrierLeft.setPosition(0.25); // raise barrier

            leftSpinningUp = false;
            leftShooting = true;
            leftTimer.reset();
        }

        // Shooting window for left
        if (leftShooting && leftTimer.seconds() > 0.25) {
            barrierLeft.setPosition(0.4); // lower barrier
            leftShooting = false;
            shootDelay.reset();
            launcherBusy = false;


            // Stop launcher only if right side is NOT shooting or spinning up
            if (!rightShooting && !rightSpinningUp) {
                launcher.setPower(0);
                launcher2.setPower(0);
            }
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
            else if (toggle == -1 && !launcherBusy) intake.setPower(0);
        }

        if (gamepad1.yWasPressed() || gamepad2.yWasPressed()) toggle2 = -toggle2;
        if (toggle2 == 1) {
            launcher.setPower(1);
            launcher2.setPower(1);
        }

        else if (toggle2 == -1 && !launcherBusy) {
            launcher.setPower(0);
            launcher2.setPower(0);
        }
    }

    private void telemetry()
    {
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
        telemetry.addData("variable speed", variableSpeed);
        telemetry.addData("slower", slower);

        telemetry.addData("April Tag ID", aprilTags.detectedId);
        telemetry.addData("April Tag Distance (Inches)", aprilTags.detectedDistance);

        telemetry.update();
    }

}