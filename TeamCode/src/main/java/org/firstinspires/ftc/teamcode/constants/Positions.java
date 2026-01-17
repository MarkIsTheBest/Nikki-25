package org.firstinspires.ftc.teamcode.constants;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;

public class Positions {

    @Configurable
    public static class Field {
        public static final Pose RED_GOAL = new Pose(130,136);
        public static final Pose BLUE_GOAL = RED_GOAL.mirror();

        public static final Pose RED_BASE = new Pose(38.5, 33.5, Math.toRadians(90));
        public static final Pose BLUE_BASE = RED_BASE.mirror();
    }

    @Configurable
    public static class Auto {
        public static final Pose START_POSE = new Pose(9,9, Math.toRadians(90));
    }

    @Configurable
    public static class Motor {

    }

    @Configurable
    public static class Servo {
            public static double H_PREPARE = 0.5;
            public static double H_CLOSE = 0.425;
            public static double H_LAUNCH = 0.275;

            // -- LEFT INTAKE SERVO POSITIONS -- \\
            public static double L_D1_PREPARE = 0.5;
            public static double L_D2_PREPARE = 0.2355;

            public static double L_D1_PARTIAL = 0.82;

            // -- CENTER INTAKE SERVO POSITIONS -- \\
            public static double C_D1_PREPARE = 0.78;
            public static double C_D2_PREPARE = 0.171;

            // -- RIGHT INTAKE SERVO POSITIONS -- \\
            public static double R_D1_PREPARE = 0.704;
            public static double R_D2_PREPARE = 0.477;

            public static double R_D2_PARTIAL = 0.1;
    }
}