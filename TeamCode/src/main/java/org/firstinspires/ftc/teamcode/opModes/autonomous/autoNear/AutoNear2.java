package org.firstinspires.ftc.teamcode.opModes.autonomous.autoNear;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
import org.firstinspires.ftc.teamcode.constants.enums.AutoNearState;
import org.firstinspires.ftc.teamcode.constants.enums.AutoOrderNear;
import org.firstinspires.ftc.teamcode.helper.Drawing;
import org.firstinspires.ftc.teamcode.helper.control.CustomFlywheelPID;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import java.util.List;

@Autonomous

public class AutoNear2 extends LinearOpMode {
    private final LinearOpMode opMode;
    private final AutoOrderNear[] artifactOrder;
    private double turretAngle;
    private int limelightPipeline;
    private boolean hasFinished = false;


    public AutoNear2(LinearOpMode opMode, AllianceColor color, AutoOrderNear[] artifactOrder) {

        this.opMode = opMode;
        this.artifactOrder = artifactOrder;
        turretAngle = 44;

        startPose = new Pose(110, 135, Math.toRadians(0)); // Start position
        launchPose = new Pose(85, 84, Math.toRadians(0)); // Launch
        intake1Pose = new Pose(125.5, 84, Math.toRadians(0)); // Intake_1
        prepIntake2Pose = new Pose(101, 62, Math.toRadians(0)); // Prep_Intake_2
        intake2Pose = new Pose(132, 62, Math.toRadians(0)); // Intake_2
        prepIntakeGatePose = new Pose(128, 61, Math.toRadians(37.7)); // Intake_Gate
        intakeGatePose = new Pose(131.5, 56.5, Math.toRadians(45.0)); // Intake_Gate
        finalIntakeGatePose = new Pose(133.3, 60, Math.toRadians(60.0));
        prepIntake3Pose = new Pose(101, 35.5, Math.toRadians(0)); // Prep_Intake_3
        intake3Pose = new Pose(132, 37, Math.toRadians(0)); // Intake_3
        leavePose = new Pose(96.8, 78.5, Math.toRadians(0)); // Leave
        launchPreloadCp0 = new Pose(90.5, 115.5); // Control point 1 for Launch_Preload
        prepIntake2Cp0 = new Pose(88, 66.5); // Control point 1 for Prep_Intake_2
        launch2Cp0 = new Pose(92, 58.5); // Control point 1 for Launch_2
        intakeGateCp0 = new Pose(93, 57); // Control point 1 for Intake_Gate
        launchGateCp0 = new Pose(91.4375, 52.43750000000001); // Control point 1 for Launch_Gate
        prepIntake3Cp0 = new Pose(85, 42.5); // Control point 1 for Prep_Intake_3
        launch3Cp0 = new Pose(91.006, 35.006); // Control point 1 for Launch_3

        switch (color) {

            case RED:
                turretAngle = 44;
                limelightPipeline = 0;
                break;

            case BLUE:
                turretAngle = -46;
                limelightPipeline = 3;
                startPose = startPose.mirror();
                launchPose = launchPose.mirror();
                intake1Pose = intake1Pose.mirror();
                prepIntake2Pose = prepIntake2Pose.mirror();
                intake2Pose = intake2Pose.mirror();
                prepIntakeGatePose = prepIntakeGatePose.mirror();
                intakeGatePose = intakeGatePose.mirror();
                prepIntake3Pose = prepIntake3Pose.mirror();
                finalIntakeGatePose = finalIntakeGatePose.mirror();
                intake3Pose = intake3Pose.mirror();
                leavePose = leavePose.mirror();
                launchPreloadCp0 = launchPreloadCp0.mirror();
                prepIntake2Cp0 = prepIntake2Cp0.mirror();
                launch2Cp0 = launch2Cp0.mirror();
                intakeGateCp0 = intakeGateCp0.mirror();
                launchGateCp0 = launchGateCp0.mirror();
                prepIntake3Cp0 = prepIntake3Cp0.mirror();
                launch3Cp0 = launch3Cp0.mirror();
                break;
        }
    }
    private Follower follower;
    private Timer pathTimer;
    private AutoNearState pathState;

    private Pose startPose;

