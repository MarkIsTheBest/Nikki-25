package org.firstinspires.ftc.teamcode.helper;

import static org.firstinspires.ftc.teamcode.constants.Distances.Intake.BALL_DETECT_DISTANCE;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.C_D1_PREPARE;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.L_D1_PARTIAL;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.C_D2_PREPARE;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.L_D1_PREPARE;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.L_D2_PREPARE;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.H_CLOSE;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.H_PREPARE;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.R_D1_PREPARE;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.R_D2_PARTIAL;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.R_D2_PREPARE;

import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.constants.Timers;
import org.firstinspires.ftc.teamcode.constants.enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.constants.enums.IntakeStep;
import org.firstinspires.ftc.teamcode.constants.enums.Launcher;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.ColorSensors;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.DistanceSensors;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LEDs;

import java.util.Arrays;

public class IntakeHelper {

    public static IntakeHelper INSTANCE;

    private double leftDistance;
    private double rightDistance;
    private double distance;
    private boolean jammed = false;
    private boolean spinIntake = false;

    private Launchers launcherHelper;

    public IntakeHelper() {
        launcherHelper = Launchers.INSTANCE;
        initPositions();
    }

    private final Timer intakeTimer = new Timer();
    private final Timer jamTimer = new Timer();

    private IntakeStep currentStep = IntakeStep.PREPARE_DOORS;
    private Launcher activeLauncher = null;

    private boolean hasBall = false;

    double intakeTime;

    private void changeStep(IntakeStep newStep) {
        currentStep = newStep;
        intakeTimer.resetTimer();
    }

    private void initPositions() {
        Servos.setPosition(Servos.Holder1(), H_PREPARE);
        Servos.setPosition(Servos.Holder2(), H_PREPARE);
        Servos.setPosition(Servos.Holder3(), H_PREPARE);

        Servos.setPosition(Servos.Door1(), L_D1_PREPARE);
        Servos.setPosition(Servos.Door2(), L_D2_PREPARE);
    }

    private Launcher getFirstEmptyLauncher() {
        for (Launcher l : Launcher.values()) {
            if (!launcherHelper.getFilledLaunchers()[l.index]) {
                return l;
            }
        }
        return null;
    }

    private void selectLauncherIfNeeded() {
        if (activeLauncher != null) return;

        Launcher next = getFirstEmptyLauncher();
        if (next == null) return;

        activeLauncher = next;
        currentStep = IntakeStep.PREPARE_DOORS;
        intakeTimer.resetTimer();
    }

    private void endIntake(Launcher launcher) {
        ArtifactColor color = ColorSensors.getColor(ColorSensors.AllColorSensors()[launcher.index]);

        if (color != ArtifactColor.EMPTY) {
            launcherHelper.setFilledLaunchers(launcher.index, true);
            launcherHelper.setLauncherColor(launcher.index, color);
            launcherHelper.HandleLEDS();
        }
        activeLauncher = null;
        hasBall = false;
        currentStep = IntakeStep.PREPARE_DOORS;
    }

    private void intake() {
        if (activeLauncher == null) return;

        Servo holder = (activeLauncher == Launcher.LEFT) ? Servos.Holder1() :
                (activeLauncher == Launcher.CENTER) ? Servos.Holder2() : Servos.Holder3();

        switch (currentStep) {
            case PREPARE_DOORS:
                applyDoorPositions(activeLauncher);
                Servos.setPosition(holder, H_PREPARE);

                if (hasBall) {
                    changeStep(IntakeStep.PARTIAL);
                }
                break;

            case PARTIAL:
                if (intakeTime > Timers.Intake.PARTIAL_DELAY && !jammed) {
                    if (activeLauncher == Launcher.LEFT) Servos.setPosition(Servos.Door1(), L_D1_PARTIAL);
                    if (activeLauncher == Launcher.RIGHT) Servos.setPosition(Servos.Door2(), R_D2_PARTIAL);

                    if (!Servos.isBusy(Servos.Door1()) && !Servos.isBusy(Servos.Door2())) {
                        Servos.setPosition(holder, H_CLOSE);
                        changeStep(IntakeStep.CLOSE);
                    }
                }
                break;

            case CLOSE:
                if (intakeTime > Timers.Intake.PARTIAL_DELAY && !jammed) {
                    endIntake(activeLauncher);
                }
                break;
        }
    }

    private void applyDoorPositions(Launcher launcher) {
        switch (launcher) {
            case LEFT:
                Servos.setPosition(Servos.Door1(), L_D1_PREPARE);
                Servos.setPosition(Servos.Door2(), L_D2_PREPARE);
                break;
            case CENTER:
                Servos.setPosition(Servos.Door1(), C_D1_PREPARE);
                Servos.setPosition(Servos.Door2(), C_D2_PREPARE);
                break;
            case RIGHT:
                Servos.setPosition(Servos.Door1(), R_D1_PREPARE);
                Servos.setPosition(Servos.Door2(), R_D2_PREPARE);
                break;
        }
    }

    private double computeMinDistance(double left, double right) {
        double avg = (left + right) / 2.0;
        if (avg <= BALL_DETECT_DISTANCE * 2) return Math.min(left, right);
        return 999;
    }

    private void checkJammed() {
        if (jammed) {
            if (jamTimer.getElapsedTimeSeconds() > Timers.Intake.UNJAM_TIME) {
                jammed = false;
                launcherHelper.HandleLEDS();
                jamTimer.resetTimer();
            }
            LEDs.playRedFlashAnimation();
            return;
        }

        if (!Servos.isBusy(Servos.Door1()) && !Servos.isBusy(Servos.Door2())) {
            jamTimer.resetTimer();
        } else if (jamTimer.getElapsedTimeSeconds() > Timers.Intake.JAMMED_TIME) {
            // JAM DETECTED
            jammed = true;
            hasBall = false;
            currentStep = IntakeStep.PREPARE_DOORS;
            jamTimer.resetTimer();
        }
    }

    public void HandleIntakeSpin() {
        if(spinIntake && !jammed) Motors.Intake().setPower(1);
        else if (!spinIntake && !jammed) Motors.Intake().setPower(0);
        else Motors.Intake().setPower(-1);
    }

    public void update() {
        checkJammed();
        selectLauncherIfNeeded();

        if (!jammed) {
            if (getFirstEmptyLauncher() != null && !hasBall) {
                rightDistance = (DistanceSensors.Right().getDistance(DistanceUnit.INCH));
                distance = rightDistance;

                if (distance < BALL_DETECT_DISTANCE) {
                    hasBall = true;
                    intakeTimer.resetTimer();
                }
            }
        }

        intakeTime = intakeTimer.getElapsedTimeSeconds();
        intake();
    }

    public void spinIntake(boolean value) {
        spinIntake = value;
    }

    public boolean isIntakeSpinning() {
        return spinIntake;
    }

    public void showTelemetry() {
        Debug.INSTANCE.addData("Current Step", currentStep);
        Debug.INSTANCE.addData("Current Launcher", activeLauncher == null ? "NULL" : activeLauncher);
        Debug.INSTANCE.addData("Has Ball", hasBall);
        Debug.INSTANCE.addData("Intake Spinning", spinIntake);
        Debug.INSTANCE.addData("Distance Sensor Right", distance);
        Debug.INSTANCE.addData("Jammed", jammed);
    }
}
