package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Drive {

    private final LinearOpMode opMode;
    private final Follower follower;

    private double speedMultiplier = 1;
    private boolean isMoving = false;

    public Drive(LinearOpMode OpMode, Follower PPfollower) {
        follower = PPfollower;
        opMode = OpMode;
    }

    public void start() {
        follower.startTeleOpDrive(true);
    }

    public void update(boolean slowMode) {
        speedMultiplier = slowMode ? 0.33 : 1;
        follower.setTeleOpDrive(
                -opMode.gamepad1.left_stick_y * speedMultiplier,
                -opMode.gamepad1.left_stick_x * speedMultiplier,
                -opMode.gamepad1.right_stick_x * 1.1 * speedMultiplier,
                true
        );
    }
}
