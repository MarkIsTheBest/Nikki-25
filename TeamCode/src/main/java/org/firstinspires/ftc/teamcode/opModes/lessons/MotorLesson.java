package org.firstinspires.ftc.teamcode.opModes.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/*
SUMMARY : When the a button on controller 1 is pressed down, the motor turns clockwise, when the player lets
go off a, the motor stops rotating.
*/

@TeleOp
public class MotorLesson extends LinearOpMode {

    private DcMotor motor; // Declare Motor variable, which can be later called to rotate
    private Servo servo;
    private double MoveForward;
    private double Steering;

    @Override
    public void runOpMode() throws InterruptedException {

        motor = hardwareMap.get(DcMotor.class, "motor"); // Assign the variable to a reference from the config map on DS
        servo = hardwareMap.get(Servo.class, "servo");
        waitForStart();

        while (opModeIsActive())
        {
            MoveForward = gamepad1.left_stick_y;
            Steering = gamepad1.right_stick_x;

            if(gamepad1.left_stick_y > 0.03) { // Every frame a(xbox)/x(playstation) is held down, the boolean returns true
                motor.setPower(-0.75); // powers the motor to run at a set speed [-1,1] is the interval (negative numbers mean backwards)
            }
            else if(gamepad1.left_stick_y < -0.03) {
                motor.setPower(0.75);
            }
            else {
                motor.setPower(0); // stops motor
            }

            if(gamepad1.right_stick_x < -0.03){
                servo.setPosition(1);
            }
            else if(gamepad1.right_stick_x > 0.03){
                servo.setPosition(0);
            }
            else{
                servo.setPosition(0.5);
            }
        }
    }
}
