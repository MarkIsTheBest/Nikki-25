package org.firstinspires.ftc.teamcode.helper;

import org.firstinspires.ftc.teamcode.constants.enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LEDs;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Arrays;

public class Launchers {

    private Debug debug;

    public Launchers(Debug debug) {
        this.debug = debug;
    }

    private boolean[] filledLaunchers = {false,false,false};
    private ArtifactColor[] launcherColor = {ArtifactColor.EMPTY, ArtifactColor.EMPTY, ArtifactColor.EMPTY};
    private ArtifactColor[] lastLedState = {null, null, null}; // Cache for LEDs

    public ArtifactColor[] getLauncherColorArray() {
        return launcherColor;
    }

    public boolean[] getFilledLaunchers() {
        return filledLaunchers;
    }

    public void setLauncherColor(int index, ArtifactColor color) {
        launcherColor[index] = color;
    }

    public void setFilledLaunchers(int index, boolean value) {
        filledLaunchers[index] = value;
    }

    public void clearAllLaunchers() {
        Arrays.fill(getFilledLaunchers(), false);
        Arrays.fill(getLauncherColorArray(), ArtifactColor.EMPTY);
        HandleLEDS(); // Force update
    }

    public void clearLauncher(int index) {
        getFilledLaunchers()[index] = false;
    }

    public void HandleLEDS() {
        updateLedForLauncher(0, LEDs.LauncherLeft());
        updateLedForLauncher(1, LEDs.LauncherCenter());
        updateLedForLauncher(2, LEDs.LauncherRight());
    }

    private void updateLedForLauncher(int index, Servo ledServo) {
        ArtifactColor color = launcherColor[index];
        // Optimization: Don't set servo position if color hasn't changed
        if (color != lastLedState[index]) {
            if (color == ArtifactColor.GREEN) LEDs.setGreen(ledServo);
            else if (color == ArtifactColor.PURPLE) LEDs.setPurple(ledServo);
            else LEDs.setEmpty(ledServo);

            lastLedState[index] = color;
        }
    }

    public void showTelemetry() {
        debug.addData("Filled", Arrays.toString(filledLaunchers));
    }
}