    private Pose launchPose;
    private Pose intake1Pose;
    private Pose prepIntake2Pose;
    private Pose intake2Pose;
    private Pose prepIntakeGatePose;
    private Pose intakeGatePose;
    private Pose finalIntakeGatePose;
    private Pose prepIntake3Pose;
    private Pose intake3Pose;
    private Pose leavePose;

    private Pose launchPreloadCp0;
    private Pose prepIntake2Cp0;
    private Pose launch2Cp0;
    private Pose intakeGateCp0;
    private Pose launchGateCp0;
    private Pose prepIntake3Cp0;
    private Pose launch3Cp0;

    private PathChain launchPreloadPath, intake1Path,
    launch1Path, intake2Path, launch2Path,
    intakeGatePath, launchGatePath,
    intake3Path, launch3Path, leavePath;

    private Intake intake;
    private Turret turret;
    private Launcher launcher;
    private CustomFlywheelPID flywheelPID;
    private Hardware hardware;
    private Debug debug;
    private boolean cachedHasTag = false;
    private double cachedTx = 0;
    private int artifactIndex = 0;
    private List<LynxModule> allHubs;

    private PathChain getIntakePath(AutoOrderNear a) {
        return switch (a) {
            case SPIKE_MARK_1 -> intake1Path;
            case SPIKE_MARK_2 -> intake2Path;
            case SPIKE_MARK_3 -> intake3Path;
            case INTAKE_GATE -> intakeGatePath;
        };
    }

