package org.firstinspires.ftc.teamcode.opModes.tests;

import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.helper.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.DistanceSensors;

import java.util.Arrays;

enum IntakeStep {
    PREPARE_DOORS,
    PARTIAL,
    CLOSE
}

@TeleOp
public class IntakeTest extends LinearOpMode {

    private final double L_H_PREPARE = (0.23 + 1) / 2;
    private final double L_H_PARTIAL = (-0.58 + 1) / 2;
    private final double L_H_CLOSE = (-0.1 + 1) / 2;

    // -- LEFT INTAKE SERVO POSITIONS -- \\
    private final double L_D1_PREPARE = (0.0 + 1) / 2;
    private final double L_D2_PREPARE = (-0.529 + 1) / 2;

    private final double L_D1_PARTIAL = (0.597 + 1) / 2;

    // -- CENTER INTAKE SERVO POSITIONS -- \\
    private final double C_D1_PREPARE = (0.56 + 1) / 2;
    private final double C_D2_PREPARE = (-0.658 + 1) / 2;

    // -- RIGHT INTAKE SERVO POSITIONS -- \\
    private final double R_D1_PREPARE = (0.408 + 1) / 2;
    private final double R_D2_PREPARE = (-0.046 + 1) / 2;

    private final double R_D2_PARTIAL = (-0.0752 + 1) / 2;

    boolean[] filledLaunchers = {false,false,false};
    IntakeStep currentStep = IntakeStep.PREPARE_DOORS;
    Timer intakeTimer = new Timer();

    boolean hasBall = false;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        Hardware.init();
        initPositions();
    }

    private void play() {
        // start logic
    }

    private void changeStep(IntakeStep newStep) {
        currentStep = newStep;
        intakeTimer.resetTimer();
    }

    private void initPositions() {
        Servos.Holder1().setPosition(L_H_PREPARE);
        Servos.Holder2().setPosition(L_H_PREPARE);
        Servos.Holder3().setPosition(L_H_PREPARE);

        Servos.Door1().setPosition(C_D1_PREPARE);
        Servos.Door2().setPosition(C_D2_PREPARE);
    }

    private void intakeLeft() {
        switch (currentStep) {
            case PREPARE_DOORS:
                Servos.Door1().setPosition(L_D1_PREPARE);
                Servos.Door2().setPosition(L_D2_PREPARE);

                changeStep(IntakeStep.PARTIAL);
                break;
            case PARTIAL:
                if(intakeTimer.getElapsedTime() > 0.5) {
                    Servos.Door1().setPosition(L_D1_PARTIAL);
                    //if door1 reached position (absolute encoder cable)
                    Servos.Holder1().setPosition(L_H_PARTIAL);

                    Servos.Door1().setPosition(C_D1_PREPARE);
                    Servos.Door2().setPosition(C_D2_PREPARE);

                    changeStep(IntakeStep.CLOSE);
                }
                break;
            case CLOSE:
                // if door1 reached position
                Servos.Holder1().setPosition(L_H_CLOSE);
                filledLaunchers[0] = true;
                hasBall = false;
                break;
        }
    }

    private void intakeCenter() {
        switch (currentStep) {
            case PREPARE_DOORS:
                Servos.Door1().setPosition(C_D1_PREPARE);
                Servos.Door2().setPosition(C_D2_PREPARE);

                changeStep(IntakeStep.CLOSE);
                break;
            case PARTIAL:
                break;
            case CLOSE:
                if(intakeTimer.getElapsedTimeSeconds() > 0.5) {
                    Servos.Holder2().setPosition(L_H_CLOSE);
                    filledLaunchers[1] = true;
                    hasBall = false;
                }
                break;
        }
    }

    private void intakeRight() {
        switch (currentStep) {
            case PREPARE_DOORS:
                Servos.Door1().setPosition(R_D1_PREPARE);
                Servos.Door2().setPosition(R_D2_PREPARE);

                changeStep(IntakeStep.PARTIAL);
                break;
            case PARTIAL:
                if(intakeTimer.getElapsedTime() > 0.5) {
                    Servos.Door2().setPosition(R_D2_PARTIAL);
                    //if door1 reached position (absolute encoder cable)
                    Servos.Holder3().setPosition(L_H_PARTIAL);

                    Servos.Door1().setPosition(C_D1_PREPARE);
                    Servos.Door2().setPosition(C_D2_PREPARE);

                    changeStep(IntakeStep.CLOSE);
                }
                break;
            case CLOSE:
                // if door2 reached position
                Servos.Holder3().setPosition(L_H_CLOSE);
                filledLaunchers[2] = true;
                hasBall = false;
                break;
        }
    }

    private void intake() {
        for (int i = 0; i < filledLaunchers.length; i++) {
            if(filledLaunchers[i]) continue;

            if(i==0) {
                intakeLeft();
                return;
            }
            if(i==1) {
                intakeCenter();
                return;
            }
            if(i==2) {
                intakeRight();
                return;
            }
        }
    }

    private void update() {

        if(hasBall) intake();

        double distance = DistanceSensors.Min(DistanceSensors.Left(), DistanceSensors.Right(), DistanceUnit.INCH);
        if(!hasBall) hasBall = distance < 7;

        // loop logic
        if(gamepad1.aWasPressed()) Motors.Intake().setPower(1);
        if(gamepad1.bWasPressed()) Motors.Intake().setPower(0);

        if(gamepad1.xWasPressed()) {
            initPositions();
            Arrays.fill(filledLaunchers, false);
        }


        Debug.INSTANCE.addData("left", DistanceSensors.getDistance(DistanceSensors.Left(), DistanceUnit.INCH));
        Debug.INSTANCE.addData("right", DistanceSensors.getDistance(DistanceSensors.Left(), DistanceUnit.INCH));
        Debug.INSTANCE.addData("Ball inside", distance < 7);
        Debug.INSTANCE.update();
    }
}