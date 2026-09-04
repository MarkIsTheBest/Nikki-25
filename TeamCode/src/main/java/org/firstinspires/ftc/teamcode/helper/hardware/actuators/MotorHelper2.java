package org.firstinspires.ftc.teamcode.helper.hardware.actuators;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import dev.nextftc.ftc.ActiveOpMode;

public class MotorHelper2 {
    private DcMotorEx leftFront; public DcMotorEx LeftFront() { return leftFront; }
    private DcMotorEx leftRear; public DcMotorEx LeftRear() { return leftRear; }
    private DcMotorEx rightFront; public DcMotorEx RightFront() { return rightFront; }
    private DcMotorEx rightRear; public DcMotorEx RightRear() { return rightRear; }

    private DcMotorEx sliderLeft; public DcMotorEx SliderLeft() { return sliderLeft; }
    private DcMotorEx sliderRight; public DcMotorEx SliderRight() { return sliderRight; }
    private DcMotorEx intake; public DcMotorEx Intake() { return intake; }
    private DcMotorEx feeder; public DcMotorEx Feeder() { return feeder; }

    private DcMotorEx[] allMotors; public DcMotorEx[] AllMotors() { return allMotors; }

    public MotorHelper2() {
        init();
    }

    public void init() {
        getHardware(ActiveOpMode.hardwareMap());
        setAllMotors();
        setDirection();
        setZeroPowerBehaviour();
        setMode();
    }

    private void getHardware(HardwareMap hardwareMap) {
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRear");

        sliderLeft = hardwareMap.get(DcMotorEx.class, "sliderLeft");
        sliderRight = hardwareMap.get(DcMotorEx.class, "sliderRight");
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        feeder = hardwareMap.get(DcMotorEx.class, "feeder");
    }

    private void setDirection() {
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightRear.setDirection(DcMotorSimple.Direction.FORWARD);

        sliderLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        sliderRight.setDirection(DcMotorSimple.Direction.REVERSE);
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        feeder.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    private void setZeroPowerBehaviour() {
        leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        sliderLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        sliderRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        feeder.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
    }

    private void setMode() {
        sliderLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sliderRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        sliderLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        sliderRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        feeder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void setAllMotors() {
        allMotors = new DcMotorEx[]{leftFront, rightFront, leftRear, rightRear, sliderLeft, sliderRight, intake, feeder};
    }
}