package org.firstinspires.ftc.teamcode.subsystems.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Servos {
    public static Servo[] allServos = new Servo[0];

    public static void init(HardwareMap hardwareMap) {
        try {

        } catch (Exception ignore) {
        }
    }

    public static void getHardware(HardwareMap hardwareMap) {

    }

    private static void setZeroPowerBehaviour() {

    }

    private static void setDirection() {

    }

    private static void setAllMotors() {
        Servo[] servos = {};

        for (int i = 0; i < servos.length; i++) {
            if (servos[i] != null) {
                allServos[i] = servos[i];
            } else {
                allServos[i] = null;
            }
        }
    }
}