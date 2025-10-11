package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.teamcode.helper.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class LEDs {
    public static final LEDs INSTANCE = new LEDs();

    public LEDs() {
        init();
    }

    private Servo launcher1; public Servo Launcher1() { return launcher1; }
    private Servo launcher2; public Servo Launcher2() { return launcher2; }
    private Servo launcher3; public Servo Launcher3() { return launcher3; }

    private Servo[] allLEDs; public Servo[] AllLEDs() { return allLEDs; }

    private void init() {
        try {
            getHardware(ActiveOpMode.hardwareMap());
            setAllLEDs();
        } catch (Exception ex) {
            Debug.INSTANCE.addData("ERROR", ex.getMessage());
            Debug.INSTANCE.update();
        }
    }

    private void getHardware(HardwareMap hardwareMap) {
        launcher1 = hardwareMap.tryGet(Servo.class, "launcherLight1");
        launcher2 = hardwareMap.tryGet(Servo.class, "launcherLight2");
        launcher3 = hardwareMap.tryGet(Servo.class, "launcherLight3");
    }

    private void setAllLEDs() {
        allLEDs = new Servo[] {launcher1, launcher2, launcher3};
    }

    public void setEmpty(Servo led) {
        led.setPosition(0);
    }

    public void setPurple(Servo led) {
        led.setPosition(0.72);
    }

    public void setGreen(Servo led) {
        led.setPosition(0.47);
    }
}