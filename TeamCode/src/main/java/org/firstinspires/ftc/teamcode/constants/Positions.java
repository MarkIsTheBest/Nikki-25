package org.firstinspires.ftc.teamcode.constants;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;

public class Positions {

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
