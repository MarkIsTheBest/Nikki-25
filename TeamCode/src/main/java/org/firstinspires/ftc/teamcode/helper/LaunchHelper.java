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
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class LaunchHelper {
    public final static LaunchHelper INSTANCE = new LaunchHelper();

    private Launchers launcherHelper = Launchers.INSTANCE;
    private LaunchStep currentStep = LaunchStep.SPIN_UP;
    private Timer launchTimer = new Timer();

    private boolean launch = false;
    private double targetRPM = 6000;
    private final double RPM_TOLERANCE = 100;

    private Motif currentMotif = Motif.GPP;
    private int nrOfLaunches = 0;

    public void update() {
        if(launch) launch();
    }

    public double GetTargetRPM() { return targetRPM; }
    public void SetTargetRPM(double targetRPM) {
        this.targetRPM = targetRPM;
    }

    public void setMotif(Motif motif) {
        this.currentMotif = motif;
    }

    private void changeStep(LaunchStep newStep) {
        currentStep = newStep;
        launchTimer.resetTimer();
    }

    private Queue<Launcher> launcherQueue() {

        Queue<Launcher> queue = new LinkedList<>();
        int greenIndex;

        switch (currentMotif) {
            case GPP:
                greenIndex = 0;
                break;
            case PGP:
                greenIndex = 1;
                break;
            case PPG:
                greenIndex = 2;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentMotif);
        }

        for (int i = 0; i < 3; i++) {
            if (launcherHelper.getFilledLaunchers()[i]) {
                if (launcherHelper.getLauncherColorArray()[i] == ArtifactColor.GREEN && i == greenIndex) {
                    queue.add(Launcher.values()[i]);
                    nrOfLaunches++;
                    continue;
                }
                if (launcherHelper.getLauncherColorArray()[i] == ArtifactColor.PURPLE && i != greenIndex) {
                    queue.add(Launcher.values()[i]);
                    nrOfLaunches++;
                }
            }
        }

        return queue;
    }

    public void launch() {
        switch (currentStep) {
            case SPIN_UP:
                MotorHelper.setRPM(Motors.Launchers(), targetRPM);
                changeStep(LaunchStep.LAUNCH);
                break;

            case LAUNCH:
                if(MathHelper.inInterval(
                        MotorHelper.getCurrentRPM(Motors.Launchers()[0]),
                        targetRPM - RPM_TOLERANCE,
                        targetRPM + RPM_TOLERANCE) && launchTimer.getElapsedTimeSeconds() > LAUNCH_DELAY) {

                    switch(Objects.requireNonNull(launcherQueue().poll())) {
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
                    nrOfLaunches--;
                    if(nrOfLaunches == 0) {
                        changeStep(LaunchStep.RESET);
                    }
                } changeStep(LaunchStep.LAUNCH);
                break;

            case RESET:
                MotorHelper.setRPM(Motors.Launchers(), 0);
                launcherHelper.clearAllLaunchers();
                Servos.setPosition(Servos.Holder1(), H_PREPARE);
                Servos.setPosition(Servos.Holder2(), H_PREPARE);
                Servos.setPosition(Servos.Holder3(), H_PREPARE);
                break;
        }
    }

    public void showTelemetry() {
        Debug.INSTANCE.addData("Current Step", currentStep);
        Debug.INSTANCE.addData("Current Motif", currentMotif);
        Debug.INSTANCE.addData("Next in Queue", launcherQueue().peek());
        Debug.INSTANCE.addData("Current RPM", MotorHelper.getCurrentRPM(Motors.Launchers()[0]));
    }
}
