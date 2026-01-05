package org.firstinspires.ftc.teamcode.opModes.autonomous;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous
public class AutoFar extends LinearOpMode {
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
    private final Pose startPose = new Pose(85.7, 134, Math.toRadians(90)); // Start position

    // Trajectory Poses
    private final Pose motifPose = new Pose(85.7, 115.5, Math.toRadians(90)); // Motif
    private final Pose launch0Pose = new Pose(85.7, 94.7, Math.toRadians(36)); // Launch_0
    private final Pose prepArtifacts1Pose = new Pose(103, 84, Math.toRadians(0)); // Prep_Artifacts_1
    private final Pose intakeArtifacts1Pose = new Pose(118, 84, Math.toRadians(0)); // Intake_Artifacts_1
    private final Pose launch1Pose = new Pose(85.7, 94.7, Math.toRadians(40)); // Launch_1
    private final Pose prepArtifacts2Pose = new Pose(103, 59.5, Math.toRadians(0)); // Prep_Artifacts_2
    private final Pose intakeArtifacts2Pose = new Pose(118, 59.5, Math.toRadians(0)); // Intake_Artifacts_2
    private final Pose launch2Pose = new Pose(85.7, 94.7, Math.toRadians(40)); // Launch_2
    private final Pose prepArtifacts3Pose = new Pose(103, 35.5, Math.toRadians(0)); // Prep_Artifacts_3
    private final Pose intakeArtifacts3Pose = new Pose(118, 35.5, Math.toRadians(0)); // Intake_Artifacts_3
    private final Pose launch3Pose = new Pose(85.7, 94.7, Math.toRadians(40)); // Launch_3

    private PathChain motifPath, launch0Path, prepArtifacts1Path, intakeArtifacts1Path, launch1Path,
            prepArtifacts2Path, intakeArtifacts2Path, launch2Path, prepArtifacts3Path, intakeArtifacts3Path,
            launch3Path;

    public void buildPaths() {
        motifPath = follower.pathBuilder()
                .addPath(new BezierLine(startPose, motifPose))
                .setConstantHeadingInterpolation(motifPose.getHeading())
                .build();

        launch0Path = follower.pathBuilder()
                .addPath(new BezierLine(motifPose, launch0Pose))
                .setLinearHeadingInterpolation(motifPose.getHeading(), launch0Pose.getHeading())
                .build();

        prepArtifacts1Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        launch0Pose,
                        new Pose(83.348, 85.637), // Control point
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
                        new Pose(107.38, 97.081), // Control point
                        launch1Pose
                ))
                .setLinearHeadingInterpolation(intakeArtifacts1Pose.getHeading(), launch1Pose.getHeading())
                .build();

        prepArtifacts2Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        launch1Pose,
                        new Pose(81.823, 59.698), // Control point
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
                        new Pose(115.391, 93.838), // Control point
                        launch2Pose
                ))
                .setLinearHeadingInterpolation(intakeArtifacts2Pose.getHeading(), launch2Pose.getHeading())
                .build();

        prepArtifacts3Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        launch2Pose,
                        new Pose(82.776, 40.244), // Control point
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
                        new Pose(119.205, 83.73), // Control point
                        launch3Pose
                ))
                .setLinearHeadingInterpolation(intakeArtifacts3Pose.getHeading(), launch3Pose.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {

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

        Debug.INSTANCE.addData("path state", pathState);
        Debug.INSTANCE.addData("x", follower.getPose().getX());
        Debug.INSTANCE.addData("y", follower.getPose().getY());
        Debug.INSTANCE.addData("heading", follower.getPose().getHeading());
        Debug.INSTANCE.update();
    }
}

