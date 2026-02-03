package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import dev.nextftc.ftc.ActiveOpMode;

public class LEDHelper {
    private Servo leftLight; public Servo LeftLight() { return leftLight; }
    private Servo rightLight; public Servo RightLight() { return rightLight; }

    private Servo[] allLEDs; public Servo[] AllLEDs() { return allLEDs; }

    boolean firstScene = true;
    Timer flashTimer = new Timer();

    public LEDHelper() {
        init();
    }

    public void init() {
        getHardware(ActiveOpMode.hardwareMap());
        setAllLEDs();
    }

    private void getHardware(HardwareMap hardwareMap) {
        leftLight = hardwareMap.tryGet(Servo.class, "leftLight");
        rightLight = hardwareMap.tryGet(Servo.class, "rightLight");
    }

    private void setAllLEDs() {
        allLEDs = new Servo[] {rightLight, leftLight};
        for (Servo LED :
                allLEDs) {
            LED.setPosition(0);
        }
    }

    public void playRedFlashAnimation() {
        if(firstScene && flashTimer.getElapsedTimeSeconds() > 0.25) {
            allLEDs[0].setPosition(0);
            allLEDs[1].setPosition(0);
            flashTimer.resetTimer();
            firstScene = false;
        }
        else if (!firstScene && flashTimer.getElapsedTimeSeconds() > 0.1) {
            allLEDs[0].setPosition(0.28);
            allLEDs[1].setPosition(0.28);
            flashTimer.resetTimer();
            firstScene = true;
        }
    }

    public void setEmpty(Servo led) {
        led.setPosition(1);
    }

    public void setRed(Servo led) {
        led.setPosition(0.28);
    }

    public void setGreen(Servo led) {
        led.setPosition(0.47);
    }
}