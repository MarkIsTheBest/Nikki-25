package org.firstinspires.ftc.teamcode.opModes.tuners;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.teamcode.helper.color.ColorHelper;
import org.firstinspires.ftc.teamcode.helper.color.ColorRGB;
import org.firstinspires.ftc.teamcode.helper.general.Debug;

@Configurable
@TeleOp(name = "Color Interval Tuner", group = "Tuners")
public class ColorIntervalTuner extends LinearOpMode {

    private ColorSensor colorSensor;

    public static ColorRGB min;
    public static ColorRGB max;
    public static boolean lightOn = true;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        //colorSensor = ColorSensors.INSTANCE.Launcher1();
    }

    private void play() {
        // start logic
    }

    private void update() {
        colorSensor.enableLed(lightOn);

        ColorRGB currentColor = new ColorRGB(
                colorSensor.red(),
                colorSensor.green(),
                colorSensor.blue()
        );
//
//        Debug.INSTANCE.addData("Red", currentColor.red);
//        Debug.INSTANCE.addData("Green", currentColor.green);
//        Debug.INSTANCE.addData("Blue", currentColor.blue);
//
//        Debug.INSTANCE.addData("Is in Interval?", ColorHelper.inInterval(currentColor, min, max));
    }
}