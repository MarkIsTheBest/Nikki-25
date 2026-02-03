package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.constants.Control;
import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.MathHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;

public class Turret {
    private final DcMotorEx turret;

    private final Servo barrierLeft;
    private final Servo barrierRight;
    private final Servo hoodLeft;
    private final Servo hoodRight;

    private final Debug debug;
    private final LinearOpMode opMode;

    private static final double MIN_ANGLE_DEG = -80;
    private static final double MAX_ANGLE_DEG = 80;

    private PIDFCoefficients pidf = Control.Turret.pidf;

    private static final double DEADBAND_DEG = 3.0;
    private static final double I_ZONE_DEG = 8.0;

    private static final double TARGET_ALPHA = 0.85;
    private static final double MAX_TARGET_STEP = 3.0;

    private double targetAngleDeg = 0;
    private double filteredTargetDeg = 0;

    private double integral = 0;
    private double lastError = 0;

    private final Timer timer = new Timer();

    public Turret(Hardware hardware, Debug debug, LinearOpMode opMode) {
        this.debug = debug;
        this.opMode = opMode;

        turret = hardware.Motors().Turret();

        barrierLeft = hardware.Servos().BarrierLeft();
        barrierRight = hardware.Servos().BarrierRight();
        hoodLeft = hardware.Servos().HoodLeft();
        hoodRight = hardware.Servos().HoodRight();

        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public Turret(Hardware hardware, Debug debug, LinearOpMode opMode, PIDFCoefficients pidf) {
        this(hardware, debug, opMode);
        this.pidf = pidf;
    }

    public void setAngleRaw(double angleDeg) {
        angleDeg = MathHelper.clamp(angleDeg, MIN_ANGLE_DEG, MAX_ANGLE_DEG);
        targetAngleDeg = angleDeg;
        filteredTargetDeg = angleDeg;
    }

    public void setAngle(double angleDeg) {
        angleDeg = MathHelper.clamp(angleDeg, MIN_ANGLE_DEG, MAX_ANGLE_DEG);
        targetAngleDeg = angleDeg;
    }

    public void zeroAngle() {
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public double getAngle() {
        return MathHelper.ticksToAngle(turret.getCurrentPosition());
    }

    public void update(PIDFCoefficients pidf) {
        this.pidf = pidf;
        update();
    }

    public void update() {
        double smoothedTarget = smoothTarget(targetAngleDeg);
        applyVelocityControl(smoothedTarget);
    }

    private double smoothTarget(double rawTarget) {

        filteredTargetDeg =
                TARGET_ALPHA * filteredTargetDeg
                        + (1 - TARGET_ALPHA) * rawTarget;

        double delta = filteredTargetDeg - rawTarget;
        delta = MathHelper.clamp(delta, -MAX_TARGET_STEP, MAX_TARGET_STEP);
        filteredTargetDeg -= delta;

        return filteredTargetDeg;
    }

    private void applyVelocityControl(double targetDeg) {
        double currentDeg = getAngle();
        double error = angleWrap(targetDeg - currentDeg);

        if (Math.abs(error) < DEADBAND_DEG) {
            turret.setPower(0);
            integral = 0;
            lastError = error;
            return;
        }

        double dt = timer.getElapsedTimeSeconds();
        timer.resetTimer();
        if (dt <= 0 || dt > 0.1) dt = 0.02;

        if (Math.abs(error) < I_ZONE_DEG) {
            integral += error * dt;
        } else {
            integral = 0;
        }

        double derivative = (error - lastError) / dt;
        lastError = error;

        double power = (pidf.P * error) + (pidf.I * integral) + (pidf.D * derivative);

        double ff = Math.signum(error) * pidf.F;

        double totalOutput = power + ff;
        turret.setPower(MathHelper.clamp(totalOutput, -1.0, 1.0));
    }

    public void closeBarrier() {
        moveBarrier(Positions.Servo.BARRIER_OPENED);
    }

    public void openBarrier() {
        moveBarrier(Positions.Servo.BARRIER_CLOSED);
    }

    public void setHoodAngle(double angle) {
        double pos = -0.03055555555 * angle + 1.525;
        moveHood(pos);
    }

    private void moveHood(double pos) {
        hoodLeft.setPosition(pos);
        hoodRight.setPosition(pos + 0.05);
    }

    private void moveBarrier(double pos) {
        barrierLeft.setPosition(pos);
        barrierRight.setPosition(pos + 0.1);
    }

    public void showTelemetry() {
        debug.addData("Turret Angle (deg)", getAngle());
        debug.addData("Turret Target (deg)", filteredTargetDeg);
        debug.addData("Turret Error (deg)", angleWrap(filteredTargetDeg - getAngle()));
        debug.addData("Turret Current (A)", turret.getCurrent(CurrentUnit.AMPS));
    }

    private double angleWrap(double deg) {
        return Math.toDegrees(
                Math.atan2(
                        Math.sin(Math.toRadians(deg)),
                        Math.cos(Math.toRadians(deg))
                )
        );
    }
}
