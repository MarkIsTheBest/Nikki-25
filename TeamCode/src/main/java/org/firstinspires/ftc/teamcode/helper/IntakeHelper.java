package org.firstinspires.ftc.teamcode.helper;

import static org.firstinspires.ftc.teamcode.constants.Distances.Intake.BALL_DETECT_DISTANCE;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.*;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.constants.Timers;
import org.firstinspires.ftc.teamcode.constants.enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.constants.enums.IntakeStep;
import org.firstinspires.ftc.teamcode.constants.enums.Launcher;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.ColorSensors;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.DistanceSensors;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LEDs;

public class IntakeHelper {
    public static IntakeHelper INSTANCE;

    private final Timer intakeTimer = new Timer();
    private final Timer sensorTimer = new Timer();
    private final Timer jamTimer = new Timer();

    private IntakeStep currentStep = IntakeStep.PREPARE_DOORS;
    private Launcher activeLauncher = null;
    private boolean hasBall = false;
    private boolean spinIntake = false;
    private boolean jammed = false;
    private double lastIntakePower = 0;

    public IntakeHelper() {
        initPositions();
    }

    private void initPositions() {
        Servos.setPosition(Servos.Holder1(), H_PREPARE);
        Servos.setPosition(Servos.Holder2(), H_PREPARE);
        Servos.setPosition(Servos.Holder3(), H_PREPARE);
        Servos.setPosition(Servos.Door1(), L_D1_PREPARE);
        Servos.setPosition(Servos.Door2(), L_D2_PREPARE);
    }

    private void changeStep(IntakeStep newStep) {
        currentStep = newStep;
        intakeTimer.resetTimer();

        // Execute servo commands ONCE upon entering state
        if (activeLauncher != null) {
            handleStateEntry();
        }
    }

    private void handleStateEntry() {
        switch (currentStep) {
            case PREPARE_DOORS:
                applyDoorPositions(activeLauncher);
                break;
            case CLOSE:
                if (activeLauncher == Launcher.LEFT) Servos.setPosition(Servos.Holder1(), H_CLOSE);
                if (activeLauncher == Launcher.CENTER) Servos.setPosition(Servos.Holder2(), H_CLOSE);
                if (activeLauncher == Launcher.RIGHT) Servos.setPosition(Servos.Holder3(), H_CLOSE);
                break;
        }
    }

    public void update() {
        handleIntakeSpin();
        checkJammed();

        // Throttled Sensor Reading (Check every 50ms instead of every loop)
        if (sensorTimer.getElapsedTime() > 50 && !hasBall && !jammed) {
            if (Launchers.INSTANCE.getFirstEmptyLauncher() != null) {
                double d = Math.min(DistanceSensors.getDistance(DistanceSensors.Left()),
                        DistanceSensors.getDistance(DistanceSensors.Right()));
                if (d < BALL_DETECT_DISTANCE) {
                    hasBall = true;
                    selectLauncher();
                }
            }
            sensorTimer.resetTimer();
        }

        runStateMachine();
    }

    private void runStateMachine() {
        if (activeLauncher == null) return;

        switch (currentStep) {
            case PREPARE_DOORS:
                if (hasBall) changeStep(IntakeStep.PARTIAL);
                break;
            case PARTIAL:
                if (intakeTimer.getElapsedTimeSeconds() > Timers.Intake.PARTIAL_DELAY) {
                    if (activeLauncher == Launcher.LEFT) Servos.setPosition(Servos.Door1(), L_D1_PARTIAL);
                    if (activeLauncher == Launcher.RIGHT) Servos.setPosition(Servos.Door2(), R_D2_PARTIAL);
                    changeStep(IntakeStep.CLOSE);
                }
                break;
            case CLOSE:
                if (intakeTimer.getElapsedTimeSeconds() > Timers.Intake.PARTIAL_DELAY) {
                    finalizeIntake();
                }
                break;
        }
    }

    private void selectLauncher() {
        activeLauncher = Launchers.INSTANCE.getFirstEmptyLauncher();
        if (activeLauncher != null) changeStep(IntakeStep.PREPARE_DOORS);
    }

    private void finalizeIntake() {
        ArtifactColor color = ColorSensors.getColor(ColorSensors.AllColorSensors()[activeLauncher.index]);
        if (color != ArtifactColor.EMPTY) {
            Launchers.INSTANCE.setFilledLaunchers(activeLauncher.index, true);
            Launchers.INSTANCE.setLauncherColor(activeLauncher.index, color);
            Launchers.INSTANCE.HandleLEDS();
        }
        activeLauncher = null;
        hasBall = false;
        changeStep(IntakeStep.PREPARE_DOORS);
    }

    private void handleIntakeSpin() {
        double power = jammed ? -1 : (spinIntake ? 1 : 0);
        if (Math.abs(power - lastIntakePower) > 0.05) {
            Motors.Intake().setPower(power);
            lastIntakePower = power;
        }
    }

    private void checkJammed() {
        if (jammed && jamTimer.getElapsedTimeSeconds() > Timers.Intake.UNJAM_TIME) {
            jammed = false;
        }
        // Logic for detecting jam would go here based on motor current or stalled sensors
    }

    private void applyDoorPositions(Launcher launcher) {
        switch (launcher) {
            case LEFT: Servos.setPosition(Servos.Door1(), L_D1_PREPARE); Servos.setPosition(Servos.Door2(), L_D2_PREPARE); break;
            case CENTER: Servos.setPosition(Servos.Door1(), C_D1_PREPARE); Servos.setPosition(Servos.Door2(), C_D2_PREPARE); break;
            case RIGHT: Servos.setPosition(Servos.Door1(), R_D1_PREPARE); Servos.setPosition(Servos.Door2(), R_D2_PREPARE); break;
        }
    }

    public void spinIntake(boolean val) { spinIntake = val; }
    public boolean isIntakeSpinning() { return spinIntake; }
    public void showTelemetry() { Debug.INSTANCE.addData("Intake Step", currentStep); }
}