package org.firstinspires.ftc.teamcode.subsystems.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Motors
{
    public static DcMotorEx leftFront;
    public static DcMotorEx leftRear;
    public static DcMotorEx rightFront;
    public static DcMotorEx rightRear;
    public static DcMotorEx rotateSlider;
    public static DcMotorEx extendSlider;

    public static DcMotorEx[] allMotors = new DcMotorEx[6];

    public static void init(HardwareMap hardwareMap) {
        try {
            getHardware(hardwareMap);
            setAllMotors();
            setDirection();
            setZeroPowerBehaviour();
        } catch (Exception ignored) {}
    }

    public static void getHardware(HardwareMap hardwareMap) {
        leftFront = hardwareMap.tryGet(DcMotorEx.class, "leftFront");
        leftRear = hardwareMap.tryGet(DcMotorEx.class, "leftRear");
        rightFront = hardwareMap.tryGet(DcMotorEx.class, "rightFront");
        rightRear = hardwareMap.tryGet(DcMotorEx.class, "rightRear");
        rotateSlider = hardwareMap.tryGet(DcMotorEx.class, "rotateSlider");
        extendSlider = hardwareMap.tryGet(DcMotorEx.class, "extendSlider");
    }

    private static void setZeroPowerBehaviour() {
        leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rotateSlider.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extendSlider.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    private static void setDirection() {
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightRear.setDirection(DcMotorSimple.Direction.FORWARD);
        rotateSlider.setDirection(DcMotorSimple.Direction.FORWARD);
        extendSlider.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    private static void setAllMotors() {
        DcMotorEx[] motors = {leftFront, rightFront, leftRear, rightRear, rotateSlider, extendSlider};

        for (int i = 0; i < motors.length; i++) {
            if (motors[i] != null) {
                allMotors[i] = motors[i];
            } else {
                allMotors[i] = null;
            }
        }
    }

    public static void setPosition(DcMotorEx motor, int position, double power)
    {
        motor.setTargetPosition(position);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(power);
    }

    public static void goToPosition(DcMotorEx motor, int position, double power) {
        // Set target position
        motor.setTargetPosition(position);

        // Run to position
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(power);

        // Wait until the motor reaches the target
        while (motor.isBusy()) {
            // You might want to add an OpMode idle() or Thread.yield() here
            // if running inside a LinearOpMode
        }

        // Stop applying power so it doesn't hold
        motor.setPower(0);

        // Switch back to normal mode (so it's free afterwards)
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


}
