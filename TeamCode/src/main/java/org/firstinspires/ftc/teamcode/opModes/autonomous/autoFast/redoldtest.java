package org.firstinspires.ftc.teamcode.opModes.autonomous.autoFast;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware2;
import org.firstinspires.ftc.teamcode.helper.hardware.actuators.MotorHelper2;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LimelightHelper;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Slider;

@Autonomous
public class redoldtest extends LinearOpMode {
    LimelightHelper limelight = new LimelightHelper();

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        limelight.setPipeline(7);
        limelight.start();
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
    private final Pose startPose = new Pose(144-9.651, 164.969, Math.toRadians(270)); // Start position

    // Trajectory Poses
    private final Pose path1Pose = new Pose(144-10.103, 214.905, Math.toRadians(0)); // Path 1
    private final Pose path2Pose = new Pose(144-37.163, 214.149, Math.toRadians(0)); // Path 2
    private final Pose path3Pose = new Pose(144-51, 18, Math.toRadians(270)); // Path 3
    private final Pose path4Pose = new Pose(144-10.921, 142.566, Math.toRadians(90)); // Path 4
    private final Pose path5Pose = new Pose(144-10.921, 130.566, Math.toRadians(90));
    private final Pose path6Pose = new Pose(144-10.921, 130.566, Math.toRadians(280));
    private PathChain path1Path, path2Path, path3Path, path4Path, path5Path, path6Path, park1Path,park2Path,park3Path;

    public int april=-1;

    public void buildPaths() {
        path1Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        startPose,
                        new Pose(144-8.114, 191.808), // Control point
                        path1Pose
                ))
                .setLinearHeadingInterpolation(startPose.getHeading(), path1Pose.getHeading())
                .build();

        path2Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        path1Pose,
                        new Pose(144-24.312, 219.95), // Control point
                        path2Pose
                ))
                .setLinearHeadingInterpolation(path1Pose.getHeading(), path2Pose.getHeading())
                .build();

        path3Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        path2Pose,
                        new Pose(144-55.16, 128.219), // Control point
                        path3Pose
                ))
                .setLinearHeadingInterpolation(path2Pose.getHeading(), path3Pose.getHeading())
                .build();

        path4Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        path3Pose,
                        new Pose(144-11.346, 75.888), // Control point
                        path4Pose
                ))
                .setLinearHeadingInterpolation(path3Pose.getHeading(), path4Pose.getHeading())
                .build();
        park1Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        path6Pose,
                        new Pose(144-44.147, 150.961), // Control point
                        park1Pose
                ))
                .setLinearHeadingInterpolation(path4Pose.getHeading(), park1Pose.getHeading())
                .build();
        park2Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        path6Pose,
                        new Pose(144-44.147, 150.961), // Control point
                        park2Pose
                ))
                .setLinearHeadingInterpolation(path4Pose.getHeading(), park2Pose.getHeading())
                .build();
        park3Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        path6Pose,
                        new Pose(144-44.147, 150.961), // Control point
                        park3Pose
                ))
                .setLinearHeadingInterpolation(path4Pose.getHeading(), park3Pose.getHeading())
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
    private final boolean Back = false;
    private final Pose park1Pose = Back? new Pose(144-56.801, 57.599, Math.toRadians(90)):new Pose(144-56.801, 47.32*1.666, Math.toRadians(90)); // Path 4
    private final Pose park2Pose = Back? new Pose(144-56.801, 64.24*1.6666, Math.toRadians(90)):new Pose(144-56.801, 77.97*1.666, Math.toRadians(90)); // Path 4
    private final Pose park3Pose = Back? new Pose(144-56.801, 91.95*1.6666, Math.toRadians(90)):new Pose(56.801, 106.91*1.666, Math.toRadians(90)); // Path 4

    private Hardware2 hardware = new Hardware2();
    private Debug debug;
    private Claw claw = new Claw(hardware);
    private Slider slider = new Slider(hardware);
    private MotorHelper2 motors = new MotorHelper2();

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(path1Path);
                setPathState(1);
                motors.Intake().setPower(1);
                break;

            case 1:
                if (!follower.isBusy()) {
                    follower.followPath(path2Path);
                    setPathState(2);
                }
                break;

            case 2:
                if (!follower.isBusy()) {
                    follower.followPath(path3Path);
                    motors.Intake().setPower(0);
                    slider.goToStep(1);
                    claw.open();
                    setPathState(3);
                }
                break;

            case 3:
                if (!follower.isBusy()) {
                    sleep(1500);
                    while (slider.isBusy()) sleep(20);
                    claw.close();
                    motors.Feeder().setPower(0.5);
                    motors.Intake().setPower(1);
                    sleep(4000);
                    motors.Feeder().setPower(0);
                    slider.goToStep(2);
                    while (slider.isBusy()) sleep(20);
                    follower.followPath(path4Path);
                    setPathState(4);
                }
                break;

            case 4:
                if (!follower.isBusy()) {
                    slider.goToStep(1);
                    while (slider.isBusy()) sleep(20);
                    claw.open();
                    sleep(1800);
                    follower.followPath(path5Path);
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) {
                    setPathState(6);
                }
                break;
            case 6:
                if (!follower.isBusy()&&limelight.ID()!=-1) {
                    PathChain park = limelight.ID()==21?park1Path:(limelight.ID()==22?park2Path:park3Path);
                    follower.followPath(park);
                    setPathState(7);
                }
                break;
            case 7:

                break;
        }
    }

    private void initialize() {
        pathTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);
    }

    private void play() {
        setPathState(0);
    }

    private void update() {
        follower.update();
        autonomousPathUpdate();
        limelight.update();

        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }
}