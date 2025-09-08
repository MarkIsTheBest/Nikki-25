package org.firstinspires.ftc.teamcode.helper.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.helper.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class Servos
{
    public static final Servos INSTANCE = new Servos();
    public Servos() {
        init();
    }

    private Servo servo; public Servo Servo() { return servo; }

    private final Servo[] allServos = new Servo[1]; public Servo[] AllServos() { return allServos; }

    private void init() {
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

    private void getHardware(HardwareMap hardwareMap) {
        servo = hardwareMap.tryGet(Servo.class, "servo");
    }

    private void setScaleRange() {
        servo.scaleRange(0,1);
    }

    private void setDirection() {
        servo.setDirection(Servo.Direction.FORWARD);
    }

    private void setAllServos() {
        Servo[] servos = {servo};

        for (int i = 0; i < servos.length; i++) {
            if (servos[i] != null) {
                allServos[i] = servos[i];
            } else {
                allServos[i] = null;
            }
        }
    }

}