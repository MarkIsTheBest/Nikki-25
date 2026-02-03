package org.firstinspires.ftc.teamcode.opModes.autonomous.autoNear;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
import org.firstinspires.ftc.teamcode.constants.enums.AutoFarState;
import org.firstinspires.ftc.teamcode.constants.enums.AutoNearState;
import org.firstinspires.ftc.teamcode.constants.enums.AutoOrderNear;
import org.firstinspires.ftc.teamcode.helper.control.CustomFlywheelPID;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;
import org.firstinspires.ftc.teamcode.subsystems.Turret;

@Autonomous
public class AutoNear2 extends LinearOpMode {


    private final LinearOpMode opMode;
    private final AutoOrderNear[] artifactOrder;
    private double turretAngle;

    public AutoNear2(LinearOpMode opMode, AllianceColor color, AutoOrderNear[] artifactOrder) {
        this.opMode = opMode;
        this.artifactOrder = artifactOrder;

        Pose turretAnglePose = new Pose(0,0, Math.toRadians(68.79718135635073));

        startPose = new Pose(110.246, 135.131, Math.toRadians(0)); // Start position
        launchPose = new Pose(85, 84, Math.toRadians(0)); // Launch
        intake1Pose = new Pose(125, 84, Math.toRadians(0)); // Intake_1
        prepIntake2Pose = new Pose(101, 59.5, Math.toRadians(0)); // Prep_Intake_2
        intake2Pose = new Pose(133, 59.5, Math.toRadians(0)); // Intake_2
        intakeGatePose = new Pose(132.5, 59, Math.toRadians(40)); // Intake_Gate
        prepIntake3Pose = new Pose(101, 35.5, Math.toRadians(0)); // Prep_Intake_3
        intake3Pose = new Pose(132, 35.5, Math.toRadians(0)); // Intake_3
        leavePose = new Pose(85, 65, Math.toRadians(270)); // Leave

        launchPreloadCp0 = new Pose(90.5, 115.5); // Control point 1 for Launch_Preload
        prepIntake2Cp0 = new Pose(88, 66.5); // Control point 1 for Prep_Intake_2
        launch2Cp0 = new Pose(92, 58.5); // Control point 1 for Launch_2
        intakeGateCp0 = new Pose(93, 57); // Control point 1 for Intake_Gate
        launchGateCp0 = new Pose(99, 55); // Control point 1 for Launch_Gate
        prepIntake3Cp0 = new Pose(85, 42.5); // Control point 1 for Prep_Intake_3
        launch3Cp0 = new Pose(91.006, 35.006); // Control point 1 for Launch_3

        switch (color) {
            case RED:
                turretAngle = Math.toDegrees(turretAnglePose.getHeading());

                break;

            case BLUE:
                turretAngle = Math.toDegrees(turretAnglePose.mirror().getHeading());

                startPose = startPose.mirror();
                launchPose = launchPose.mirror();
                intake1Pose = intake1Pose.mirror();
                prepIntake2Pose = prepIntake2Pose.mirror();
                intake2Pose = intake2Pose.mirror();
                intakeGatePose = intakeGatePose.mirror();
                prepIntake3Pose = prepIntake3Pose.mirror();
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
    private Pose intakeGatePose;
    private Pose prepIntake3Pose;
    private Pose intake3Pose;
    private Pose leavePose;

    // Control Point Poses
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

    private int artifactIndex = 0;

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
                        intakeGatePose
                ))
                .setLinearHeadingInterpolation(launchPose.getHeading(), intakeGatePose.getHeading())
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
                .setLinearHeadingInterpolation(launchPose.getHeading(), leavePose.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case INITIALIZE:
                launcher.spinUp();
                follower.followPath(launchPreloadPath);
                setPathState(AutoNearState.LAUNCH_PRELOAD);
                break;
            case LAUNCH_PRELOAD:
                if(!follower.isBusy()) {
                    launcher.shoot();
                    setPathState(AutoNearState.INTAKE);
                }
            case INTAKE:
                if(!launcher.isShooting()) {
                    if (artifactIndex >= artifactOrder.length) {
                        setPathState(AutoNearState.LEAVE);
                        return;
                    }
                    intake.setIntake(true);
                    follower.followPath(getIntakePath(artifactOrder[artifactIndex]));
                    setPathState(AutoNearState.DRIVE_TO_LAUNCH);
                }

            case DRIVE_TO_LAUNCH:
                if(!follower.isBusy()) {
                    intake.setIntake(false);
                    launcher.spinUp();
                    follower.followPath(getLaunchPath(artifactOrder[artifactIndex]));
                    setPathState(AutoNearState.SHOOT);
                }

            case SHOOT:
                if(!follower.isBusy()) {
                    launcher.shoot();
                    setPathState(AutoNearState.LEAVE);
                }
            case LEAVE:
                if(!follower.isBusy()) {
                    follower.followPath(leavePath);
                }
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

        launcher.setTargetAngle(45);
        launcher.setTargetRPM(3800);
    }

    public void play() {
        setPathState(AutoNearState.INITIALIZE);
    }

    public void update() {
        follower.update();
        turret.update();
        autonomousPathUpdate();

        debug.addData("path state", pathState);
        debug.addData("x", follower.getPose().getX());
        debug.addData("y", follower.getPose().getY());
        debug.addData("heading", follower.getPose().getHeading());
        debug.update();
    }
}