    private PathChain getLaunchPath(AutoOrderNear a) {
        return switch (a) {
            case SPIKE_MARK_1 -> launch1Path;
            case SPIKE_MARK_2 -> launch2Path;
            case SPIKE_MARK_3 -> launch3Path;
            case INTAKE_GATE -> launchGatePath;
        };
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void setPathState(AutoNearState pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    public void buildPaths() {

        launchPreloadPath = follower.pathBuilder()

                .addPath(new BezierCurve(
                        startPose,
                        launchPreloadCp0,
                        launchPose
                ))
                .setConstantHeadingInterpolation(launchPose.getHeading())
                .build();

        intake1Path = follower.pathBuilder()
                .addPath(new BezierLine(launchPose, intake1Pose))
                .setConstantHeadingInterpolation(intake1Pose.getHeading())
                .build();

        launch1Path = follower.pathBuilder()
                .addPath(new BezierLine(intake1Pose, launchPose))
                .setConstantHeadingInterpolation(launchPose.getHeading())
                .build();

        intake2Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        launchPose,
                        prepIntake2Cp0,
                        prepIntake2Pose
                ))
                .setConstantHeadingInterpolation(prepIntake2Pose.getHeading())
                .addPath(new BezierLine(prepIntake2Pose, intake2Pose))
                .setConstantHeadingInterpolation(intake2Pose.getHeading())
                .build();

        launch2Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        intake2Pose,
                        launch2Cp0,
                        launchPose
                ))

                .setConstantHeadingInterpolation(launchPose.getHeading())
                .build();

        intakeGatePath = follower.pathBuilder()

                .addPath(new BezierCurve(
                        launchPose,
                        intakeGateCp0,
                        prepIntakeGatePose

                ))
                .setLinearHeadingInterpolation(launchPose.getHeading(), prepIntakeGatePose.getHeading())
                .addPath(new BezierLine(
                        prepIntakeGatePose,
                        intakeGatePose
                ))
                .setLinearHeadingInterpolation(prepIntakeGatePose.getHeading(), intakeGatePose.getHeading())
                .build();

        launchGatePath = follower.pathBuilder()

                .addPath(new BezierCurve(
                        intakeGatePose,
                        launchGateCp0,
                        launchPose
                ))
                .setLinearHeadingInterpolation(intakeGatePose.getHeading(), launchPose.getHeading())
                .build();

        intake3Path = follower.pathBuilder()
                .addPath(new BezierCurve(
                        launchPose,
                        prepIntake3Cp0,
                        prepIntake3Pose
                ))
                .setConstantHeadingInterpolation(prepIntake3Pose.getHeading())
                .addPath(new BezierLine(prepIntake3Pose, intake3Pose))
                .setConstantHeadingInterpolation(intake3Pose.getHeading())
                .build();

        launch3Path = follower.pathBuilder()

                .addPath(new BezierCurve(
                        intake3Pose,
                        launch3Cp0,
                        launchPose
                ))
                .setConstantHeadingInterpolation(launchPose.getHeading())
                .build();

        leavePath = follower.pathBuilder()
                .addPath(new BezierLine(launchPose, leavePose))
                .setConstantHeadingInterpolation(leavePose.getHeading())
                .build();

    }

    public void autonomousPathUpdate() {

        switch (pathState) {

            case INITIALIZE:
                follower.followPath(launchPreloadPath);
                launcher.spinUp();
                setPathState(AutoNearState.LAUNCH_PRELOAD);
                break;

            case LAUNCH_PRELOAD:
                if(follower.isBusy()) pathTimer.resetTimer();
                if(!follower.isBusy() && pathTimer.getElapsedTime() > 250) {
                    launcher.shoot();
                    setPathState(AutoNearState.INTAKE);
                }
                break;

            case INTAKE:

                if(!launcher.isShooting() && pathTimer.getElapsedTime() > 250) {
                    intake.setIntake(true);
                    follower.followPath(getIntakePath(artifactOrder[artifactIndex]));
                    if(artifactOrder[artifactIndex] != AutoOrderNear.INTAKE_GATE) {
                        setPathState(AutoNearState.DRIVE_TO_LAUNCH);
                    }
                    else if (artifactOrder[artifactIndex] == AutoOrderNear.INTAKE_GATE) {
                        setPathState(AutoNearState.INTAKE_GATE_DELAY);
                    }
                }
                break;

            case INTAKE_GATE_DELAY:
                if(follower.isBusy()) pathTimer.resetTimer();
                if(pathTimer.getElapsedTime() > 1500) {
                    setPathState(AutoNearState.DRIVE_TO_LAUNCH);
                }
                break;

            case DRIVE_TO_LAUNCH:

                if(!follower.isBusy()) {
                    intake.setIntake(false);
                    launcher.spinUp();

                    follower.followPath(getLaunchPath(artifactOrder[artifactIndex]));

                    setPathState(AutoNearState.SHOOT);

                }
                break;

            case SHOOT:
                if(follower.isBusy()) pathTimer.resetTimer();
                if(!follower.isBusy() && turret.hasReachedPosition && pathTimer.getElapsedTime() > 250) {
                    artifactIndex++;
                    launcher.shoot();
                    if (artifactIndex >= artifactOrder.length) {
                        setPathState(AutoNearState.LEAVE);
                        return;
                    }
                    setPathState(AutoNearState.INTAKE);
                }
                break;

            case LEAVE:
                if(!follower.isBusy() && !launcher.isShooting()) {
                    follower.followPath(leavePath);
                    hasFinished = true;
                    setPathState(AutoNearState.DONE);

                }

                break;

            case DONE:
            //DONE
                break;
        }
    }

    public void initialize() {

        allHubs = opMode.hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        pathTimer = new Timer();

        follower = Constants.createFollower(opMode.hardwareMap);

        buildPaths();

        follower.setStartingPose(startPose);
        Drawing.init();
        hardware = new Hardware();
        debug = new Debug(opMode.telemetry);
        turret = new Turret(hardware, debug, opMode);
        flywheelPID = new CustomFlywheelPID();
        launcher = new Launcher(opMode, hardware, flywheelPID, turret, debug);
        intake = new Intake(opMode, hardware);

        hardware.Limelight().init();
        hardware.Limelight().setPipeline(limelightPipeline);

        turret.closeBarrier();
        turret.setAngle(turretAngle);
        launcher.setTargetAngle(43);
        launcher.setTargetRPM(3300);
    }

    public void play() {

        setPathState(AutoNearState.INITIALIZE);

        hardware.Limelight().start();

    }

    public void update() {

        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }

        follower.update();

        if(!hasFinished) {
            turret.updateVisionOnly(cachedHasTag, cachedTx);
        } else {
            turret.setAngle(0);
        }

        Drawing.drawDebug(follower);

        turret.update(true);
        intake.update();
        launcher.update();

        autonomousPathUpdate();

        hardware.Limelight().update();
        cachedHasTag = hardware.Limelight().HasAprilTag();
        cachedTx = hardware.Limelight().Tx();

        //launcher.showTelemetry();
        debug.update();
    }
}