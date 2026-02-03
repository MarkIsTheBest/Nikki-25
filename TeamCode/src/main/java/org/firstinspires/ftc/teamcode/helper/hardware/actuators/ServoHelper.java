package org.firstinspires.ftc.teamcode.helper.hardware.actuators;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import java.util.HashMap;
import java.util.Map;
import dev.nextftc.ftc.ActiveOpMode;

public class ServoHelper
{
    private Servo barrierLeft; public Servo BarrierLeft() { return barrierLeft; }
    private Servo barrierRight; public Servo BarrierRight() { return barrierRight; }
    private Servo hoodLeft; public Servo HoodLeft() { return hoodLeft; }
    private Servo hoodRight; public Servo HoodRight() { return hoodRight; }
    private CRServo feedLeft; public CRServo FeedLeft() { return feedLeft; }
    private CRServo feedRight; public CRServo FeedRight() { return feedRight; }

    private Servo[] allServos; public Servo[] AllServos() { return allServos; }

    public ServoHelper() {
        init();
    }

    public void init() {
        getHardware(ActiveOpMode.hardwareMap());
        setAllServos();
        setDirection();
        setScaleRange();
    }

    private void getHardware(HardwareMap hardwareMap) {
            barrierLeft = hardwareMap.get(Servo.class, "barrierLeft");
            barrierRight = hardwareMap.get(Servo.class, "barrierRight");
            hoodLeft = hardwareMap.get(Servo.class, "hoodLeft");
            hoodRight = hardwareMap.get(Servo.class, "hoodRight");
            feedLeft = hardwareMap.get(CRServo.class, "leftFeed");
            feedRight = hardwareMap.get(CRServo.class, "rightFeed");
    }

    private void setScaleRange() {
    }

    private void setDirection() {
        barrierLeft.setDirection(Servo.Direction.FORWARD);
        barrierRight.setDirection(Servo.Direction.REVERSE);

        hoodLeft.setDirection(Servo.Direction.FORWARD);
        hoodRight.setDirection(Servo.Direction.REVERSE);

        feedLeft.setDirection(CRServo.Direction.FORWARD);
        feedRight.setDirection(CRServo.Direction.REVERSE);

    }

    private void setAllServos() {
        allServos = new Servo[]{};
    }
}