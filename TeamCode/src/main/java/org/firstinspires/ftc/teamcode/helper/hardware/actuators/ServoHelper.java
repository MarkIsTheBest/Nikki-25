package org.firstinspires.ftc.teamcode.helper.hardware.actuators;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import java.util.HashMap;
import java.util.Map;
import dev.nextftc.ftc.ActiveOpMode;

public class ServoHelper
{
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
        allServos = new Servo[]{};
    }
}