package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import dev.nextftc.ftc.ActiveOpMode;

public class DistanceSensorHelper {
    private Rev2mDistanceSensor left; public Rev2mDistanceSensor Left() { return left; }
    private Rev2mDistanceSensor right; public Rev2mDistanceSensor Right() { return right; }

    private Rev2mDistanceSensor[] allDistanceSensors; public Rev2mDistanceSensor[] AllDistanceSensors() { return allDistanceSensors; }

    public DistanceSensorHelper() {
        init();
    }

    public void init() {
        getHardware(ActiveOpMode.hardwareMap());
        setAllDistanceSensors();
    }

    public double getDistance(Rev2mDistanceSensor distanceSensor, DistanceUnit DU) {
        return distanceSensor.getDistance(DU);
    }

    public double getDistance(Rev2mDistanceSensor distanceSensor) {
        return distanceSensor.getDistance(DistanceUnit.INCH);
    }

    public double Min(Rev2mDistanceSensor distanceSensor1,
                             Rev2mDistanceSensor distanceSensor2,
                             DistanceUnit DU) {
            return Math.min(distanceSensor1.getDistance(DU), distanceSensor2.getDistance(DU));
    }

    public double Max(Rev2mDistanceSensor distanceSensor1,
                             Rev2mDistanceSensor distanceSensor2,
                             DistanceUnit DU) {
        return Math.max(distanceSensor1.getDistance(DU), distanceSensor2.getDistance(DU));
    }

    private void getHardware(HardwareMap hardwareMap) {
        left = hardwareMap.tryGet(Rev2mDistanceSensor.class, "distanceLeft");
        right = hardwareMap.tryGet(Rev2mDistanceSensor.class, "distanceRight");
    }

    private void setAllDistanceSensors()
    {
        allDistanceSensors = new Rev2mDistanceSensor[]{left, right};
    }
}
