package org.firstinspires.ftc.teamcode.opModes.tests;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.helper.general.Debug;


@TeleOp
public class DistanceTest extends LinearOpMode {

    Rev2mDistanceSensor outtake;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            update();
            telemetry();
        }
    }

    private void initialize() {
        outtake = hardwareMap.get(Rev2mDistanceSensor.class, "outtake");
    }

    private void play() {

    }

    private void update() {

    }

    private void telemetry() {
        telemetry.addData("Distance", outtake.getDistance(DistanceUnit.INCH));
        telemetry.update();

    }

}