//package tests.teleop;
//
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//import subsystems.Constants;
//import subsystems.Func;
//import subsystems.Input;
//import subsystems.hardware.Motors;
//import subsystems.hardware.Servos;
//
//@TeleOp(name="TeleOp", group = "Teleop")
//public class TeleOpeu extends LinearOpMode {
//
//    private double linkagePosLeft;
//    private double linkagePosRight;
//    private double rotateAxisPos;
//    private double rotateBodyPos ;
//    private double rotateHeadPos;
//    private double rotateClawPos;
//    private double clawPos;

//    private boolean isOpen = false;
//
//    private boolean isLeavingSpecimen = false;
//    private int currentStep = 0;
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//        initialize();
//        waitForStart();
//        if (isStopRequested()) return;
//        while (opModeIsActive()) startLoop();
//    }
//
//    private void initialize() {
//        Motors.init(hardwareMap);
//        Servos.init(hardwareMap);
//        initPos();
//        updateIntakePositions();
//    }
//
//
//    private void startLoop() {
//        try {
//            handleChassis(); // Player 1
//            handleScoring(); // Player 2
//        } catch (Exception e) {
//        }
//    }
//
//    // -- Chassis Controls -- //
//
//    private void handleChassis() {
//        handleMovement();
//    }
//
//    private void handleMovement() {
//        int invertInput = Input.isDown("chassis_right_bumper", gamepad1.right_bumper) ? 1 : -1;
//        double speedInput = gamepad1.right_trigger;
//        double maxSpeed = 0.7 + speedInput * (1 - 0.7);
//
//        double forwardInput = -gamepad1.left_stick_y * invertInput;
//        double lateralInput = gamepad1.left_stick_x * 1.1 * invertInput;
//        double angularInput = -gamepad1.right_stick_x;
//
//        double denominator = Math.max(Math.abs(forwardInput) + Math.abs(lateralInput) + Math.abs(angularInput), 1);
//        double frontLeftPower = ((forwardInput + lateralInput + angularInput) / denominator) * maxSpeed;
//        double backLeftPower = ((forwardInput - lateralInput + angularInput) / denominator) * maxSpeed;
//        double frontRightPower = ((forwardInput - lateralInput - angularInput) / denominator) * maxSpeed;
//        double backRightPower = ((forwardInput + lateralInput - angularInput) / denominator) * maxSpeed;
//
//        Motors.leftFront.setPower(frontLeftPower);
//        Motors.leftRear.setPower(backLeftPower);
//        Motors.rightFront.setPower(-frontRightPower);
//        Motors.rightRear.setPower(-backRightPower);
//    }
//
//    private void stateMachine()
//    {
//        if(isLeavingSpecimen)
//        {
//            switch(currentStep)
//            {
//                case 0:
//                    preparePickupSpecimen();
//                    break;
//                case 1:
//                    closeClaw();
//                    break;
//                case 2:
//                    prepareTransfer();
//                    break;
//                case 3:
//                    transfer();
//                    break;
//                case 4:
//                    prepareLeaveSpecimen();
//                    break;
//                case 5:
//                    leaveSpecimen();
//                    break;
//                case 6:
//                    currentStep = 0;
//                    break;
//            }
//        }
//        else
//        {
//            switch(currentStep)
//            {
//                case 0:
//                    idling();
//                    break;
//                case 1:
//                    openClaw();
//                    break;
//                case 2:
//                    pickupSample();
//                    break;
//                case 3:
//                    prepareLeaveSample();
//                    break;
//                case 4:
//                    closeClaw();
//                    break;
//                case 5:
//                    currentStep = 0;
//                    break;
//            }
//        }
//
//    }
//
//    // -- Manipulator / Scorer Controls -- //
//
//    private void handleScoring() {
//        handleClaws();
//        updateIntakePositions();
//    }
//
//    private void handleVipers() {
//        double rotateInput = -gamepad1.right_stick_y;
//        double extendInput = -gamepad1.left_stick_y;
//        double armsInput = gamepad1.right_bumper ? 1 : 0 - gamepad1.right_trigger;
//
//
//        //setPositions();
//
//    }
//
//    private void updateIntakePositions() {
//        Servos.linkageRight.setPosition(linkagePosRight);
//        Servos.linkageLeft.setPosition(linkagePosLeft);
//
//        Servos.rotateAxis.setPosition(rotateAxisPos);
//        Servos.rotateBody.setPosition(rotateBodyPos);
//        Servos.rotateHead.setPosition(rotateHeadPos);
//        Servos.rotateClaw.setPosition(rotateClawPos);
//        Servos.claw.setPosition(clawPos);
//
//        telemetry.addData("axis", rotateAxisPos);
//        telemetry.addData("body", rotateBodyPos);
//        telemetry.addData("head", rotateHeadPos);
//        telemetry.addData("claw", rotateClawPos);
//        telemetry.update();
//    }
//
//    private void handleClaws() {
//        double intakeLinkageInput = Input.isDown("scorer_dpad_down", gamepad2.dpad_down) ? -1
//                : Input.isDown("scorer_dpad_up", gamepad2.dpad_up) ? 1
//                : 0;
//
//        double intakeAxisInput = gamepad2.left_stick_x;
//
//        double intakeBodyInput = gamepad2.left_stick_y;
//
//        double intakeHeadInput = gamepad2.right_stick_y;
//
//        double intakeClawInput = Input.isDown("scorer_dpad_left", gamepad2.dpad_left) ? -1
//                : Input.isDown("scorer_dpad_right", gamepad2.dpad_right) ? 1
//                : 0;
//
//
//        linkagePosLeft = Func.adjustPositionServo(linkagePosLeft,
//                -intakeLinkageInput,
//                Constants.linkageLeft.EXTENDED,
//                Constants.linkageLeft.RETRACTED,
//               0.25);
//
//        linkagePosRight = Func.adjustPositionServo(linkagePosRight,
//                intakeLinkageInput,
//                Constants.linkageRight.RETRACTED,
//                Constants.linkageRight.EXTENDED,
//                0.25);
//
//        rotateAxisPos = Func.adjustPositionServo(rotateAxisPos,
//                intakeAxisInput,
//                0,
//                1,
//                0.5);
//
//        rotateBodyPos = Func.adjustPositionServo(rotateBodyPos,
//                -intakeBodyInput,
//                Constants.rotateBody.MIN,
//                Constants.rotateBody.MAX,
//                0.5);
//
//        rotateHeadPos = Func.adjustPositionServo(rotateHeadPos,
//                intakeHeadInput,
//                Constants.rotateHead.MIN,
//                Constants.rotateHead.PICKUP,
//                0.5);
//
//        rotateClawPos = Func.adjustPositionServo(rotateClawPos,
//                -intakeClawInput,
//                Constants.rotateClaw.MIN,
//                Constants.rotateClaw.MAX,
//                0.5);
//
//        if (Input.onKeyDown("scorer_options", gamepad2.start)) isLeavingSpecimen = !isLeavingSpecimen;
//
//        if (Input.onKeyDown("scorer_a", gamepad2.a))
//        {
//            if(isOpen)
//            {
//                closeClaw();
//                isOpen = false;
//            }
//            else
//            {
//                openClaw();
//                isOpen = true;
//            }
//
//        }
//
//        if(Input.onKeyDown("scorer_b", gamepad2.b))
//        {
//
//        }
//
//        /*if (Input.isDown("scorer_a", gamepad2.a) || Input.isDown("scorer_b", gamepad2.b)) {
//            currentStep++;
//            stateMachine();
//
//        }*/
//        Func.updateLastTime();
//    }
//
//    private void openClaw()
//    {
//        clawPos = Constants.backClaw.OPENED;
//    }
//
//    private void closeClaw()
//    {
//        clawPos = Constants.backClaw.CLOSED;
//    }
//
//    private void initPos()
//    {
//        linkagePosLeft = Constants.linkageLeft.RETRACTED;
//        linkagePosRight = Constants.linkageRight.RETRACTED;
//        rotateAxisPos = 0.45; //Init Axis straight forward
//        rotateBodyPos = 0.8; //Init body
//        rotateHeadPos = 0.7; //Init head
//        rotateClawPos = 0.5; //Init claw
//
//    }
//
//    private void idling()
//    {
//        linkagePosLeft = Constants.linkageLeft.RETRACTED;
//        linkagePosRight = Constants.linkageRight.RETRACTED;
//        rotateAxisPos = 0.45;
//        rotateBodyPos = 0.3;
//        rotateHeadPos = 0.36;
//        rotateClawPos = 0.35;
//    }
//
//    private void prepareLeaveSample()
//    {
//        linkagePosLeft = Constants.linkageLeft.RETRACTED;
//        linkagePosRight = Constants.linkageRight.RETRACTED;
//        rotateAxisPos = 0.45;
//        rotateBodyPos = 0.3;
//        rotateHeadPos = 0.36;
//        rotateClawPos = 0.35;
//    }
//    private void leaveSample()
//    {
//
//    }
//
//    private void pickupSample()
//    {
//
//    }
//
//    private void preparePickupSpecimen()
//    {
//
//    }
//
//    private void prepareLeaveSpecimen()
//    {
//
//    }
//
//    private void leaveSpecimen()
//    {
//
//    }
//
//    private void prepareTransfer()
//    {
//
//    }
//
//    private void transfer()
//    {
//
//    }
//}