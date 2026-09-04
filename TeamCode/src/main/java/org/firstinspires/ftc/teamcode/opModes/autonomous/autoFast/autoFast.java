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
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LimelightHelper;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.subsystems.Slider;

@Autonomous
public class autoFast extends LinearOpMode {
    public int aprilId = -1;
    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        LimelightHelper limelight = new LimelightHelper();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            update();
            limelight.update();
        }
    }
    private void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    private Follower follower;
    private Timer pathTimer;
    private int pathState;

    // Start Pose
    private final Pose startPose = new Pose(8.425, 170.215, Math.toRadians(90)); // Start position

    // Trajectory Poses
    private final Pose path1Pose = new Pose(23.629, 218.497, Math.toRadians(0)); // Path 1
    private final Pose path2Pose = new Pose(63.419, 218.716, Math.toRadians(0)); // Path 2
    private final Pose path3Pose = new Pose(13.141, 149.14, Math.toRadians(0)); // Path 3
    private final Pose path4Pose = new Pose(13.141, 149.14, Math.toRadians(290)); // Path 3
    private final boolean Back = false;
    private final Pose park1Pose = Back? new Pose(56.801, 57.599, Math.toRadians(90)):new Pose(56.801, 47.32*1.666, Math.toRadians(90)); // Path 4
    private final Pose park2Pose = Back? new Pose(56.801, 64.24*1.6666, Math.toRadians(90)):new Pose(56.801, 77.97*1.666, Math.toRadians(90)); // Path 4
    private final Pose park3Pose = Back? new Pose(56.801, 91.95*1.6666, Math.toRadians(90)):new Pose(56.801, 106.91*1.666, Math.toRadians(90)); // Path 4

    private Hardware2 hardware;
    private Debug debug;
    private Drive drive;
    private Claw claw;
    private Slider slider;
    private PathChain path1Path, path2Path, path3Path, path4Path, park1Path, park2Path, park3Path;

    public void buildPaths() {
        path1Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        startPose,
                        new Pose(6.283, 206.345), // Control point
                        path1Pose
                ))
                .setLinearHeadingInterpolation(startPose.getHeading(), path1Pose.getHeading())
                .build();

        path2Path = follower.pathBuilder()
                .addPath(new BezierLine(path1Pose, path2Pose))
                .setLinearHeadingInterpolation(path1Pose.getHeading(), path2Pose.getHeading())
                .build();

        path3Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        path2Pose,
                        new Pose(44.147, 150.961), // Control point
                        path3Pose
                ))
                .setLinearHeadingInterpolation(path2Pose.getHeading(), path3Pose.getHeading())
                .build();
        park1Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        path4Pose,
                        new Pose(44.147, 150.961), // Control point
                        park1Pose
                ))
                .setLinearHeadingInterpolation(path4Pose.getHeading(), park1Pose.getHeading())
                .build();
        park2Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        path4Pose,
                        new Pose(44.147, 150.961), // Control point
                        park2Pose
                ))
                .setLinearHeadingInterpolation(path4Pose.getHeading(), park2Pose.getHeading())
                .build();
        park3Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        path4Pose,
                        new Pose(44.147, 150.961), // Control point
                        park3Pose
                ))
                .setLinearHeadingInterpolation(path4Pose.getHeading(), park3Pose.getHeading())
                .build();

        path4Path = follower.pathBuilder()
                .addPath(new BezierLine(path3Pose, path4Pose))
                .setLinearHeadingInterpolation(path3Pose.getHeading(), path4Pose.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(path1Path);
                setPathState(1);
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
                    setPathState(3);
                }
                break;

            case 3:
                if (!follower.isBusy()) {

                    follower.followPath(path4Path);
                    setPathState(4);
                }
                break;

            case 4:
                if (!follower.isBusy()&&aprilId!=-1) {
                    PathChain parkPath = aprilId==34?park1Path:(aprilId==35?park2Path:park3Path);
                    follower.followPath(parkPath);
                    setPathState(5);
                }
                break;
            case 5:
                break;
        }
    }

    private void initialize() {
        pathTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);

        hardware = new Hardware2();
        debug = new Debug(telemetry);
        claw = new Claw(hardware);
        slider = new Slider(hardware);
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
}
