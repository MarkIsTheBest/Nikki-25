package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.constants.Colors;
import org.firstinspires.ftc.teamcode.constants.enums.ArtifactColor;

public class ColorSensors {
    public static ColorSensor[] launchers = new ColorSensor[3];
    private static final float[] hsvValues = new float[3]; // Reusable array to prevent GC spikes

    public static void init(HardwareMap hwMap) {
        launchers[0] = hwMap.tryGet(ColorSensor.class, "launcher1Color");
        launchers[1] = hwMap.tryGet(ColorSensor.class, "launcher2Color");
        launchers[2] = hwMap.tryGet(ColorSensor.class, "launcher3Color");

        for (ColorSensor s : launchers) {
            if (s != null) s.enableLed(true);
        }
    }

    public static ArtifactColor getColor(int index) {
        ColorSensor sensor = launchers[index];
        if (sensor == null) return ArtifactColor.EMPTY;

        // Optimized conversion without creating new objects
        Color.RGBToHSV(sensor.red() * 8, sensor.green() * 8, sensor.blue() * 8, hsvValues);
        float hue = hsvValues[0];
        float sat = hsvValues[1] * 100;

        if (hue > Colors.GREEN_MIN.hue && hue < Colors.GREEN_MAX.hue && sat > 45)
            return ArtifactColor.GREEN;
        if (hue > Colors.PURPLE_MIN.hue && hue < Colors.PURPLE_MAX.hue)
            return ArtifactColor.PURPLE;

        return ArtifactColor.EMPTY;
    }
}