package org.firstinspires.ftc.teamcode.helper.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import java.util.HashMap;
import java.util.Map;
import dev.nextftc.ftc.ActiveOpMode;

public class Servos
{
    private Servo door1; public Servo Door1() { return door1; }
    private Servo door2; public Servo Door2() { return door2; }

    private AnalogInput door1Pos; public AnalogInput Door1Pos() { return door1Pos; }
    private AnalogInput door2Pos; public AnalogInput Door2Pos() { return door2Pos; }

    private Servo holder1; public Servo Holder1() { return holder1; }
    private Servo holder2; public Servo Holder2() { return holder2; }
    private Servo holder3; public Servo Holder3() { return holder3; }

    private Servo[] allServos; public Servo[] AllServos() { return allServos; }

    private final Map<Servo, Double> targetPositions = new HashMap<>();
    private final Map<Servo, Double> lastSentPositions = new HashMap<>();

    public void init() {
        resetCache();
        try {
            getHardware(ActiveOpMode.hardwareMap());
            setAllServos();
            setDirection();
            setScaleRange();
        } catch (Exception ex) {
            // Logging an error here helps you know if a wire is unplugged
        }
    }

    private void resetCache() {
        door1 = null; door2 = null;
        door1Pos = null; door2Pos = null;
        holder1 = null; holder2 = null; holder3 = null;
        targetPositions.clear();
        lastSentPositions.clear();
    }

    private void getHardware(HardwareMap hardwareMap) {
        // Using tryGet prevents the entire OpMode from crashing if one servo is missing
        door1 = hardwareMap.tryGet(Servo.class, "door1");
        door2 = hardwareMap.tryGet(Servo.class, "door2");
        door1Pos = hardwareMap.tryGet(AnalogInput.class, "door1Analog");
        door2Pos = hardwareMap.tryGet(AnalogInput.class, "door2Analog");
        holder1 = hardwareMap.tryGet(Servo.class, "holder1");
        holder2 = hardwareMap.tryGet(Servo.class, "holder2");
        holder3 = hardwareMap.tryGet(Servo.class, "holder3");
    }

    private void setScaleRange() {
        for (Servo s : allServos) {
            if (s != null) s.scaleRange(0, 1);
        }
    }

    private void setDirection() {
        for (Servo s : allServos) {
            if (s != null) s.setDirection(Servo.Direction.FORWARD);
        }
    }

    private void setAllServos() {
        allServos = new Servo[]{door1, door2, holder1, holder2, holder3};
    }

    public void setPosition(Servo servo, double pos) {
        if (servo == null) return; // FIX: Prevents jumping/crashing if hardware is missing

        targetPositions.put(servo, pos);

        Double lastSent = lastSentPositions.get(servo);
        if (lastSent == null || Math.abs(lastSent - pos) > 0.001) {
            servo.setPosition(pos);
            lastSentPositions.put(servo, pos);
        }
    }

    private AnalogInput getMatchingServoAnalog(Servo servo) {
        if (servo == door1) return door1Pos;
        if (servo == door2) return door2Pos;
        return null;
    }

    private final double SERVO_MIN_V = 0.221;
    private final double SERVO_RANGE_V = 2.834;

    public double analogToServoPos(Servo servo) {
        AnalogInput analog = getMatchingServoAnalog(servo);
        if (analog == null) return Double.NaN;

        double v = analog.getVoltage();
        return (v - SERVO_MIN_V) / SERVO_RANGE_V;
    }

    private final double POSITION_TOLERANCE = 0.05;

    public boolean isBusy(Servo servo) {
        if (servo == null || !targetPositions.containsKey(servo)) return false;

        double target = targetPositions.get(servo);
        double current = analogToServoPos(servo);

        if (Double.isNaN(current)) return false;

        return Math.abs(target - current) > POSITION_TOLERANCE;
    }
}