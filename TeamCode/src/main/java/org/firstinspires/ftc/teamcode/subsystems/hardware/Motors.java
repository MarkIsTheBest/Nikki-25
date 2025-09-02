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

    public static DcMotorEx[] allMotors = new DcMotorEx[4];

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
    }

    private static void setZeroPowerBehaviour() {
        leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
    }

    private static void setDirection() {
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightRear.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    private static void setAllMotors() {
        DcMotorEx[] motors = {leftFront, rightFront, leftRear, rightRear};

        for (int i = 0; i < motors.length; i++) {
            if (motors[i] != null) {
                allMotors[i] = motors[i];
            } else {
                allMotors[i] = null;
            }
        }
    }

}
