package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.panels.Panels;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.hardware.Other;

@TeleOp
public class LimelightTest extends LinearOpMode {

    TelemetryManager panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        while (opModeIsActive())
        {
            update();
        }
    }

    private void initialize()
    {
        Other.init(hardwareMap);
        Other.Limelight.setPollRateHz(100);
        Other.Limelight.start();
        Other.Limelight.pipelineSwitch(1);

    }

    private void update()
    {
        LLResult result = Other.Limelight.getLatestResult();
        if (result != null && result.isValid()) {
            double tx = result.getTx(); // How far left or right the target is (degrees)
            double ty = result.getTy(); // How far up or down the target is (degrees)
            double ta = result.getTa(); // How big the target looks (0%-100% of the image)

            panelsTelemetry.addData("Target X", tx);
            panelsTelemetry.addData("Target Y", ty);
            panelsTelemetry.addData("Target Area", ta);
        } else {
            panelsTelemetry.addData("Limelight", "No Targets");
        }

        panelsTelemetry.update(telemetry);
    }
}
