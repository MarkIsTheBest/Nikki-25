package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.constants.Control;
import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.MathHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LimelightHelper;

public class Turret {
    private final DcMotorEx turret;

    private final Servo barrierLeft;
    private final Servo barrierRight;
    private final Servo hoodLeft;
    private final Servo hoodRight;
    private final LimelightHelper limelight;

    private final Debug debug;
    private final LinearOpMode opMode;

    private static final double MIN_ANGLE_DEG = -65;
    private static final double MAX_ANGLE_DEG = 56.5;

    private PIDFCoefficients pidf = Control.Turret.pidf;
    private PIDFCoefficients pidfNear = Control.Turret.pidfNear;

    private static final double DEADBAND_DEG = 0.25;
    private static final double I_ZONE_DEG = 6.0;

    private static final double PINPOINT_ALPHA = 0.50;
    private static final double LIMELIGHT_ALPHA = 0.6;
    private static final double VISION_BLEND_ALPHA = 0.20;

    private static final double MAX_POWER_CHANGE_PER_SEC = 7.0;
    private double lastPower = 0;

    private double targetAngleDeg = 0;
    private double filteredTargetDeg = 0;
    private double currentAlpha = PINPOINT_ALPHA;

    private double integral = 0;
    private double lastError = 0;

    private final Timer timer = new Timer();

    private double pinpointOffset = 0.0;
    private final Timer timeSinceSeenAprilTag = new Timer();
    private boolean currentlySeeingTag = false;

    private double lastWrittenPower = 0.0;

    // OPTIMIZATION: Cache servo positions
    private double lastHoodPos = -999;
    private double lastBarrierPos = -999;

    public boolean hasReachedPosition = false;
    public boolean hasReachedLimit = false;

