package org.firstinspires.ftc.teamcode.opModes.template;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.helper.general.Debug;


@TeleOp
public class LinearOpMode_Template extends LinearOpMode {

    Debug debug = new Debug(telemetry);

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            update();
            telemetry();
            debug.update();
        }
    }

    private void initialize() {

    }

    private void play() {

    }

    private void update() {

    }

    private void telemetry() {

    }

}