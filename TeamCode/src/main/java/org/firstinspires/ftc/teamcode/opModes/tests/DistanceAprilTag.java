package org.firstinspires.ftc.teamcode.opModes.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.helper.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.Limelight;

@TeleOp
public class DistanceAprilTag extends LinearOpMode {

    final double e = 2.71828;
    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        Limelight.init();
        Limelight.setPipeline(0);
        Limelight.start();
    }

    private void play() {
        // start logic
    }

    private void update() {
        Limelight.update();
        Debug.INSTANCE.addData("Ta", Limelight.Ta());
        Debug.INSTANCE.addData("Distance", 180.2858*Math.pow(Limelight.Ta(),-0.5027412));
        Debug.INSTANCE.update();
    }
}