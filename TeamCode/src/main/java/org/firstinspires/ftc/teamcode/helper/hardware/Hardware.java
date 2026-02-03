package org.firstinspires.ftc.teamcode.helper.hardware;

import org.firstinspires.ftc.teamcode.helper.hardware.actuators.MotorHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.actuators.ServoHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.DistanceSensorHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LEDHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LimelightHelper;

public class Hardware {
    private final MotorHelper motors; public MotorHelper Motors() { return motors; }
    private final ServoHelper servos; public ServoHelper Servos() { return servos; }
    private final LimelightHelper limelight; public LimelightHelper Limelight() { return limelight; }
    private final LEDHelper leds; public LEDHelper LEDs() { return leds; }
    private final DistanceSensorHelper distanceSensors; public DistanceSensorHelper DistanceSensors() { return distanceSensors; }

    public Hardware() {
        motors = new MotorHelper();
        servos = new ServoHelper();
        limelight = new LimelightHelper();
        leds = new LEDHelper();
        distanceSensors = new DistanceSensorHelper();
    }
}