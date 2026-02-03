package org.firstinspires.ftc.teamcode.helper.hardware.actuators;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.constants.Control;

import dev.nextftc.ftc.ActiveOpMode;

public class MotorHelper {
    private DcMotorEx leftFront; public DcMotorEx LeftFront() { return leftFront; }
    private DcMotorEx leftRear; public DcMotorEx LeftRear() { return leftRear; }
    private DcMotorEx rightFront; public DcMotorEx RightFront() { return rightFront; }
    private DcMotorEx rightRear; public DcMotorEx RightRear() { return rightRear; }

    private DcMotorEx leftLauncher; public DcMotorEx LeftLauncher() { return leftLauncher; }
    private DcMotorEx rightLauncher; public DcMotorEx RightLauncher() { return rightLauncher; }
    private DcMotorEx turret; public DcMotorEx Turret() { return turret; }
    private DcMotorEx intake; public DcMotorEx Intake() { return intake; }

    private DcMotorEx[] allMotors; public DcMotorEx[] AllMotors() { return allMotors; }
    private DcMotorEx[] launchers; public DcMotorEx[] Launchers() { return launchers; }


    public MotorHelper() {
        init();
    }

    public void init() {
        getHardware(ActiveOpMode.hardwareMap());
        setAllMotors();
        setDirection();
        setZeroPowerBehaviour();
        setPIDF();
        setMode();
    }

    private void setPIDF() {
        PIDFCoefficients vel = Control.Flywheel.pidf;
        leftLauncher.setVelocityPIDFCoefficients(vel.P, vel.I, vel.D, vel.F);
        rightLauncher.setVelocityPIDFCoefficients(vel.P, vel.I, vel.D, vel.F);
    }

    private void setMode() {
        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftLauncher.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightLauncher.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void getHardware(HardwareMap hardwareMap) {
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftRear = hardwareMap.get(DcMotorEx.class, "leftRear");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        rightRear = hardwareMap.get(DcMotorEx.class, "rightRear");

        intake = hardwareMap.get(DcMotorEx.class, "intake");
        turret = hardwareMap.get(DcMotorEx.class, "turret");

        leftLauncher = hardwareMap.get(DcMotorEx.class, "launcherLeft");
        rightLauncher = hardwareMap.get(DcMotorEx.class, "launcherRight");
    }

    private void setZeroPowerBehaviour() {
        leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        leftLauncher.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        rightLauncher.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
    }

    private void setDirection() {
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightRear.setDirection(DcMotorSimple.Direction.FORWARD);

        leftLauncher.setDirection(DcMotorSimple.Direction.FORWARD);
        rightLauncher.setDirection(DcMotorSimple.Direction.REVERSE);

        intake.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private void setAllMotors() {
        allMotors = new DcMotorEx[]{leftFront, rightFront, leftRear, rightRear, leftLauncher, rightFront, intake, turret};
        launchers = new DcMotorEx[]{leftLauncher, rightLauncher};
    }

    public void setLauncherRPM(double rpm, boolean updatePIDF) {
        if (updatePIDF) {
            setPIDF();
        }

        for (DcMotorEx launcher : launchers) {
            launcher.setVelocity(rpm, AngleUnit.DEGREES);
        }
    }

    public void setLauncherRPM(double rpm) {
        setLauncherRPM(rpm, false);
    }
}