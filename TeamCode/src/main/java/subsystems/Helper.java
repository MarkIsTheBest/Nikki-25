package subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


public class Helper {
    private static final ElapsedTime timer = new ElapsedTime();

    public static void SetMotorPosition(DcMotor motor, int position) {
        motor.setPower(1);
        motor.setTargetPosition(position);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public static double deltaTime() {
        return timer.milliseconds() / 1000;
    }

    public static void resetDeltaTime() {
        timer.reset();
    }

    public static int adjustPositionMotor(int currentPosition, double input, int min, int max, double speed) {
        currentPosition += (int) (deltaTime() * speed * input);
        return Math.max(min, Math.min(currentPosition, max));
    }

    public static double adjustPositionServo(double currentPosition, double input, double min, double max, double speed) {
        currentPosition += Helper.deltaTime() * speed * input;
        return Math.max(min, Math.min(currentPosition, max));
    }

    public static double map(double value, double ogMin, double ogMax, double newMin, double newMax) {
        return newMin + (value - ogMin) * (newMax - newMin) / (ogMax - ogMin);
    }

    public static double getParallel(double x) {
        return 1.1 * x + 0.29;
    }
}
