package org.firstinspires.ftc.teamcode.constants;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.helper.ColorHSV;
import org.firstinspires.ftc.teamcode.helper.ColorRGB;

@Configurable
public class Colors {
    // ----- Green interval -----
    public static ColorHSV GREEN_MIN = new ColorHSV(148,0,0);
    public static ColorHSV GREEN_MAX = new ColorHSV(170,0,0);
    // ----- Purple interval -----
    public static ColorHSV PURPLE_MIN = new ColorHSV(170,0,0);
    public static ColorHSV PURPLE_MAX = new ColorHSV(360,0,0);
}
