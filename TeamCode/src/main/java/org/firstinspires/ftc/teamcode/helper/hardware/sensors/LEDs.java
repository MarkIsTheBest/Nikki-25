package org.firstinspires.ftc.teamcode.helper.hardware.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class LEDs {
    public static Servo[] lights = new Servo[3];
    private static final ElapsedTime animTimer = new ElapsedTime();

    public static void init(HardwareMap hwMap) {
        lights[0] = hwMap.tryGet(Servo.class, "launcherLight1");
        lights[1] = hwMap.tryGet(Servo.class, "launcherLight2");
        lights[2] = hwMap.tryGet(Servo.class, "launcherLight3");
    }

    public static void playSineWaveAnimation() {
        // Uses time-based sine wave for a smooth "pulsing" effect
        double pos = (Math.sin(animTimer.seconds() * 2 * Math.PI) + 1) / 2;
        // Map sine (0-1) to your preferred LED range
        double ledPos = 0.28 + (pos * (0.71 - 0.28));
        for (Servo s : lights) if (s != null) s.setPosition(ledPos);
    }

    public static void setStatus(int index, String state) {
        if (lights[index] == null) return;
        switch (state) {
            case "PURPLE": lights[index].setPosition(0.72); break;
            case "GREEN":  lights[index].setPosition(0.47); break;
            case "EMPTY":  lights[index].setPosition(1.0);  break;
            default:       lights[index].setPosition(0.0);  break;
        }
    }
}