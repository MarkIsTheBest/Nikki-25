package org.firstinspires.ftc.teamcode.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.constants.enums.FlywheelState;
import org.firstinspires.ftc.teamcode.helper.control.CustomFlywheelPID;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.MathHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;

@Configurable
public class Launcher {

    public static final double DISTANCE_THRESHOLD = 10;
    public static double RPM_TOLERANCE = 50;
    public static double SHOT_DROP_THRESHOLD = 300;
    public static double FEED_POWER = 0.5;
    public static double REVERSE_POWER = -0.5;
    public static int MAX_THROWS = 3;
    public static double EMPTY_TIMEOUT = 300;
    public static double IDLE_POWER = 0.5;

    // OPTIMIZATION: Hardware polling rate increased to reduce I2C blocking
    // I2C calls are blocking and take ~10-20ms per sensor.
    // Doing this too often destroys FPS.
    private static final double SENSOR_POLL_MS = 100;

    private final LinearOpMode opMode;
    private final DcMotorEx flywheelLeft, flywheelRight, intake;
    private final Servo leftLight, rightLight;
    private final CRServo leftFeed, rightFeed;

    private final Rev2mDistanceSensor intakeSensor;
    private final Rev2mDistanceSensor intakeSensor2;
    private final Rev2mDistanceSensor intakeSensor3;

    private final CustomFlywheelPID pid;
    private final Hardware hardware;
    private final Turret turret;
    private final Debug debug;

    private FlywheelState state = FlywheelState.SPIN_UP;
    private final Timer stateTimer = new Timer();
    private final Timer stopBall = new Timer();
    private final Timer shotCooldown = new Timer();
    private final Timer emptyMagazineTimer = new Timer();

    private double distance1;
    private double distance2;
    private double distance3;

    // Optimization Timers
    private final Timer sensorPollTimer = new Timer();

    private boolean shooting = false;
    private boolean spinningUp = false;
    private boolean readyForNextShot = true;
    private boolean feeding = false;
    private boolean seesBall = false;

    private int shotsFired = 0;
    private double currentRPM = 0;
    private double powerLauncher;

    public static double targetRPM = 0;
    private double currentSetRPM = 0;
    private double hoodAngle = 45;

    // Hardware Caches
    private double lastRightPower = 0;
    private double lastLeftPower = 0;
    private double lastIntakePower = 0;
    private double lastFeedPower = 0; // Added cache for CR servos

    public Launcher(LinearOpMode opMode, Hardware hardware, CustomFlywheelPID pid, Turret turret, Debug debug) {
        this.opMode = opMode;
        this.hardware = hardware;
        this.pid = pid;
        this.turret = turret;
        this.debug = debug;

        flywheelLeft = hardware.Motors().LeftLauncher();
        flywheelRight = hardware.Motors().RightLauncher();
        intake = hardware.Motors().Intake();
        intakeSensor = hardware.DistanceSensors().Outtake();
        intakeSensor2 = hardware.DistanceSensors().Outtake2();
        intakeSensor3 = hardware.DistanceSensors().Outtake3();

        leftLight = hardware.LEDs().LeftLight();
        rightLight = hardware.LEDs().RightLight();

        leftFeed = hardware.Servos().FeedLeft();
        rightFeed = hardware.Servos().FeedRight();

        leftFeed.setPower(0);
        rightFeed.setPower(0);

        turret.closeBarrier();
    }

    public void input() {
        if (opMode.gamepad1.rightBumperWasPressed()) {
            if (shooting) stop();
            else shoot();
        }
    }

    public void spinUp() {
        if (!shooting) {
            spinningUp = true;
            reset();
        }
    }

    public void shoot() {
        shooting = true;
        if (!spinningUp) reset();
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
        setFlywheelPower(powerLauncher);

        boolean atSpeed = MathHelper.inInterval(
                currentRPM,
                targetRPM - RPM_TOLERANCE,
                targetRPM + RPM_TOLERANCE
        );
        boolean shouldPollSensors = sensorPollTimer.getElapsedTime() > SENSOR_POLL_MS;

        if (shouldPollSensors) {
            distance1 = intakeSensor.getDistance(DistanceUnit.CM);
            distance2 = intakeSensor2.getDistance(DistanceUnit.CM);
            distance3 = intakeSensor3.getDistance(DistanceUnit.CM);

            boolean rawSeesBall = false;

            rawSeesBall = distance1 < 14 || distance2 < 18 || distance3 < 21;
            if (rawSeesBall) {
                emptyMagazineTimer.resetTimer();
                seesBall = true;
            } else {
                seesBall = false;
            }
            sensorPollTimer.resetTimer();

        }

        turret.setHoodAngle(hoodAngle);

        if (shooting || spinningUp) {
            shootingUpdate(atSpeed);
        }

        if (shooting || spinningUp) {
            currentSetRPM = targetRPM;
        } else {
            currentSetRPM = targetRPM * 0.8;
        }

        updateLEDs();
    }

