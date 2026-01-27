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
        public static final Pose START_POSE = new Pose(65,13, Math.toRadians(90));
    }

    @Configurable
    public static class Motor {

    }

    @Configurable
    public static class Servo {
    }
}