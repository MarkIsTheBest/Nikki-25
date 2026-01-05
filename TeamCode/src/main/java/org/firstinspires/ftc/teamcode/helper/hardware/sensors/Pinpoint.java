package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorGoBildaPinpoint;
import org.firstinspires.ftc.teamcode.helper.general.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class Pinpoint {
    private static SensorGoBildaPinpoint pinpoint;

    public static SensorGoBildaPinpoint PinpointSensor() {
        return pinpoint;
    }

    public static void init() {
        try {
            getHardware(ActiveOpMode.hardwareMap());
        } catch (Exception ex) {
            Debug.INSTANCE.addData("ERROR", ex.getMessage());
            Debug.INSTANCE.update();
        }
    }

    private static void getHardware(HardwareMap hardwareMap) {
        pinpoint = hardwareMap.tryGet(SensorGoBildaPinpoint.class, "pinpoint");
    }
}