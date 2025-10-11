package org.firstinspires.ftc.teamcode.constants;
import android.graphics.Color;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.helper.ColorRGB;

@Configurable
public class Configurables {
    public static class PIDs {

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

    public static class Positions {

        @Configurable
        public static class AutoPosition {
            public static final Pose STARTPOSE = new Pose(0,0, Math.toRadians(-90));
        }

        @Configurable
        public static class MotorPosition {

        }

        @Configurable
        public static class ServoPosition {

        }
    }

    public static class Colors {
        // ----- Green interval -----
        public static ColorRGB GREEN_MIN = new ColorRGB(0,0,0);
        public static ColorRGB GREEN_MAX = new ColorRGB(0,0,0);
        // ----- Purple interval -----
        public static ColorRGB PURPLE_MIN = new ColorRGB(0,0,0);
        public static ColorRGB PURPLE_MAX = new ColorRGB(0,0,0);
    }

}
