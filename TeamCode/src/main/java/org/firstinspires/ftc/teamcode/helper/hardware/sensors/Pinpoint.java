package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorGoBildaPinpoint;
import org.firstinspires.ftc.teamcode.helper.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class Pinpoint {
    public static final Pinpoint INSTANCE = new Pinpoint();

    public Pinpoint() {
        init();
    }

    private SensorGoBildaPinpoint pinpoint;

    public SensorGoBildaPinpoint PinpointSensor() {
        return pinpoint;
    }

    private void init() {
        try {
            getHardware(ActiveOpMode.hardwareMap());
        } catch (Exception ex) {
            Debug.INSTANCE.addData("ERROR", ex.getMessage());
            Debug.INSTANCE.update();
        }
    }

    private void getHardware(HardwareMap hardwareMap) {
        pinpoint = hardwareMap.tryGet(SensorGoBildaPinpoint.class, "pinpoint");
    }
}