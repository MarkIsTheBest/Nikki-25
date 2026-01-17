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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LaunchHelper {

    private final Debug debug;
    private final Launchers launchers;
    private final Servos servos;

    private LaunchStep currentStep = LaunchStep.SPIN_UP;
    private Timer launchTimer = new Timer();
    private boolean isLaunching = false;

    private double targetRPM = 2000;
    private final double RPM_TOLERANCE = 150; // Increased tolerance for reliability
    private Motif currentMotif = Motif.GPP;
    private Queue<Launcher> executionQueue = new LinkedList<>();

    public LaunchHelper(Debug debug, Launchers launchers, Servos servos) {
        this.debug = debug;
        this.launchers = launchers;
        this.servos = servos;
    }

    public void update() {
        if (isLaunching) {
            runLaunchStateMachine();
        }
    }

    public void startLaunchSequence() {
        if (!isLaunching) {
            generateExecutionQueue();
            if (executionQueue.isEmpty()) return;

            isLaunching = true;
            changeStep(LaunchStep.SPIN_UP);
        }
    }

    /**
     * SMART QUEUE LOGIC:
     * 1. Tries to find balls that match the Motif sequence.
     * 2. If it can't find a match, it fills the rest of the queue Left-to-Right.
     */
    private void generateExecutionQueue() {
        executionQueue.clear();

        // Track which launchers are available (0=Left, 1=Center, 2=Right)
        List<Integer> availableIndices = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (launchers.getFilledLaunchers()[i]) availableIndices.add(i);
        }

        // Determine the color sequence we WANT based on the Motif
        ArtifactColor[] desiredSequence = getMotifSequence();

        // 1. Try to fulfill the Motif sequence using available balls
        for (ArtifactColor desiredColor : desiredSequence) {
            int matchIndex = -1;
            for (int availableIdx : availableIndices) {
                if (launchers.getLauncherColorArray()[availableIdx] == desiredColor) {
                    matchIndex = availableIdx;
                    break;
                }
            }

            if (matchIndex != -1) {
                executionQueue.add(Launcher.values()[matchIndex]);
                availableIndices.remove(Integer.valueOf(matchIndex)); // Use this ball, remove from available
            }
        }

        // 2. FALLBACK: Any balls left that didn't fit the motif? Add them Left-to-Right.
        for (int remainingIdx : availableIndices) {
            executionQueue.add(Launcher.values()[remainingIdx]);
        }
    }

    private ArtifactColor[] getMotifSequence() {
        switch (currentMotif) {
            case GPP: return new ArtifactColor[]{ArtifactColor.GREEN, ArtifactColor.PURPLE, ArtifactColor.PURPLE};
            case PGP: return new ArtifactColor[]{ArtifactColor.PURPLE, ArtifactColor.GREEN, ArtifactColor.PURPLE};
            case PPG: return new ArtifactColor[]{ArtifactColor.PURPLE, ArtifactColor.PURPLE, ArtifactColor.GREEN};
            default:  return new ArtifactColor[]{};
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
                servos.setPosition(servos.Door1(), C_D1_PREPARE);
                servos.setPosition(servos.Door2(), C_D2_PREPARE);

                double currentRPM = MotorHelper.getCurrentRPM(Motors.Launchers()[1]);
                boolean isSpeedCorrect = MathHelper.inInterval(currentRPM, targetRPM - RPM_TOLERANCE, targetRPM + RPM_TOLERANCE);

                // Safety: If it takes > 2s to spin up, just move to launch anyway
                if (isSpeedCorrect || launchTimer.getElapsedTimeSeconds() > 2.0) {
                    changeStep(LaunchStep.LAUNCH);
                }
                break;

            case LAUNCH:
                if (!executionQueue.isEmpty()) {
                    Launcher toFire = executionQueue.poll();
                    fireLauncher(toFire);

                    // Crucial: Clear the specific launcher data so the intake knows it's empty
                    launchers.clearLauncher(toFire.ordinal());

                    // Move to WAIT so the servo has time to move before we check the next ball
                    changeStep(LaunchStep.WAIT_FOR_RELOAD);
                } else {
                    changeStep(LaunchStep.RESET);
                }
                break;

            case WAIT_FOR_RELOAD:
                // Wait for the ball to actually leave the robot
                if (launchTimer.getElapsedTimeSeconds() > LAUNCH_DELAY) {
                    if (!executionQueue.isEmpty()) {
                        // Go back to SPIN_UP to ensure the wheels recover speed after the last shot
                        changeStep(LaunchStep.SPIN_UP);
                    } else {
                        changeStep(LaunchStep.RESET);
                    }
                }
                break;

            case RESET:
                MotorHelper.setRPM(Motors.Launchers(), 0);
                servos.setPosition(servos.Holder1(), H_PREPARE);
                servos.setPosition(servos.Holder2(), H_PREPARE);
                servos.setPosition(servos.Holder3(), H_PREPARE);

                if(launchTimer.getElapsedTimeSeconds() > 0.5) {

                    launchers.clearAllLaunchers();

                    isLaunching = false;
                    currentStep = LaunchStep.SPIN_UP;
                }
                break;
        }
    }

    private void fireLauncher(Launcher launcher) {
        switch (launcher) {
            case LEFT:   servos.setPosition(servos.Holder1(), H_LAUNCH); break;
            case CENTER: servos.setPosition(servos.Holder2(), H_LAUNCH); break;
            case RIGHT:  servos.setPosition(servos.Holder3(), H_LAUNCH); break;
        }
    }

    public void setMotif(Motif motif) { this.currentMotif = motif; }
    public boolean IsLaunching() { return isLaunching; }
}