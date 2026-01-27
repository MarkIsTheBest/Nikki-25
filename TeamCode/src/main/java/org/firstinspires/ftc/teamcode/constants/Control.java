package org.firstinspires.ftc.teamcode.constants;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Control {

    @Configurable
    public static class FlywheelPIDF {
        public static double p = 1.4;
        public static double i = 0.0015;
        public static double d = 15;
        public static double f = 15;

        public static PIDFCoefficients pidf = new PIDFCoefficients(p,i,d,f);
    }

}
