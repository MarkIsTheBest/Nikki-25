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
public class AutoFarRed extends LinearOpMode {

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

    // Start Pose
    private final Pose startPose = new Pose(22.1, 127, Math.toRadians(143)).mirror(); // Start position

    // Trajectory Poses
    private final Pose path1Pose = new Pose(30.5-2, 116.7-2, Math.toRadians(140)).mirror(); // Path 1
    private final Pose path2Pose = new Pose(50, 84, Math.toRadians(180)).mirror(); // Path 2
    private final Pose path3Pose = new Pose(16, 84, Math.toRadians(180)).mirror(); // Path 3
    private final Pose path5Pose = new Pose(30.5-2, 116.7-2, Math.toRadians(140)).mirror(); // Path 5
    private final Pose path6Pose = new Pose(50, 60, Math.toRadians(180)).mirror(); // Path 6
    private final Pose path7Pose = new Pose(16-14, 60, Math.toRadians(180)).mirror(); // Path 7
    private final Pose path8Pose = new Pose(30.5-2, 116.7-2, Math.toRadians(150)).mirror(); // Path 8
    private final Pose parkPose = new Pose(30.6, 79, Math.toRadians(-90)).mirror(); // Path Pose


    private PathChain path1Path, path2Path, path3Path, path4Path, path6Path, path7Path, path8Path, parkPath;

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
                .addPath(new BezierLine(path3Pose, path5Pose))
                .setLinearHeadingInterpolation(path3Pose.getHeading(), path5Pose.getHeading())
                .build();

        path6Path = follower.pathBuilder()
                .addPath(new BezierLine(path5Pose, path6Pose))
                .setLinearHeadingInterpolation(path5Pose.getHeading(), path6Pose.getHeading())
                .build();

        path7Path = follower.pathBuilder()
                .addPath(new BezierLine(path6Pose, path7Pose))
                .setConstantHeadingInterpolation(path7Pose.getHeading())
                .build();

        path8Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        path7Pose,
                        new Pose(60, 40), // Control point
                        path8Pose
                ))
                .setLinearHeadingInterpolation(path7Pose.getHeading(), path8Pose.getHeading())
                .build();

        parkPath = follower.pathBuilder()
                .addPath(new BezierLine(path8Pose, parkPose))
                .setLinearHeadingInterpolation(path8Pose.getHeading(), parkPose.getHeading())
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

            /* -------------------------- */
            /*       START SEQUENCE       */
            /* -------------------------- */

            case 0: // Start → launch zone
                throwCycle = 0;
                shootStep = 0;       // left → right → both
                follower.followPath(path1Path);
                setPathState(1);
                break;

            case 1: // Arrived at launch → spin up shooter
                if (!follower.isBusy()) {
                    intake.setPower(1);
                    launcher.setPower(0.8);
                    setPathState(2);
                }
                break;

            /* -------------------------- */
            /*       SHOOTING STATE       */
            /* -------------------------- */

            case 2:   // Spin-up then fire correct step
                if (Math.abs(launcher.getVelocity(AngleUnit.DEGREES)) >= 170 &&
                        pathTimer.getElapsedTimeSeconds() > 0.5)
                {
                    fireCurrentStep();
                    setPathState(3);
                }
                break;

            case 3:  // Wait and close barriers
                if (pathTimer.getElapsedTimeSeconds() > 0.5) {

                    closeBarriers();

                    shootStep++;

                    if (shootStep < 3) {
                        // next throw in this cycle
                        setPathState(2);
                    } else {
                        // finished 3 throws → go collect
                        shootStep = 0;
                        throwCycle++;
                        launcher.setPower(0);
                        intake.setPower(1);

                        if (throwCycle < 3) {
                            // go collect balls
                            if(throwCycle == 1)
                                follower.followPath(path2Path);
                            else if(throwCycle == 2)
                                follower.followPath(path6Path);
                            setPathState(10);
                        } else {
                            // finished all 3 cycles → END
                            setPathState(-1);
                        }
                    }
                }
                break;

            /* -------------------------- */
            /*      COLLECTING STATES     */
            /* -------------------------- */

            case 10:  // Path2 done → go to stack
                if (!follower.isBusy()) {
                    if(throwCycle == 1)
                        follower.followPath(path3Path);
                    else if (throwCycle == 2)
                        follower.followPath(path7Path);
                    setPathState(11);
                }
                break;

            case 11: // At stack → return to launch point
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 1) {

                    // spin up launcher for next cycle
                    launcher.setPower(0.8);

                    if (throwCycle == 1) {
                        follower.followPath(path4Path);  // return path for 2nd cycle
                    } else {
                        follower.followPath(path8Path);  // return path for 3rd cycle
                    }
                    setPathState(12);
                }
                break;

            case 12: // Back at launcher → shoot next cycle
                if (!follower.isBusy()) {
                    setPathState(2);  // restart shooting
                }
                break;

            /* -------------------------- */
            /*            END             */
            /* -------------------------- */

            case -1:
                intake.setPower(0);
                launcher.setPower(0);
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
