package org.firstinspires.ftc.teamcode.opModes.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class LinearValueLesson extends LinearOpMode {

    final double SERVO_MIN = 0; // Final denotes constants
    final double SERVO_MAX = 1;
    double motorSpeed = 1; // Variable that always gets updated
    double servoPosition = 0; // Variable that always gets updated
    DcMotor motor;
    Servo servo;

    // Helper Function to transform a value from an interval (INPUT) to another (OUTPUT)
    public static double map(double value, double inMin, double inMax, double outMin, double outMax) {
        return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    @Override
    public void runOpMode() throws InterruptedException {

        waitForStart();

        while (opModeIsActive()) {
            // For applications where the intervals are the same (motor speed [-1,1] and gamepad.left_stick_y [-1,1]) \/
            motorSpeed = gamepad1.left_stick_y;
            motor.setPower(motorSpeed);
            // For application where the intervals are different (servo pos [SERVO_MIN,SERVO_MAX] gamepad.right_stick_x [-1,1])
                servoPosition = map(gamepad1.right_stick_x,-1,1, SERVO_MIN, SERVO_MAX);
            servo.setPosition(servoPosition);
        }
    }
}
