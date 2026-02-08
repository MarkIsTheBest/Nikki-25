package org.firstinspires.ftc.teamcode.opModes.autonomous.autoFar;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
import org.firstinspires.ftc.teamcode.constants.enums.AutoFarState;
import org.firstinspires.ftc.teamcode.constants.enums.AutoNearState;
import org.firstinspires.ftc.teamcode.constants.enums.AutoOrderFar;
import org.firstinspires.ftc.teamcode.constants.enums.AutoOrderNear;
import org.firstinspires.ftc.teamcode.helper.Drawing;
import org.firstinspires.ftc.teamcode.helper.control.CustomFlywheelPID;
import org.firstinspires.ftc.teamcode.helper.control.FlywheelPID;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;
import org.firstinspires.ftc.teamcode.subsystems.Turret;

public class AutoFar2 {

    private final LinearOpMode opMode;
    private final AutoOrderFar[] artifactOrder;
    private double turretAngle;
    private int limelightPipeline;
    private Pose goalPosition;
    private AllianceColor currentAllianceColor;
    private double delayStart;
    private boolean onlyLeave;

    public AutoFar2(LinearOpMode opMode, AllianceColor color, AutoOrderFar[] artifactOrder, double delayStart, boolean onlyLeave) {
        this.opMode = opMode;
        this.artifactOrder = artifactOrder;
        currentAllianceColor = color;
        this.delayStart = delayStart;
        this.onlyLeave = onlyLeave;

        startPose = new Pose(87, 7.7, Math.toRadians(0)); // Start position
        launchPose = new Pose(87, 15, Math.toRadians(45)); // Launch

        prepIntake1Pose = new Pose(99, 35, Math.toRadians(0)); // Prep_Intake_1
        intake1Pose = new Pose(130, 35, Math.toRadians(0)); // Intake_1
        prepIntakeHpPose = new Pose(16, 27, Math.toRadians(-122.8)).mirror(); // Intake_HP
        intakeHpPose = new Pose(13, 12, Math.toRadians(-105)).mirror(); // Intake_HP
        prepIntake2Pose = new Pose(99, 59.9, Math.toRadians(0)); // Prep_Intake_2
        intake2Pose = new Pose(132, 59.9, Math.toRadians(0)); // Intake_2
        prepIntake3Pose = new Pose(99, 83.5, Math.toRadians(0)); // Prep_Intake_3
        intake3Pose = new Pose(126, 83.5, Math.toRadians(0)); // Intake_3
        leavePose = new Pose(108, 15, Math.toRadians(90)); // Leave

        prepIntake1Cp0 = new Pose(87.5, 35.75); // Control point 1 for Prep_Intake_1
        launch1Cp0 = new Pose(104.8, 33.5); // Control point 1 for Launch_1
        intakeHpCp0 = new Pose(89, 9); // Control point 1 for Intake_HP
        launchHpCp0 = new Pose(89, 9); // Control point 1 for Launch_HP
        prepIntake2Cp0 = new Pose(93.222, 57.03); // Control point 1 for Prep_Intake_2
        launch2Cp0 = new Pose(88.5, 57.5); // Control point 1 for Launch_2
        prepIntake3Cp0 = new Pose(90.003, 83.697); // Control point 1 for Prep_Intake_3
        launch3Cp0 = new Pose(90, 77); // Control point 1 for Launch_3
        leaveCp0 = new Pose(103, 21); // Control point 1 for Leave

        switch (color) {
            case RED:
                turretAngle = 76 - 45;
                limelightPipeline = 0;
                goalPosition = Positions.Field.RED_GOAL;
                break;

            case BLUE:
                turretAngle = -76 + 45;
                limelightPipeline = 3;
                goalPosition = Positions.Field.BLUE_GOAL;

                startPose = startPose.mirror();
                launchPose = launchPose.mirror();
                prepIntake1Pose = prepIntake1Pose.mirror();
                intake1Pose = intake1Pose.mirror();
                prepIntakeHpPose = prepIntakeHpPose.mirror();
                intakeHpPose = intakeHpPose.mirror();
                prepIntake2Pose = prepIntake2Pose.mirror();
                intake2Pose = intake2Pose.mirror();
                prepIntake3Pose = prepIntake3Pose.mirror();
                intake3Pose = intake3Pose.mirror();
                leavePose = leavePose.mirror();

                prepIntake1Cp0 = prepIntake1Cp0.mirror();
                launch1Cp0 = launch1Cp0.mirror();
                intakeHpCp0 = intakeHpCp0.mirror();
                launchHpCp0 = launchHpCp0.mirror();
                prepIntake2Cp0 = prepIntake2Cp0.mirror();
                launch2Cp0 = launch2Cp0.mirror();
                prepIntake3Cp0 = prepIntake3Cp0.mirror();
                launch3Cp0 = launch3Cp0.mirror();
                leaveCp0 = leaveCp0.mirror();
                break;
        }
    }

    private Follower follower;
    private Timer pathTimer;
    private AutoFarState pathState;

