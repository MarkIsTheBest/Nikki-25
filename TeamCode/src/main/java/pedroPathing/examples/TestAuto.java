package pedroPathing.examples;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.localization.PoseUpdater;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.DashboardPoseTracker;
import com.pedropathing.util.Drawing;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;


@Autonomous
public class TestAuto extends OpMode
{
    Follower follower;
    Telemetry telm;
    PathChain path;
    int pathState;
    Timer pathTimer;
    private final Pose startPose = new Pose(8.267223382045929, 47.94989561586639, Math.toRadians(180));  // Starting position
    private final Pose scorePose = new Pose(39.53235908141963, 67.49060542797496, Math.toRadians(180)); // Scoring position
    private final Pose pickup1PoseControl= new Pose(20,35,Math.toRadians(180));

    private final Pose pickup1Pose = new Pose(58.321503131524004, 33.219206680584556, Math.toRadians(180));// Push Sample 1
    public Path scorePreload, park;
    public PathChain grabPickup1,scorePickup1;

    @Override
    public void init()
    {
        pathTimer = new Timer();
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();

    }

    @Override
    public void start() {
        follower.followPath(path, true);
    }

    @Override
    public void loop()
    {
        follower.update();
        autonomousPathUpdate();
        telemetry.addData("Path State", pathState);
        telemetry.addData("Position", follower.getPose().toString());
        telemetry.update();
    }
    public void buildPaths(){
        scorePreload = new Path(new BezierLine(new Point(startPose), new Point(scorePose)));
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());
        grabPickup1 = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(scorePose),new Point(pickup1PoseControl), new Point(pickup1Pose)))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup1Pose.getHeading())
                .build();

    }
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0: // Move from start to scoring position
                follower.followPath(scorePreload);
                setPathState(1);
                break;

            case 1: // Wait until the robot is near the scoring position
                if (!follower.isBusy()) {
                    follower.followPath(grabPickup1, true);
                    setPathState(2);
                }
                break;

            case 2: // Wait until the robot is near the first sample pickup position
                if (!follower.isBusy()) {
                    follower.followPath(scorePickup1, true);
                    setPathState(-1);
                }
                break;
        }
    }
}
