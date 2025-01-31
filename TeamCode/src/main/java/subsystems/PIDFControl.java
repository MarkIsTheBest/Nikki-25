package subsystems;

import com.qualcomm.robotcore.util.ElapsedTime;

public class PIDFControl
{
    private double lastError;
    private double integralSum = 0;
    private double target;
    private double current;
    private double p;
    private double i;
    private double d;
    private double f;

    private ElapsedTime timer;

    public PIDFControl(double target, double current, double p, double i, double d, double f)
    {
        this.target = target;
        this.current = current;
        this.p = p;
        this.i = i;
        this.d = d;
        this.f = f;
        timer = new ElapsedTime();
    }

    public double getPower()
    {
        double error = target - current;
        integralSum += error * timer.seconds();

        double derivative = (error - lastError) / timer.seconds();
        lastError = error;

        timer.reset();

        return (error * p) + (derivative * d) + (integralSum * i) + (current * f);
    }
}
