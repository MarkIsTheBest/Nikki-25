package tests.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import subsystems.Func;
import subsystems.Input;
import subsystems.hardware.Motors;
import subsystems.hardware.Servos;

@Config
@TeleOp
public class TeleOpTest extends LinearOpMode
{
    private double linkagePos;
    private double rotateAxisPos;
    private double rotateBodyPos;
    private double rotateHeadPos;
    private double rotateClawPos;
    private double clawPos;
    private int verticalPos;

    private boolean isSpecimen = false;
    private boolean attachSpecimen = false;
    private ElapsedTime timer = new ElapsedTime();
    private Telemetry debug;

    public enum State
    {
        INIT,

        PREPARE_SAMPLE,
        PICKUP_SAMPLE,
        LEAVE_SAMPLE_BASKET,
        LEAVE_SAMPLE_OBSERVATION,

        TRANSFER,

        OPEN_CLAW,
        CLOSE_CLAW,
        HOLD_SAMPLE,

        PREPARE_SPECIMEN,
        PICKUP_SPECIMEN,
        PREPARE_LEAVE_SPECIMEN,
        LEAVE_SPECIMEN
    }

    State intakeState = State.INIT;
    @Override
    public void runOpMode() throws InterruptedException
    {
        onInit();
        waitForStart();
        onStart();
        while (opModeIsActive())
        {
            onUpdate();
        }
    }

    private void onStart()
    {
        intakeState = State.PREPARE_SAMPLE;
    }

    private void onInit()
    {
        debug = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        initHardware();
        initPositions();
        updatePositions();
    }

    private void onUpdate()
    {
        if(Input.onKeyDown("scorer_b",gamepad2.b)) intakeState = State.PREPARE_SAMPLE;

        updatePositions();
        checkStates();
    }

    private void initHardware()
    {
        Motors.init(hardwareMap);
        Servos.init(hardwareMap);
    }

    private void initPositions()
    {

    }

    private void checkStates()
    {
        if(isSpecimen && !attachSpecimen) specimenPickupStates();
        else if (isSpecimen) specimenAttachStates();
        else sampleStates();
    }

    private void sampleStates()
    {
        switch (intakeState)
        {
            case PREPARE_SAMPLE:
                if(Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.PICKUP_SAMPLE;
                break;
            case PICKUP_SAMPLE:
                timer.reset();
                if(timer.seconds() > 0.1) intakeState = State.OPEN_CLAW;
                break;
            case OPEN_CLAW:
                if(Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.HOLD_SAMPLE;
                break;
            case HOLD_SAMPLE:
                if(Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.LEAVE_SAMPLE_BASKET;
                break;
            case LEAVE_SAMPLE_BASKET:
                timer.reset();
                if(timer.seconds() > 0.1) intakeState = State.CLOSE_CLAW;
                break;
            case CLOSE_CLAW:
                if(Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.PREPARE_SAMPLE;
                break;
        }
    }

    private void specimenPickupStates()
    {
        switch (intakeState)
        {
            case PREPARE_SAMPLE:
                if(Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.PICKUP_SAMPLE;
                break;
            case PICKUP_SAMPLE:
                timer.reset();
                if(timer.seconds() > 0.1) intakeState = State.OPEN_CLAW;
                break;
            case OPEN_CLAW:
                if(Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.HOLD_SAMPLE;
                break;
            case HOLD_SAMPLE:
                if(Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.LEAVE_SAMPLE_BASKET;
                break;
            case LEAVE_SAMPLE_OBSERVATION:
                timer.reset();
                if(timer.seconds() > 0.1) intakeState = State.CLOSE_CLAW;
                break;
            case CLOSE_CLAW:
                if(Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.PREPARE_SAMPLE;
                break;
        }
    }

    private void specimenAttachStates()
    {
        switch (intakeState)
        {
            case PREPARE_SPECIMEN:
                break;
            case PICKUP_SPECIMEN:
                break;
            case TRANSFER:
                break;
            case PREPARE_LEAVE_SPECIMEN:
                break;
            case LEAVE_SPECIMEN:
                break;
        }
    }

    private void updatePositions()
    {
        Servos.linkageLeft.setPosition(linkagePos);
        Servos.linkageRight.setPosition(linkagePos);
        Servos.rotateAxis.setPosition(rotateAxisPos);
        Servos.rotateBody.setPosition(rotateBodyPos);
        Servos.rotateHead.setPosition(rotateHeadPos);
        Servos.rotateClaw.setPosition(rotateClawPos);
        Servos.claw.setPosition(clawPos);
        Func.SetMotorPosition(Motors.verticalLeft, verticalPos);
        Func.SetMotorPosition(Motors.verticalRight, verticalPos);
    }

}
