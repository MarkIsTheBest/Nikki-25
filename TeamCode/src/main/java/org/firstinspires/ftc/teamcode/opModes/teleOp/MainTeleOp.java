package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
import org.firstinspires.ftc.teamcode.helper.Drawing;
import org.firstinspires.ftc.teamcode.helper.control.CustomFlywheelPID;
import org.firstinspires.ftc.teamcode.helper.control.ShooterLookupTable;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.FpsCounter;
import org.firstinspires.ftc.teamcode.helper.general.MathHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;
import org.firstinspires.ftc.teamcode.subsystems.Locator;
import org.firstinspires.ftc.teamcode.subsystems.Turret;

import java.util.List;

public class MainTeleOp {

    public static double shooterAngle = 45;
    public static double shooterRpm = 3900;

    private FpsCounter fps = new FpsCounter();
    private Pose goalPosition;
    private final Pose startingPosition;
    private final LinearOpMode opMode;

    private Hardware hardware;
    private Debug debug;
    private Follower follower;

    private Drive drive;
    private Intake intake;
    private Turret turret;
    private Launcher launcher;

    private CustomFlywheelPID flywheelPID;
    private ShooterLookupTable shooterTable;

    private double distanceAprilTag = 0;
    private double realDistance = 0;
    private int limelightPipeline = 0;

    private boolean slowMode = false;

    // Toggle between tracking the alliance goal and a fixed alternate goal position
    private boolean trackingAltGoal = false;
    private final Pose altGoalPosition = new Pose(72, -144, 0);

    private List<LynxModule> allHubs;

    private ShooterLookupTable.ShooterState targetState;

    private final Timer telemetryTimer = new Timer();
    private final Timer drawingTimer = new Timer();
    private final Timer shooterUpdateTimer = new Timer();

    private boolean cachedHasTag = false;
    private double cachedDist = 0;
    private double cachedTx = 0;

    private boolean manualOverride = false;

    private Pose lastShooterPose = new Pose(0,0,0);

    private double offset = 0;

    public MainTeleOp(AllianceColor alliance, LinearOpMode opMode, Pose startPose) {
        this.opMode = opMode;
        this.startingPosition = startPose;

        switch (alliance) {
            case RED:
                goalPosition = Positions.Field.RED_GOAL;
                limelightPipeline = 0;
                break;
            case BLUE:
                goalPosition = Positions.Field.BLUE_GOAL;
                limelightPipeline = 3;
                break;
        }
    }

    public void initialize() {
        initPedro();
        initHelpers();
        Drawing.init();
        turret.closeBarrier();
        telemetryTimer.resetTimer();
    }

    private void initPedro() {
        follower = Constants.createFollower(opMode.hardwareMap);
        follower.setStartingPose(startingPosition);
        follower.update();
    }

    private void initHelpers() {
        allHubs = opMode.hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        hardware = new Hardware();
        debug = new Debug(opMode.telemetry);

        flywheelPID = new CustomFlywheelPID();
        shooterTable = new ShooterLookupTable();

        drive = new Drive(opMode, follower);
        intake = new Intake(opMode, hardware);
        turret = new Turret(hardware, debug, opMode);
        launcher = new Launcher(opMode, hardware, flywheelPID, turret, debug);

        hardware.Limelight().setPipeline(limelightPipeline);

        launcher.setTargetRPM(3800);
        launcher.setTargetAngle(45);

        shooterTable.add(123.75, 4500, 42.5);
        shooterTable.add(105.75, 4250, 42.5);
        shooterTable.add(100.0, 4250, 42.5);
        shooterTable.add(68.5, 3400, 40);
        shooterTable.add(60, 3250, 38);
        shooterTable.add(45, 3000, 35);
        shooterTable.add(25.0, 2750, 32.5);
    }

    public void play() {
        hardware.Limelight().start();
        drive.start();
        telemetryTimer.resetTimer();
        drawingTimer.resetTimer();
    }

    public void update() {
        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }

        follower.update();
        hardware.Limelight().update();

        cachedHasTag = hardware.Limelight().HasAprilTag();
        cachedDist = hardware.Limelight().Distance();
        cachedTx = hardware.Limelight().Tx();

        // Inputs
        if(opMode.gamepad1.leftBumperWasPressed()) {
            slowMode = !slowMode;
        }

        if(opMode.gamepad1.optionsWasPressed()) {
            manualOverride = !manualOverride;
            if(manualOverride)
                turret.setAngle(0);
        }

        if(opMode.gamepad1.triangleWasPressed()) {
            trackingAltGoal = !trackingAltGoal;
            // force an immediate shooter recalculation for the newly selected goal
            shooterUpdateTimer.resetTimer();
            lastShooterPose = new Pose(0, 0, 0);
        }

        // Subsystem Updates
        drive.update(slowMode);
        turret.update(false);
        launcher.input();
        launcher.update();

        if (drawingTimer.getElapsedTime() > 50) {
            Drawing.drawDebug(follower);
            drawingTimer.resetTimer();
        }

        if (!launcher.isShooting()) {
            intake.input();
            intake.update();
        }

        updateShooter();

        if(!manualOverride) {
            turret.updateTurretLocking(follower, getActiveGoal(), cachedHasTag, cachedTx);
        } else if (cachedHasTag) {
            turret.updateVisionOnly(cachedHasTag, cachedTx);
        } else if(!cachedHasTag && manualOverride) {
            turret.setAngle(offset, true);
        }

        if(manualOverride) {
            if(opMode.gamepad1.dpadRightWasPressed()) offset-=5;
            if(opMode.gamepad1.dpadLeftWasPressed()) offset+=5;
        }
    }

    /* ===================== GOAL SELECTION ===================== */

    private Pose getActiveGoal() {
        return trackingAltGoal ? altGoalPosition : goalPosition;
    }

    /* ===================== SHOOTER ===================== */

    private void updateShooter() {
        double distMoved = MathHelper.dist(follower.getPose(), lastShooterPose);

        if (distMoved < 1.0 && shooterUpdateTimer.getElapsedTime() < 100 && !cachedHasTag) {
            return;
        }

        shooterUpdateTimer.resetTimer();
        lastShooterPose = follower.getPose();

        distanceAprilTag = Math.sqrt(
                Math.pow(cachedDist * 39.37, 2) - Math.pow(22.801, 2)
        );

        if(!manualOverride) {
            realDistance = cachedHasTag
                    ? distanceAprilTag
                    : getDistanceToGoal();
        } else {
            realDistance = cachedHasTag ? distanceAprilTag : 55;
        }

        targetState = shooterTable.get(realDistance);

        shooterRpm = targetState.rpm;
        shooterAngle = targetState.angle;

        launcher.setTargetRPM(shooterRpm);
        launcher.setTargetAngle(shooterAngle);
    }

    private double getDistanceToGoal() {
        return MathHelper.dist(follower.getPose(), getActiveGoal()) - 10;
    }

    public void telemetry() {
        if (telemetryTimer.getElapsedTime() < 250) {
            return;
        }

        debug.addData("Limelight Distance", Math.sqrt(Math.pow(hardware.Limelight().Distance() * 39.37, 2) - Math.pow(22.801, 2)));
        debug.addData("Pinpoint Distance", getDistanceToGoal());
        debug.addData("Hood Angle", shooterAngle);
        debug.addData("Tracking Alt Goal", trackingAltGoal);

        telemetryTimer.resetTimer();

        launcher.showTelemetry();
        debug.update();
    }
}