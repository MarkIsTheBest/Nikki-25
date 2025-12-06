package org.firstinspires.ftc.teamcode.opModes.lessons;

import static java.lang.Math.pow;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ServoPositionLesson extends LinearOpMode {

    private Servo bL;
    private Servo bR;
    private double position = 0.5;

    @Override
    public void runOpMode() throws InterruptedException {

        bL = hardwareMap.get(Servo.class, "bL");
        bR = hardwareMap.get(Servo.class, "bR");


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
                bL.setPosition(position);
                bR.setPosition(position);
            }


            telemetry.addData("Servo Position", position);
            telemetry.update();
        }
    }
}
