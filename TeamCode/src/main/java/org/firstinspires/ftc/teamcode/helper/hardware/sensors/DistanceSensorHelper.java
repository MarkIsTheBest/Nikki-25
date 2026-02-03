package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import dev.nextftc.ftc.ActiveOpMode;

public class DistanceSensorHelper {
    private Rev2mDistanceSensor outtake; public Rev2mDistanceSensor Outtake() { return outtake; }

    private Rev2mDistanceSensor[] allDistanceSensors; public Rev2mDistanceSensor[] AllDistanceSensors() { return allDistanceSensors; }

    public DistanceSensorHelper() {
        init();
    }

    public void init() {
        getHardware(ActiveOpMode.hardwareMap());
        setAllDistanceSensors();
    }

    private void getHardware(HardwareMap hardwareMap) {
        outtake = hardwareMap.tryGet(Rev2mDistanceSensor.class, "outtake");
    }

    private void setAllDistanceSensors()
    {
        allDistanceSensors = new Rev2mDistanceSensor[]{outtake};
    }
}
