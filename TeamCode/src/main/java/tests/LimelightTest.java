package tests;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@TeleOp(name = "Limelight Test")
public class LimelightTest extends LinearOpMode {

    private Limelight3A limelight;

    @Override
    public void runOpMode() throws InterruptedException {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        telemetry.setMsTransmissionInterval(11);

        limelight.pipelineSwitch(1);

        limelight.start(); // Start polling for data

        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                Pose3D botpose = result.getBotpose();
                //Pose3D targetPose = LLResultTypes.(); // Get target pose relative to the camera

                telemetry.addData("tx", result.getTx());
                telemetry.addData("ty", result.getTy());
                telemetry.addData("Botpose", botpose.toString());

                if (targetPose != null) {
                    double x = targetPose.getX(); // Target X position in camera space
                    double y = targetPose.getY(); // Target Y position in camera space

                    // Calculate angle using atan2
                    double angle = Math.toDegrees(Math.atan2(y, x));

                    telemetry.addData("Target Angle", angle);
                }
            }
            telemetry.update();
        }
    }
}
