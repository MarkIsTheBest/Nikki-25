package org.firstinspires.ftc.teamcode.helper;

public class ColorHelper {
    public static boolean inInterval(ColorRGB color, ColorRGB min, ColorRGB max)
    {
        boolean inRed = color.red > min.red && color.red < max.red;
        boolean inGreen = color.green > min.green && color.green < max.green;
        boolean inBlue = color.blue > min.blue && color.blue < max.blue;

        return (inRed && inGreen) && inBlue;
    }
}
