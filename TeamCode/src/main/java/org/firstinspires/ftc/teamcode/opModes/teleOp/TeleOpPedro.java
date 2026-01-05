package org.firstinspires.ftc.teamcode.opModes.teleOp;
import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.FpsCounter;
import org.firstinspires.ftc.teamcode.helper.pid.HeadingPID;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Configurable
@TeleOp
public class TeleOpPedro extends OpMode {

    FpsCounter fps = new FpsCounter();
    HeadingPID headingPID = new HeadingPID();

    private Follower follower;
    public static Pose startingPose; //See ExampleAuto to understand how to use this

    private Pose redGoalPosition = new Pose(130,136);

    private boolean lockMode = false;
    private double turn = 0;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
        follower.update();
    }

    @Override
    public void start() {
        follower.startTeleopDrive(true);
    }

    @Override
    public void loop() {
        //Call this once per loop
        follower.update();

        double turnInput;

        if (lockMode) {
            turnInput = headingPID.update(
                    getAngleToGoal(),
                    follower.getPose().getHeading()
            );
        } else {
            turnInput = -gamepad1.right_stick_x;
        }

        follower.setTeleOpDrive(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                turnInput,
                true
        );

        if (gamepad1.rightBumperWasPressed()) {
            lockMode = !lockMode;
            headingPID.reset();
        }

        Debug.INSTANCE.addData("lockMode", lockMode);
        Debug.INSTANCE.addData("currentHeading", Math.toDegrees(follower.getPose().getHeading()));
        Debug.INSTANCE.addData("targetHeadingDeg", Math.toDegrees(getAngleToGoal()));
        Debug.INSTANCE.addData("fps", fps.getFps());
        Debug.INSTANCE.addData(
                "Heading Error Deg",
                Math.toDegrees(
                        getAngleToGoal() - follower.getPose().getHeading()
                )
        );
        Debug.INSTANCE.addData("PID Turn", turnInput);

        Debug.INSTANCE.update();
    }

    private double getAngleToGoal() {
        double dx = redGoalPosition.getX() - follower.getPose().getX();
        double dy = redGoalPosition.getY() - follower.getPose().getY();

        return Math.atan2(dy, dx);
    }
}