package org.firstinspires.ftc.teamcode.helper.color;

public class ColorHelper {
    public static boolean inInterval(ColorRGB color, ColorRGB min, ColorRGB max)
    {
        boolean inRed = color.red > min.red && color.red < max.red;
        boolean inGreen = color.green > min.green && color.green < max.green;
        boolean inBlue = color.blue > min.blue && color.blue < max.blue;

        return (inRed && inGreen) && inBlue;
    }

    public static boolean inHue(double hue, ColorHSV min, ColorHSV max) {
        boolean inHue = hue > min.hue && hue < max.hue;
        return inHue;
    }

    public static ColorHSV fromRGB(ColorRGB color) {
        double hue = 0;
        double saturation = 0;
        double value = 0;

        double red = color.red / 255.0;
        double green = color.green / 255.0;
        double blue = color.blue / 255.0;

        double max = Math.max(Math.max(red, green), blue);
        double min = Math.min(Math.min(red,green), blue);

        double delta = max - min;

        //calculate hue
        if(delta == 0) hue = 0;
        if(max == red) hue = 60 * (((green - blue) / delta) % 6);
        if(max == green) hue = 60 * (((blue - red) / delta) + 2);
        if(max == blue) hue = 60 * (((red - green) / delta) + 4);

        //calculate saturation
        if(max == 0) saturation = 0;
        else saturation = (delta / max) * 100;

        //calculate value
        value = max * 100;

        return new ColorHSV(hue, saturation, value);
    }
}
