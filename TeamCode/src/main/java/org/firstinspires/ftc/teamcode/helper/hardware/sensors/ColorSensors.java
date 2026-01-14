package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.constants.Colors;
import org.firstinspires.ftc.teamcode.constants.enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.helper.color.ColorHSV;
import org.firstinspires.ftc.teamcode.helper.color.ColorHelper;
import org.firstinspires.ftc.teamcode.helper.color.ColorRGB;
import org.firstinspires.ftc.teamcode.helper.general.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class ColorSensors {
    private static ColorSensor launcher1; public static ColorSensor Launcher1() { return launcher1; }
    private static ColorSensor launcher2; public static ColorSensor Launcher2() { return launcher2; }
    private static ColorSensor launcher3; public static ColorSensor Launcher3() { return launcher3; }

    private static ColorSensor[] allColorSensors; public static ColorSensor[] AllColorSensors() { return allColorSensors; }

    public static void init() {

        launcher1 = null;
        launcher2 = null;
        launcher3 = null;
        allColorSensors = null;

        try {
            getHardware(ActiveOpMode.hardwareMap());
            setAllColorSensors();
            activateLED();
        } catch (Exception ex) {
        }
    }

    private static void getHardware(HardwareMap hardwareMap) {
        launcher1 = hardwareMap.tryGet(ColorSensor.class, "launcher1Color");
        launcher2 = hardwareMap.tryGet(ColorSensor.class, "launcher2Color");
        launcher3 = hardwareMap.tryGet(ColorSensor.class, "launcher3Color");
    }

    private static void setAllColorSensors()
    {
        allColorSensors = new ColorSensor[]{launcher3, launcher2, launcher1};
    }

    private static void activateLED() {
        launcher1.enableLed(true);
        launcher2.enableLed(true);
        launcher3.enableLed(true);
    }

    private static boolean isGreen(ColorSensor colorSensor) {
        ColorRGB color = new ColorRGB(colorSensor.red(), colorSensor.green(), colorSensor.blue());
        ColorHSV hsvColor = ColorHelper.fromRGB(color);
        return ColorHelper.inHue(hsvColor.hue,
                Colors.GREEN_MIN,
                Colors.GREEN_MAX
        ) && hsvColor.saturation > 45;
    }

    private static boolean isPurple(ColorSensor colorSensor) {
        ColorRGB color = new ColorRGB(colorSensor.red(), colorSensor.green(), colorSensor.blue());
        ColorHSV hsvColor = ColorHelper.fromRGB(color);
        return ColorHelper.inHue(hsvColor.hue,
                Colors.PURPLE_MIN,
                Colors.PURPLE_MAX
        );
    }

    public static ArtifactColor getColor(ColorSensor colorSensor) {
        if(isGreen(colorSensor)) return ArtifactColor.GREEN;
        else if(isPurple(colorSensor)) return ArtifactColor.PURPLE;
        else return ArtifactColor.EMPTY;
    }
}
