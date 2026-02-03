package org.firstinspires.ftc.teamcode.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.constants.enums.FlywheelState;
import org.firstinspires.ftc.teamcode.helper.control.CustomFlywheelPID;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.MathHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;

@Configurable
public class Launcher {

    public static double RPM_TOLERANCE = 100;
    public static double FEED_POWER = 1.0;
    public static double REVERSE_POWER = -0.5;
    public static int MAX_THROWS = 3;

    private final LinearOpMode opMode;
    private final DcMotorEx flywheelLeft, flywheelRight, intake;
    private final Servo leftLight, rightLight;
    private final CRServo leftFeed, rightFeed;

    private final CustomFlywheelPID pid;
    private final Hardware hardware;
    private final Turret turret;
    private final Debug debug;

    private FlywheelState state = FlywheelState.SPIN_UP;
    private final Timer stateTimer = new Timer();
    private final Timer stopBall = new Timer();
    private final Timer rpmReadDelay = new Timer();

    private boolean shooting = false;
    private boolean spinningUp = false;
    private boolean readyForNextShot = true;

    private static final double SHOT_RECOVERY_TIME_MS = 150;

    private int shotsFired = 0;
    private double currentRPM = 0;
    private double lastCurrentRPM = 0;

    public static double targetRPM = 0;
    private double currentSetRPM = 0;
    private double hoodAngle = 45;

    private double powerLauncher;

    public Launcher(
            LinearOpMode opMode,
            Hardware hardware,
            CustomFlywheelPID pid,
            Turret turret,
            Debug debug
    ) {
        this.opMode = opMode;
        this.hardware = hardware;
        this.pid = pid;
        this.turret = turret;
        this.debug = debug;

        flywheelLeft = hardware.Motors().LeftLauncher();
        flywheelRight = hardware.Motors().RightLauncher();
        intake = hardware.Motors().Intake();

        leftLight = hardware.LEDs().LeftLight();
        rightLight = hardware.LEDs().RightLight();

        leftFeed = hardware.Servos().FeedLeft();
        rightFeed = hardware.Servos().FeedRight();

        turret.closeBarrier();
    }

    public void input() {
        if (opMode.gamepad1.rightBumperWasPressed()) {
            if (shooting) {
                stop();
            } else {
                shoot();
            }
        }

        if(opMode.gamepad1.leftBumperWasPressed()) {
            if (shooting || spinningUp) {
                stop();
            } else {
                spinUp();
            }
        }
    }

    public void spinUp() {
        if(!shooting) {
            spinningUp = true;
            reset();
        }
    }

    public void shoot() {
        shooting = true;
        if(!spinningUp) reset();
        spinningUp = false;
    }

    public void setTargetRPM(double rpm) {
        targetRPM = rpm;
    }

    public void setTargetAngle(double angle) {
        hoodAngle = angle;
    }

    public void update() {
        currentRPM = pid.getCurrentRPM(flywheelLeft);

        powerLauncher = pid.update(currentSetRPM, currentRPM);
        flywheelRight.setPower(powerLauncher);
        flywheelLeft.setPower(powerLauncher);

        boolean atSpeed = MathHelper.inInterval(
                currentRPM,
                targetRPM - RPM_TOLERANCE,
                targetRPM + RPM_TOLERANCE
        );

        turret.setHoodAngle(hoodAngle);

        if (shooting || spinningUp) {
            shootingUpdate(atSpeed);
        }

        if(rpmReadDelay.getElapsedTime() > 100) {
            lastCurrentRPM = currentRPM;
            rpmReadDelay.resetTimer();
        }
        updateLEDs();
    }

    private void shootingUpdate(boolean atSpeed) {

        switch (state) {
            case SPIN_UP:
                if(shooting) {
                    turret.openBarrier();
                }

                currentSetRPM = targetRPM;
                intake.setPower(stateTimer.getElapsedTime() < 75 ? REVERSE_POWER : 0);
                feedBall(stateTimer.getElapsedTime() < 125 ? REVERSE_POWER : 0);

                if (atSpeed && shooting) {
                    readyForNextShot = true;
                    changeState(FlywheelState.LAUNCH);
                }
                break;

            case LAUNCH:
                if(spinningUp && stateTimer.getElapsedTime() < 750) return;
                spinningUp = false;
                if(atSpeed && readyForNextShot) {
                    intake.setPower(FEED_POWER);
                    feedBall(1);
                }
                else {
                    intake.setPower(0);
                    feedBall(0);
                }

                if(!atSpeed) {
                    stopBall.resetTimer();
                }

                if (!readyForNextShot && atSpeed) {
                    readyForNextShot = true;
                }

                if (
                        currentRPM - lastCurrentRPM > 500 &&
                                readyForNextShot &&
                                stateTimer.getElapsedTime() > SHOT_RECOVERY_TIME_MS
                ) {
                    shotsFired++;
                    stateTimer.resetTimer();
                    stopBall.resetTimer();
                    readyForNextShot = false;
                }

                if(shotsFired >= MAX_THROWS || stopBall.getElapsedTime() > 250) {
                    changeState(FlywheelState.STOP);
                }
                break;
            case STOP:
                if(stateTimer.getElapsedTime() > 200) {
                    stop();
                }
                break;
        }
    }

    public void feedBall(double power) {
        leftFeed.setPower(power);
        rightFeed.setPower(power);
    }

    public void stop() {
        shooting = false;
        spinningUp = false;

        intake.setPower(0);
        feedBall(0);
        currentSetRPM = 0;

        turret.closeBarrier();
        reset();
    }

    private void reset() {
        state = FlywheelState.SPIN_UP;
        shotsFired = 0;
        stateTimer.resetTimer();
    }

    private void changeState(FlywheelState newState) {
        state = newState;
        stateTimer.resetTimer();

        if (newState == FlywheelState.LAUNCH) {
            stopBall.resetTimer();
        }
    }

    private void updateLEDs() {
        if (shooting) {
            hardware.LEDs().setRed(leftLight);
            hardware.LEDs().setRed(rightLight);
        } else if (intake.getPower() > 0.1) {
            hardware.LEDs().setGreen(leftLight);
            hardware.LEDs().setGreen(rightLight);
        }
        else {
            hardware.LEDs().setEmpty(leftLight);
            hardware.LEDs().setEmpty(rightLight);
        }
    }

    public boolean isShooting() {
        return shooting;
    }

    public void showTelemetry() {
        debug.addData("Launcher State", state);
        debug.addData("shooting", shooting);
        debug.addData("spinningUp", spinningUp);
        debug.addData("current set rpm", currentSetRPM);
        debug.addData("Target RPM", targetRPM);
        debug.addData("Current RPM", (int) currentRPM);
        debug.addData("Hood Angle", hoodAngle);
        debug.addData("Ready For Next Shot", readyForNextShot);
        debug.addData("Shots Fired", shotsFired);
        debug.addData("power left", hardware.Motors().LeftLauncher().getPower());
        debug.addData("power right", hardware.Motors().RightLauncher().getPower());
        debug.addData("power intake", hardware.Motors().Intake().getPower());
        debug.addData("stateTimer", stateTimer.getElapsedTime());
    }
}
