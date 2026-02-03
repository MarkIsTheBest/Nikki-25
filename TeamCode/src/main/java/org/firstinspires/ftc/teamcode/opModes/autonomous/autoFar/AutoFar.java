//package org.firstinspires.ftc.teamcode.opModes.autonomous.autoFar;
//import com.pedropathing.follower.Follower;
//import com.pedropathing.geometry.BezierCurve;
//import com.pedropathing.geometry.BezierLine;
//import com.pedropathing.geometry.Pose;
//import com.pedropathing.paths.PathChain;
//import com.pedropathing.util.Timer;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//
//import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
//import org.firstinspires.ftc.teamcode.constants.enums.AutoOrderFar;
//import org.firstinspires.ftc.teamcode.helper.control.FlywheelPID;
//import org.firstinspires.ftc.teamcode.helper.general.Debug;
//import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
//import org.firstinspires.ftc.teamcode.subsystems.Intake;
//import org.firstinspires.ftc.teamcode.subsystems.Launcher;
//import org.firstinspires.ftc.teamcode.subsystems.Turret;
//
//public class AutoFar {
//
//    public AutoFar(LinearOpMode opMode, AllianceColor color, AutoOrderFar[] artifactOrder) {
//        this.opMode = opMode;
//        this.artifactOrder = artifactOrder;
//
//        startPose = new Pose(85, 9, Math.toRadians(90));
//        launchPose = new Pose(85, 20, Math.toRadians(90));
//        prepArtifacts1Pose = new Pose(103, 35, Math.toRadians(0));
//        intakeArtifacts1Pose = new Pose(122, 35, Math.toRadians(0));
//        prepArtifacts2Pose = new Pose(103, 59.5, Math.toRadians(0));
//        intakeArtifacts2Pose = new Pose(122, 59.5, Math.toRadians(0));
//        prepArtifacts3Pose = new Pose(103, 83.5, Math.toRadians(0));
//        intakeArtifacts3Pose = new Pose(122, 83.5, Math.toRadians(0));
//        prepArtifactsHpPose = new Pose(120, 8, Math.toRadians(0));
//        intakeArtifactsHpPose = new Pose(135, 8, Math.toRadians(0));
//        leavePose = new Pose(107, 20, Math.toRadians(90));
//
//        Pose turretAnglePose = new Pose(0,0, Math.toRadians(68.79718135635073));
//
//        switch (color) {
//            case RED:
//                turretAngle = Math.toDegrees(turretAnglePose.getHeading());
//                break;
//
//            case BLUE:
//                turretAngle = Math.toDegrees(turretAnglePose.mirror().getHeading());
//                startPose = startPose.mirror();
//                launchPose = launchPose.mirror();
//                prepArtifacts1Pose = prepArtifacts1Pose.mirror();
//                intakeArtifacts1Pose = intakeArtifacts1Pose.mirror();
//                prepArtifacts2Pose = prepArtifacts2Pose.mirror();
//                intakeArtifacts2Pose = intakeArtifacts2Pose.mirror();
//                prepArtifacts3Pose = prepArtifacts3Pose.mirror();
//                intakeArtifacts3Pose = intakeArtifacts3Pose.mirror();
//                prepArtifactsHpPose = prepArtifactsHpPose.mirror();
//                intakeArtifactsHpPose = intakeArtifactsHpPose.mirror();
//                leavePose = leavePose.mirror();
//                break;
//        }
//    }
//
//    private final LinearOpMode opMode;
//    private final AutoOrderFar[] artifactOrder;
//    private double turretAngle;
//
//    private void setPathState(int pState) {
//        pathState = pState;
//        pathTimer.resetTimer();
//    }
//
//    private Follower follower;
//    private Timer pathTimer;
//    private int pathState;
//
//    // Start Pose
//    private Pose startPose; // Start position
//
//    // Trajectory Poses
//    private Pose launchPose;
//    private Pose prepArtifacts1Pose; // Prep_Artifacts_1
//    private Pose intakeArtifacts1Pose; // Intake_Artifacts_1
//    private Pose prepArtifacts2Pose; // Prep_Artifacts_2
//    private Pose intakeArtifacts2Pose; // Intake_Artifacts_2
//    private Pose prepArtifacts3Pose; // Prep_Artifacts_3
//    private Pose intakeArtifacts3Pose; // Intake_Artifacts_3
//    private Pose prepArtifactsHpPose; // Prep_Artifacts_HP
//    private Pose intakeArtifactsHpPose; // Intake_Artifacts_HP
//    private Pose leavePose; // Leave
//
//    private PathChain launch0Path, prepArtifacts1Path, intakeArtifacts1Path,
//            launch1Path, prepArtifacts2Path, intakeArtifacts2Path, launch2Path,
//            prepArtifacts3Path, intakeArtifacts3Path, launch3Path, prepArtifactsHpPath,
//            intakeArtifactsHpPath, launchHpPath, leavePath;
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
//    private PathChain getPrepPath(AutoOrderFar a) {
//        switch (a) {
//            case SPIKE_MARK_1: return prepArtifacts1Path;
//            case SPIKE_MARK_2: return prepArtifacts2Path;
//            case SPIKE_MARK_3: return prepArtifacts3Path;
//            case SPIKE_MARK_HUMAN_PLAYER: return prepArtifactsHpPath;
//        }
//        return null;
//    }
//
//    private PathChain getIntakePath(AutoOrderFar a) {
//        switch (a) {
//            case SPIKE_MARK_1: return intakeArtifacts1Path;
//            case SPIKE_MARK_2: return intakeArtifacts2Path;
//            case SPIKE_MARK_3: return intakeArtifacts3Path;
//            case SPIKE_MARK_HUMAN_PLAYER: return intakeArtifactsHpPath;
//        }
//        return null;
//    }
//
//    private PathChain getLaunchPath(AutoOrderFar a) {
//        switch (a) {
//            case SPIKE_MARK_1: return launch1Path;
//            case SPIKE_MARK_2: return launch2Path;
//            case SPIKE_MARK_3: return launch3Path;
//            case SPIKE_MARK_HUMAN_PLAYER: return launchHpPath;
//        }
//        return null;
//    }
//
//    public void buildPaths() {
//        launch0Path = follower.pathBuilder()
//                .addPath(new BezierLine(startPose, launchPose))
//                .setConstantHeadingInterpolation(launchPose.getHeading())
//                .build();
//
//        prepArtifacts1Path = follower.pathBuilder()
//                .addPath(new BezierCurve(
//                        launchPose,
//                        new Pose(84.25, 38.75), // Control point
//                        prepArtifacts1Pose
//                ))
//                .setLinearHeadingInterpolation(launchPose.getHeading(), prepArtifacts1Pose.getHeading())
//                .addPath(new BezierLine(prepArtifacts1Pose, intakeArtifacts1Pose))
//                .setConstantHeadingInterpolation(intakeArtifacts1Pose.getHeading())
//                .build();
//
//        launch1Path = follower.pathBuilder()
//                .addPath(new BezierCurve(
//                        intakeArtifacts1Pose,
//                        new Pose(102.845, 38.034), // Control point
//                        launchPose
//                ))
//                .setLinearHeadingInterpolation(intakeArtifacts1Pose.getHeading(), launchPose.getHeading())
//                .build();
//
//        prepArtifacts2Path = follower.pathBuilder()
//                .addPath(new BezierCurve(
//                        launchPose,
//                        new Pose(106.365, 39.802), // Control point
//                        prepArtifacts2Pose
//                ))
//                .setLinearHeadingInterpolation(launchPose.getHeading(), prepArtifacts2Pose.getHeading())
//
//                .addPath(new BezierLine(prepArtifacts2Pose, intakeArtifacts2Pose))
//                .setConstantHeadingInterpolation(intakeArtifacts2Pose.getHeading())
//                .build();
//
//
//        launch2Path = follower.pathBuilder()
//                .addPath(new BezierCurve(
//                        intakeArtifacts2Pose,
//                        new Pose(103.199, 36.367), // Control point
//                        launchPose
//                ))
//                .setLinearHeadingInterpolation(intakeArtifacts2Pose.getHeading(), launchPose.getHeading())
//                .build();
//
//        prepArtifacts3Path = follower.pathBuilder()
//                .addPath(new BezierCurve(
//                        launchPose,
//                        new Pose(89.459, 71.174), // Control point
//                        prepArtifacts3Pose
//                ))
//                .setLinearHeadingInterpolation(launchPose.getHeading(), prepArtifacts3Pose.getHeading())
//                .addPath(new BezierLine(prepArtifacts3Pose, intakeArtifacts3Pose))
//                .setConstantHeadingInterpolation(intakeArtifacts3Pose.getHeading())
//                .build();
//
//        launch3Path = follower.pathBuilder()
//                .addPath(new BezierCurve(
//                        intakeArtifacts3Pose,
//                        new Pose(93.18, 38.349), // Control point
//                        launchPose
//                ))
//                .setLinearHeadingInterpolation(intakeArtifacts3Pose.getHeading(), launchPose.getHeading())
//                .build();
//
//        prepArtifactsHpPath = follower.pathBuilder()
//                .addPath(new BezierCurve(
//                        launchPose,
//                        new Pose(92.353, 10.607), // Control point
//                        prepArtifactsHpPose
//                ))
//                .setLinearHeadingInterpolation(launchPose.getHeading(), prepArtifactsHpPose.getHeading())
//                .build();
//
//        intakeArtifactsHpPath = follower.pathBuilder()
//                .addPath(new BezierLine(prepArtifactsHpPose, intakeArtifactsHpPose))
//                .setConstantHeadingInterpolation(intakeArtifactsHpPose.getHeading())
//                .build();
//
//        launchHpPath = follower.pathBuilder()
//                .addPath(new BezierCurve(
//                        intakeArtifactsHpPose,
//                        new Pose(100.578, 25.552), // Control point
//                        launchPose
//                ))
//                .setLinearHeadingInterpolation(intakeArtifactsHpPose.getHeading(), launchPose.getHeading())
//                .build();
//
//        leavePath = follower.pathBuilder()
//                .addPath(new BezierLine(
//                        launchPose,
//                        leavePose
//                ))
//                .setConstantHeadingInterpolation(leavePose.getHeading())
//                .build();
//    }
//
//    public void autonomousPathUpdate() {
//        switch (pathState) {
//
//            case 0:
//                follower.followPath(launch0Path);
//                setPathState(1);
//                break;
//
//            case 1:
//                if (!follower.isBusy()) {
//                    //launcher.toggleShooting();
//                    setPathState(2);
//                }
//                break;
//
//            case 2:
//                if (!follower.isBusy() && !launcher.isShooting()) {
//                    if (artifactIndex >= artifactOrder.length) {
//                        setPathState(6);
//                        return;
//                    }
//                    intake.toggleIntake();
//                    follower.followPath(getPrepPath(artifactOrder[artifactIndex]));
//                    setPathState(3);
//                }
//                break;
//
//            case 3:
//                if (!follower.isBusy()) {
//                    follower.followPath(getIntakePath(artifactOrder[artifactIndex]));
//                    setPathState(4);
//                }
//                break;
//
//            case 4:
//                if (!follower.isBusy()) {
//                    intake.toggleIntake();
//                    follower.followPath(getLaunchPath(artifactOrder[artifactIndex]));
//                    setPathState(5);
//                }
//                break;
//
//            case 5:
//                if (!follower.isBusy()) {
//                    //launcher.toggleShooting();
//                    artifactIndex++;
//                    setPathState(2);
//                }
//                break;
//
//            case 6:
//                if(!follower.isBusy() && !launcher.isShooting()) {
//                    follower.followPath(leavePath);
//                    setPathState(99);
//                }
//                break;
//
//            case 99:
//                //DONE
//                break;
//        }
//    }
//
//    public void initialize() {
//        pathTimer = new Timer();
//        follower = Constants.createFollower(opMode.hardwareMap);
//        buildPaths();
//        follower.setStartingPose(startPose);
//
//        debug = new Debug(opMode.telemetry);
//        turret = new Turret(hardware, debug, opMode);
//        flywheelPID = new FlywheelPID();
//        launcher = new Launcher(opMode, hardware, flywheelPID, turret, debug);
//        intake = new Intake(opMode, hardware);
//
//        turret.setHoodAngle(45);
//        turret.closeBarrier();
//        turret.setAngle(-turretAngle);
//    }
//
//    public void play() {
//        setPathState(0);
//    }
//
//    public void update() {
//        follower.update();
//        launcher.update(4800, 45);
//        intake.update();
//        turret.update();
//
//        autonomousPathUpdate();
//
//        debug.addData("path state", pathState);
//        debug.addData("x", follower.getPose().getX());
//        debug.addData("y", follower.getPose().getY());
//        debug.addData("heading", follower.getPose().getHeading());
//        debug.addData("isShooting", launcher.isShooting());
//        debug.update();
//    }
//}
//