    private Pose startPose;

    private Pose launchPose;

    private Pose prepIntake1Pose;
    private Pose intake1Pose;
    private Pose prepIntakeHpPose;
    private Pose intakeHpPose;
    private Pose prepIntake2Pose;
    private Pose intake2Pose;
    private Pose prepIntake3Pose;
    private Pose intake3Pose;
    private Pose leavePose;

    // Control Poses
    private Pose prepIntake1Cp0;
    private Pose launch1Cp0;
    private Pose intakeHpCp0;
    private Pose launchHpCp0;
    private Pose prepIntake2Cp0;
    private Pose launch2Cp0;
    private Pose prepIntake3Cp0;
    private Pose launch3Cp0;
    private Pose leaveCp0;

    private PathChain launchPreloadPath, intake1Path, launch1Path, intakeHpPath, launchHpPath,
            intake2Path, launch2Path, intake3Path, launch3Path, leavePath;

    private Intake intake;
    private Turret turret;
    private Launcher launcher;
    private CustomFlywheelPID flywheelPID;
    private Hardware hardware;
    private Debug debug;

    private boolean cachedHasTag = false;
    private double cachedTx = 0;
    private boolean hasFinished = false;

    private int artifactIndex = 0;

    private PathChain getIntakePath(AutoOrderFar a) {
        return switch (a) {
            case SPIKE_MARK_1 -> intake1Path;
            case SPIKE_MARK_2 -> intake2Path;
            case SPIKE_MARK_3 -> intake3Path;
            case SPIKE_MARK_HUMAN_PLAYER -> intakeHpPath;
        };
    }

    private PathChain getLaunchPath(AutoOrderFar a) {
        return switch (a) {
            case SPIKE_MARK_1 -> launch1Path;
            case SPIKE_MARK_2 -> launch2Path;
            case SPIKE_MARK_3 -> launch3Path;
            case SPIKE_MARK_HUMAN_PLAYER -> launchHpPath;
        };
    }

    private void setPathState(AutoFarState pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    public void buildPaths() {
        launchPreloadPath = follower.pathBuilder()
                .addPath(new BezierLine(startPose, launchPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), launchPose.getHeading())
                .setHeadingConstraint(0.1)
                .build();

        intake1Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        launchPose,
                        prepIntake1Cp0,
                        prepIntake1Pose
                ))
                .setLinearHeadingInterpolation(launchPose.getHeading() ,prepIntake1Pose.getHeading())
                .addPath(new BezierLine(prepIntake1Pose, intake1Pose))
                .setConstantHeadingInterpolation(intake1Pose.getHeading())
                .build();

