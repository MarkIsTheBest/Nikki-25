package org.firstinspires.ftc.teamcode.helper.hardware.actuators;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import dev.nextftc.ftc.ActiveOpMode;

public class ServoHelper2 {
    private Servo claw; public Servo Claw() { return claw; }
    private AnalogInput clawFeedback; public AnalogInput ClawFeedback() { return clawFeedback; }

    private Servo[] allServos; public Servo[] AllServos() { return allServos; }

    public ServoHelper2() {
        init();
    }

    public void init() {
        getHardware(ActiveOpMode.hardwareMap());
        setAllServos();
        setDirection();
    }

    private void getHardware(HardwareMap hardwareMap) {
        claw = hardwareMap.get(Servo.class, "claw");
        clawFeedback = hardwareMap.get(AnalogInput.class, "clawFeedback");
    }

    private void setDirection() {
        claw.setDirection(Servo.Direction.FORWARD);
    }

    private void setAllServos() {
        allServos = new Servo[]{claw};
    }
}