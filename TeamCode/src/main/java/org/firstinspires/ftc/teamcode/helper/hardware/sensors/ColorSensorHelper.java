package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.constants.Colors;
import org.firstinspires.ftc.teamcode.constants.enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.helper.color.ColorHSV;
import org.firstinspires.ftc.teamcode.helper.color.ColorHelper;
import org.firstinspires.ftc.teamcode.helper.color.ColorRGB;

import dev.nextftc.ftc.ActiveOpMode;

public class ColorSensorHelper {
    private ColorSensor launcher1; public ColorSensor Launcher1() { return launcher1; }
    private ColorSensor launcher2; public ColorSensor Launcher2() { return launcher2; }
    private ColorSensor launcher3; public ColorSensor Launcher3() { return launcher3; }

    private ColorSensor[] allColorSensors; public ColorSensor[] AllColorSensors() { return allColorSensors; }

    public ColorSensorHelper() {
        init();
    }

    public void init() {
        getHardware(ActiveOpMode.hardwareMap());
        setAllColorSensors();
        activateLED();
    }

    private void getHardware(HardwareMap hardwareMap) {
        launcher1 = hardwareMap.tryGet(ColorSensor.class, "launcher1Color");
        launcher2 = hardwareMap.tryGet(ColorSensor.class, "launcher2Color");
        launcher3 = hardwareMap.tryGet(ColorSensor.class, "launcher3Color");
    }

    private void setAllColorSensors()
    {
        allColorSensors = new ColorSensor[]{launcher3, launcher2, launcher1};
    }

    private void activateLED() {
        launcher1.enableLed(true);
        launcher2.enableLed(true);
        launcher3.enableLed(true);
    }

    private boolean isGreen(ColorSensor colorSensor) {
        ColorRGB color = new ColorRGB(colorSensor.red(), colorSensor.green(), colorSensor.blue());
        ColorHSV hsvColor = ColorHelper.fromRGB(color);
        return ColorHelper.inHue(hsvColor.hue,
                Colors.GREEN_MIN,
                Colors.GREEN_MAX
        ) && hsvColor.saturation > 45;
    }

    private boolean isPurple(ColorSensor colorSensor) {
        ColorRGB color = new ColorRGB(colorSensor.red(), colorSensor.green(), colorSensor.blue());
        ColorHSV hsvColor = ColorHelper.fromRGB(color);
        return ColorHelper.inHue(hsvColor.hue,
                Colors.PURPLE_MIN,
                Colors.PURPLE_MAX
        );
    }

    public ArtifactColor getColor(ColorSensor colorSensor) {
        if(isGreen(colorSensor)) return ArtifactColor.GREEN;
        else if(isPurple(colorSensor)) return ArtifactColor.PURPLE;
        else return ArtifactColor.EMPTY;
    }
}
