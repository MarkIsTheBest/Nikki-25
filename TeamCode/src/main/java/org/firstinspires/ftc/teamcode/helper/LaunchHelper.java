package org.firstinspires.ftc.teamcode.helper;

import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.H_LAUNCH;
import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.H_PREPARE;
import static org.firstinspires.ftc.teamcode.constants.Timers.Launch.LAUNCH_DELAY;

import com.pedropathing.util.Timer;

import org.firstinspires.ftc.teamcode.constants.enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.constants.enums.LaunchStep;
import org.firstinspires.ftc.teamcode.constants.enums.Launcher;
import org.firstinspires.ftc.teamcode.constants.enums.Motif;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.MathHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
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
        initialize();
    }

    private void initialize() {
    }

    /**
     * Call this in your TeleOp loop
     */
    public void update() {
        if (isLaunching) {
            runLaunchStateMachine();
        }
    }

    public void startLaunchSequence() {
        if (!isLaunching) {
            isLaunching = true;
            generateExecutionQueue(); // Build the plan ONCE

            if (executionQueue.isEmpty()) {
                // Nothing to shoot, abort immediately
                isLaunching = false;
                return;
            }

            currentStep = LaunchStep.SPIN_UP;
            launchTimer.resetTimer();
        }
    }

    private void generateExecutionQueue() {
        executionQueue.clear();
        int greenIndex;

        switch (currentMotif) {
            case GPP: greenIndex = 0; break;
            case PGP: greenIndex = 1; break;
            case PPG: greenIndex = 2; break;
            default: greenIndex = -1; break; // Should not happen
        }

        // Iterate through launchers and decide order based on color rules
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
                // Move immediately to LAUNCH state logic
                changeStep(LaunchStep.LAUNCH);
                break;

            case LAUNCH:
                double currentRPM = MotorHelper.getCurrentRPM(Motors.Launchers()[0]);
                boolean isSpeedCorrect = MathHelper.inInterval(currentRPM, targetRPM - RPM_TOLERANCE, targetRPM + RPM_TOLERANCE);
                boolean isDelayFinished = launchTimer.getElapsedTimeSeconds() > LAUNCH_DELAY;

                if (isSpeedCorrect && isDelayFinished) {
                    if (!executionQueue.isEmpty()) {
                        fireLauncher(executionQueue.poll());

                        // Reset timer to ensure delay between THIS shot and the NEXT shot
                        launchTimer.resetTimer();
                    } else {
                        // Queue is empty, we are done
                        changeStep(LaunchStep.RESET);
                    }
                }
                break;

            case RESET:
                MotorHelper.setRPM(Motors.Launchers(), 0);

                // Clear data
                launcherHelper.clearAllLaunchers();

                // Reset Servos
                Servos.setPosition(Servos.Holder1(), H_PREPARE);
                Servos.setPosition(Servos.Holder2(), H_PREPARE);
                Servos.setPosition(Servos.Holder3(), H_PREPARE);

                isLaunching = false; // Sequence complete
                break;
        }
    }

    private void fireLauncher(Launcher launcher) {
        switch (launcher) {
            case LEFT:
                Servos.setPosition(Servos.Holder1(), H_LAUNCH);
                break;
            case CENTER:
                Servos.setPosition(Servos.Holder2(), H_LAUNCH);
                break;
            case RIGHT:
                Servos.setPosition(Servos.Holder3(), H_LAUNCH);
                break;
        }
    }

    public double GetTargetRPM() { return targetRPM; }

    public void SetTargetRPM(double targetRPM) {
        this.targetRPM = targetRPM;
    }

    public void setMotif(Motif motif) {
        this.currentMotif = motif;
    }

    public void showTelemetry() {
        Debug.INSTANCE.addData("Launch Active", isLaunching);
        Debug.INSTANCE.addData("Current Step", currentStep);
        Debug.INSTANCE.addData("Current Motif", currentMotif);
        Debug.INSTANCE.addData("Shots Remaining", executionQueue.size());

        Debug.INSTANCE.addData("Launcher1 RPM", MotorHelper.getCurrentRPM(Motors.Launcher1()));
        Debug.INSTANCE.addData("Launcher2 RPM", MotorHelper.getCurrentRPM(Motors.Launcher2()));

        Launcher next = executionQueue.peek();
        Debug.INSTANCE.addData("Next in Queue", next != null ? next : "None");

        Debug.INSTANCE.addData("Target RPM", targetRPM);
    }
}