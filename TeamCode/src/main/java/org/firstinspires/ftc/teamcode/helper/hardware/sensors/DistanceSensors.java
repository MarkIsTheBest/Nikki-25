package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.helper.general.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class DistanceSensors {
    private static Rev2mDistanceSensor left; public static Rev2mDistanceSensor Left() { return left; }
    private static Rev2mDistanceSensor right; public static Rev2mDistanceSensor Right() { return right; }

    private static Rev2mDistanceSensor[] allDistanceSensors; public static Rev2mDistanceSensor[] AllDistanceSensors() { return allDistanceSensors; }

    public static void init() {
        left = null;
        right = null;

        try {
            getHardware(ActiveOpMode.hardwareMap());
            setAllDistanceSensors();
        } catch (Exception ex) {
        }
    }

    public static double getDistance(Rev2mDistanceSensor distanceSensor, DistanceUnit DU) {
        return distanceSensor.getDistance(DU);
    }

    public static double getDistance(Rev2mDistanceSensor distanceSensor) {
        return distanceSensor.getDistance(DistanceUnit.INCH);
    }

    public static double Min(Rev2mDistanceSensor distanceSensor1,
                             Rev2mDistanceSensor distanceSensor2,
                             DistanceUnit DU) {
            return Math.min(distanceSensor1.getDistance(DU), distanceSensor2.getDistance(DU));
    }

    public static double Max(Rev2mDistanceSensor distanceSensor1,
                             Rev2mDistanceSensor distanceSensor2,
                             DistanceUnit DU) {
        return Math.max(distanceSensor1.getDistance(DU), distanceSensor2.getDistance(DU));
    }

    private static void getHardware(HardwareMap hardwareMap) {
        left = hardwareMap.tryGet(Rev2mDistanceSensor.class, "distanceLeft");
        right = hardwareMap.tryGet(Rev2mDistanceSensor.class, "distanceRight");
    }

    private static void setAllDistanceSensors()
    {
        allDistanceSensors = new Rev2mDistanceSensor[]{left, right};
    }
}
