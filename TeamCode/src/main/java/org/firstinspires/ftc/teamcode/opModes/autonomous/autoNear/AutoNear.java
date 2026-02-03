//package org.firstinspires.ftc.teamcode.opModes.autonomous.autoNear;
//
//import com.pedropathing.follower.Follower;
//import com.pedropathing.geometry.BezierCurve;
//import com.pedropathing.geometry.BezierLine;
//import com.pedropathing.geometry.Pose;
//import com.pedropathing.paths.PathChain;
//import com.pedropathing.util.Timer;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//
//import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
//import org.firstinspires.ftc.teamcode.constants.enums.AutoOrderNear;
//import org.firstinspires.ftc.teamcode.helper.control.FlywheelPID;
//import org.firstinspires.ftc.teamcode.helper.general.Debug;
//import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
//import org.firstinspires.ftc.teamcode.subsystems.Intake;
//import org.firstinspires.ftc.teamcode.subsystems.Launcher;
//import org.firstinspires.ftc.teamcode.subsystems.Turret;
//
//public class AutoNear {
//
//    private final LinearOpMode opMode;
//    private final AutoOrderNear[] artifactOrder;
//    private double turretAngle;
//
//    private Follower follower;
//    private Timer pathTimer;
//    private Timer launchTimer; // <--- timer for launch timeout
//    private int pathState;
//
//    // Poses
//    private Pose startPose, launchPose, prepArtifacts1Pose, intakeArtifacts1Pose,
//            prepArtifacts2Pose, intakeArtifacts2Pose, prepArtifacts3Pose, intakeArtifacts3Pose,
//            openGatePose, leavePose;
//
//    private PathChain launch0Path, intakeArtifacts1Path,
//            launch1Path, intakeArtifacts2Path, launch2Path, intakeArtifacts3Path, launch3Path,
//            openGatePath, leavePath;
//
//    private Intake intake;
//    private Turret turret;
//    private Launcher launcher;
//    private FlywheelPID flywheelPID;
//    private Hardware hardware;
//    private Debug debug;
//
//    private int artifactIndex = 0;
//
//    public AutoNear(LinearOpMode opMode, AllianceColor color, AutoOrderNear[] artifactOrder) {
//        this.opMode = opMode;
//        this.artifactOrder = artifactOrder;
//
//        // Define poses
//        startPose = new Pose(119, 127.5, Math.toRadians(36));
//        launchPose = new Pose(102.5, 111, Math.toRadians(36));
//        prepArtifacts1Pose = new Pose(103, 84, Math.toRadians(0));
//        intakeArtifacts1Pose = new Pose(121, 84, Math.toRadians(0));
//        prepArtifacts2Pose = new Pose(103, 59.5, Math.toRadians(0));
//        intakeArtifacts2Pose = new Pose(129, 61, Math.toRadians(0));
//        prepArtifacts3Pose = new Pose(103, 35.5, Math.toRadians(0));
//        intakeArtifacts3Pose = new Pose(121, 35.5, Math.toRadians(0));
//        openGatePose = new Pose(123, 70, Math.toRadians(0));
//        leavePose = new Pose(122, 96, Math.toRadians(270));
//
//        Pose turretAnglePose = new Pose(0, 0, Math.toRadians(30));
//
//        if (color == AllianceColor.RED) {
//            turretAngle = Math.toDegrees(turretAnglePose.getHeading());
//        } else {
//            turretAngle = Math.toDegrees(turretAnglePose.mirror().getHeading());
//            startPose = startPose.mirror();
//            launchPose = launchPose.mirror();
//            prepArtifacts1Pose = prepArtifacts1Pose.mirror();
//            intakeArtifacts1Pose = intakeArtifacts1Pose.mirror();
//            prepArtifacts2Pose = prepArtifacts2Pose.mirror();
//            intakeArtifacts2Pose = intakeArtifacts2Pose.mirror();
//            prepArtifacts3Pose = prepArtifacts3Pose.mirror();
//            intakeArtifacts3Pose = intakeArtifacts3Pose.mirror();
//            openGatePose = openGatePose.mirror();
//            leavePose = leavePose.mirror();
//        }
//    }
//
//    private void setPathState(int pState) {
//        pathState = pState;
//        pathTimer.resetTimer();
//    }
//
//    private PathChain getIntakePath(AutoOrderNear a) {
//        switch (a) {
//            case SPIKE_MARK_1: return intakeArtifacts1Path;
//            case SPIKE_MARK_2: return intakeArtifacts2Path;
//            case SPIKE_MARK_3: return intakeArtifacts3Path;
//        }
//        return null;
//    }
//
//    private PathChain getLaunchPath(AutoOrderNear a) {
//        switch (a) {
//            case SPIKE_MARK_1: return launch1Path;
//            case SPIKE_MARK_2: return launch2Path;
//            case SPIKE_MARK_3: return launch3Path;
//        }
//        return null;
//    }
//
//    public void buildPaths() {
//        // Build all paths exactly like before
//        launch0Path = follower.pathBuilder()
//                .addPath(new BezierLine(startPose, launchPose))
//                .setConstantHeadingInterpolation(launchPose.getHeading())
//                .build();
//
//        intakeArtifacts1Path = follower.pathBuilder()
//                .addPath(new BezierCurve(launchPose, new Pose(60, 90.11).mirror(), prepArtifacts1Pose))
//                .setLinearHeadingInterpolation(launchPose.getHeading(), prepArtifacts1Pose.getHeading())
//                .addPath(new BezierLine(prepArtifacts1Pose, intakeArtifacts1Pose))
//                .setConstantHeadingInterpolation(intakeArtifacts1Pose.getHeading())
//                .build();
//
//        launch1Path = follower.pathBuilder()
//                .addPath(new BezierCurve(intakeArtifacts1Pose, new Pose(50, 98.613).mirror(), launchPose))
//                .setLinearHeadingInterpolation(intakeArtifacts1Pose.getHeading(), launchPose.getHeading())
//                .build();
//
//        intakeArtifacts2Path = follower.pathBuilder()
//
//                .addPath(new BezierCurve(launchPose, new Pose(65, 83.443).mirror(), prepArtifacts2Pose))
//                .setLinearHeadingInterpolation(launchPose.getHeading(), prepArtifacts2Pose.getHeading())
//                .addPath(new BezierLine(prepArtifacts2Pose, intakeArtifacts2Pose))
//                .setConstantHeadingInterpolation(intakeArtifacts2Pose.getHeading())
//                .build();
//
//        launch2Path = follower.pathBuilder()
//                .addPath(new BezierCurve(openGatePose, new Pose(50, 76.446).mirror(), launchPose))
//                .setLinearHeadingInterpolation(openGatePose.getHeading(), launchPose.getHeading())
//                .build();
//
//        intakeArtifacts3Path = follower.pathBuilder()
//
//                .addPath(new BezierCurve(launchPose, new Pose(50, 49.257).mirror(), prepArtifacts3Pose))
//                .setLinearHeadingInterpolation(launchPose.getHeading(), prepArtifacts3Pose.getHeading())
//                .addPath(new BezierLine(prepArtifacts3Pose, intakeArtifacts3Pose))
//                .setConstantHeadingInterpolation(intakeArtifacts3Pose.getHeading())
//                .build();
//
//        launch3Path = follower.pathBuilder()
//                .addPath(new BezierCurve(intakeArtifacts3Pose, new Pose(50, 54.747).mirror(), launchPose))
//                .setLinearHeadingInterpolation(intakeArtifacts3Pose.getHeading(), launchPose.getHeading())
//                .build();
//
//        openGatePath = follower.pathBuilder()
//                .addPath(new BezierCurve(intakeArtifacts2Pose, new Pose(94.84293193717279,74.17408376963353), openGatePose))
//                .setConstantHeadingInterpolation(openGatePose.getHeading())
//                .build();
//
//        leavePath = follower.pathBuilder()
//                .addPath(new BezierLine(launchPose, leavePose))
//                .setLinearHeadingInterpolation(launchPose.getHeading(), leavePose.getHeading())
//                .build();
//    }
//
//    public void initialize() {
//        pathTimer = new Timer();
//        launchTimer = new Timer(); // <--- launch timeout timer
//        follower = Constants.createFollower(opMode.hardwareMap);
//        buildPaths();
//        follower.setStartingPose(startPose);
//
//        hardware = new Hardware();
//        debug = new Debug(opMode.telemetry);
//        turret = new Turret(hardware, debug, opMode);
//        flywheelPID = new FlywheelPID();
//        launcher = new Launcher(opMode, hardware, flywheelPID, turret, debug);
//        intake = new Intake(opMode, hardware);
//
//        turret.closeBarrier();
//
//        // Spin up launcher immediately
//        launcher.update(3200, 37);
//        launcher.startShooting();
//    }
//
//    public void play() {
//        setPathState(0);
//    }
//
//    public void update() {
//        follower.update();
//        launcher.update(3300, 37); // always update launcher FSM
//        if (!launcher.isShooting()) intake.update();
//        turret.update();
//        autonomousPathUpdate();
//
//        debug.addData("path state", pathState);
//        debug.addData("x", follower.getPose().getX());
//        debug.addData("y", follower.getPose().getY());
//        debug.addData("heading", follower.getPose().getHeading());
//        debug.addData("is intaking", intake.isIntakeing());
//        debug.update();
//    }
//
//    public void autonomousPathUpdate() {
//        switch (pathState) {
//            case 0:
//                follower.followPath(launch0Path);
//                setPathState(1);
//                break;
//
//            case 1:
//                if (!follower.isBusy()) setPathState(2);
//                break;
//
//            case 2:
//                if ((!follower.isBusy() && !launcher.isShooting()) || pathTimer.getElapsedTimeSeconds() > 2) {
//                    launcher.stop();
//                    if (artifactIndex >= artifactOrder.length) {
//                        setPathState(7);
//                        return;
//                    }
//                    intake.setIntake(true);
//                    follower.followPath(getIntakePath(artifactOrder[artifactIndex]));
//                    if (artifactOrder[artifactIndex] == AutoOrderNear.SPIKE_MARK_2) setPathState(6);
//                    else setPathState(4);
//                }
//                break;
//
//            case 4:
//                // Drive to launch pose
//                if (!follower.isBusy()) {
//                    intake.setIntake(false);
//                    follower.followPath(getLaunchPath(artifactOrder[artifactIndex]));
//                    setPathState(5);
//                }
//                break;
//
//            case 5:
//                // Wait until we reach launch pose, then shoot
//                if (!follower.isBusy()) {
//                    launcher.startShooting();
//                    launchTimer.resetTimer();
//                    setPathState(55); // NEW state number
//                }
//                break;
//
//            case 55:
//                // Shooting or timeout
//                if (!launcher.isShooting() || launchTimer.getElapsedTimeSeconds() > 3.0) {
//                    launcher.stop();
//                    artifactIndex++;
//                    setPathState(2);
//                }
//                break;
//
//            case 6:
//                if (!follower.isBusy()) {
//                    intake.setIntake(false);
//                    follower.followPath(openGatePath);
//                    if(pathTimer.getElapsedTimeSeconds() > 5) {
//                        setPathState(4); // NEW
//                    }
//                }
//                break;
//
//            case 7:
//                if (!follower.isBusy()) {
//                    follower.followPath(leavePath);
//                    setPathState(99);
//                }
//                break;
//
//            case 99:
//                // DONE
//                break;
//        }
//    }
//}
