package org.firstinspires.ftc.teamcode.helper.pid;

public class PIDF
{
    public double p;
    public double i;
    public double d;
    public double f;

    public PIDF(double kP, double kI, double kD, double kF)
    {
        p = kP;
        i = kI;
        d = kD;
        f = kF;
    }
}
