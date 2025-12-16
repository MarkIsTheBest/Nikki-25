package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.constants.enums.State;
import org.firstinspires.ftc.teamcode.helper.Debug;
//import org.firstinspires.ftc.teamcode.helper.hardware.sensors.ColorSensors;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LEDs;
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

    private void initialize() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(Positions.AutoPosition.STARTPOSE);
    }

    private void play() {
        follower.startTeleopDrive();
    }

    private void update() {
        drive();
        manipulate();
        checkArtifacts();
        debug();
    }

    private void drive() {
        double speedScale = gamepad1.right_trigger > 0.1 ? 0.33 : 1;

        follower.setTeleOpDrive(-gamepad1.left_stick_y * speedScale,
                -gamepad1.left_stick_x * speedScale,
                -gamepad1.right_stick_x * speedScale,
                true);

        //follower.turn
        follower.update();
    }

    private void manipulate() {
        switch (currentState)
        {
            case INIT:

                break;
        }
    }

    private void checkArtifacts() {
    }


    private void debug() {
        Debug.INSTANCE.addData("X", follower.getPose().getX());
        Debug.INSTANCE.addData("Y", follower.getPose().getY());
        Debug.INSTANCE.addData("Heading in Degrees", Math.toDegrees(follower.getPose().getHeading()));

        Debug.INSTANCE.update();
    }
}
