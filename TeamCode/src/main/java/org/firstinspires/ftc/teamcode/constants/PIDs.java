package org.firstinspires.ftc.teamcode.constants;

import com.bylazar.configurables.annotations.Configurable;

public class PIDs {

    @Configurable
    public static class SlidersPID {
        public static double p;
        public static double i;
        public static double d;
        public static double f;
    }

    @Configurable
    public static class FlywheelPID {
        public static double p;
        public static double i;
        public static double d;
    }

    @Configurable
    public static class IntakePID {
        public static double p;
        public static double i;
        public static double d;
    }
}
