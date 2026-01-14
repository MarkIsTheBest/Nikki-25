package org.firstinspires.ftc.teamcode.helper.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import java.util.Arrays;
import java.util.List;

public class Motors {
    public static DcMotorEx leftFront, leftRear, rightFront, rightRear, launcher1, launcher2, intake;
    public static List<DcMotorEx> allDriveMotors;
    public static List<DcMotorEx> launchers;

    public static void init(HardwareMap hwMap) {
        // Use tryGet to prevent crashing if one motor is missing
        leftFront = hwMap.tryGet(DcMotorEx.class, "leftFront");
        leftRear = hwMap.tryGet(DcMotorEx.class, "leftRear");
        rightFront = hwMap.tryGet(DcMotorEx.class, "rightFront");
        rightRear = hwMap.tryGet(DcMotorEx.class, "rightRear");
        launcher1 = hwMap.tryGet(DcMotorEx.class, "launcher1");
        launcher2 = hwMap.tryGet(DcMotorEx.class, "launcher2");
        intake = hwMap.tryGet(DcMotorEx.class, "intake");

        allDriveMotors = Arrays.asList(leftFront, leftRear, rightFront, rightRear);
        launchers = Arrays.asList(launcher1, launcher2);

        // Batch configuration
        for (DcMotorEx motor : allDriveMotors) {
            if (motor == null) continue;
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        if (leftFront != null) leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        if (leftRear != null) leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        if (launcher1 != null) launcher1.setDirection(DcMotorSimple.Direction.REVERSE);
        if (intake != null) intake.setDirection(DcMotorSimple.Direction.REVERSE);
    }
}