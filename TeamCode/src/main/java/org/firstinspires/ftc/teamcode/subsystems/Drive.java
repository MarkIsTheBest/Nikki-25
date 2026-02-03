package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Drive {

    private final LinearOpMode opMode;
    private final Follower follower;

    public Drive(LinearOpMode OpMode, Follower PPfollower) {
        follower = PPfollower;
        opMode = OpMode;
    }

    public void start() {
        follower.startTeleOpDrive(true);
    }

    public void update() {
        follower.setTeleOpDrive(
                -opMode.gamepad1.left_stick_y,
                -opMode.gamepad1.left_stick_x,
                -opMode.gamepad1.right_stick_x * 1.1,
                true
        );
    }
}
