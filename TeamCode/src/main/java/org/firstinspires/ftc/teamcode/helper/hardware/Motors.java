package org.firstinspires.ftc.teamcode.helper.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.helper.general.Debug;
import dev.nextftc.ftc.ActiveOpMode;

public class Motors
{
    private static DcMotorEx leftFront; public static DcMotorEx LeftFront() { return leftFront; }
    private static DcMotorEx leftRear; public static DcMotorEx LeftRear() { return leftRear; }
    private static DcMotorEx rightFront; public static DcMotorEx RightFront() { return rightFront; }
    private static DcMotorEx rightRear; public static DcMotorEx RightRear() { return rightRear; }
    private static DcMotorEx launcher1; public static DcMotorEx Launcher1() { return launcher1; }
    private static DcMotorEx launcher2; public static DcMotorEx Launcher2() { return launcher2; }
    private static DcMotorEx intake; public static DcMotorEx Intake() { return intake; }

    private static DcMotorEx[] allMotors; public static DcMotorEx[] AllMotors() { return allMotors; }
    private static DcMotorEx[] launchers; public static DcMotorEx[] Launchers() { return launchers; }

    public static void init() {
        try {
            getHardware(ActiveOpMode.hardwareMap());
            setAllMotors();
            setDirection();
            setZeroPowerBehaviour();
        } catch (Exception ex) {
        }
    }

    private static void getHardware(HardwareMap hardwareMap) {
        leftFront = hardwareMap.tryGet(DcMotorEx.class, "leftFront");
        leftRear = hardwareMap.tryGet(DcMotorEx.class, "leftRear");
        rightFront = hardwareMap.tryGet(DcMotorEx.class, "rightFront");
        rightRear = hardwareMap.tryGet(DcMotorEx.class, "rightRear");
        launcher1 = hardwareMap.tryGet(DcMotorEx.class, "launcher1");
        launcher2 = hardwareMap.tryGet(DcMotorEx.class, "launcher2");
        intake = hardwareMap.tryGet(DcMotorEx.class, "intake");
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

        launcher1.setDirection(DcMotorSimple.Direction.REVERSE);
        launcher2.setDirection(DcMotorSimple.Direction.FORWARD);

        intake.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private static void setAllMotors() {
        allMotors = new DcMotorEx[]{leftFront, rightFront, leftRear, rightRear, launcher1, launcher2, intake};

        launchers = new DcMotorEx[]{launcher1, launcher2};
    }
}