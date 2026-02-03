package org.firstinspires.ftc.teamcode.constants;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.PIDFCoefficients;

public class Control {

    @Configurable
    public static class Flywheel {
        public static double p = 2;
        public static double i = 0;
        public static double d = 0;
        public static double f = 11;

        public static PIDFCoefficients pidf = new PIDFCoefficients(p,i,d,f);
    }

    @Configurable
    public static class Turret {
        public static double p = 0.02;
        public static double i = 0;
        public static double d = 0.0025;
        public static double f = 0.26;

        public static PIDFCoefficients pidf = new PIDFCoefficients(p,i,d,f);
    }
}
