package subsystems;

import com.acmerobotics.dashboard.config.Config;

@Config
public final class Constants
{
    public static class ROTATE_AXIS
    {
        public static double MID = 0.449;
        public static double TRANSFER = 0;
    }

    public static class ROTATE_BODY
    {
        public static double MIN = 0.29;
        public static double MAX = 0.86;
        public static double TRANSFER = 0;
    }

    public static class ROTATE_BACK_BODY
    {
        public static double INIT = 0.25;
        public static double TRANSFER = 0;
    }

    public static class LINKAGE
    {
        public static double CLOSED = 0.65;
        public static double OPENED = 0.5;
    }

    public static class ROTATE_HEAD
    {
        public static double INIT = 0.85;
        public static double TRANSFER = 0;
    }

    public static class ROTATE_CLAW
    {
        public static double MIN = 0;
        public static double MAX = 1;
        public static double INIT = 0.34;
        public static double TRANSFER = 0;
    }

    public static class ROTATE_BACK_CLAW
    {
        public static double MIN = 0;
        public static double MAX = 1;
        public static double INIT = 0.64;
        public static double TRANSFER = 0;
    }

    public static class VERTICAL
    {
        public static int MIN = 0;
        public static int HIGH_RUNG = 0;
        public static int MAX = 0;
        public static int TRANSFER = 0;
    }
}
