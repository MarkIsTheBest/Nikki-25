package org.firstinspires.ftc.teamcode.helper.hardware;

import org.firstinspires.ftc.teamcode.helper.hardware.actuators.MotorHelper2;
import org.firstinspires.ftc.teamcode.helper.hardware.actuators.ServoHelper2;

public class Hardware2 {
    private final MotorHelper2 motors; public MotorHelper2 Motors() { return motors; }
    private final ServoHelper2 servos; public ServoHelper2 Servos() { return servos; }

    public Hardware2() {
        motors = new MotorHelper2();
        servos = new ServoHelper2();
    }
}
