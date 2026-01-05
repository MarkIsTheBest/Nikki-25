package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.helper.general.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class LEDs {
    private static Servo launcher1; public static Servo LauncherRight() { return launcher1; }
    private static Servo launcher2; public static Servo LauncherLeft() { return launcher2; }
    private static Servo launcher3; public static Servo LauncherCenter() { return launcher3; }

    private static Servo[] allLEDs; public static Servo[] AllLEDs() { return allLEDs; }

    static boolean firstScene = true;
    static Timer flashTimer = new Timer();

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
        allLEDs = new Servo[] {launcher2, launcher3, launcher1};
        for (Servo LED :
                allLEDs) {
            LED.setPosition(0);
        }
    }

    public static void playGradientAnimation(double multiplier) {
        for(int i = 0; i < 3; i++) {
            Servo LED = allLEDs[i];
            if(LED.getPosition() == 0) LED.setPosition(0.28);

            if(LED.getPosition() >= 0.71 && firstScene) {
                firstScene = false;
            }
            else if (LED.getPosition() <= 0.28 && !firstScene) {
                firstScene = true;
            }
            if(firstScene) LED.setPosition(LED.getPosition() + 0.0001 * multiplier);
            else LED.setPosition(LED.getPosition() - 0.0001 * multiplier);
        }
    }

    public static void playRedWhiteAnimation(Timer timer, double delay) {
        if(firstScene && timer.getElapsedTimeSeconds() > delay) {
            allLEDs[0].setPosition(1);
            allLEDs[1].setPosition(0.28);
            allLEDs[2].setPosition(1);
            timer.resetTimer();
            firstScene = false;
        }
        else if (!firstScene && timer.getElapsedTimeSeconds() > delay) {
            allLEDs[0].setPosition(0.28);
            allLEDs[1].setPosition(1);
            allLEDs[2].setPosition(0.28);
            timer.resetTimer();
            firstScene = true;
        }
    }

    public static void playRedFlashAnimation() {
        if(firstScene && flashTimer.getElapsedTimeSeconds() > 0.25) {
            allLEDs[0].setPosition(0);
            allLEDs[1].setPosition(0);
            allLEDs[2].setPosition(0);
            flashTimer.resetTimer();
            firstScene = false;
        }
        else if (!firstScene && flashTimer.getElapsedTimeSeconds() > 0.1) {
            allLEDs[0].setPosition(0.28);
            allLEDs[1].setPosition(0.28);
            allLEDs[2].setPosition(0.28);
            flashTimer.resetTimer();
            firstScene = true;
        }
    }

    public static void setEmpty(Servo led) {
        led.setPosition(1);
    }

    public static void setPurple(Servo led) {
        led.setPosition(0.72);
    }

    public static void setGreen(Servo led) {
        led.setPosition(0.47);
    }
}