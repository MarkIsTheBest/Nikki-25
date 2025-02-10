package tests.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import subsystems.Constants;
import subsystems.Func;
import subsystems.Input;
import subsystems.hardware.Motors;
import subsystems.hardware.Servos;

@Config
@TeleOp
public class TeleOpTestMihnea extends LinearOpMode
{
    private double linkagePos;
    private double rotateAxisPos;
    private double rotateBodyPos;
    private double rotateHeadPos;
    private double rotateClawPos;
    private double clawPos;
    private double rotateBackBodyPos;
    private double rotateBackClawPos;
    private double backClawPos;
    private int verticalPos;

    private boolean isSpecimen = true;
    private boolean attachSpecimen = false;
    private boolean isParallel = false;
    private final ElapsedTime timer = new ElapsedTime();
    private final ElapsedTime deltaTime = new ElapsedTime();
    private final STATES robotStates = new STATES();
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
        LEAVE_SPECIMEN,
        DO_TRANSFER,
        TRANSFER2,
        RAISE_VERTICAL,
        LEAVE_SPECIMEN_RANK,
        LOWER_VERTICAL
    }

    State intakeState = State.INIT;
    State lastState = intakeState;

    private void handleMovement()
    {

        int invertInput = Input.isDown("chassis_right_bumper", gamepad1.right_bumper) ? 1 : -1;
        double forwardInput = (-gamepad1.left_stick_y) * invertInput;
        double lateralInput = (gamepad1.left_stick_x * 1.1) * invertInput;
        double angularInput = -gamepad1.right_stick_x;

        double denominator = Math.max(Math.abs(forwardInput) + Math.abs(lateralInput) + Math.abs(angularInput), 1);
        double frontLeftPower = ((forwardInput + lateralInput + angularInput) / denominator);
        double backLeftPower = ((forwardInput - lateralInput + angularInput) / denominator);
        double frontRightPower = ((forwardInput - lateralInput - angularInput) / denominator);
        double backRightPower = ((forwardInput + lateralInput - angularInput) / denominator);

        Motors.leftFront.setPower(frontLeftPower);
        Motors.leftRear.setPower(backLeftPower);
        Motors.rightFront.setPower(-frontRightPower);
        Motors.rightRear.setPower(-backRightPower);
    }

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
        intakeState = State.PREPARE_SPECIMEN;
    }

    private void onInit()
    {
        debug = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        initHardware();
        robotStates.init();
        updatePositions();
    }

    private void initHardware()
    {
        Motors.init(hardwareMap);
        Servos.init(hardwareMap);
    }

    private void onUpdate()
    {
        handleMovement();

        manualManipulation();

        if(Input.onKeyDown("scorer_b",gamepad1.b)) intakeState = isSpecimen ? State.PREPARE_SPECIMEN : State.PREPARE_SAMPLE;
        if(Input.onKeyDown("scorer_b",gamepad1.dpad_up)) {
            isSpecimen=!isSpecimen;
            intakeState = isSpecimen ? State.PREPARE_SPECIMEN : State.PREPARE_SAMPLE;
        }


        updatePositions();
        checkStates();
        if (isParallel) keepParallel();

        onDebug();

        if(Input.onKeyDown("scorer_x", gamepad1.x))
        {
            verticalPos = 1500;
        }
        if(Input.onKeyDown("scorer_y", gamepad1.y))
        {
            verticalPos = 800;
            linkagePos = Constants.LINKAGE.CLOSED;
            rotateBodyPos = Constants.ROTATE_BODY.MAX;
        }
    }

    private void onDebug()
    {
        debug.addData("CurrentState", intakeState);
        debug.addData("CurrentState2", isSpecimen);
        debug.addData("Timer", timer.seconds());
        debug.addData("Motor Encoder Ticks", Motors.verticalLeft.getCurrentPosition());
        debug.update();
    }

    private void keepParallel()
    {
        rotateHeadPos = getParallel(rotateBodyPos);
    }

    private void manualManipulation()
    {
        //double angleJoystick = Math.toDegrees(Math.atan2(gamepad1.right_stick_y, gamepad1.right_stick_x));

        double timeStep = deltaTime.milliseconds() / 1000.0; // Convert to seconds
        deltaTime.reset();

        double linkageInput = gamepad1.right_stick_y;
        double clawRotateInput = -gamepad1.right_trigger + gamepad1.left_trigger;

        linkagePos += timeStep * 0.25 * linkageInput;
        // rotateClawPos = (angleJoystick/180) * 0.7;
        rotateClawPos += timeStep * 2 * clawRotateInput;

        linkagePos = Math.max(Constants.LINKAGE.OPENED,Math.min(Constants.LINKAGE.CLOSED,linkagePos));
        rotateClawPos = Math.max(Constants.ROTATE_CLAW.MIN, Math.min(Constants.ROTATE_CLAW.MAX, rotateClawPos));
    }

    private void checkStates()
    {
        if(isSpecimen)
            specimenPickupStates();
        else
            samplePickupStates();
    }

    private void samplePickupStates()
    {
        switch (intakeState)
        {
            case PREPARE_SAMPLE:
                robotStates.prepareSample();
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.PICKUP_SAMPLE;
                break;

            case PICKUP_SAMPLE:
                robotStates.pickupSample();
                if(lastState != State.PICKUP_SAMPLE)
                    timer.reset();
                lastState = intakeState;

                if(timer.seconds() > 0.5) intakeState = State.OPEN_CLAW;
                break;

            case OPEN_CLAW:
                openFrontClaw(true);
                if(lastState != State.OPEN_CLAW)
                    timer.reset();
                lastState = intakeState;

                if(timer.seconds() > 0.25) intakeState = State.PICKUP_SPECIMEN;
                break;

            case PICKUP_SPECIMEN:
                robotStates.pickupSpecimen();

                if(lastState != State.PICKUP_SPECIMEN)
                    timer.reset();
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.RAISE_VERTICAL;
                break;

            case RAISE_VERTICAL:
                robotStates.raiseVertical();

                if(lastState != State.RAISE_VERTICAL)
                    timer.reset();
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.TRANSFER;
                break;

            case TRANSFER:
                robotStates.transfer();

                if(lastState != State.TRANSFER)
                    timer.reset();
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.TRANSFER2;
                break;

            case TRANSFER2:
                robotStates.transfer2();

                if(lastState != State.TRANSFER2)
                    timer.reset();
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.DO_TRANSFER;
                break;

            case DO_TRANSFER:
                robotStates.doTransfer();

                if(lastState != State.DO_TRANSFER)
                    timer.reset();
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.LEAVE_SAMPLE_BASKET;
                break;

            case LEAVE_SAMPLE_BASKET:
                robotStates.leaveSampleBasket();
                if(lastState != State.LEAVE_SAMPLE_BASKET)
                    timer.reset();
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.CLOSE_CLAW;
                break;

            case CLOSE_CLAW:
                backClawPos = 0.55;
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.PREPARE_SAMPLE;
                break;
        }
    }

    private void specimenPickupStates()
    {
        switch (intakeState)
        {
            case PREPARE_SPECIMEN:
                robotStates.prepareSpecimen();
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.PICKUP_SAMPLE;
                break;

            case PICKUP_SAMPLE:
                robotStates.pickupSample();
                if(lastState != State.PICKUP_SAMPLE)
                    timer.reset();
                lastState = intakeState;

                if(timer.seconds() > 0.5) intakeState = State.OPEN_CLAW;
                break;

            case OPEN_CLAW:
                openFrontClaw(false);
                if(lastState != State.OPEN_CLAW)
                    timer.reset();
                lastState = intakeState;

                if(timer.seconds() > 0.25) intakeState = State.PICKUP_SPECIMEN;
                break;

            case PICKUP_SPECIMEN:
                robotStates.pickupSpecimen();

                if(lastState != State.PICKUP_SPECIMEN)
                    timer.reset();
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.RAISE_VERTICAL;
                break;

            case RAISE_VERTICAL:
                robotStates.raiseVertical();

                if(lastState != State.RAISE_VERTICAL)
                    timer.reset();
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.TRANSFER;
                break;

            case TRANSFER:
                robotStates.transferSpecimen();

                if(lastState != State.TRANSFER)
                    timer.reset();
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.TRANSFER2;
                break;

            case TRANSFER2:
                robotStates.transfer2();

                if(lastState != State.TRANSFER2)
                    timer.reset();
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.DO_TRANSFER;
                break;

            case DO_TRANSFER:
                openBackClaw(false);
                robotStates.doTransferSpecimen();
                if(lastState != State.DO_TRANSFER)
                    timer.reset();
                if(timer.seconds() > 0.5) {
                    openFrontClaw(true);
                }
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.LEAVE_SPECIMEN_RANK;
                break;

            case LEAVE_SPECIMEN_RANK:
                robotStates.leaveSpecimenRank();
                if(lastState != State.LEAVE_SPECIMEN_RANK)
                    timer.reset();
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.LOWER_VERTICAL;
                break;

            case LOWER_VERTICAL:
                verticalPos=500;
                if(lastState != State.LOWER_VERTICAL)
                    timer.reset();
                lastState = intakeState;
                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.CLOSE_CLAW;
                break;
            case CLOSE_CLAW:
                backClawPos = 0.55;
                lastState = intakeState;

                if(Input.onKeyDown("scorer_a", gamepad1.a)) intakeState = State.PREPARE_SPECIMEN;
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
        Servos.rotateBackBody.setPosition(rotateBackBodyPos);
        Servos.rotateBackClaw.setPosition(rotateBackClawPos);
        Servos.backClaw.setPosition(backClawPos);

        Func.SetMotorPosition(Motors.verticalLeft, verticalPos);
        Func.SetMotorPosition(Motors.verticalRight, verticalPos);
    }

    private double getParallel(double x)
    {
        return 1.1 * x + 0.29;
    }

    private void openFrontClaw(boolean value)
    {
        if (value) clawPos = 0.29;
        else clawPos = 0.19;
    }

    private void openBackClaw(boolean value)
    {
        if (value) backClawPos = 0.26;
        else backClawPos = 0.4;
    }

    private class STATES
    {
        private void init()
        {
            isParallel = false;
            rotateAxisPos = Constants.ROTATE_AXIS.MID;
            rotateClawPos = Constants.ROTATE_CLAW.INIT;
            rotateBodyPos = 0.73; //Constants.ROTATE_BODY.MAX;
            rotateHeadPos = Constants.ROTATE_HEAD.INIT;
            verticalPos = Constants.VERTICAL.MIN;
            linkagePos = Constants.LINKAGE.CLOSED;
            openFrontClaw(true);
            openBackClaw(true);
            rotateBackBodyPos = Constants.ROTATE_BACK_BODY.INIT;
            rotateBackClawPos = Constants.ROTATE_BACK_CLAW.INIT;
        }

        private void prepareSample()
        {
            isParallel = true;
            if(State.PREPARE_SAMPLE != lastState)
            {
                rotateClawPos = Constants.ROTATE_CLAW.INIT;
                linkagePos = Constants.LINKAGE.CLOSED;
                verticalPos = Constants.VERTICAL.MIN;
                rotateBodyPos = 0.23;
            }
            rotateAxisPos = Constants.ROTATE_AXIS.MID;
            rotateHeadPos = 0.5;//Constants.ROTATE_HEAD.INIT;

            openFrontClaw(false);
            openBackClaw(true);
            rotateBackBodyPos = Constants.ROTATE_BACK_BODY.INIT;
            rotateBackClawPos = Constants.ROTATE_BACK_CLAW.INIT;

        }

        private void pickupSample()
        {
            isParallel = true;
            rotateBodyPos = 0.15;
        }

        private void prepareSpecimen()
        {
            isParallel = true;
            if(State.PREPARE_SPECIMEN != lastState)
            {
                rotateClawPos = Constants.ROTATE_CLAW.INIT;
                linkagePos = Constants.LINKAGE.CLOSED;
                verticalPos = Constants.VERTICAL.MIN;
                rotateBodyPos = 0.23;
            }
            rotateAxisPos = Constants.ROTATE_AXIS.MID;
            rotateHeadPos = 0.5;

            openFrontClaw(true);
            openBackClaw(true);
            rotateBackBodyPos = Constants.ROTATE_BACK_BODY.INIT;
            rotateBackClawPos = Constants.ROTATE_BACK_CLAW.INIT;

        }

        private void pickupSpecimen()
        {
            rotateBodyPos = 0.5;
            rotateClawPos = 0.21;
        }

        private void raiseVertical()
        {
            verticalPos = 1100;
            backClawPos = 0.55;
        }

        private void transfer()
        {
            isParallel = false;
            rotateAxisPos = 0.42;
            rotateHeadPos = 0.25;//
            rotateBodyPos = 0.48;//
            rotateClawPos = 0.6;
            linkagePos = 0.62;
            backClawPos = 0.55;
            rotateBackClawPos=0.72;
        }
        private void transferSpecimen()
        {
            isParallel = false;
            rotateAxisPos = 0.42;
            rotateHeadPos = 0.25;//
            rotateBodyPos = 0.48;//
            rotateClawPos = Constants.ROTATE_CLAW.INIT;
            linkagePos = 0.62;
            backClawPos = 0.55;
            rotateBackClawPos=0.72;
        }

        private void transfer2() {
            rotateBackBodyPos = 0.44;
        }

        private void doTransfer()
        {
            backClawPos = 0.4;
            openFrontClaw(false);
        }
        private void doTransferSpecimen()
        {
            backClawPos = 0.4;
        }

        private void leaveSpecimenRank()
        {
            rotateBackBodyPos = 0.07;
            rotateBackClawPos = 0.07;
            verticalPos = 1400;
        }

        private void leaveSampleBasket()
        {
            rotateBackBodyPos = 0.17;
            rotateBackClawPos = 0.17;
            verticalPos = 3250;
            linkagePos=Constants.LINKAGE.CLOSED;
        }
    }
}