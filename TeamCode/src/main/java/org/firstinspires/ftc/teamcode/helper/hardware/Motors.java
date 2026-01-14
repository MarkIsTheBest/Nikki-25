package org.firstinspires.ftc.teamcode.helper.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import dev.nextftc.ftc.ActiveOpMode;
import java.util.HashMap;
import java.util.Map;

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

    // Optimization: Cache power levels to prevent duplicate hardware writes
    private static final Map<DcMotorEx, Double> powerCache = new HashMap<>();

    public static void init() {
        powerCache.clear(); // Reset cache on init
        try {
            getHardware(ActiveOpMode.hardwareMap());
            setAllMotors();
            setDirection();
            setZeroPowerBehaviour();
        } catch (Exception ex) {
            // Log error if needed
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
        if(leftFront != null) leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        if(leftRear != null) leftRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        if(rightFront != null) rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        if(rightRear != null) rightRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
    }

    private static void setDirection() {
        if(leftFront != null) leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        if(leftRear != null) leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        if(rightFront != null) rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        if(rightRear != null) rightRear.setDirection(DcMotorSimple.Direction.FORWARD);

        if(launcher1 != null) launcher1.setDirection(DcMotorSimple.Direction.REVERSE);
        if(launcher2 != null) launcher2.setDirection(DcMotorSimple.Direction.FORWARD);

        if(intake != null) intake.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private static void setAllMotors() {
        // Filter out nulls to prevent crashes later
        allMotors = new DcMotorEx[]{leftFront, rightFront, leftRear, rightRear, launcher1, launcher2, intake};
        launchers = new DcMotorEx[]{launcher1, launcher2};
    }

    /**
     * Optimized setPower that checks cache before writing to hardware.
     */
    public static void setPower(DcMotorEx motor, double power) {
        if (motor == null) return;

        Double cached = powerCache.get(motor);
        // Only write if power changed by more than 0.005 (0.5%)
        if (cached == null || Math.abs(cached - power) > 0.005) {
            motor.setPower(power);
            powerCache.put(motor, power);
        }
    }
}