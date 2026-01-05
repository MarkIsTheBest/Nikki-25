package org.firstinspires.ftc.teamcode.constants;

import com.bylazar.configurables.annotations.Configurable;

public class Positions {

    @Configurable
    public static class Auto {

    }

    @Configurable
    public static class Motor {

    }

    @Configurable
    public static class Servo {
            public static double H_PREPARE = 0.566;
            public static double H_CLOSE = 0.47;
            public static double H_LAUNCH = 0.3;

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