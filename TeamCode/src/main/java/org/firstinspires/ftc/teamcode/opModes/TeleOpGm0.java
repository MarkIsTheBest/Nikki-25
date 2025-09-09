package org.firstinspires.ftc.teamcode.opModes;

import static org.firstinspires.ftc.teamcode.subsystems.Positions.CLAW_CLOSED;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.CLAW_OPENED;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.EXTEND_SLIDER_FIRST_WORD;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.EXTEND_SLIDER_FOURTH_WORD;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.EXTEND_SLIDER_MAX_EXTEND;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.EXTEND_SLIDER_MIN_EXTEND;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.EXTEND_SLIDER_PYRAMID;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.EXTEND_SLIDER_SECOND_WORD;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.EXTEND_SLIDER_THIRD_WORD;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.ROTATE_SLIDER_INIT;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.ROTATE_SLIDER_INTAKE;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.ROTATE_SLIDER_OUTTAKE_FIRST_WORD;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.ROTATE_SLIDER_OUTTAKE_FOURTH_WORD;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.ROTATE_SLIDER_OUTTAKE_PYRAMID;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.ROTATE_SLIDER_OUTTAKE_SECOND_WORD;
import static org.firstinspires.ftc.teamcode.subsystems.Positions.ROTATE_SLIDER_OUTTAKE_THIRD_WORD;
//import static org.firstinspires.ftc.teamcode.subsystems.Positions.ROTATE_SLIDER_OUTTAKE;

import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.hardware.Motors;
import org.firstinspires.ftc.teamcode.subsystems.hardware.Servos;

@TeleOp
public class TeleOpGm0 extends LinearOpMode {

    private boolean oneController = true;
    private int currentState = 0;
    private int lastState = -1;

    private long lastLoopTime = 0;
    private double deltaTime = 0; // in seconds

    private Timer pathTimer;

    private boolean word1 = true;
    private boolean word2;
    private boolean word3;
    private boolean word4;
    private boolean pyramid;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();

        if (isStopRequested()) return;

        lastLoopTime = System.nanoTime();

