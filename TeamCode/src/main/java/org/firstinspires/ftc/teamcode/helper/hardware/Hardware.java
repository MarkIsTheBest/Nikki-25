package org.firstinspires.ftc.teamcode.helper.hardware;

//import org.firstinspires.ftc.teamcode.helper.hardware.sensors.ColorSensors;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.ColorSensors;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.DistanceSensors;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LEDs;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.Limelight;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.Pinpoint;

public class Hardware {
    public static void init()
    {
        initMotors();
        initSensors();
    }

    public static void initMotors()
    {
        Motors.init();
        Servos.init();
    }

    public static void initSensors()
    {
        ColorSensors.init();
        LEDs.init();
        Limelight.init();
        DistanceSensors.init();
    }
}
