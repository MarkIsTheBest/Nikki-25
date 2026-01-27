package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.MathHelper;
import org.firstinspires.ftc.teamcode.helper.hardware.Hardware;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Drive;

public class MainTeleOp {

    private final LinearOpMode opMode;

    private Debug debug;
    private Hardware hardwareMap;
    private Follower follower;
    private Drive drive;

    private Pose goalPosition;
    private Pose basePosition;
    private final Pose startingPosition;

    public MainTeleOp(AllianceColor curAllianceColor, LinearOpMode opMode, Pose startingPose) {
        this.opMode = opMode;
        this.startingPosition = startingPose;
        switch (curAllianceColor) {
            case RED:
                goalPosition = Positions.Field.RED_GOAL;
                basePosition = Positions.Field.RED_BASE;
                break;
            case BLUE:
                goalPosition = Positions.Field.BLUE_GOAL;
                basePosition = Positions.Field.BLUE_BASE;
                break;
        }
    }

    public void initialize() {
        initHelpers();
        initPedro();
    }

    private void initHelpers() {
        hardwareMap = new Hardware();
        debug = new Debug(opMode.telemetry);
        drive = new Drive(opMode, follower);
    }

    private void initPedro() {
        follower = Constants.createFollower(opMode.hardwareMap);
        follower.setStartingPose(startingPosition);
        follower.update();
    }

    public void play() {
        hardwareMap.Limelight().start();
    }

    public void update() {
        follower.update();
        drive.update(-opMode.gamepad1.right_stick_x * 1.1);
        hardwareMap.Limelight().update();
    }

    private double getAngleToGoal(AngleUnit desiredAngleUnit) {
        double dx = goalPosition.getX() - follower.getPose().getX();
        double dy = goalPosition.getY() - follower.getPose().getY();
        double angle = desiredAngleUnit == AngleUnit.RADIANS ? Math.atan2(dy, dx) : Math.toDegrees(Math.atan2(dy, dx));
        return angle;
    }

    //in inches
    private double getDistanceToGoal() {
        return MathHelper.dist(follower.getPose(), goalPosition);
    }

    public void telemetry() {
        debug.addData("April Tag BotPose", hardwareMap.Limelight().BotPoseMT2());
        debug.update();
    }

}