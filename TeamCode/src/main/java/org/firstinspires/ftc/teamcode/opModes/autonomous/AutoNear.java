package org.firstinspires.ftc.teamcode.opModes.autonomous;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.constants.Motif;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous
public class AutoNear extends LinearOpMode {
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

    // Start Pose
    private final Pose startPose = new Pose(85, 9, Math.toRadians(90)); // Start position

    // Trajectory Poses
    private final Pose launch0Pose = new Pose(85, 20, Math.toRadians(90)); // Launch_0
    private final Pose prepArtifacts1Pose = new Pose(103, 35, Math.toRadians(0)); // Prep_Artifacts_1
    private final Pose intakeArtifacts1Pose = new Pose(118, 35, Math.toRadians(0)); // Intake_Artifacts_1
    private final Pose launch1Pose = new Pose(85, 20, Math.toRadians(67)); // Launch_1
    private final Pose prepArtifacts2Pose = new Pose(103, 59.5, Math.toRadians(0)); // Prep_Artifacts_2
    private final Pose intakeArtifacts2Pose = new Pose(118, 59.5, Math.toRadians(0)); // Intake_Artifacts_2
    private final Pose launch2Pose = new Pose(85, 20, Math.toRadians(67)); // Launch_2
    private final Pose prepArtifacts3Pose = new Pose(103, 83.5, Math.toRadians(0)); // Prep_Artifacts_3
    private final Pose intakeArtifacts3Pose = new Pose(118, 83.5, Math.toRadians(0)); // Intake_Artifacts_3
    private final Pose launch3Pose = new Pose(85, 20, Math.toRadians(67)); // Launch_3
    private final Pose prepArtifactsHpPose = new Pose(113, 20, Math.toRadians(0)); // Prep_Artifacts_HP
    private final Pose intakeArtifactsHpPose = new Pose(130, 20, Math.toRadians(0)); // Intake_Artifacts_HP
    private final Pose launchHpPose = new Pose(85, 20, Math.toRadians(67)); // Launch_HP

    private PathChain launch0Path, prepArtifacts1Path, intakeArtifacts1Path, launch1Path, prepArtifacts2Path,
            intakeArtifacts2Path, launch2Path, prepArtifacts3Path, intakeArtifacts3Path, launch3Path,
            prepArtifactsHpPath, intakeArtifactsHpPath, launchHpPath;

    private Follower follower;
    private Timer pathTimer;
    private int pathState;

    int nrOfRows = 1;
    Motif activeMotif = null;

