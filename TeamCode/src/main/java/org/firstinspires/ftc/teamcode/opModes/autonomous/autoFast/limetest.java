package org.firstinspires.ftc.teamcode.opModes.autonomous.autoFast;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.limelightvision.LLResultTypes;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.LimelightHelper;

@Autonomous(name = "Limelight ID Debug")
public class limetest extends LinearOpMode {

    private final LimelightHelper ll = new LimelightHelper();

    @Override
    public void runOpMode() {
        ll.start(); // begin capturing frames

        waitForStart();

        while (opModeIsActive()) {
            ll.update();

            telemetry.addData("Has AprilTag", ll.HasAprilTag());

            if (ll.HasAprilTag()) {
                telemetry.addData("Primary ID", ll.ID());
                telemetry.addData("Tx", ll.Tx());
                telemetry.addData("Ty", ll.Ty());
                telemetry.addData("Ta", ll.Ta());
                telemetry.addData("Distance", ll.Distance());

                // BotPose can be null even when result.isValid() — guard before reading it
                Pose3D botPose = ll.BotPose();
                if (botPose != null) {
                    telemetry.addData("BotPose", botPose.getPosition());
                } else {
                    telemetry.addData("BotPose", "null (no field pose data)");
                }

                Pose3D botPoseMT2 = ll.BotPoseMT2();
                if (botPoseMT2 != null) {
                    telemetry.addData("BotPoseMT2", botPoseMT2.getPosition());
                } else {
                    telemetry.addData("BotPoseMT2", "null (no MT2 data)");
                }

                // Walk every fiducial currently seen, not just the primary one
                int count = 0;
                for (LLResultTypes.FiducialResult fiducial : ll.LimelightSensor().getLatestResult().getFiducialResults()) {
                    count++;
                    telemetry.addLine(String.format(
                            "  Tag[%d] id=%d tx=%.2f ty=%.2f",
                            count,
                            fiducial.getFiducialId(),
                            fiducial.getTargetXDegrees(),
                            fiducial.getTargetYDegrees()
                    ));

                    // Target-space pose can be null if this tag isn't in your field layout
                    Pose3D fidPose = fiducial.getRobotPoseTargetSpace();
                    if (fidPose == null) {
                        telemetry.addLine("    (no target-space pose for this tag)");
                    }
                }
                telemetry.addData("Total tags seen", count);
            } else {
                telemetry.addLine("No AprilTag detected");
            }

            telemetry.update();
        }
    }
}