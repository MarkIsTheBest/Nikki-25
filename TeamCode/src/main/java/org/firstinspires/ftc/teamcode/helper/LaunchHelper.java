package org.firstinspires.ftc.teamcode.helper;

import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.*;
import static org.firstinspires.ftc.teamcode.constants.Timers.Launch.LAUNCH_DELAY;
import com.pedropathing.util.Timer;
import org.firstinspires.ftc.teamcode.constants.enums.*;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.MathHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;
import java.util.LinkedList;
import java.util.Queue;

public class LaunchHelper {
    public static LaunchHelper INSTANCE;
    private final Timer launchTimer = new Timer();
    private LaunchStep currentStep = LaunchStep.SPIN_UP;
    private boolean isLaunching = false;
    private double targetRPM = 3000;
    private Motif currentMotif = Motif.GPP;
    private final Queue<Launcher> executionQueue = new LinkedList<>();

    public void update() {
        if (isLaunching) runStateMachine();
    }

    public void startLaunchSequence() {
        if (isLaunching) return;

        executionQueue.clear();
        int greenGoal = (currentMotif == Motif.GPP) ? 0 : (currentMotif == Motif.PGP ? 1 : 2);

        for (int i = 0; i < 3; i++) {
            if (Launchers.INSTANCE.getFilledLaunchers()[i]) {
                ArtifactColor color = Launchers.INSTANCE.getLauncherColorArray()[i];
                if ((color == ArtifactColor.GREEN && i == greenGoal) ||
                        (color == ArtifactColor.PURPLE && i != greenGoal)) {
                    executionQueue.add(Launcher.values()[i]);
                }
            }
        }

        if (!executionQueue.isEmpty()) {
            isLaunching = true;
            changeStep(LaunchStep.SPIN_UP);
        }
    }

    private void changeStep(LaunchStep next) {
        currentStep = next;
        launchTimer.resetTimer();
    }

    private void runStateMachine() {
        switch (currentStep) {
            case SPIN_UP:
                MotorHelper.setRPM(Motors.Launchers(), targetRPM);
                changeStep(LaunchStep.LAUNCH);
                break;

            case LAUNCH:
                boolean rpmReady = MathHelper.inInterval(MotorHelper.getCurrentRPM(Motors.Launchers()[0]), targetRPM - 100, targetRPM + 100);
                if (rpmReady && launchTimer.getElapsedTimeSeconds() > LAUNCH_DELAY) {
                    if (!executionQueue.isEmpty()) {
                        fire(executionQueue.poll());
                        launchTimer.resetTimer(); // Re-wait delay for next shot
                    } else {
                        changeStep(LaunchStep.RESET);
                    }
                }
                break;

            case RESET:
                MotorHelper.setRPM(Motors.Launchers(), 0);
                Launchers.INSTANCE.clearAllLaunchers();
                Servos.setPosition(Servos.Holder1(), H_PREPARE);
                Servos.setPosition(Servos.Holder2(), H_PREPARE);
                Servos.setPosition(Servos.Holder3(), H_PREPARE);
                isLaunching = false;
                break;
        }
    }

    private void fire(Launcher l) {
        if (l == Launcher.LEFT) Servos.setPosition(Servos.Holder1(), H_LAUNCH);
        if (l == Launcher.CENTER) Servos.setPosition(Servos.Holder2(), H_LAUNCH);
        if (l == Launcher.RIGHT) Servos.setPosition(Servos.Holder3(), H_LAUNCH);
    }

    public void showTelemetry() { Debug.INSTANCE.addData("Launch Step", currentStep); }
}