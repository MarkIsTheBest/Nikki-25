package org.firstinspires.ftc.teamcode.opModes.autonomous;
import com.pedropathing.follower.Follower;
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
public class AutoNearRed extends LinearOpMode {

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
    private boolean thirdThrow = false;
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

    private final Pose startPose = new Pose(59.6, 9, Math.toRadians(90)).mirror(); // Start position

    // Trajectory Poses
    private final Pose path1Pose = new Pose(34.4, 17, Math.toRadians(90)).mirror(); // Path 1

    private PathChain parkPath;

    public void buildPaths() {
        parkPath = follower.pathBuilder()
                .addPath(new BezierLine(startPose, path1Pose))
                .setConstantHeadingInterpolation(path1Pose.getHeading())
                .build();
    }

    private int shootStep = 0;   // 0 = left, 1 = right, 2 = both
    private int throwCycle = 0;  // 0..2 (3 cycles total)

    private void fireCurrentStep() {
        switch (shootStep) {
            case 0:  // LEFT
                barrierLeft.setPosition(0.25);
                break;

            case 1:  // RIGHT
                barrierRight.setPosition(0.75);
                break;

            case 2:  // BOTH
                barrierLeft.setPosition(0.25);
                barrierRight.setPosition(0.75);
                break;
        }
    }

    private void closeBarriers() {
        barrierLeft.setPosition(0.4);
        barrierRight.setPosition(0.6);
    }

    public void autonomousPathUpdate() {

        switch (pathState) {
            case 0:
                follower.followPath(parkPath);
                setPathState(-2);
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

        telemetry.addData("isFollowerBusy",follower.isBusy());
        telemetry.addData("path state", pathState);
        telemetry.addData("times thrown", timesThrown);
        telemetry.addData("thrownSecond", secondThrow);
        telemetry.addData("throwThird", thirdThrow);
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
