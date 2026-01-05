package org.firstinspires.ftc.teamcode.constants;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Control {

    @Configurable
    public static class SlidersPID {
        public static double p;
        public static double i;
        public static double d;
        public static double f;

        public static PIDFCoefficients pidf = new PIDFCoefficients(p,i,d,f);
    }

    @Configurable
    public static class FlywheelPID {
        public static double p;
        public static double i;
        public static double d;

        public static PIDCoefficients pid = new PIDCoefficients(p,i,d);
    }

    @Configurable
    public static class IntakePID {
        public static double p;
        public static double i;
        public static double d;

        public static PIDCoefficients pid = new PIDCoefficients(p,i,d);
    }

    @Configurable
    public static class HeadingPID {
        public static double p = 2.5;
        public static double i = 0;
        public static double d = 0.15;

        public static PIDCoefficients pid = new PIDCoefficients(p,i,d);
    }

}
