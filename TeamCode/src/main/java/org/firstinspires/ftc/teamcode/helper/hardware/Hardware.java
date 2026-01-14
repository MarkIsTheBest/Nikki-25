package org.firstinspires.ftc.teamcode.helper.hardware;

//import org.firstinspires.ftc.teamcode.helper.hardware.sensors.ColorSensors;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.helper.hardware.sensors.ColorSensors;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.DistanceSensors;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LEDs;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.Limelight;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.Pinpoint;

public class Hardware {
    public static void init(HardwareMap hwMap)
    {
        {
            initMotors(hwMap);
            initSensors(hwMap);
        }
    }

    public static void initMotors(HardwareMap hwMap)
    {
        Motors.init(hwMap);
        Servos.init(hwMap);
    }

    public static void initSensors(HardwareMap hwMap)
    {
        ColorSensors.init(hwMap);
        LEDs.init(hwMap);
        Limelight.init();
        DistanceSensors.init();
    }
}