    public Turret(Hardware hardware, Debug debug, LinearOpMode opMode) {
        this.debug = debug;
        this.opMode = opMode;

        turret = hardware.Motors().Turret();
        limelight = hardware.Limelight();

        barrierLeft = hardware.Servos().BarrierLeft();
        barrierRight = hardware.Servos().BarrierRight();
        hoodLeft = hardware.Servos().HoodLeft();
        hoodRight = hardware.Servos().HoodRight();

        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void update(boolean auto) {
        double smoothedTarget = smoothTarget(targetAngleDeg);
        applyVelocityControl(smoothedTarget);

        hasReachedPosition = (Math.abs(targetAngleDeg - getAngle()) < 4);
        if(auto) return;
        hasReachedLimit = (MathHelper.inInterval(getAngle(), MIN_ANGLE_DEG - 20, MIN_ANGLE_DEG + 20)
                || MathHelper.inInterval(getAngle(), MAX_ANGLE_DEG - 20, MAX_ANGLE_DEG + 20));
    }

    private double smoothTarget(double rawTarget) {
        filteredTargetDeg = currentAlpha * filteredTargetDeg + (1 - currentAlpha) * rawTarget;
        return filteredTargetDeg;
    }

    private void applyVelocityControl(double targetDeg) {
        double currentDeg = getAngle();
        double error = angleWrap(targetDeg - currentDeg);

        if (Math.abs(error) < DEADBAND_DEG) {
            setTurretPower(0);
            integral = 0;
            lastError = error;
            lastPower = 0;
            return;
        }

        double dt = timer.getElapsedTimeSeconds();
        timer.resetTimer();
        if (dt <= 0 || dt > 0.1) dt = 0.02;

        if (Math.abs(error) < I_ZONE_DEG) {
            if (Math.signum(error) != Math.signum(lastError)) integral = 0;
            integral += error * dt;
        } else {
            integral = 0;
        }

        double derivative = (error - lastError) / dt;
        lastError = error;

        double ffWeight = Math.min(1.0, (Math.abs(error) - DEADBAND_DEG) / 3.0);

        double ff;
        double rawPower;

        if (Math.abs(error) <= 45) {
            ff = Math.signum(error) * pidfNear.F * ffWeight;

            rawPower = (pidfNear.P * error) + (pidfNear.I * integral) + (pidfNear.D * derivative) + ff;
        }
        else {
            ff = Math.signum(error) * pidf.F * ffWeight;

            rawPower = (pidf.P * error) + (pidf.I * integral) + (pidf.D * derivative) + ff;
        }

        double maxChange = MAX_POWER_CHANGE_PER_SEC * dt;
        double clampedPower = MathHelper.clamp(rawPower, lastPower - maxChange, lastPower + maxChange);

        double finalPower = MathHelper.clamp(clampedPower, -0.85, 0.85);

        setTurretPower(finalPower);
        lastPower = finalPower;
    }

    private void setTurretPower(double power) {
        if (Math.abs(power - lastWrittenPower) > 0.01) {
            turret.setPower(power);
            lastWrittenPower = power;
        }
    }

    public void updateTurretLocking(Follower follower, Pose goalPosition, boolean hasTag, double llTx) {
        double pinpointAngle = getTurretLockAngle(follower, goalPosition, AngleUnit.DEGREES);

        if (hasTag) {
            currentlySeeingTag = true;
            currentAlpha = LIMELIGHT_ALPHA;

            double visionTarget = getAngle() - llTx;

            double newTarget = (targetAngleDeg * (1 - VISION_BLEND_ALPHA)) + (visionTarget * VISION_BLEND_ALPHA);

            pinpointOffset = newTarget - pinpointAngle;
            timeSinceSeenAprilTag.resetTimer();
            setAngle(newTarget);
        } else {
            if (currentlySeeingTag) {
                if (timeSinceSeenAprilTag.getElapsedTimeSeconds() > 0.15) {
                    currentlySeeingTag = false;
                    currentAlpha = PINPOINT_ALPHA;

                    double snapTarget = pinpointAngle + pinpointOffset;
                    targetAngleDeg = snapTarget;
                    filteredTargetDeg = snapTarget;
                }
            } else {
                currentAlpha = PINPOINT_ALPHA;
                setAngle(pinpointAngle + pinpointOffset);

                if (timeSinceSeenAprilTag.getElapsedTimeSeconds() > 1.0) {
                    pinpointOffset *= 0.50;
                }
            }
        }
    }

    public void updateVisionOnly(boolean hasTarget, double llTx) {
        if (hasTarget) {
            currentAlpha = LIMELIGHT_ALPHA;
            double visionTarget = getAngle() - llTx;

            targetAngleDeg = (targetAngleDeg * (1 - VISION_BLEND_ALPHA)) + (visionTarget * VISION_BLEND_ALPHA);

            targetAngleDeg = MathHelper.clamp(targetAngleDeg, MIN_ANGLE_DEG, MAX_ANGLE_DEG);
        } else {
            currentAlpha = PINPOINT_ALPHA;
        }
    }

    private double getTurretLockAngle(Follower follower, Pose goalPosition, AngleUnit unit) {
        Pose robotPose = follower.getPose();
        double dx = goalPosition.getX() - robotPose.getX();
        double dy = goalPosition.getY() - robotPose.getY();
        double fieldAngle = Math.atan2(dy, dx);
        double robotHeading = robotPose.getHeading();
        double turretAngle = angleWrapRad(fieldAngle - robotHeading);
        return unit == AngleUnit.DEGREES ? Math.toDegrees(turretAngle) : turretAngle;
    }

    private double angleWrap(double deg) {
        return Math.toDegrees(angleWrapRad(Math.toRadians(deg)));
    }

    private double angleWrapRad(double rad) {
        return Math.atan2(Math.sin(rad), Math.cos(rad));
    }

    public void setAngle(double angleDeg, boolean ignoreClamp) {
        if(ignoreClamp) {
            targetAngleDeg = angleDeg;
            return;
        }
        targetAngleDeg = MathHelper.clamp(angleDeg, MIN_ANGLE_DEG, MAX_ANGLE_DEG);
    }

    public void setAngle(double angleDeg) {
        targetAngleDeg = MathHelper.clamp(angleDeg, MIN_ANGLE_DEG, MAX_ANGLE_DEG);
    }

    public double getAngle() { return MathHelper.ticksToAngle(turret.getCurrentPosition()); }

    public double getTargetAngle() { return targetAngleDeg; }

    public void zeroAngle() { turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }

    public void closeBarrier() { moveBarrier(Positions.Servo.BARRIER_OPENED); }
    public void openBarrier() { moveBarrier(Positions.Servo.BARRIER_CLOSED); }

    public void setHoodAngle(double angle) {
        moveHood(-0.03055555555 * angle + 1.525);
    }

    // OPTIMIZATION: Hardware write caching for servos
    private void moveHood(double pos) {
        if (Math.abs(pos - lastHoodPos) > 0.001) {
            if (hoodLeft != null) hoodLeft.setPosition(pos);
            if (hoodRight != null) hoodRight.setPosition(pos + 0.05);
            lastHoodPos = pos;
        }
    }

    private void moveBarrier(double pos) {
        if (Math.abs(pos - lastBarrierPos) > 0.001) {
            if (barrierLeft != null) barrierLeft.setPosition(pos);
            if (barrierRight != null) barrierRight.setPosition(pos + 0.1);
            lastBarrierPos = pos;
        }
    }
}