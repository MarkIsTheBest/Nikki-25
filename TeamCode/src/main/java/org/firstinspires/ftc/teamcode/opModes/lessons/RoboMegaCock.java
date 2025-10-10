package org.firstinspires.ftc.teamcode.opModes.lessons;

import android.text.InputFilter;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class RoboMegaCock extends LinearOpMode {

    private Servo steering;
    private DcMotor leftMotor;
    private DcMotor rightMotor;
    private double motorPower;
    private double steeringPos;
    private double motorPer;


    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        leftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
        rightMotor = hardwareMap.get(DcMotor.class, "rightMotor");
        steering = hardwareMap.get(Servo.class, "steering");
        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private void play() {

    }
 //DIFERENCIALLL UPDATE
    private void update() {
        motorPower = -gamepad1.left_stick_y;
        steeringPos = LinearValueLesson.map(gamepad1.right_stick_x, -1, 1, 0.3, 0.7);
        motorPer = LinearValueLesson.map(gamepad1.right_stick_x, -1, 1, -0.9, 0.9);

        if (steeringPos < 0.475) {
            leftMotor.setPower(motorPower * (1 + motorPer));
            rightMotor.setPower(motorPower);
        }
        else if (steeringPos > 0.525) {
            rightMotor.setPower(motorPower * (1 - motorPer));
            leftMotor.setPower(motorPower);
        }
        else {
            leftMotor.setPower(motorPower);
            rightMotor.setPower(motorPower);
        }

        steering.setPosition(steeringPos);

        telemetry.addData("steeringPos", steeringPos);
        telemetry.addData("leftMotor", motorPower * (1 + motorPer));
        telemetry.addData("rightMotor", motorPower * (1 - motorPer));
        telemetry.update();
    }
}

/*
package org.firstinspires.ftc.teamcode.opModes.lessons;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class RoboMegaCock extends LinearOpMode {

    private Servo steering;
    private DcMotor leftMotor;
    private DcMotor rightMotor;
    private double motorPower;
    private double steeringPos;
    private double steerPercent;


    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        leftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
        rightMotor = hardwareMap.get(DcMotor.class, "rightMotor");
        steering = hardwareMap.get(Servo.class, "steering");
    }

    private void play() {

    }
 //DIFERENCIALLL UPDATE
    private void update() {
        motorPower = -gamepad1.left_stick_y;
        steerPercent = LinearValueLesson.map(gamepad1.right_stick_x, -1, 1, -0.9, 0.9);

        if(steerPercent >= 0){
            leftMotor.setPower(motorPower);
            rightMotor.setPower(motorPower*(1-steerPercent));
        }
        else{
            leftMotor.setPower(motorPower*(1 + steerPercent));
            rightMotor.setPower(motorPower);
        }
        steeringPos = LinearValueLesson.map(gamepad1.right_stick_x, -1, 1, 0.25, 0.75);
        steering.setPosition(steeringPos);

        telemetry.addData("steerPercent", steerPercent);
        telemetry.update();
    }
}
*/