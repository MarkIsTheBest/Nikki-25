package tests.teleop;

import static subsystems.Positions.lastState;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import subsystems.Actions;
import subsystems.Constants;
import subsystems.Helper;
import subsystems.Input;
import subsystems.Positions;
import subsystems.State;
import subsystems.hardware.Motors;
import subsystems.hardware.Servos;

@Config
@TeleOp(name = "TeleOp", group = "TeleOp")
public class TeleOpTest extends LinearOpMode {
    public static boolean onePlayer = false;
    private final Gamepad chassisGamepad = new Gamepad();
    private final Gamepad scorerGamepad = new Gamepad();

    private final Telemetry debug = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

    private final ElapsedTime timer = new ElapsedTime();

    private State intakeState = State.INIT;
    private boolean isSpecimen = true;
    private boolean attachSpecimen = true;

    private int invertInput = -1;
    private boolean slow = false;

    @Override
    public void runOpMode() throws InterruptedException {
        onInit();
        waitForStart();
        while (opModeIsActive()) {
            onUpdate();
        }
    }

    private void onInit() {
        initHardware();
        Actions.init();
        initCheckStates();
        lastState = intakeState;
        Positions.update();
    }

    private void initHardware() {
        Motors.init(hardwareMap);
        Servos.init(hardwareMap);

        chassisGamepad.setGamepadId(gamepad1.getGamepadId());
        scorerGamepad.setGamepadId(onePlayer ? gamepad1.getGamepadId() : gamepad2.getGamepadId());
    }

    private void onUpdate() {
        Helper.resetDeltaTime();
        try {
            handleChassis(); // Player 1
            handleScoring(); // Player 2
        } catch (Exception e) {
            debug.addData("Error", e.getMessage());
        }

        onDebug();
    }

    private void handleChassis() {
        //handle chassis movement
        handleMovement();
    }

    private void handleMovement() {
        if (Input.isDown("chassis_right_bumper", gamepad1.right_bumper)) invertInput *= -1;
        if (Input.isDown("chassis_left_bumper", gamepad1.left_bumper)) slow = !slow;

        double dpadYInput = gamepad1.dpad_up ? 0.35 : (gamepad1.dpad_down ? -0.35 : 0);
        double dpadXInput = gamepad1.dpad_right ? 0.35 : (gamepad1.dpad_left ? -0.35 : 0);

        double forwardInput = (-gamepad1.left_stick_y + dpadYInput) * invertInput;
        double lateralInput = (gamepad1.left_stick_x * 1.1 + dpadXInput) * invertInput;
        double angularInput = -gamepad1.right_stick_x;

        double denominator = Math.max(Math.abs(forwardInput) + Math.abs(lateralInput) + Math.abs(angularInput), 1);
        double frontLeftPower = ((forwardInput + lateralInput + angularInput) / denominator) * (slow ? 0.66 : 1);
        double backLeftPower = ((forwardInput - lateralInput + angularInput) / denominator) * (slow ? 0.66 : 1);
        double frontRightPower = ((forwardInput - lateralInput - angularInput) / denominator) * (slow ? 0.66 : 1);
        double backRightPower = ((forwardInput + lateralInput - angularInput) / denominator) * (slow ? 0.66 : 1);

        Motors.leftFront.setPower(frontLeftPower);
        Motors.leftRear.setPower(backLeftPower);
        Motors.rightFront.setPower(-frontRightPower);
        Motors.rightRear.setPower(-backRightPower);
    }

    private void handleScoring() {
        manualManipulation();
        Positions.update();
        checkStates();
        keepParallel();
    }

    private void keepParallel() {
        if (Positions.isParallel)
            Positions.rotateHead = Helper.getParallel(Positions.rotateBody);
    }

    private void manualManipulation() {
        handleIntake();
        changeMode();
        handleHang();
    }

    private void handleIntake() {
        //TODO integrate limelight to obsolete manual rotation
        //TODO double angleFromCam = 90;
        //TODO Positions.rotateClaw = Helper.map(angleFromCam, 0, 180, Constants.ROTATE_CLAW.MIN,  Constants.ROTATE_CLAW.MIN);

        double linkageInput = gamepad2.right_stick_y;
        double clawRotateInput = -gamepad2.right_trigger + gamepad2.left_trigger;

        Positions.linkage = Helper.adjustPositionServo(Positions.linkage,
                linkageInput,
                Constants.LINKAGE.OPENED,
                Constants.LINKAGE.CLOSED,
                0.25);

        Positions.rotateClaw = Helper.adjustPositionServo(Positions.rotateClaw,
                clawRotateInput,
                Constants.ROTATE_CLAW.MIN,
                Constants.ROTATE_CLAW.MAX,
                2);
    }

