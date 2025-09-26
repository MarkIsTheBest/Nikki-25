package org.firstinspires.ftc.teamcode.opModes.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class testcod extends LinearOpMode {

    private DcMotor motorLeft;
    private DcMotor motorRight;
    private Servo servoSteering;
    private double motorPower;

    private double servoDir;
    private double servoPower;


    @Override
    public void runOpMode() throws InterruptedException{

        motorLeft = hardwareMap.dcMotor.get("motorLeft");
        motorRight = hardwareMap.dcMotor.get("motorRight");
        servoSteering = hardwareMap.servo.get("servoSteering");

        waitForStart();

        while (opModeIsActive()){
            motorPower = gamepad1.left_stick_y;
            motorLeft.setPower(motorPower);
            motorRight.setPower(-motorPower);

//
//
//            if(gamepad1.left_stick_y < 0.03) {
//                motorLeft.setPower(0.75);
//            }
//            else if(gamepad1.left_stick_y > -0.03){
//                motorLeft.setPower(-0.75);
//            }
//            else {
//                motorLeft.setPower(0);
//            }
            servoDir = gamepad1.right_stick_x;

            servoPower = LinearValueLesson.map(servoDir,-1,1, 0, 1);
//
            servoSteering.setPosition(servoPower);

        }
    }

}

