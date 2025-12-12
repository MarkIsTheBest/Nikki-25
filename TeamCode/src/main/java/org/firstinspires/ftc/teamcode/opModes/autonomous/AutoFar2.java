package org.firstinspires.ftc.teamcode.opModes.autonomous;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.opModes.helper.AprilTagHelper;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.ArrayList;
import java.util.Arrays;

@Autonomous
public class AutoFar2 extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }
    private void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    private Follower follower;
    private Timer pathTimer;
    private int pathState;

    private Servo barrierLeft;
    private Servo barrierRight;

    private DcMotorEx launcher;
    private DcMotorEx launcher2;
    private int shootCycle;

    private DcMotor intake;

    private ColorSensor leftSensor;
    private ColorSensor rightSensor;

    private AprilTagHelper aprilTag = new AprilTagHelper();

    private final double BARRIER_LEFT_OPEN = 0.25;
    private final double BARRIER_LEFT_CLOSE = 0.4;
    private final double BARRIER_RIGHT_OPEN = 0.75;
    private final double BARRIER_RIGHT_CLOSE = 0.6;

    // Start Pose
    private final Pose startPose = new Pose(22.1, 127, Math.toRadians(90)); // Start position

    // Trajectory Poses
    private final Pose getmotifPose = new Pose(45.36, 108.9, Math.toRadians(53)); // GetMotif
    private final Pose throwPose = new Pose(28.5, 114.7, Math.toRadians(140)); // Throw
    private final Pose prepareintake1Pose = new Pose(50, 84, Math.toRadians(180)); // PrepareIntake1
    private final Pose intake1Pose = new Pose(16, 84, Math.toRadians(180)); // Intake1
    private final Pose prepareintake2Pose = new Pose(50, 60, Math.toRadians(180)); // PrepareIntake2
    private final Pose intake2Pose = new Pose(2, 60, Math.toRadians(180)); // Intake2
    private final Pose leavePose = new Pose(30.6, 79, Math.toRadians(-90)); // Leave

    private PathChain getmotifPath, throwPath, prepareintake1Path, intake1Path, throwPath2, prepareintake2Path, intake2Path, throwPath3, leavePath;

    public void buildPaths() {
        getmotifPath = follower.pathBuilder()
                .addPath(new BezierLine(startPose, getmotifPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), getmotifPose.getHeading())
                .build();

        throwPath = follower.pathBuilder()
                .addPath(new BezierLine(getmotifPose, throwPose))
                .setLinearHeadingInterpolation(getmotifPose.getHeading(), throwPose.getHeading())
                .build();

        prepareintake1Path = follower.pathBuilder()
                .addPath(new BezierLine(throwPose, prepareintake1Pose))
                .setLinearHeadingInterpolation(throwPose.getHeading(), prepareintake1Pose.getHeading())
                .build();

        intake1Path = follower.pathBuilder()
                .addPath(new BezierLine(prepareintake1Pose, intake1Pose))
                .setConstantHeadingInterpolation(intake1Pose.getHeading())
                .build();

        throwPath2 = follower.pathBuilder()
                .addPath(new BezierLine(intake1Pose, throwPose))
                .setLinearHeadingInterpolation(intake1Pose.getHeading(), throwPose.getHeading())
                .build();

        prepareintake2Path = follower.pathBuilder()
                .addPath(new BezierLine(throwPose, prepareintake2Pose))
                .setLinearHeadingInterpolation(throwPose.getHeading(), prepareintake2Pose.getHeading())
                .build();

        intake2Path = follower.pathBuilder()
                .addPath(new BezierLine(prepareintake2Pose, intake2Pose))
                .setConstantHeadingInterpolation(intake2Pose.getHeading())
                .build();

        throwPath3 = follower.pathBuilder()
                .addPath(new BezierCurve(
                        intake2Pose,
                        new Pose(60, 50), // Control point
                        throwPose
                ))
                .setLinearHeadingInterpolation(intake2Pose.getHeading(), throwPose.getHeading())
                .build();

        leavePath = follower.pathBuilder()
                .addPath(new BezierLine(throwPose, leavePose))
                .setLinearHeadingInterpolation(throwPose.getHeading(), leavePose.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(getmotifPath);
                setPathState(1);
                break;

            case 1:
                if (!follower.isBusy()) {
                    follower.followPath(throwPath);
                    setPathState(2);
                }
                break;

            case 2:
                if (!follower.isBusy()) {
                    shoot();
                    setPathState(3);
                }
                break;

            case 3:
                if (!follower.isBusy()) {
                    follower.followPath(prepareintake1Path);
                    shootCycle = 0;
                    setPathState(4);
                }
                break;

            case 4:
                if (pathTimer.getElapsedTimeSeconds() > 3) {
                    follower.followPath(intake1Path);
                    setPathState(5);
                }
                break;

            case 5:
                if (pathTimer.getElapsedTimeSeconds() > 3) {
                    follower.followPath(throwPath2);
                    setPathState(6);
                }
                break;

            case 6:
                if (pathTimer.getElapsedTimeSeconds() > 3) {
                    shoot();
                    setPathState(7);
                }
                break;

            case 7:
                if (pathTimer.getElapsedTimeSeconds() > 3) {
                    shootCycle = 0;
                    follower.followPath(prepareintake2Path);
                    setPathState(8);
                }
                break;

            case 8:
                if (pathTimer.getElapsedTimeSeconds() > 3) {
                    follower.followPath(intake2Path);
                    setPathState(9);
                }
                break;

            case 9:
                if (pathTimer.getElapsedTimeSeconds() > 3) {
                    follower.followPath(throwPath3);
                    setPathState(10);
                }
                break;

            case 10:
                if (pathTimer.getElapsedTimeSeconds() > 3) {
                    shoot();
                    setPathState(11);
                }
                break;

            case 11:
                if (pathTimer.getElapsedTimeSeconds() > 3) {
                    shootCycle = 0;
                    follower.followPath(leavePath);
                    setPathState(12);
                }
                break;

            case 12:
                // Done
                break;
        }
    }

    private void initialize() {
        aprilTag.init(hardwareMap, "Webcam");
        aprilTag.allowedIDs(new ArrayList<>(Arrays.asList(21,22,23)));

        pathTimer = new Timer();
        leftShootingTimer = new Timer();
        rightShootingTimer = new Timer();

        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);

        initializeHardware();
    }

    private void initializeHardware() {
        barrierLeft = hardwareMap.servo.get("barrierLeft");
        barrierRight = hardwareMap.servo.get("barrierRight");
        barrierRight.setPosition(0.6);
        barrierLeft.setPosition(0.4);

        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        launcher2 = hardwareMap.get(DcMotorEx.class, "launcher2");

        rightSensor = hardwareMap.colorSensor.get("rightSensor");
        leftSensor = hardwareMap.colorSensor.get("leftSensor");

        intake = hardwareMap.dcMotor.get("intake");

    }

    private void play() {
        setPathState(0);
    }

    private void update() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }

    private enum shootingStates {
        SPIN_UP,
        SHOOT,
        CLOSE
    }

    private shootingStates leftShootingStates;
    private shootingStates rightShootingStates;

    private Timer leftShootingTimer;
    private Timer rightShootingTimer;

    private void shoot() {

        switch (shootCycle) {
            case 0:
                launcher.setVelocity(180);
                launcher.setVelocity(180);
                intake.setPower(1);
                if (launcher.getVelocity(AngleUnit.DEGREES) >= 175) {
                    barrierLeft.setPosition(BARRIER_LEFT_OPEN);
                    launcher.setPower(0);
                    launcher2.setPower(0);
                    intake.setPower(0);
                    shootCycle++;
                    break;
                }

            case 1:
                launcher.setVelocity(0.8);
                launcher.setVelocity(0.8);
                intake.setPower(1);
                if (launcher.getVelocity(AngleUnit.DEGREES) >= 175) {
                    barrierRight.setPosition(BARRIER_RIGHT_OPEN);
                    launcher.setPower(0);
                    launcher2.setPower(0);
                    intake.setPower(0);
                    break;
                }
            case 2:
                launcher.setVelocity(0.8);
                launcher.setVelocity(0.8);
                intake.setPower(1);
                if (launcher.getVelocity(AngleUnit.DEGREES) >= 175) {
                    barrierLeft.setPosition(BARRIER_LEFT_OPEN);
                    launcher.setPower(0);
                    launcher2.setPower(0);
                    intake.setPower(0);
                    break;
                }
        }
    }

    private void shootLeft()
    {
        switch(leftShootingStates)
        {
            case SPIN_UP:
                launcher.setVelocity(180);
                launcher.setVelocity(180);
                intake.setPower(1);
                leftShootingStates = shootingStates.SHOOT;
                break;

            case SHOOT:
                if (launcher.getVelocity(AngleUnit.DEGREES) >= 175) {
                    barrierLeft.setPosition(BARRIER_LEFT_OPEN);
                    leftShootingStates = shootingStates.CLOSE;
                }
                break;

            case CLOSE:
                if(leftShootingTimer.getElapsedTimeSeconds() > 1) {
                    barrierLeft.setPosition(BARRIER_LEFT_CLOSE);
                    launcher.setPower(0);
                    launcher2.setPower(0);
                    intake.setPower(0);
                }
                break;
        }
    }

    private void shootRight()
    {
        switch(rightShootingStates)
        {
            case SPIN_UP:
                launcher.setVelocity(180);
                launcher.setVelocity(180);
                intake.setPower(1);
                rightShootingStates = shootingStates.SHOOT;
                break;

            case SHOOT:
                if (launcher.getVelocity(AngleUnit.DEGREES) >= 175) {
                    barrierLeft.setPosition(BARRIER_LEFT_OPEN);
                    rightShootingStates = shootingStates.CLOSE;
                }
                break;

            case CLOSE:
                if(rightShootingTimer.getElapsedTimeSeconds() > 1) {
                    barrierLeft.setPosition(BARRIER_LEFT_CLOSE);
                    launcher.setPower(0);
                    launcher2.setPower(0);
                    intake.setPower(0);
                }
                break;
        }
    }

    public float rgbToHue(int r, int g, int b) {
        float[] hsv = new float[3];
        android.graphics.Color.RGBToHSV(r, g, b, hsv);
        return hsv[0];
    }
}

