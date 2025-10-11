package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorGoBildaPinpoint;
import org.firstinspires.ftc.teamcode.constants.Configurables;
import org.firstinspires.ftc.teamcode.helper.ColorHelper;
import org.firstinspires.ftc.teamcode.helper.ColorRGB;
import org.firstinspires.ftc.teamcode.helper.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class ColorSensors {
    public static final ColorSensors INSTANCE = new ColorSensors();

    public ColorSensors() {
        init();
    }

    private ColorSensor launcher1; public ColorSensor Launcher1() { return launcher1; }
    private ColorSensor launcher2; public ColorSensor Launcher2() { return launcher2; }
    private ColorSensor launcher3; public ColorSensor Launcher3() { return launcher3; }

    private ColorSensor[] allColorSensors; public ColorSensor[] AllColorSensors() { return allColorSensors; }

    private void init() {
        try {
            getHardware(ActiveOpMode.hardwareMap());
            setAllColorSensors();
            activateLED();
        } catch (Exception ex) {
            Debug.INSTANCE.addData("ERROR", ex.getMessage());
            Debug.INSTANCE.update();
        }
    }

    private void getHardware(HardwareMap hardwareMap) {
        launcher1 = hardwareMap.tryGet(ColorSensor.class, "launcher1");
        launcher2 = hardwareMap.tryGet(ColorSensor.class, "launcher2");
        launcher3 = hardwareMap.tryGet(ColorSensor.class, "launcher3");
    }

    private void setAllColorSensors()
    {
        allColorSensors = new ColorSensor[]{launcher1, launcher2, launcher3};
    }

    private void activateLED() {
        launcher1.enableLed(true);
        launcher2.enableLed(true);
        launcher3.enableLed(true);
    }

    public boolean isGreen(ColorSensor colorSensor) {
        ColorRGB color = new ColorRGB(colorSensor.red(), colorSensor.green(), colorSensor.blue());
        return ColorHelper.inInterval(color,
                Configurables.Colors.GREEN_MIN,
                Configurables.Colors.GREEN_MAX
        );
    }

    public boolean isPurple(ColorSensor colorSensor) {
        ColorRGB color = new ColorRGB(colorSensor.red(), colorSensor.green(), colorSensor.blue());
        return ColorHelper.inInterval(color,
                Configurables.Colors.PURPLE_MIN,
                Configurables.Colors.PURPLE_MAX
        );
    }
}