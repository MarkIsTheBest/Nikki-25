package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PIDFController;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import  com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.constants.State;
import org.firstinspires.ftc.teamcode.helper.Debug;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@TeleOp
public class TeleOpPedro extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if(isStopRequested()) return;
        while(opModeIsActive()) update();
    }

    private Follower follower;
    private State currentState = State.INIT;
    private State lastState;

    double targetHeadingRad; // Radians

    PIDFController controller;
    boolean headingLock = true;

    PIDFCoefficients coefficients = new PIDFCoefficients(0.7, 0, 0.02, 0.01);

    private void initialize() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(Positions.AutoPosition.STARTPOSE);
        controller = new PIDFController(coefficients);
    }

    private void play() {
        follower.startTeleopDrive();
    }

    private void update() {
        drive();
        debug();
    }

    private void drive() {
        if (gamepad1.left_stick_x > 0.1 || gamepad1.left_stick_x < -0.1 || gamepad1.left_stick_y > 0.1 || gamepad1.left_stick_y < -0.1) {
            headingLock = true;
        }
        else {
            headingLock = false;
            targetHeadingRad = follower.getHeading();
        }

        double speedScale = gamepad1.right_trigger > 0.1 ? 0.33 : 1;

        double error = angleWrap(targetHeadingRad - follower.getHeading());
        controller.updateError(error);

        if (headingLock)
            follower.setTeleOpDrive(
                    -gamepad1.left_stick_y * speedScale,
                    -gamepad1.left_stick_x * speedScale,
                    controller.run()
            );
        else
            follower.setTeleOpDrive(
                    -gamepad1.left_stick_y * speedScale,
                    -gamepad1.left_stick_x * speedScale,
                    -gamepad1.right_stick_x
            );

        follower.update();
    }


    private void manipulate() {
        switch (currentState)
        {
            case INIT:

                break;
        }
    }

    private void debug() {
        Debug.INSTANCE.addData("X", follower.getPose().getX());
        Debug.INSTANCE.addData("Y", follower.getPose().getY());
        Debug.INSTANCE.addData("Heading in Degrees", Math.toDegrees(follower.getPose().getHeading()));

        Debug.INSTANCE.update();
    }

    private double angleWrap(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }


}