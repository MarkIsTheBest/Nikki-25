package org.firstinspires.ftc.teamcode.opModes.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

public class OrganizedTemplate extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        // Init logic
    }

    private void play() {
        // start logic
    }

    private void update() {
        // loop logic
    }
}