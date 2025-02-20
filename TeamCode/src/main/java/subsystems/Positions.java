package subsystems;

import subsystems.hardware.Motors;
import subsystems.hardware.Servos;

public class Positions {
    public static double linkage;
    public static double rotateAxis;
    public static double rotateBody;
    public static double rotateHead;
    public static double rotateClaw;
    public static double claw;
    public static double rotateBackBody;
    public static double rotateBackClaw;
    public static double backClaw;
    public static int vertical;
    public static boolean isParallel = false;

    public static State lastState;

    public static void update() {
        Servos.linkageLeft.setPosition(linkage);
        Servos.linkageRight.setPosition(linkage);
        Servos.rotateAxis.setPosition(rotateAxis);
        Servos.rotateBody.setPosition(rotateBody);
        Servos.rotateHead.setPosition(rotateHead);
        Servos.rotateClaw.setPosition(rotateClaw);
        Servos.claw.setPosition(claw);
        Servos.rotateBackBody.setPosition(rotateBackBody);
        Servos.rotateBackClaw.setPosition(rotateBackClaw);
        Servos.backClaw.setPosition(backClaw);

        Helper.SetMotorPosition(Motors.verticalLeft, vertical);
        Helper.SetMotorPosition(Motors.verticalRight, vertical);
    }
}
