package org.firstinspires.ftc.teamcode.helper;

import org.firstinspires.ftc.teamcode.constants.enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.constants.enums.Launcher;
import java.util.Arrays;

public class Launchers {
    public static Launchers INSTANCE;
    private final boolean[] filled = {false, false, false};
    private final ArtifactColor[] colors = {ArtifactColor.EMPTY, ArtifactColor.EMPTY, ArtifactColor.EMPTY};

    public Launcher getFirstEmptyLauncher() {
        for (int i = 0; i < 3; i++) if (!filled[i]) return Launcher.values()[i];
        return null;
    }

    public void setFilledLaunchers(int i, boolean val) { filled[i] = val; }
    public void setLauncherColor(int i, ArtifactColor c) { colors[i] = c; }
    public boolean[] getFilledLaunchers() { return filled; }
    public ArtifactColor[] getLauncherColorArray() { return colors; }

    public void clearAllLaunchers() {
        Arrays.fill(filled, false);
        Arrays.fill(colors, ArtifactColor.EMPTY);
    }

    public void HandleLEDS() { /* LED implementation */ }
}