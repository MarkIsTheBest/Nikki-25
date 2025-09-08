package org.firstinspires.ftc.teamcode.helper.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.helper.Debug;

import dev.nextftc.ftc.ActiveOpMode;

public class Motors
{
    public static final Motors INSTANCE = new Motors();
    public Motors() {
        init();
    }

    private DcMotorEx leftFront; public DcMotorEx LeftFront() { return leftFront; }
    private DcMotorEx leftRear; public DcMotorEx LeftRear() { return leftRear; }
    private DcMotorEx rightFront; public DcMotorEx RightFront() { return rightFront; }
    private DcMotorEx rightRear; public DcMotorEx RightRear() { return rightRear; }

    private final DcMotorEx[] allMotors = new DcMotorEx[4]; public DcMotorEx[] AllMotors() { return allMotors; }

    private void init() {
        try {
            getHardware(ActiveOpMode.hardwareMap());
            setAllMotors();
            setDirection();
            setZeroPowerBehaviour();
        } catch (Exception ex) {
            Debug.INSTANCE.addData("ERROR", ex.getMessage());
            Debug.INSTANCE.update();
        }
    }

    private void getHardware(HardwareMap hardwareMap) {
        leftFront = hardwareMap.tryGet(DcMotorEx.class, "leftFront");
        leftRear = hardwareMap.tryGet(DcMotorEx.class, "leftRear");
        rightFront = hardwareMap.tryGet(DcMotorEx.class, "rightFront");
        rightRear = hardwareMap.tryGet(DcMotorEx.class, "rightRear");
    }

    private void setZeroPowerBehaviour() {
        leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
    }

    private void setDirection() {
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightRear.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    private void setAllMotors() {
        DcMotorEx[] motors = {leftFront, rightFront, leftRear, rightRear};

        for (int i = 0; i < motors.length; i++) {
            if (motors[i] != null) {
                allMotors[i] = motors[i];
            } else {
                allMotors[i] = null;
            }
        }
    }

    public static void setMotorPosition(DcMotorEx motor, int position, double power) {
        motor.setTargetPosition(position);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(power);
    }

    public static void setMotorPosition(DcMotorEx motor, int position) {
        motor.setTargetPosition(position);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(1);
    }
}