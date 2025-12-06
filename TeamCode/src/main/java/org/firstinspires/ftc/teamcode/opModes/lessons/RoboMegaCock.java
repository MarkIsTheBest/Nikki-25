package org.firstinspires.ftc.teamcode.opModes.lessons;

import static java.lang.Math.*;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class RoboMegaCock extends LinearOpMode {

    private Servo steering, forearm;
    private DcMotor leftMotor, rightMotor, armRotation, arm;
    private double motorPower, steeringPos, motorPer;
    private double degreeRotation, tickRotation;

    private double L1 = 63;
    private double L2 = 63;
    private double cosTheta2 = 1;
    private double theta2Rad;
    private double theta1Rad;
    private double theta1;
    private double theta2;
    private double forearmPos;
    private int armPos;
    private double unreachable = 0;
    private double x;
    private double y;


    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        leftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
        rightMotor = hardwareMap.get(DcMotor.class, "rightMotor");
        armRotation = hardwareMap.get(DcMotor.class, "armRotation");
        arm = hardwareMap.get(DcMotor.class, "arm");
        steering = hardwareMap.get(Servo.class, "steering");
        forearm = hardwareMap.get(Servo.class, "forearm");

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        arm.setDirection(DcMotorSimple.Direction.REVERSE);

        armRotation.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armRotation.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armRotation.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        steering.setPosition(0.5);
        degreeRotation = 0;
    }

    private void update() {
        Drive();
        ArmRotationControl();
        IK(x, y);

        if (gamepad1.dpadUpWasPressed()) {
            ++x;
        }
        else if (gamepad1.dpadDownWasPressed()) {
            --x;
        }
        else if (gamepad1.rightBumperWasPressed()) {
            ++y;
        }
        else if (gamepad1.leftBumperWasPressed()) {
            --y;
        }
    }

    // --- Drive system ---
    private void Drive() {
        motorPower = -gamepad1.left_stick_y;
        steeringPos = map(-gamepad1.right_stick_x, -1, 1, 0.3, 0.7);
        motorPer = map(-gamepad1.right_stick_x, -1, 1, -0.9, 0.9);

        if (steeringPos < 0.475) {
            rightMotor.setPower(motorPower * (1 + motorPer));
        }
        else if (steeringPos > 0.525){
             leftMotor.setPower(motorPower * (1 - motorPer));
        }
        else {
            leftMotor.setPower(motorPower);
            rightMotor.setPower(motorPower);
        }
        steering.setPosition(steeringPos);
    }

    // --- Arm rotation controlled by D-pad left/right ---
    private void ArmRotationControl() {
        degreeRotation = clamp(degreeRotation, -140, 140);
        tickRotation = armRotation.getCurrentPosition();

        if (gamepad1.dpadLeftWasPressed()) {
            degreeRotation -= 0.25;
            armRotation.setTargetPosition((int) (degreeRotation / 360.0 * 537.7));
            armRotation.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armRotation.setPower(-0.1);
        } else if (gamepad1.dpadRightWasPressed()) {
            degreeRotation += 0.25;
            armRotation.setTargetPosition((int) (degreeRotation / 360.0 * 537.7));
            armRotation.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armRotation.setPower(0.1);
        }
    }

    private void IK (double x, double y){
            cosTheta2 = (x * x + y * y - L1 * L1 - L2 * L2) / (2 * L1 * L2);
            theta2Rad = acos(cosTheta2);
            theta1Rad = atan2(y, x) - atan2(L2 * sin(theta2Rad), L1 + L2 * cos(theta2Rad));

            theta1 = 90 - (Math.toDegrees(theta1Rad) * -1);
            armPos = (int) (theta1 / 360.0 * 2786.2);
            if (theta1 <= 0 || theta1 >= 90) {
                unreachable = 1;
            }
            clamp(theta1, 0, 90);

            theta2 = Math.toDegrees(theta2Rad);
            forearmPos = -0.0042285714286 * theta2 + 1;
            double variLim = 1.80493 + (0.2468936 - 1.80493) / (1 + pow((armPos / 1317.849), 2.614582));
            clamp(variLim, 0.26, 1);
            if (forearmPos <= variLim || forearmPos >= 1 || theta1 <= 0 || theta1 >= 90) {
                unreachable = 1;
            }
            else {
                unreachable = 0;
            }
            clamp(forearmPos, variLim, 1);

            if (unreachable == 0) {
                arm.setTargetPosition(armPos);
                arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                arm.setPower(0.075);
                forearm.setPosition(forearmPos);
            }

            arm.setTargetPosition((int) (90 - (theta1 / 360.0 * 2786.2)));

            telemetry.addData("theta1", theta1);
            telemetry.addData("theta2", theta2);
            telemetry.addData("arm", armPos);
            telemetry.addData("forearm", forearmPos);
            telemetry.addData("variLim", variLim);
            telemetry.addData("unreachable", unreachable);
            telemetry.addData("x", x);
            telemetry.addData("y", y);
            telemetry.update();
    }

    // --- Utilities ---
    private double map(double val, double inMin, double inMax, double outMin, double outMax) {
        return (val - inMin) * (outMax - outMin)/(inMax - inMin) + outMin;
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}