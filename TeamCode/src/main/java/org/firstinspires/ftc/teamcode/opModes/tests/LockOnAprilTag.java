//package org.firstinspires.ftc.teamcode.opModes.tests;
//
//import com.pedropathing.follower.Follower;
//import com.pedropathing.geometry.Pose;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//import org.firstinspires.ftc.teamcode.helper.general.Debug;
//import org.firstinspires.ftc.teamcode.helper.hardware.sensors.Limelight;
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
//
//@TeleOp
//public class LockOnAprilTag extends LinearOpMode {
//    private Follower follower;
//    public static Pose startingPose = new Pose(0,0, Math.toRadians(90));
//
//    private double targetHeadingDeg = 0;
//    @Override
//    public void runOpMode() throws InterruptedException {
//        initialize();
//        waitForStart();
//        play();
//        if (isStopRequested()) return;
//        while (opModeIsActive()) update();
//    }
//
//    private void initialize() {
//        Limelight.init();
//        Limelight.setPipeline(0);
//
//        follower = Constants.createFollower(hardwareMap);
//        //follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
//        follower.update();
//    }
//
//    private void play() {
//        // start logic
//        Limelight.start();
//    }
//
//    private void update() {
//        input();
//        Limelight.update();
//        follower.update();
//        //follower.holdPoint(new BezierPoint(follower.getPose()), Math.toRadians(targetHeadingDeg));
//        telemetry();
//    }
//
//    private void input() {
//        if (gamepad1.dpadUpWasPressed()) targetHeadingDeg += 10;
//        if (gamepad1.dpadDownWasPressed()) targetHeadingDeg -= 10;
//    }
//
//    private void telemetry() {
//
//        try {
//            Debug.INSTANCE.addData("rotation", Math.toDegrees(follower.getPose().getHeading()));
//            Debug.INSTANCE.addData("currentTargetRotation", targetHeadingDeg);
//
//            Debug.INSTANCE.addData("Tx", Limelight.Tx());
//            Debug.INSTANCE.addData("Ty", Limelight.Ty());
//            Debug.INSTANCE.addData("Area %", Limelight.Ta());
//            Debug.INSTANCE.addData("Bot pose", Limelight.BotPose());
//        } catch (Exception ex) {
//            Debug.INSTANCE.addData("Error", ex.getMessage());
//        }
//
//        Debug.INSTANCE.update();
//
//    }
//}