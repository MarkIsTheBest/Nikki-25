package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
import org.firstinspires.ftc.teamcode.helper.Drawing;
import org.firstinspires.ftc.teamcode.helper.control.CustomFlywheelPID;
import org.firstinspires.ftc.teamcode.helper.control.ShooterLookupTable;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.MathHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Drive;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;
import org.firstinspires.ftc.teamcode.subsystems.Locator;
import org.firstinspires.ftc.teamcode.subsystems.Turret;

@Configurable
public class MainTeleOp {

    /* ===================== CONFIG ===================== */

    public static double LL_FINE_GAIN = 0.05;        // Limelight trim strength
    public static double LL_MAX_CORRECTION = 3.0;    // Max degrees LL can offset
    public static double LL_DEADBAND = 0.5;          // Ignore jitter near 0
    public static double LL_DECAY = 0.90;             // Correction decay when tag lost

    public static double shooterAngle = 45;
    public static double shooterRpm = 3900;

    /* ===================== STATE ===================== */

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
    private Locator locator;

    private CustomFlywheelPID flywheelPID;
    private ShooterLookupTable shooterTable;

    private double limelightCorrection = 0.0;
    private double trackedTurretHeading = 0.0;

    private double distanceAprilTag = 0;
    private double realDistance = 0;

    private ShooterLookupTable.ShooterState targetState;

    private final Timer timeSinceSeenAprilTag = new Timer();

    /* ===================== CONSTRUCTOR ===================== */

    public MainTeleOp(AllianceColor alliance, LinearOpMode opMode, Pose startPose) {
        this.opMode = opMode;
        this.startingPosition = startPose;

        switch (alliance) {
            case RED:
                goalPosition = Positions.Field.RED_GOAL;
                break;
            case BLUE:
                goalPosition = Positions.Field.BLUE_GOAL;
                break;
        }
    }

    /* ===================== INIT ===================== */

    public void initialize() {
        initPedro();
        initHelpers();
        Drawing.init();
        turret.closeBarrier();
    }

    private void initPedro() {
        follower = Constants.createFollower(opMode.hardwareMap);
        follower.setStartingPose(startingPosition);
        follower.update();
    }

    private void initHelpers() {
        hardware = new Hardware();
        debug = new Debug(opMode.telemetry);

        flywheelPID = new CustomFlywheelPID();
        shooterTable = new ShooterLookupTable();

        drive = new Drive(opMode, follower);
        intake = new Intake(opMode, hardware);
        turret = new Turret(hardware, debug, opMode);
        launcher = new Launcher(opMode, hardware, flywheelPID, turret, debug);
        locator = new Locator(follower);

        launcher.setTargetRPM(3800);
        launcher.setTargetAngle(45);

        shooterTable.add(123.75, 4375, 42.5);
        shooterTable.add(105.75, 4000, 42.5);
        shooterTable.add(100.0, 3950, 45);
        shooterTable.add(68.5, 3500, 42.5);
        shooterTable.add(25.0, 2750, 40);
    }

    /* ===================== START ===================== */

    public void play() {
        hardware.Limelight().start();
        drive.start();
    }

    /* ===================== LOOP ===================== */

    public void update() {
        follower.update();
        Drawing.drawDebug(follower);
        drive.update();
        turret.update();

        launcher.input();
        launcher.update();

        hardware.Limelight().update();

        if (!launcher.isShooting()) {
            intake.input();
            intake.update();
        }

        updateShooter();
        updateTurretLocking();
    }

    /* ===================== SHOOTER ===================== */

    private void updateShooter() {
        distanceAprilTag = Math.sqrt(
                Math.pow(hardware.Limelight().Distance() * 39.37, 2)
                        - Math.pow(22.801, 2)
        );

        realDistance = hardware.Limelight().HasAprilTag()
                ? distanceAprilTag
                : getDistanceToGoal();

        targetState = shooterTable.get(realDistance);

        shooterRpm = targetState.rpm;
        shooterAngle = targetState.angle;

        launcher.setTargetRPM(shooterRpm);
        launcher.setTargetAngle(shooterAngle);
    }

    /* ===================== TURRET ===================== */

    private void updateTurretLocking() {

        // Always compute base angle from pinpoint
        double pinpointAngle = getTurretLockAngle(AngleUnit.DEGREES);

        if (hardware.Limelight().HasAprilTag()) {
            double tx = hardware.Limelight().Tx();

            if (Math.abs(tx) > LL_DEADBAND) {
                limelightCorrection -= tx * LL_FINE_GAIN;
            }

            limelightCorrection = MathHelper.clamp(
                    limelightCorrection,
                    -LL_MAX_CORRECTION,
                    LL_MAX_CORRECTION
            );

            timeSinceSeenAprilTag.resetTimer();
        }
        else {
            // Smoothly decay correction when tag disappears
            limelightCorrection *= LL_DECAY;

            if (Math.abs(limelightCorrection) < 0.05) {
                limelightCorrection = 0;
            }
        }

        trackedTurretHeading = pinpointAngle + limelightCorrection;
        turret.setAngle(trackedTurretHeading);
    }

    private double getTurretLockAngle(AngleUnit unit) {
        Pose robotPose = follower.getPose();

        double dx = goalPosition.getX() - robotPose.getX();
        double dy = goalPosition.getY() - robotPose.getY();

        double fieldAngle = Math.atan2(dy, dx);
        double robotHeading = robotPose.getHeading();

        double turretAngle = fieldAngle - robotHeading;
        turretAngle = Math.atan2(Math.sin(turretAngle), Math.cos(turretAngle));

        return unit == AngleUnit.DEGREES
                ? Math.toDegrees(turretAngle)
                : turretAngle;
    }

    private double getDistanceToGoal() {
        return MathHelper.dist(follower.getPose(), goalPosition) - 10;
    }

    /* ===================== TELEMETRY ===================== */

    public void telemetry() {
        debug.addData("Turret Angle", turret.getAngle());
        debug.addData("LL Correction", limelightCorrection);
        debug.addData("Distance (Pinpoint)", getDistanceToGoal());
        debug.addData("Distance (AprilTag)", distanceAprilTag);
        debug.addData("Real Distance", realDistance);
        debug.addData("LL Tx", hardware.Limelight().Tx());
        debug.addData("Has AprilTag", hardware.Limelight().HasAprilTag());
        debug.addData("Current Location", locator.getLocation());
        debug.addBreak();

        launcher.showTelemetry();
        debug.update();
    }
}