    public void buildPaths() {
        launch0Path = follower.pathBuilder()
                .addPath(new BezierLine(startPose, launch0Pose))
                .setLinearHeadingInterpolation(startPose.getHeading(), launch0Pose.getHeading())
                .build();

        prepArtifacts1Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        launch0Pose,
                        new Pose(86.438, 35.525), // Control point
                        prepArtifacts1Pose
                ))
                .setLinearHeadingInterpolation(launch0Pose.getHeading(), prepArtifacts1Pose.getHeading())
                .build();

        intakeArtifacts1Path = follower.pathBuilder()
                .addPath(new BezierLine(prepArtifacts1Pose, intakeArtifacts1Pose))
                .setConstantHeadingInterpolation(intakeArtifacts1Pose.getHeading())
                .build();

        launch1Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        intakeArtifacts1Pose,
                        new Pose(116.535, 13.923), // Control point
                        launch1Pose
                ))
                .setLinearHeadingInterpolation(intakeArtifacts1Pose.getHeading(), launch1Pose.getHeading())
                .build();

        prepArtifacts2Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        launch1Pose,
                        new Pose(88.338, 60.602), // Control point
                        prepArtifacts2Pose
                ))
                .setLinearHeadingInterpolation(launch1Pose.getHeading(), prepArtifacts2Pose.getHeading())
                .build();

        intakeArtifacts2Path = follower.pathBuilder()
                .addPath(new BezierLine(prepArtifacts2Pose, intakeArtifacts2Pose))
                .setConstantHeadingInterpolation(intakeArtifacts2Pose.getHeading())
                .build();

        launch2Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        intakeArtifacts2Pose,
                        new Pose(116.535, 15.258), // Control point
                        launch2Pose
                ))
                .setLinearHeadingInterpolation(intakeArtifacts2Pose.getHeading(), launch2Pose.getHeading())
                .build();

        prepArtifacts3Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        launch2Pose,
                        new Pose(86.438, 74.66), // Control point
                        prepArtifacts3Pose
                ))
                .setLinearHeadingInterpolation(launch2Pose.getHeading(), prepArtifacts3Pose.getHeading())
                .build();

        intakeArtifacts3Path = follower.pathBuilder()
                .addPath(new BezierLine(prepArtifacts3Pose, intakeArtifacts3Pose))
                .setConstantHeadingInterpolation(intakeArtifacts3Pose.getHeading())
                .build();

        launch3Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        intakeArtifacts3Pose,
                        new Pose(112.844, 22.037), // Control point
                        launch3Pose
                ))
                .setLinearHeadingInterpolation(intakeArtifacts3Pose.getHeading(), launch3Pose.getHeading())
                .build();

        prepArtifactsHpPath = follower.pathBuilder()
                .addPath(new BezierLine(launch3Pose, prepArtifactsHpPose))
                .setLinearHeadingInterpolation(launch3Pose.getHeading(), prepArtifactsHpPose.getHeading())
                .build();

        intakeArtifactsHpPath = follower.pathBuilder()
                .addPath(new BezierLine(prepArtifactsHpPose, intakeArtifactsHpPose))
                .setConstantHeadingInterpolation(intakeArtifactsHpPose.getHeading())
                .build();

        launchHpPath = follower.pathBuilder()
                .addPath(new BezierLine(intakeArtifactsHpPose, launchHpPose))
                .setLinearHeadingInterpolation(intakeArtifactsHpPose.getHeading(), launchHpPose.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(launch0Path,true);
                setPathState(1);
                break;

            case 1:
                if(!follower.isBusy()) {
                    follower.followPath(prepArtifacts1Path,true);
                    setPathState(2);
                }
                break;

            case 2:
                if(!follower.isBusy()) {
                    follower.followPath(intakeArtifacts1Path,0.1,true);
                    setPathState(3);
                }
                break;

            case 3:
                if(!follower.isBusy()) {
                    follower.followPath(launch1Path,true);
                    if (nrOfRows > 1) setPathState(4);
                    else setPathState(10);
                }
                break;

            case 4:
                if(!follower.isBusy()) {
                    follower.followPath(prepArtifacts2Path,true);
                    setPathState(5);
                }
                break;

            case 5:
                if(!follower.isBusy()) {
                    follower.followPath(intakeArtifacts2Path,0.1,true);
                    setPathState(6);
                }
                break;

            case 6:
                if(!follower.isBusy()) {
                    follower.followPath(launch2Path,true);
                    if (nrOfRows > 2) setPathState(7);
                    else setPathState(10);
                }
                break;

            case 7:
                if(!follower.isBusy()) {
                    follower.followPath(prepArtifacts3Path,true);
                    setPathState(8);
                }
                break;

            case 8:
                if(!follower.isBusy()) {
                    follower.followPath(intakeArtifacts3Path,0.1,true);
                    setPathState(9);
                }
                break;

            case 9:
                if(!follower.isBusy()) {
                    follower.followPath(launch3Path,true);
                    if (nrOfRows > 3) setPathState(10);
                }
                break;

            case 10:
                if(!follower.isBusy()) {
                    follower.followPath(prepArtifactsHpPath,true);
                    setPathState(11);
                }
                break;

            case 11:
                if(!follower.isBusy()) {
                    follower.followPath(intakeArtifactsHpPath,0.1,true);
                    setPathState(12);
                }
                break;

            case 12:
                if(!follower.isBusy()) {
                    follower.followPath(launchHpPath,true);
                    setPathState(11);
                }
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

        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }
}

