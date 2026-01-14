package org.firstinspires.ftc.teamcode.helper.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import java.util.HashMap;

public class Servos {
    public static Servo door1, door2, holder1, holder2, holder3;
    public static AnalogInput door1Encoder, door2Encoder;

    private static final HashMap<Servo, Double> targetCache = new HashMap<>();

    public static void init(HardwareMap hwMap) {
        door1 = hwMap.tryGet(Servo.class, "door1");
        door2 = hwMap.tryGet(Servo.class, "door2");
        door1Encoder = hwMap.tryGet(AnalogInput.class, "door1Analog");
        door2Encoder = hwMap.tryGet(AnalogInput.class, "door2Analog");
        holder1 = hwMap.tryGet(Servo.class, "holder1");
        holder2 = hwMap.tryGet(Servo.class, "holder2");
        holder3 = hwMap.tryGet(Servo.class, "holder3");
    }

    public static void setPosition(Servo servo, double pos) {
        if (servo == null) return;
        // Only write if the position actually changed (saves Hub bandwidth)
        if (!targetCache.containsKey(servo) || Math.abs(targetCache.get(servo) - pos) > 0.001) {
            servo.setPosition(pos);
            targetCache.put(servo, pos);
        }
    }

    public static boolean isBusy(Servo servo) {
        if (servo == null || !targetCache.containsKey(servo)) return false;

        AnalogInput analog = (servo == door1) ? door1Encoder : (servo == door2 ? door2Encoder : null);
        if (analog == null) return false;

        double currentPos = (analog.getVoltage() - 0.221) / 2.834;
        return Math.abs(targetCache.get(servo) - currentPos) > 0.05;
    }
}