    private void changeMode() {
        boolean sampleInput = Input.onKeyDown("scorer_dpad_up", gamepad2.dpad_up);
        boolean specimenInput = Input.onKeyDown("scorer_dpad_left", gamepad2.dpad_left);
        boolean attachInput = Input.onKeyDown("scorer_dpad_right", gamepad2.dpad_right);

        int currentMode = sampleInput ? 0 : (specimenInput ? 1 : (attachInput) ? 2 : -1);

        switch (currentMode) {
            case 0: // -- Sample -- //
                isSpecimen = false;
                attachSpecimen = false;
                gamepad2.rumbleBlips(1);
                break;
            case 1: // -- Specimen -- //
                isSpecimen = true;
                attachSpecimen = false;
                gamepad2.rumbleBlips(2);
                break;
            case 2: // -- Attach -- //
                isSpecimen = true;
                attachSpecimen = true;
                gamepad2.rumbleBlips(3);
                break;
        }

    }

    private void handleHang() {
        if (Input.onKeyDown("scorer_x", gamepad2.x)) {
            Positions.vertical = 1500;
        }
        if (Input.onKeyDown("scorer_y", gamepad2.y)) {
            Positions.vertical = 800;
            Positions.linkage = Constants.LINKAGE.CLOSED;
            Positions.rotateBody = Constants.ROTATE_BODY.MAX;
        }
    }

    private void initCheckStates() {
        if (isSpecimen && !attachSpecimen) {
            gamepad2.setLedColor(202, 3, 252, -1);
            intakeState = State.PREPARE_SAMPLE;
            //specimenPickupStates();
        } else if (isSpecimen) {
            gamepad2.setLedColor(32, 252, 3, -1);
            intakeState = State.PREPARE_SPECIMEN;
        } else {
            gamepad2.setLedColor(252, 211, 3, -1);
            intakeState = State.PREPARE_SAMPLE;
            //samplePickupStates();
        }
    }

    private void checkStates() {
        if (isSpecimen && !attachSpecimen) {
            gamepad2.setLedColor(202, 3, 252, -1);
            //specimenPickupStates();
        } else if (isSpecimen) {
            gamepad2.setLedColor(32, 252, 3, -1);
            specimenAttachStates();
        } else {
            gamepad2.setLedColor(252, 211, 3, -1);
            //samplePickupStates();
        }
    }

/*    private void samplePickupStates() {
        switch (intakeState) {
            case PREPARE_SAMPLE:
                Actions.prepareSample();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.PICKUP_SAMPLE;
                break;

            case PICKUP_SAMPLE:
                Actions.pickupSample();
                if (lastState != State.PICKUP_SAMPLE)
                    timer.reset();
                lastState = intakeState;

                if (timer.seconds() > 0.5) intakeState = State.OPEN_CLAW;
                break;

            case OPEN_CLAW:
                Actions.openFrontClaw(true);
                if (lastState != State.OPEN_CLAW)
                    timer.reset();
                lastState = intakeState;

                if (timer.seconds() > 0.25) intakeState = State.PICKUP_SPECIMEN;
                break;

            case PICKUP_SPECIMEN:
                Actions.pickupSpecimen();

                if (lastState != State.PICKUP_SPECIMEN)
                    timer.reset();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a))
                    intakeState = State.RAISE_VERTICAL;
                break;

            case RAISE_VERTICAL:
                Actions.raiseVertical();

                if (lastState != State.RAISE_VERTICAL)
                    timer.reset();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.TRANSFER;
                break;

            case TRANSFER:
                Actions.transfer();

                if (lastState != State.TRANSFER)
                    timer.reset();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.TRANSFER2;
                break;

            case TRANSFER2:
                Actions.transfer2();

                if (lastState != State.TRANSFER2)
                    timer.reset();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.DO_TRANSFER;
                break;

            case DO_TRANSFER:
                Actions.doTransfer();

                if (lastState != State.DO_TRANSFER)
                    timer.reset();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a))
                    intakeState = State.LEAVE_SAMPLE_BASKET;
                break;

            case LEAVE_SAMPLE_BASKET:
                Actions.leaveSampleBasket();
                if (lastState != State.LEAVE_SAMPLE_BASKET)
                    timer.reset();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.CLOSE_CLAW;
                break;

            case CLOSE_CLAW:
                Positions.backClaw = 0.55;
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a))
                    intakeState = State.PREPARE_SAMPLE;
                break;
        }
    }

    private void specimenPickupStates() {
        switch (intakeState) {
            case PREPARE_SPECIMEN:
                Actions.prepareSpecimen();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.PICKUP_SAMPLE;
                break;

            case PICKUP_SAMPLE:
                Actions.pickupSample();
                if (lastState != State.PICKUP_SAMPLE)
                    timer.reset();
                lastState = intakeState;

                if (timer.seconds() > 0.5) intakeState = State.OPEN_CLAW;
                break;

            case OPEN_CLAW:
                Actions.openFrontClaw(false);
                if (lastState != State.OPEN_CLAW)
                    timer.reset();
                lastState = intakeState;

                if (timer.seconds() > 0.25) intakeState = State.PICKUP_SPECIMEN;
                break;

            case PICKUP_SPECIMEN:
                Actions.pickupSpecimen();

                if (lastState != State.PICKUP_SPECIMEN)
                    timer.reset();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a))
                    intakeState = State.RAISE_VERTICAL;
                break;

            case RAISE_VERTICAL:
                Actions.raiseVertical();

                if (lastState != State.RAISE_VERTICAL)
                    timer.reset();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.TRANSFER;
                break;

            case TRANSFER:
                Actions.transferSpecimen();

                if (lastState != State.TRANSFER)
                    timer.reset();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.TRANSFER2;
                break;

            case TRANSFER2:
                Actions.transfer2();

                if (lastState != State.TRANSFER2)
                    timer.reset();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.DO_TRANSFER;
                break;

            case DO_TRANSFER:
                Actions.openBackClaw(false);
                Actions.doTransferSpecimen();
                if (lastState != State.DO_TRANSFER)
                    timer.reset();
                if (timer.seconds() > 0.5) {
                    Actions.openFrontClaw(true);
                }
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a))
                    intakeState = State.LEAVE_SPECIMEN_RANK;
                break;

            case LEAVE_SPECIMEN_RANK:
                Actions.leaveSpecimenRank();
                if (lastState != State.LEAVE_SPECIMEN_RANK)
                    timer.reset();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a))
                    intakeState = State.LOWER_VERTICAL;
                break;

            case LOWER_VERTICAL:
                Positions.vertical = 500;
                if (lastState != State.LOWER_VERTICAL)
                    timer.reset();
                lastState = intakeState;
                if (Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.CLOSE_CLAW;
                break;
            case CLOSE_CLAW:
                Positions.backClaw = 0.55;
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a))
                    intakeState = State.PREPARE_SPECIMEN;
                break;
        }
    }*/

