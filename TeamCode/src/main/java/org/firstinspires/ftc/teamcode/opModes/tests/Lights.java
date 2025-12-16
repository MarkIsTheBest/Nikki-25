package org.firstinspires.ftc.teamcode.opModes.tests;

import android.graphics.Color;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.constants.Colors;
import org.firstinspires.ftc.teamcode.helper.ColorHelper;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@TeleOp(name = "Color Sensor Test (with Purple)", group = "Sensor")
public class Lights extends LinearOpMode {

    private RevColorSensorV3 colorSensor;
    private Servo colorLed;

    @Override
    public void runOpMode() {
        // Initialize color sensor (check name in configuration!)
        colorSensor = hardwareMap.get(RevColorSensorV3.class, "colorSensor");
        colorLed = hardwareMap.get(Servo.class, "ledServo");

        telemetry.addLine("Color Sensor Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Read raw RGB + alpha (brightness)
            int r = colorSensor.red();
            int g = colorSensor.green();
            int b = colorSensor.blue();
            int alpha = colorSensor.alpha();

            // Normalize RGB to the maximum value for comparison
            double max = Math.max(r, Math.max(g, b));
            double rn = r / max;
            double gn = g / max;
            double bn = b / max;

            float[] hsv = new float[3];
            Color.RGBToHSV(r,g,b,hsv);

            // Identify approximate color
            String colorName;

            if(ColorHelper.inHue(hsv[0], Colors.GREEN_MIN, Colors.GREEN_MAX))
            {
                colorName = "Green";
                colorLed.setPosition(0.476);
            }
            else if(ColorHelper.inHue(hsv[0], Colors.PURPLE_MIN, Colors.PURPLE_MAX))
            {
                colorName = "Purple";
                colorLed.setPosition(0.7);
            }
            else
            {
                colorName = "Other";
                colorLed.setPosition(0 );
            }

            telemetry.addData("Raw", "R:%d G:%d B:%d A:%d", r, g, b, alpha);
            telemetry.addData("Normalized", "R:%.2f G:%.2f B:%.2f", rn, gn, bn);
            telemetry.addData("Hue", hsv[0]);
            telemetry.addData("Detected Color", colorName);
            telemetry.update();
        }
    }
}