        while (opModeIsActive()) {
            updateDeltaTime();
            update();
        }
    }

    public void setPathState(int pState) {
        currentState = pState;
        pathTimer.resetTimer();
    }

    private void updateCurrentWord()
    {
        if(gamepad2.xWasPressed())
        {
            word1 = true;
            word2 = false;
            word3 = false;
            word4 = false;
            pyramid = false;
        }
        if(gamepad2.yWasPressed())
        {
            word1 = false;
            word2 = true;
            word3 = false;
            word4 = false;
            pyramid = false;
        }
        if(gamepad2.rightBumperWasPressed())
        {
            word1 = false;
            word2 = false;
            word3 = true;
            word4 = false;
            pyramid = false;
        }
        if(gamepad2.touchpadWasPressed())
        {
            word1 = false;
            word2 = false;
            word3 = false;
            word4 = false;
            pyramid = true;
        }
        if(gamepad2.leftBumperWasPressed())
        {
            word1 = false;
            word2 = false;
            word3 = false;
            word4 = true;
            pyramid = false;
        }
    }

    private void updateDrive()
    {
        double y = oneController ? (gamepad1.dpad_up ? 1 : (gamepad1.dpad_down ? -1 : 0)) : -gamepad1.left_stick_y;
        double x = oneController ? (gamepad1.dpad_right ? 1 : (gamepad1.dpad_left ? -1 : 0)) : gamepad1.left_stick_x * 1.1;
        double rx = oneController ? gamepad1.left_stick_x : gamepad1.right_stick_x;

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        double speedScale = ((oneController ? gamepad1.left_trigger > 0.1 : gamepad1.right_trigger > 0.1)) ? 0.33 : 0.7;

        Motors.leftFront.setPower(frontLeftPower * speedScale);
        Motors.leftRear.setPower(backLeftPower * speedScale);
        Motors.rightFront.setPower(frontRightPower * speedScale);
        Motors.rightRear.setPower(backRightPower * speedScale);
    }

    private void updateStateMachine()
    {
        if(oneController ? gamepad1.right_trigger > 0.1 : gamepad2.right_trigger > 0.1)
        {
            if(Motors.extendSlider.getTargetPosition() > 0) {
                Motors.setPosition(Motors.extendSlider, Motors.extendSlider.getCurrentPosition() + (int) (deltaTime * 50000), 1);
            }
            else {
                Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_MAX_EXTEND, 1);

            }
        }

        if(gamepad2.left_trigger > 0.1)
        {
            if(Motors.extendSlider.getTargetPosition() > 0)
            {
                Motors.setPosition(Motors.extendSlider,Motors.extendSlider.getCurrentPosition() - (int)(deltaTime * 50000), 1);
            }
            else
            {
                Motors.setPosition(Motors.extendSlider,0, 1);
            }
        }

        if(gamepad2.dpad_down)
        {
            Motors.setPosition(Motors.rotateSlider,Motors.rotateSlider.getCurrentPosition() + (int)(deltaTime * 25000), 1);
        }

        if(gamepad2.dpad_up)
        {
            Motors.setPosition(Motors.rotateSlider,Motors.rotateSlider.getCurrentPosition() - (int)(deltaTime * 25000), 1);
        }

        if(oneController ? gamepad1.bWasPressed() : gamepad2.bWasPressed())
        {
            currentState = 1;
            Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_MIN_EXTEND, 1);
        }

        switch (currentState)
        {
            case 0:
                if(lastState != currentState)
                {
                    Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_MIN_EXTEND, 1);
                    Motors.setPosition(Motors.rotateSlider, ROTATE_SLIDER_INIT, 1);
                    Servos.claw.setPosition(CLAW_OPENED);
                }
                lastState = currentState;
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed()) setPathState(1);
                break;

            case 1:
                if(lastState != currentState)
                {
                    Motors.setPosition(Motors.rotateSlider, ROTATE_SLIDER_INTAKE,1 );
                    Servos.claw.setPosition(CLAW_OPENED);
                }
                lastState = currentState;
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed()) setPathState(2);

                break;

            case 2:
                if(lastState != currentState)
                {
                    if(!pyramid)
                    {
                        Motors.setPosition(Motors.extendSlider, 2000,1 );
                    } else
                    {
                        Motors.setPosition(Motors.extendSlider, 500,1 );
                    }

                    Motors.goToPosition(Motors.rotateSlider, ROTATE_SLIDER_INTAKE,1 );
                    Servos.claw.setPosition(CLAW_OPENED);
                }
                lastState = currentState;
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed())
                {
                    if(!pyramid) setPathState(3);
                    else setPathState(300);
                }
                break;

            case 300:
                if(lastState != currentState)
                {
                    Servos.claw.setPosition(0.5);
                }
                lastState = currentState;
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed()) setPathState(4);
                break;

            case 3:
                if(lastState != currentState)
                {
                    Servos.claw.setPosition(CLAW_CLOSED);
                }
                lastState = currentState;
                if(pathTimer.getElapsedTimeSeconds() > 0.5) setPathState(4);
                break;

            case 4:
                if(lastState != currentState)
                {
                    Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_MIN_EXTEND,1 );
                    Motors.goToPosition(Motors.rotateSlider, 0, 1);
                }
                lastState = currentState;

                if(!Motors.extendSlider.isBusy() && !Motors.rotateSlider.isBusy())
                {
                    if(word1) setPathState(5);
                    else if(word2) setPathState(6);
                    else if(word3) setPathState(7);
                    else if(word4) setPathState(8);
                    else if(pyramid) setPathState(100);
                }
                break;

            case 100:
                if(lastState != currentState)
                {
                    Motors.goToPosition(Motors.rotateSlider, ROTATE_SLIDER_OUTTAKE_PYRAMID, 1);
                }
                lastState = currentState;
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed()) setPathState(101);
                break;

            case 101:
                if(lastState != currentState) {
                    Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_PYRAMID, 1);
                }
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed()) setPathState(9);
                break;

            case 5:
                if(lastState != currentState)
                {
                    Motors.goToPosition(Motors.rotateSlider, ROTATE_SLIDER_OUTTAKE_FIRST_WORD, 1);
                }
                lastState = currentState;
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed())
                {
                    if(word1) setPathState(-5);
                    else if(word2) setPathState(6);
                    else if(word3) setPathState(7);
                    else if(word4) setPathState(8);
                    else if(pyramid) setPathState(100);
                }
                break;

            case -5:
                if(lastState != currentState)
                {
                    Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_FIRST_WORD,1 );
                }
                lastState = currentState;
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed()) setPathState(9);
                break;

            case 6:
                if(lastState != currentState)
                {
                    Motors.goToPosition(Motors.rotateSlider, ROTATE_SLIDER_OUTTAKE_SECOND_WORD, 1);
                }
                lastState = currentState;
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed())
                {
                    if(word1) setPathState(5);
                    else if(word2) setPathState(-6);
                    else if(word3) setPathState(7);
                    else if(word4) setPathState(8);
                    else if(pyramid) setPathState(100);
                }
                break;

            case -6:
                if(lastState != currentState)
                {
                    Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_SECOND_WORD,1 );
                }
                lastState = currentState;
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed()) setPathState(9);
                break;

            case 7:
                if(lastState != currentState)
                {
                    Motors.goToPosition(Motors.rotateSlider, ROTATE_SLIDER_OUTTAKE_THIRD_WORD, 1);
                }
                lastState = currentState;
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed())
                {
                    if(word1) setPathState(5);
                    else if(word2) setPathState(6);
                    else if(word3) setPathState(-7);
                    else if(word4) setPathState(8);
                    else if(pyramid) setPathState(100);
                }
                break;

            case -7:
                if(lastState != currentState)
                {
                    Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_THIRD_WORD,1 );
                }
                lastState = currentState;
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed()) setPathState(9);
                break;

            case 8:
                if(lastState != currentState)
                {
                    Motors.goToPosition(Motors.rotateSlider, ROTATE_SLIDER_OUTTAKE_FOURTH_WORD, 1);
                }
                lastState = currentState;
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed())
                {
                    if(word1) setPathState(5);
                    else if(word2) setPathState(6);
                    else if(word3) setPathState(7);
                    else if(word4) setPathState(-8);
                    else if(pyramid) setPathState(100);
                }
                break;

            case -8:
                if(lastState != currentState)
                {
                    Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_FOURTH_WORD,1 );
                }
                lastState = currentState;
                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed()) setPathState(9);
                break;


            case 9:
                if(lastState != currentState)
                {
                    Servos.claw.setPosition(CLAW_OPENED);
                }
                lastState = currentState;

                if(oneController ? gamepad1.aWasPressed() : gamepad2.aWasPressed()) setPathState(10);
                break;

            case 10:
                if(lastState != currentState)
                {
                    Motors.goToPosition(Motors.extendSlider, EXTEND_SLIDER_MIN_EXTEND, 1);
                }
                lastState = currentState;
                if(!Motors.extendSlider.isBusy()) setPathState(1);
                break;

        }
    }

    private void updateDeltaTime() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastLoopTime) / 1e9; // convert ns → seconds
        lastLoopTime = currentTime;
    }

    private void initialize()
    {
        Servos.init(hardwareMap);
        Motors.init(hardwareMap);
        pathTimer = new Timer();

        Motors.setPosition(Motors.extendSlider, EXTEND_SLIDER_MIN_EXTEND, 1);
        Motors.goToPosition(Motors.rotateSlider, ROTATE_SLIDER_INIT, 1);
        Servos.claw.setPosition(CLAW_OPENED);
    }

    private void update()
    {
        updateDrive();
        updateCurrentWord();
        updateStateMachine();

        if(gamepad1.shareWasPressed()) oneController = !oneController;

        telemetry.addData("Current State", currentState);
        telemetry.addData("word1 pressed", word1);
        telemetry.addData("word2 pressed", word2);
        telemetry.addData("word3 pressed", word3);
        telemetry.addData("word4 pressed", word4);
        telemetry.addData("pyramid pressed", pyramid);
        telemetry.addData("delta time", deltaTime);
        telemetry.addData("first word", oneController);
        telemetry.addData("extendMotor", Motors.extendSlider.getCurrentPosition());
        telemetry.addData("rotate", Motors.rotateSlider.getCurrentPosition());

        telemetry.update();
    }
}