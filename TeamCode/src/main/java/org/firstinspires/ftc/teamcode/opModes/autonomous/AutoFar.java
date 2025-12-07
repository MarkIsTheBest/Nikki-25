package org.firstinspires.ftc.teamcode.opModes.autonomous;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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

@Autonomous
public class AutoFar extends LinearOpMode {

    private double launchSpeed;
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

    private DcMotor intake;
    private DcMotorEx launcher;

    private AprilTagHelper aprilTags = new AprilTagHelper();

    private int motif;

    private int timesThrown = 0;
    private boolean secondThrow = false;
    //mark aster suge pula
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

    // Start Pose
    private final Pose startPose = new Pose(57, 135, Math.toRadians(90)); // Start position

    // Trajectory Poses
    private final Pose path1Pose = new Pose(44.5, 117.5, Math.toRadians(150)); // Path 1
    private final Pose path2Pose = new Pose(44.5, 85, Math.toRadians(180)); // Path 2
    private final Pose path3Pose = new Pose(20, 85, Math.toRadians(180)); // Path 3
    private final Pose path4Pose = new Pose(44.5, 117.5, Math.toRadians(45)); // Path 4
    private final Pose path5Pose = new Pose(44.6, 117.6, Math.toRadians(150)); // Path 5
    private final Pose path6Pose = new Pose(44.5, 78, Math.toRadians(-90)); // Path 6

    private PathChain path1Path, path2Path, path3Path, path4Path, path5Path, path6Path;

    public void buildPaths() {
        path1Path = follower.pathBuilder()
                .addPath(new BezierLine(startPose, path1Pose))
                .setLinearHeadingInterpolation(startPose.getHeading(), path1Pose.getHeading())
                .build();

        path2Path = follower.pathBuilder()
                .addPath(new BezierLine(path1Pose, path2Pose))
                .setLinearHeadingInterpolation(path1Pose.getHeading(), path2Pose.getHeading())
                .build();

        path3Path = follower.pathBuilder()
                .addPath(new BezierLine(path2Pose, path3Pose))
                .setConstantHeadingInterpolation(path3Pose.getHeading())
                .build();

        path4Path = follower.pathBuilder()
                .addPath(new BezierLine(path3Pose, path4Pose))
                .setLinearHeadingInterpolation(path3Pose.getHeading(), path4Pose.getHeading())
                .build();

        path5Path = follower.pathBuilder()
                .addPath(new BezierLine(path4Pose, path5Pose))
                .setLinearHeadingInterpolation(path4Pose.getHeading(), path5Pose.getHeading())
                .build();

        path6Path = follower.pathBuilder()
                .addPath(new BezierLine(path5Pose, path6Pose))
                .setLinearHeadingInterpolation(path5Pose.getHeading(), path6Pose.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                secondThrow = false;
                follower.followPath(path1Path);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) {
                    intake.setPower(1);
                    launcher.setVelocity(180,AngleUnit.DEGREES);
                    setPathState(2);
                }
                break;
            case 2:
                if (Math.abs(launcher.getVelocity(AngleUnit.DEGREES)) >= 175 && pathTimer.getElapsedTimeSeconds() > 0.25 && !follower.isBusy()) {
                    barrierLeft.setPosition(0.25); //open
                    timesThrown++;
                    if(timesThrown <= 4 && !secondThrow) setPathState(3);
                    else if(!secondThrow) setPathState(5);
                    else if(secondThrow && timesThrown <= 8) setPathState(3);
                    else setPathState(14);
                }
                break;
            case 3:
                if (pathTimer.getElapsedTimeSeconds() > 0.3) {
                    barrierLeft.setPosition(0.4); //close
                    if(timesThrown <= 4 && !secondThrow) setPathState(-2);
                    else if(!secondThrow) setPathState(5);
                    else if(secondThrow && timesThrown <= 8) setPathState(-2);
                    else setPathState(14);
                }
                break;
            case -2:
                if (Math.abs(launcher.getVelocity(AngleUnit.DEGREES)) >= 175 && pathTimer.getElapsedTimeSeconds() > 0.25) {
                    barrierRight.setPosition(0.75); //open
                    timesThrown++;
                    if(timesThrown <= 4 && !secondThrow) setPathState(-3);
                    else if(!secondThrow) setPathState(5);
                    else if(secondThrow && timesThrown <= 8) setPathState(-3);
                    else setPathState(14);
                }
                break;
            case -3:
                if (pathTimer.getElapsedTimeSeconds() > 0.3) {
                    barrierRight.setPosition(0.6); //close
                    intake.setPower(1);
                    if(timesThrown <= 4) setPathState(2);
                    else if(!secondThrow) setPathState(5);
                    else if(secondThrow && timesThrown <= 8) setPathState(2);
                    else setPathState(14);
                }
                break;

            case 5:
                if (pathTimer.getElapsedTimeSeconds() > 2) {
                    follower.followPath(path2Path);
                    setPathState(6);
                }
                break;
            case 6:
                if (!follower.isBusy()) {
                    intake.setPower(1);
                    follower.followPath(path3Path);
                    setPathState(7);
                }
                break;
            case 7:
                if (!follower.isBusy()) {
                    intake.setPower(0);
                    follower.followPath(path4Path);
                    setPathState(8);
                }
                break;
            case 8:
                if (!follower.isBusy()) {
                    motif = getMotif(aprilTags.detectedId);
                    setPathState(9);
                }
                break;
            case 9:
                if (pathTimer.getElapsedTimeSeconds() > 1) {
                    secondThrow = true;
                    follower.followPath(path5Path);
                    if(timesThrown <= 8) setPathState(2);
                    else setPathState(5);
                }
                break;
            case 14:
                if (pathTimer.getElapsedTimeSeconds() > 2) {
                    follower.followPath(path6Path);
                    setPathState(-1);
                }
                break;
        }
    }

    private void initialize() {

        aprilTags.init(hardwareMap, "Webcam");
        aprilTags.allowedIDs(new ArrayList<>(Arrays.asList(21,22,23)));

        initializeHardware();
        pathTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);
    }

    private int getMotif(int id)
    {
        //GPP 0
        //PGP 1
        //PPG 2

        if(id == 21) return 0;
        else if (id == 22) return 1;
        else if (id == 23) return 2;
        return -1;
    }

    private void play() {
        setPathState(0);
    }

    private void update() {
        aprilTags.update();
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.addData("velocity",launcher.getVelocity(AngleUnit.DEGREES));
        telemetry.update();
    }

    private void initializeHardware()
    {
        intake = hardwareMap.dcMotor.get("intake");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        barrierRight = hardwareMap.servo.get("barrierRight");
        barrierLeft = hardwareMap.servo.get("barrierLeft");

        barrierRight.setPosition(0.6);
        barrierLeft.setPosition(0.4);

        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }
}