        launch1Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        intake1Pose,
                        launch1Cp0,
                        launchPose
                ))
                .setLinearHeadingInterpolation(intake1Pose.getHeading(), launchPose.getHeading())
                .setHeadingConstraint(0.1)
                .setTranslationalConstraint(0.1)
                .build();

        intakeHpPath = follower.pathBuilder()
                .addPath(new BezierCurve(
                        launchPose,
                        intakeHpCp0,
                        prepIntakeHpPose
                ))
                .setLinearHeadingInterpolation(launchPose.getHeading(), prepIntakeHpPose.getHeading())
                .addPath(new BezierLine(
                        prepIntakeHpPose,
                        intakeHpPose
                ))
                .setLinearHeadingInterpolation(prepIntakeHpPose.getHeading(), intakeHpPose.getHeading())
                .build();

        launchHpPath = follower.pathBuilder()
                .addPath(new BezierCurve(
                        intakeHpPose,
                        launchHpCp0,
                        launchPose
                ))
                .setLinearHeadingInterpolation(intakeHpPose.getHeading(), launchPose.getHeading())
                .setHeadingConstraint(0.1)
                .setTranslationalConstraint(0.1)
                .build();

        intake2Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        launchPose,
                        prepIntake2Cp0,
                        prepIntake2Pose
                ))
                .setLinearHeadingInterpolation(launchPose.getHeading() ,prepIntake2Pose.getHeading())
                .addPath(new BezierLine(prepIntake2Pose, intake2Pose))
                .setConstantHeadingInterpolation(intake2Pose.getHeading())
                .build();

        launch2Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        intake2Pose,
                        launch2Cp0,
                        launchPose
                ))
                .setLinearHeadingInterpolation(intake2Pose.getHeading() ,launchPose.getHeading())
                .setHeadingConstraint(0.1)
                .setTranslationalConstraint(0.1)
                .build();

        intake3Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        launchPose,
                        prepIntake3Cp0,
                        prepIntake3Pose
                ))
                .setLinearHeadingInterpolation(launchPose.getHeading() ,prepIntake3Pose.getHeading())
                .addPath(new BezierLine(prepIntake3Pose, intake3Pose))
                .setConstantHeadingInterpolation(intake3Pose.getHeading())
                .build();

        launch3Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        intake3Pose,
                        launch3Cp0,
                        launchPose
                ))
                .setLinearHeadingInterpolation(intake3Pose.getHeading() ,launchPose.getHeading())
                .setHeadingConstraint(0.1)
                .setTranslationalConstraint(0.1)
                .build();

        leavePath = follower.pathBuilder()
                .addPath(new BezierCurve(
                        launchPose,
                        leaveCp0,
                        leavePose
                ))
                .setLinearHeadingInterpolation(launchPose.getHeading(), leavePose.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case INITIALIZE:
                if( pathTimer.getElapsedTime() > delayStart) {

                    if(!onlyLeave) {
                        launcher.spinUp();
                    }
                    follower.followPath(launchPreloadPath, true);
                    setPathState(AutoFarState.LAUNCH_PRELOAD);

                }

                break;

            case LAUNCH_PRELOAD:
                if(!onlyLeave) {
                    if (!follower.isBusy()) {
                        launcher.shoot();
                        setPathState(AutoFarState.INTAKE);
                    }
                } else {
                    if(!follower.isBusy()) {
                        follower.followPath(leavePath, true);
                        hasFinished = true;
                        setPathState(AutoFarState.DONE);
                    }
                }
                break;

            case INTAKE:
                if(!launcher.isShooting() && pathTimer.getElapsedTime() > 250) {
                    intake.setIntake(true);
                    follower.followPath(getIntakePath(artifactOrder[artifactIndex]),artifactOrder[artifactIndex] == AutoOrderFar.SPIKE_MARK_HUMAN_PLAYER ? 0.75 : 1, true);

                    if(artifactOrder[artifactIndex] != AutoOrderFar.SPIKE_MARK_HUMAN_PLAYER) {
                        setPathState(AutoFarState.DRIVE_TO_LAUNCH);
                    }
                    else if (artifactOrder[artifactIndex] == AutoOrderFar.SPIKE_MARK_HUMAN_PLAYER) {
                        setPathState(AutoFarState.HUMAN_PLAYER_DELAY);
                    }
                }
                break;

            case HUMAN_PLAYER_DELAY:
                if(follower.isBusy()) pathTimer.resetTimer();
                if(pathTimer.getElapsedTime() > 750) {
                    setPathState(AutoFarState.DRIVE_TO_LAUNCH);
                }
                break;

            case DRIVE_TO_LAUNCH:
                if(!follower.isBusy() && pathTimer.getElapsedTime() > 350) {
                    intake.setIntake(false);
                    launcher.spinUp();
                    follower.followPath(getLaunchPath(artifactOrder[artifactIndex]),true);
                    setPathState(AutoFarState.SHOOT);
                }
                break;

            case SHOOT:
                if(!follower.isBusy()) {
                    launcher.shoot();
                    artifactIndex++;

                    if (artifactIndex >= artifactOrder.length) {
                        setPathState(AutoFarState.LEAVE);
                        return;
                    }

                    setPathState(AutoFarState.INTAKE);
                }
                break;

            case LEAVE:
                if(!follower.isBusy() && !launcher.isShooting()) {
                    follower.followPath(leavePath, true);
                    setPathState(AutoFarState.DONE);
                    hasFinished = true;
                }
                break;

            case DONE:
                break;
                //DONE
        }
    }

    public void initialize() {
        pathTimer = new Timer();
        follower = Constants.createFollower(opMode.hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);

        hardware = new Hardware();
        debug = new Debug(opMode.telemetry);
        turret = new Turret(hardware, debug, opMode);
        flywheelPID = new CustomFlywheelPID();
        launcher = new Launcher(opMode, hardware, flywheelPID, turret, debug);
        intake = new Intake(opMode, hardware);

        turret.closeBarrier();
        turret.setAngle(turretAngle);

        launcher.setTargetAngle(46);
        launcher.setTargetRPM(4300);

        hardware.Limelight().init();
        hardware.Limelight().setPipeline(limelightPipeline);

        //Drawing.init();
    }

    public void play() {
        setPathState(AutoFarState.INITIALIZE);
        hardware.Limelight().start();
    }

    public void update() {
        follower.update();
        if(!hasFinished) {
            turret.updateVisionOnly(cachedHasTag, cachedTx + (currentAllianceColor == AllianceColor.BLUE ? -1 : 1));
        } else {
            turret.setAngle(0);
        }
        turret.update(true);
        intake.update();
        launcher.update();
        autonomousPathUpdate();

        hardware.Limelight().update();
        cachedHasTag = hardware.Limelight().HasAprilTag();
        cachedTx = hardware.Limelight().Tx();

        //Drawing.drawDebug(follower);

        debug.addData("path state", pathState);
        debug.addData("x", follower.getPose().getX());
        debug.addData("y", follower.getPose().getY());
        debug.addData("heading", follower.getPose().getHeading());
        debug.addData("isShooting", launcher.isShooting());
        debug.addData("artifact Index", artifactIndex);
        debug.addData("artifact Order", artifactOrder.length);
        debug.addData("Turret Angle", turret.getAngle());
        launcher.update();
        debug.update();
    }
}

