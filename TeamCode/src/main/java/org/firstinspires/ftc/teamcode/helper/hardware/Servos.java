package org.firstinspires.ftc.teamcode.helper.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.helper.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class Servos
{
    private static Servo door1; public static Servo Door1() { return door1; }
    private static Servo door2; public static Servo Door2() { return door2; }

    private static Servo holder1; public static Servo Holder1() { return holder1; }
    private static Servo holder2; public static Servo Holder2() { return holder2; }
    private static Servo holder3; public static Servo Holder3() { return holder3; }

    private static Servo[] allServos; public static Servo[] AllServos() { return allServos; }

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

}