    private void specimenAttachStates() {
        switch (intakeState) {
            case PREPARE_SPECIMEN:
                Actions.prepareSpecimen();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a)) intakeState = State.CLOSE_CLAW;
                break;

            case CLOSE_CLAW:
                Actions.openFrontClaw(false);
                if (lastState != State.CLOSE_CLAW)
                    timer.reset();
                lastState = intakeState;

                if (timer.seconds() > 0.25) intakeState = State.PICKUP_SPECIMEN;
                break;

            case PICKUP_SPECIMEN:
                Actions.pickupSpecimen();
                if (lastState != State.PICKUP_SPECIMEN)
                    timer.reset();
                lastState = intakeState;

                if (timer.seconds() > 1) intakeState = State.PREPARE_TRANSFER;
                break;

            case PREPARE_TRANSFER:
                Actions.prepareTransfer();
                if (lastState != State.PREPARE_TRANSFER)
                    timer.reset();
                lastState = intakeState;

                if (timer.seconds() > 1) intakeState = State.DO_TRANSFER;
                break;

            case DO_TRANSFER:
                Actions.openBackClaw(false);

                if (lastState != State.DO_TRANSFER)
                    timer.reset();
                lastState = intakeState;

                if (timer.seconds() > 0.25) intakeState = State.DO_TRANSFER2;
                break;

            case DO_TRANSFER2:
                Actions.openFrontClaw(true);

                if (lastState != State.DO_TRANSFER)
                    timer.reset();
                lastState = intakeState;

                if (timer.seconds() > 0.25) intakeState = State.RAISE_VERTICAL;
                break;

            case RAISE_VERTICAL:
                Actions.raiseVertical();
                if (lastState != State.RAISE_VERTICAL)
                    timer.reset();
                lastState = intakeState;

                if (timer.seconds() > 0.5)
                    intakeState = State.LEAVE_SPECIMEN_RANK;
                break;

            case LEAVE_SPECIMEN_RANK:
                Actions.leaveSpecimenRank();
                if (lastState != State.LEAVE_SPECIMEN_RANK)
                    timer.reset();
                lastState = intakeState;

                if (Input.onKeyDown("scorer_a", gamepad2.a))
                    intakeState = State.LOWER_VERTICAL;
                break;

            case LOWER_VERTICAL:
                Actions.lowerVertical();
                if (lastState != State.LOWER_VERTICAL)
                    timer.reset();
                lastState = intakeState;
                if (timer.seconds() > 0.5) intakeState = State.OPEN_CLAW;
                break;

            case OPEN_CLAW:
                Actions.openBackClaw(true);
                lastState = intakeState;

                if (timer.seconds() <= 0.5) return;
                Actions.setAxisStraight();
                if (timer.seconds() <= 0.5 + 0.5) return;
                intakeState = State.PREPARE_SPECIMEN;
                break;

        }
    }

    private void onDebug() {
        debug.addData("CurrentState", intakeState);
        debug.addData("lastState", lastState);
        debug.addData("CurrentState2", isSpecimen);
        debug.addData("isbuttonadown", Input.onKeyDown("scorer_a", gamepad2.a));
        debug.addData("Timer", timer.seconds());
        debug.addData("Motor Encoder Ticks", Motors.verticalLeft.getCurrentPosition());
        debug.update();
    }
}