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
    Rev2mDistanceSensor outtake2;
    Rev2mDistanceSensor outtake3;

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
        outtake2 = hardwareMap.get(Rev2mDistanceSensor.class, "outtake2");
        outtake3 = hardwareMap.get(Rev2mDistanceSensor.class, "outtake3");
    }

    private void play() {

    }

    private void update() {

    }

    private void telemetry() {
        telemetry.addData("Distance", outtake.getDistance(DistanceUnit.CM));
        telemetry.addData("Distance 2", outtake2.getDistance(DistanceUnit.CM));
        telemetry.addData("Distance 3", outtake3.getDistance(DistanceUnit.CM));
        telemetry.update();

    }

}