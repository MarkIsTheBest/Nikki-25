package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;

public class Intake {

    private final LinearOpMode opMode;
    private final DcMotorEx intake;
    private boolean isIntaking;
    private boolean isEjecting;

    public Intake(LinearOpMode OpMode, Hardware hwMap) {
        opMode = OpMode;
        intake = hwMap.Motors().Intake();
    }

    public void input() {
        if(opMode.gamepad1.aWasPressed()) {
            toggleIntake();
        }

        if(opMode.gamepad1.b) {
            intake.setPower(-1);
            isEjecting = true;
        }
        if(opMode.gamepad1.bWasReleased()) {
            isEjecting = false;
        }
    }

    public void toggleIntake() {
        if(isIntaking && !isEjecting) {
            isIntaking = false;
        }
        else {
            isIntaking = true;
        }
    }

    public void setIntake(boolean value) {
        isIntaking = value;
    }
    public void update() {

        if(isIntaking) {
            intake.setPower(1);
        }
        else {
            intake.setPower(0);
        }

        if(isEjecting) {
            intake.setPower(-1);
        }
        else if(!isIntaking) {
            intake.setPower(0);
        }
    }

    public boolean isIntakeing() {
        return isIntaking;
    }
}
