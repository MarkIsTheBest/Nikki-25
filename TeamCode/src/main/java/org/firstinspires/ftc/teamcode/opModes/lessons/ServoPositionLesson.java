package org.firstinspires.ftc.teamcode.opModes.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ServoPositionLesson extends LinearOpMode {

    private Servo servo;
    private double position;

    @Override
    public void runOpMode() throws InterruptedException {

        servo = hardwareMap.get(Servo.class, "servo");
        waitForStart();

        while (opModeIsActive())
        {
            if(gamepad1.dpadRightWasPressed()) {
                position+=0.01;
            }
            else if(gamepad1.dpadLeftWasPressed()) {
                position-=0.01;
            }

            if(gamepad1.aWasPressed()) {
                servo.setPosition(position);
            }

            telemetry.addData("Servo Position", position);
            telemetry.update();
        }
    }
}
