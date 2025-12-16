package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.helper.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class LEDs {
    private static Servo launcher1; public static Servo Launcher1() { return launcher1; }
    private static Servo launcher2; public static Servo Launcher2() { return launcher2; }
    private static Servo launcher3; public static Servo Launcher3() { return launcher3; }

    private static Servo[] allLEDs; public static Servo[] AllLEDs() { return allLEDs; }

    public static void init() {
        try {
            getHardware(ActiveOpMode.hardwareMap());
            setAllLEDs();
        } catch (Exception ex) {
            Debug.INSTANCE.addData("ERROR", ex.getMessage());
            Debug.INSTANCE.update();
        }
    }

    private static void getHardware(HardwareMap hardwareMap) {
        launcher1 = hardwareMap.tryGet(Servo.class, "launcherLight1");
        launcher2 = hardwareMap.tryGet(Servo.class, "launcherLight2");
        launcher3 = hardwareMap.tryGet(Servo.class, "launcherLight3");
    }

    private static void setAllLEDs() {
        allLEDs = new Servo[] {launcher1, launcher2, launcher3};
    }

    public static void setEmpty(Servo led) {
        led.setPosition(0);
    }

    public static void setPurple(Servo led) {
        led.setPosition(0.72);
    }

    public static void setGreen(Servo led) {
        led.setPosition(0.47);
    }
}