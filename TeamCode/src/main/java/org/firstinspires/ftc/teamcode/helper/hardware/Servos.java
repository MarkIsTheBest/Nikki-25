package org.firstinspires.ftc.teamcode.helper.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.helper.general.Debug;

import java.util.HashMap;
import java.util.Map;

import dev.nextftc.ftc.ActiveOpMode;

public class Servos
{
    private static Servo door1; public static Servo Door1() { return door1; }
    private static Servo door2; public static Servo Door2() { return door2; }

    private static AnalogInput door1Pos; public static AnalogInput Door1Pos() { return door1Pos; }
    private static AnalogInput door2Pos; public static AnalogInput Door2Pos() { return door2Pos; }

    private static Servo holder1; public static Servo Holder1() { return holder1; }
    private static Servo holder2; public static Servo Holder2() { return holder2; }
    private static Servo holder3; public static Servo Holder3() { return holder3; }

    private static Servo[] allServos; public static Servo[] AllServos() { return allServos; }

    private static final Map<Servo, Double> targetPositions = new HashMap<>();

    public static void init() {
        try {
            getHardware(ActiveOpMode.hardwareMap());
            setAllServos();
            setDirection();
            setScaleRange();
        } catch (Exception ex) {
            Debug.INSTANCE.addData("ERROR", ex.getMessage());
            Debug.INSTANCE.update();
        }
    }

    private static void getHardware(HardwareMap hardwareMap) {
        door1 = hardwareMap.get(Servo.class, "door1");
        door2 = hardwareMap.get(Servo.class, "door2");
        door1Pos = hardwareMap.get(AnalogInput.class, "door1Analog");
        door2Pos = hardwareMap.get(AnalogInput.class, "door2Analog");

        holder1 = hardwareMap.get(Servo.class, "holder1");
        holder2 = hardwareMap.get(Servo.class, "holder2");
        holder3 = hardwareMap.get(Servo.class, "holder3");
    }

    private static void setScaleRange() {
        door1.scaleRange(0, 1);
        door2.scaleRange(0, 1);

        holder1.scaleRange(0, 1);
        holder2.scaleRange(0, 1);
        holder3.scaleRange(0, 1);
    }

    private static void setDirection() {
        door1.setDirection(Servo.Direction.FORWARD);
        door2.setDirection(Servo.Direction.FORWARD);

        holder1.setDirection(Servo.Direction.FORWARD);
        holder2.setDirection(Servo.Direction.FORWARD);
        holder3.setDirection(Servo.Direction.FORWARD);
    }

    private static void setAllServos() {
        allServos = new Servo[]{door1, door2, holder1, holder2, holder3};
    }

    public static void setPosition(Servo servo, double pos) {
        servo.setPosition(pos);
        targetPositions.put(servo, pos);
    }

    private static AnalogInput getMatchingServoAnalog(Servo servo) {
        if (servo == door1) return door1Pos;
        if (servo == door2) return door2Pos;
        return null;
    }

    private static final double SERVO_MIN_V = 0.221;
    private static final double SERVO_RANGE_V = 2.834;

    public static double analogToServoPos(Servo servo) {
        AnalogInput analog = getMatchingServoAnalog(servo);
        if (analog == null) return Double.NaN;

        double v = analog.getVoltage();
        return (v - SERVO_MIN_V) / SERVO_RANGE_V;
    }

    private static final double POSITION_TOLERANCE = 0.05;

    public static boolean isBusy(Servo servo) {
        if (!targetPositions.containsKey(servo)) return false;

        double target = targetPositions.get(servo);
        double current = analogToServoPos(servo);

        if (Double.isNaN(current)) return false;

        return Math.abs(target - current) > POSITION_TOLERANCE;
    }
}