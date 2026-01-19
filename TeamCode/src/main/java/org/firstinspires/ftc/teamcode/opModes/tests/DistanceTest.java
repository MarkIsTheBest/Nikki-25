package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.helper.MotorHelper;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.Limelight;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@TeleOp
@Configurable
public class DistanceTest extends LinearOpMode {

    private final Servos servos = new Servos();
    private Follower follower;
    private Debug debug = new Debug(telemetry);
    public static double TargetRPM = 0;
    private double lastTargetRPM = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        Motors.init();
        servos.init();
        Limelight.init();
        Limelight.setPipeline(0);
        follower = Constants.createFollower(hardwareMap);
        //follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
        follower.update();

    }

    private void play() {
        Limelight.start();
        // start logic
//        for (DcMotorEx motor : Motors.Launchers()) {
//            motor.setPower(1);
//        }
    }

    private void update() {
        follower.update();
        if(lastTargetRPM != TargetRPM) {

            MotorHelper.setRPM(Motors.Launchers(), TargetRPM);
            lastTargetRPM = TargetRPM;
        }

        if(gamepad1.a) servos.setPosition(servos.Holder2(), Positions.Servo.H_PREPARE);
        if(gamepad1.b) servos.setPosition(servos.Holder2(), Positions.Servo.H_CLOSE);
        if(gamepad1.x) servos.setPosition(servos.Holder2(), Positions.Servo.H_LAUNCH);

        Limelight.update();

        debug.addData("RPM MOTOR Left", MotorHelper.getCurrentRPM(Motors.Launchers()[1]));
        debug.addData("Target Power MOTOR", TargetRPM);
        debug.addData("Distance to Goal", getDistance(follower.getPose(), Positions.Field.BLUE_GOAL));
        debug.addData("Current Position", follower.getPose());
        debug.addData("Current Position Limelight", Limelight.BotPose() != null ? new Pose(
                Limelight.BotPose().getPosition().x * 39.37007,
                Limelight.BotPose().getPosition().y * 39.37007) : "ERROR");

        debug.update();
    }

    private double getDistance(Pose currentPose, Pose distanceToPose) {
        return Math.sqrt(Math.pow(distanceToPose.getX() - currentPose.getX(), 2) + Math.pow(distanceToPose.getY() - currentPose.getY(), 2));
    }
}