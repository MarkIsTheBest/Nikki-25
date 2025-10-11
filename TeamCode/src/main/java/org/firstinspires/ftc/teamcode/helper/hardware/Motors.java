package org.firstinspires.ftc.teamcode.helper.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.helper.Debug;
import org.firstinspires.ftc.teamcode.helper.MotorHelper;

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
    private DcMotorEx launcher1; public DcMotorEx Launcher1() { return launcher1; }
    private DcMotorEx launcher2; public DcMotorEx Launcher2() { return launcher2; }
    private DcMotorEx intake; public DcMotorEx Intake() { return intake; }

    private DcMotorEx[] allMotors = new DcMotorEx[7]; public DcMotorEx[] AllMotors() { return allMotors; }

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
        launcher1 = hardwareMap.tryGet(DcMotorEx.class, "launcher1");
        launcher2 = hardwareMap.tryGet(DcMotorEx.class, "launcher2");
        intake = hardwareMap.tryGet(DcMotorEx.class, "intake");
    }

    private void setZeroPowerBehaviour() {
        leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        launcher1.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        launcher2.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
    }

    private void setDirection() {
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightRear.setDirection(DcMotorSimple.Direction.FORWARD);
        launcher1.setDirection(DcMotorSimple.Direction.FORWARD);
        launcher2.setDirection(DcMotorSimple.Direction.REVERSE);
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    private void setAllMotors() {
        allMotors = new DcMotorEx[]{leftFront, rightFront, leftRear, rightRear, launcher1, launcher2, intake};
    }

    public void setRPM(
            DcMotorEx motor,
            double targetRpm,
            double motorTicksPerRev,
            double motorMaxRPM,
            PIDCoefficients pid
    ) {
        MotorHelper.setRPM(motor, targetRpm, motorTicksPerRev, motorMaxRPM, pid);
    }

    public void setSlidePosition(
            DcMotorEx motor,
            double targetPosition,
            PIDFCoefficients pidf
    ) {
        MotorHelper.setSlidePosition(motor, targetPosition, pidf);
    }

    public void setArmAngle(
            DcMotorEx motor,
            double targetAngle,
            double motorTicksPerRev,
            double power,
            PIDFCoefficients pidf
    ) {
        MotorHelper.setArmAngle(motor, targetAngle, motorTicksPerRev, power, pidf);
    }
}