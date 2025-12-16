package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.constants.Colors;
import org.firstinspires.ftc.teamcode.helper.ColorHSV;
import org.firstinspires.ftc.teamcode.helper.ColorHelper;
import org.firstinspires.ftc.teamcode.helper.ColorRGB;
import org.firstinspires.ftc.teamcode.helper.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class ColorSensors {
    private static ColorSensor launcher1; public static ColorSensor Launcher1() { return launcher1; }
    private static ColorSensor launcher2; public static ColorSensor Launcher2() { return launcher2; }
    private static ColorSensor launcher3; public static ColorSensor Launcher3() { return launcher3; }

    private static ColorSensor[] allColorSensors; public static ColorSensor[] AllColorSensors() { return allColorSensors; }

    public static void init() {
        try {
            getHardware(ActiveOpMode.hardwareMap());
            setAllColorSensors();
            activateLED();
        } catch (Exception ex) {
            Debug.INSTANCE.addData("ERROR", ex.getMessage());
            Debug.INSTANCE.update();
        }
    }

    private static void getHardware(HardwareMap hardwareMap) {
        launcher1 = hardwareMap.tryGet(ColorSensor.class, "launcher1");
        launcher2 = hardwareMap.tryGet(ColorSensor.class, "launcher2");
        launcher3 = hardwareMap.tryGet(ColorSensor.class, "launcher3");
    }

    private static void setAllColorSensors()
    {
        allColorSensors = new ColorSensor[]{launcher1, launcher2, launcher3};
    }

    private static void activateLED() {
        launcher1.enableLed(true);
        launcher2.enableLed(true);
        launcher3.enableLed(true);
    }

    public static boolean isGreen(ColorSensor colorSensor) {
        ColorRGB color = new ColorRGB(colorSensor.red(), colorSensor.green(), colorSensor.blue());
        ColorHSV hsvColor = ColorHelper.fromRGB(color);
        return ColorHelper.inHue(hsvColor.hue,
                Colors.GREEN_MIN,
                Colors.GREEN_MAX
        );
    }

    public static boolean isPurple(ColorSensor colorSensor) {
        ColorRGB color = new ColorRGB(colorSensor.red(), colorSensor.green(), colorSensor.blue());
        ColorHSV hsvColor = ColorHelper.fromRGB(color);
        return ColorHelper.inHue(hsvColor.hue,
                Colors.PURPLE_MIN,
                Colors.PURPLE_MAX
        );
    }
}
