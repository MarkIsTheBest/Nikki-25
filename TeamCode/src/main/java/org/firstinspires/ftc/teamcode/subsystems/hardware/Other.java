package org.firstinspires.ftc.teamcode.subsystems.hardware;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Other
{
    public static Limelight3A Limelight;

    public static void init(HardwareMap hardwareMap) {
        try {
            getHardware(hardwareMap);
        } catch (Exception ignore){};
    }

    public static void getHardware(HardwareMap hardwareMap) {
        Limelight = hardwareMap.get(Limelight3A.class, "Limelight");
    }
}
