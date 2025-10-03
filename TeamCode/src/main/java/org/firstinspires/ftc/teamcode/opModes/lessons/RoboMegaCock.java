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

    private void update() {
        motorPower = gamepad1.left_stick_y;
        leftMotor.setPower(motorPower);
        rightMotor.setPower(motorPower);


    }
}