    private void setFlywheelPower(double power) {
        if (Math.abs(power - lastLeftPower) > 0.01) {
            flywheelLeft.setPower(power);
            lastLeftPower = power;
        }
        if (Math.abs(power - lastRightPower) > 0.01) {
            flywheelRight.setPower(power);
            lastRightPower = power;
        }
    }

    private void setIntakePower(double power) {
        if (Math.abs(power - lastIntakePower) > 0.01) {
            intake.setPower(power);
            lastIntakePower = power;
        }
    }

    private void shootingUpdate(boolean atSpeed) {
        switch (state) {
            case SPIN_UP:
                if (shooting) {
                    turret.openBarrier();
                }

                intake.setPower(0);

                if(stateTimer.getElapsedTime() < 175) {
                    feedBall(-1);
                } else {
                    feedBall(0);
                }

                if (stateTimer.getElapsedTime() < 100) {
                    intake.setPower(-1);
                }
                else {
                    intake.setPower(0);
                }

                if (atSpeed && shooting && (turret.hasReachedPosition || turret.hasReachedLimit)) {
                    readyForNextShot = true;
                    stopBall.resetTimer();
                    changeState(FlywheelState.LAUNCH);
                }
                break;

            case LAUNCH:
                if (spinningUp && stateTimer.getElapsedTime() > 300) return;
                spinningUp = false;

                setIntakePower(FEED_POWER);

                boolean justStarted = stateTimer.getElapsedTime() < 100;

                if (feeding || (justStarted && readyForNextShot)) {
                    feedBall(1);
                } else {
                    feedBall(-1);
                }

                boolean majorDrop = currentRPM < (targetRPM - SHOT_DROP_THRESHOLD);

                if (majorDrop && readyForNextShot) {
                    shotsFired++;
                    readyForNextShot = false;
                    feeding = false;

                    shotCooldown.resetTimer();
                }

                if (atSpeed && !readyForNextShot && !majorDrop) {
                    readyForNextShot = true;
                }

                if (!atSpeed) {
                    stopBall.resetTimer();
                }

                if (atSpeed && readyForNextShot) {
                    feeding = true;
                }
                boolean isMagazineEmpty = emptyMagazineTimer.getElapsedTime() > EMPTY_TIMEOUT;

                if (shotsFired >= MAX_THROWS || stopBall.getElapsedTime() > 2000 || isMagazineEmpty) {
                    changeState(FlywheelState.STOP);
                }

                break;

            case STOP:
                if (stateTimer.getElapsedTime() > 200) {
                    stop();
                }
                break;
        }
    }

    public void feedBall(double power) {
        if (Math.abs(power - lastFeedPower) > 0.01) {
            leftFeed.setPower(power);
            rightFeed.setPower(power);
            lastFeedPower = power;
        }
    }

    public void stop() {
        shooting = false;
        spinningUp = false;
        setIntakePower(0);
        feedBall(0);
        turret.closeBarrier();
        reset();
    }

    private void reset() {
        state = FlywheelState.SPIN_UP;
        shotsFired = 0;
        stateTimer.resetTimer();
        emptyMagazineTimer.resetTimer();
    }

    private void changeState(FlywheelState newState) {
        state = newState;
        stateTimer.resetTimer();
        if (newState == FlywheelState.LAUNCH) {
            stopBall.resetTimer();
        }
    }

    private void updateLEDs() {
        if (turret.hasReachedLimit && shooting) {
            hardware.LEDs().setRed(leftLight);
            hardware.LEDs().setRed(rightLight);
        } else if (!turret.hasReachedLimit && shooting) {
            hardware.LEDs().setPurple(leftLight);
            hardware.LEDs().setPurple(rightLight);
        } else if (intake.getPower() > 0.1) {
            hardware.LEDs().setGreen(leftLight);
            hardware.LEDs().setGreen(rightLight);
        } else {
            hardware.LEDs().setEmpty(leftLight);
            hardware.LEDs().setEmpty(rightLight);
        }
    }

    public boolean isShooting() { return shooting; }
    public void showTelemetry() {
        debug.addData("Launcher State", state);
        debug.addData("Shots Fired", shotsFired);
        debug.addData("Current RPM", (int) currentRPM);
        debug.addData("Target RPM", targetRPM);
        debug.addData("SeenRaw", seesBall);
        debug.addData("dist1", distance1);
        debug.addData("dist2", distance2);
        debug.addData("dist3", distance3);
        debug.addData("power", powerLauncher);
        debug.addData("Empty Timer", emptyMagazineTimer.getElapsedTime());
    }
}