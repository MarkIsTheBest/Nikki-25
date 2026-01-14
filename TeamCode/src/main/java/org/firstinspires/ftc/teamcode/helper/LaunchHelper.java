package org.firstinspires.ftc.teamcode.helper;

import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.*;
import static org.firstinspires.ftc.teamcode.constants.Timers.Launch.LAUNCH_DELAY;

import com.pedropathing.util.Timer;

import org.firstinspires.ftc.teamcode.constants.enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.constants.enums.LaunchStep;
import org.firstinspires.ftc.teamcode.constants.enums.Launcher;
import org.firstinspires.ftc.teamcode.constants.enums.Motif;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.MathHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;

import java.util.LinkedList;
import java.util.Queue;

public class LaunchHelper {
    public static LaunchHelper INSTANCE;

    private Launchers launcherHelper;
    private LaunchStep currentStep = LaunchStep.SPIN_UP;
    private Timer launchTimer = new Timer();

    private boolean isLaunching = false;
    private double targetRPM = 3500;
    private final double RPM_TOLERANCE = 100;

    private Motif currentMotif = Motif.GPP;

    private Queue<Launcher> executionQueue = new LinkedList<>();

    public LaunchHelper() {
        launcherHelper = Launchers.INSTANCE;
    }

    public void update() {
        if (isLaunching) {
            runLaunchStateMachine();
        }
    }

    public void startLaunchSequence() {
        if (!isLaunching) {
            generateExecutionQueue();

            if (executionQueue.isEmpty()) {
                isLaunching = false;
                return;
            }

            isLaunching = true;
            changeStep(LaunchStep.SPIN_UP);
        }
    }

    private void generateExecutionQueue() {
        executionQueue.clear();
        int greenIndex;

        switch (currentMotif) {
            case GPP: greenIndex = 0; break;
            case PGP: greenIndex = 1; break;
            case PPG: greenIndex = 2; break;
            default: greenIndex = -1; break;
        }

        for (int i = 0; i < 3; i++) {
            if (launcherHelper.getFilledLaunchers()[i]) {
                ArtifactColor color = launcherHelper.getLauncherColorArray()[i];
                Launcher l = Launcher.values()[i];

                if (color == ArtifactColor.GREEN && i == greenIndex) {
                    executionQueue.add(l);
                } else if (color == ArtifactColor.PURPLE && i != greenIndex) {
                    executionQueue.add(l);
                }
            }
        }
    }

    private void changeStep(LaunchStep newStep) {
        currentStep = newStep;
        launchTimer.resetTimer();
    }

    private void runLaunchStateMachine() {
        switch (currentStep) {
            case SPIN_UP:
                MotorHelper.setRPM(Motors.Launchers(), targetRPM);
                changeStep(LaunchStep.LAUNCH);
                break;

            case LAUNCH:
                // Optimization: getRPM is reading encoder.
                // Because of Bulk Reads in TeleOpPedro, this is now instant.
                double currentRPM = MotorHelper.getCurrentRPM(Motors.Launchers()[0]);
                boolean isSpeedCorrect = MathHelper.inInterval(currentRPM, targetRPM - RPM_TOLERANCE, targetRPM + RPM_TOLERANCE);
                boolean isDelayFinished = launchTimer.getElapsedTimeSeconds() > LAUNCH_DELAY;

                if (isSpeedCorrect && isDelayFinished) {
                    if (!executionQueue.isEmpty()) {
                        fireLauncher(executionQueue.poll());
                        launchTimer.resetTimer();
                    } else {
                        changeStep(LaunchStep.RESET);
                    }
                }
                break;

            case RESET:
                MotorHelper.setRPM(Motors.Launchers(), 0);
                launcherHelper.clearAllLaunchers();

                Servos.setPosition(Servos.Holder1(), H_PREPARE);
                Servos.setPosition(Servos.Holder2(), H_PREPARE);
                Servos.setPosition(Servos.Holder3(), H_PREPARE);

                isLaunching = false;
                break;
        }
    }

    private void fireLauncher(Launcher launcher) {
        switch (launcher) {
            case LEFT: Servos.setPosition(Servos.Holder1(), H_LAUNCH); break;
            case CENTER: Servos.setPosition(Servos.Holder2(), H_LAUNCH); break;
            case RIGHT: Servos.setPosition(Servos.Holder3(), H_LAUNCH); break;
        }
    }

    // Setters/Getters...
}