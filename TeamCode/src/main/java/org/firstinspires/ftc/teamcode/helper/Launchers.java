package org.firstinspires.ftc.teamcode.helper;

import static org.firstinspires.ftc.teamcode.constants.Positions.Servo.H_PREPARE;

import org.firstinspires.ftc.teamcode.constants.enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LEDs;

import java.util.Arrays;

public class Launchers {

    public static Launchers INSTANCE;

    private boolean[] filledLaunchers = {false,false,false};
    private ArtifactColor[] launcherColor = {ArtifactColor.EMPTY, ArtifactColor.EMPTY, ArtifactColor.EMPTY};

    public ArtifactColor[] getLauncherColorArray() {
        return launcherColor;
    }

    public boolean[] getFilledLaunchers() {
        return filledLaunchers;
    }

    public String ToString() {
        return Arrays.toString(filledLaunchers);
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
    }

    public void clearLauncher(int index) {
        getFilledLaunchers()[index] = false;
    }

    public void HandleLEDS() {
        if(getLauncherColorArray()[0] == ArtifactColor.GREEN) LEDs.setGreen(LEDs.LauncherLeft());
        if(getLauncherColorArray()[0] == ArtifactColor.PURPLE) LEDs.setPurple(LEDs.LauncherLeft());
        if(getLauncherColorArray()[0] == ArtifactColor.EMPTY) LEDs.setEmpty(LEDs.LauncherLeft());

        if(getLauncherColorArray()[1] == ArtifactColor.GREEN) LEDs.setGreen(LEDs.LauncherCenter());
        if(getLauncherColorArray()[1] == ArtifactColor.PURPLE) LEDs.setPurple(LEDs.LauncherCenter());
        if(getLauncherColorArray()[1] == ArtifactColor.EMPTY) LEDs.setEmpty(LEDs.LauncherCenter());

        if(getLauncherColorArray()[2] == ArtifactColor.GREEN) LEDs.setGreen(LEDs.LauncherRight());
        if(getLauncherColorArray()[2] == ArtifactColor.PURPLE) LEDs.setPurple(LEDs.LauncherRight());
        if(getLauncherColorArray()[2] == ArtifactColor.EMPTY) LEDs.setEmpty(LEDs.LauncherRight());
    }

    public void showTelemetry() {
        Debug.INSTANCE.addData("Filled Launchers", Arrays.toString(filledLaunchers));
        Debug.INSTANCE.addData("Filled Launchers", Arrays.toString(launcherColor));
    }
}
