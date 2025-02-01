package subsystems.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Servos
{
    public static Servo linkageLeft;
    public static Servo linkageRight;
    public static Servo rotateBody;
    public static Servo rotateHead;
    public static Servo rotateClaw;
    public static Servo claw;
    public static Servo rotateAxis;
    public static Servo rotateBackBody;
    public static Servo rotateBackClaw;
    public static Servo backClaw;

    public static void init(HardwareMap hardwareMap) {
        try
        {
            linkageLeft = hardwareMap.get(Servo.class, "linkageLeft");
            linkageRight = hardwareMap.get(Servo.class, "linkageRight");
            rotateBody = hardwareMap.get(Servo.class, "rotateBody");
            rotateHead = hardwareMap.get(Servo.class, "rotateHead");
            rotateClaw = hardwareMap.get(Servo.class, "rotateClaw");
            claw = hardwareMap.get(Servo.class, "claw");
            rotateAxis = hardwareMap.get(Servo.class, "rotateAxis");
            rotateBackBody = hardwareMap.get(Servo.class, "rotateBackBody");
            rotateBackClaw = hardwareMap.get(Servo.class, "rotateBackClaw");
            backClaw = hardwareMap.get(Servo.class, "backClaw");
        }
        catch (Exception ignore) {}

        linkageRight.setDirection(Servo.Direction.REVERSE);
    }
}
