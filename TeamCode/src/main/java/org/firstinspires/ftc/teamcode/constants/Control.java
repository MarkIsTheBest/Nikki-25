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
        public static double p = 0.04;
        public static double i = 0;
        public static double d = 0.0035;
        public static double f = 0;

        public static PIDFCoefficients pidf = new PIDFCoefficients(p,i,d,f);

        public static double pNear = 0.04;
        public static double iNear = 0.0045;
        public static double dNear = 0.0075;
        public static double fNear = 0;
        public static PIDFCoefficients pidfNear = new PIDFCoefficients(pNear,iNear,dNear,fNear);
    }
}
