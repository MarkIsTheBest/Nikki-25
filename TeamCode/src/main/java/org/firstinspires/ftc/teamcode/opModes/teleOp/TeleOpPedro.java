package org.firstinspires.ftc.teamcode.opModes.teleOp;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.helper.*;
import org.firstinspires.ftc.teamcode.helper.hardware.*;
import org.firstinspires.ftc.teamcode.helper.hardware.sensors.*;
import java.util.List;

@TeleOp
public class TeleOpPedro extends OpMode {
    private IntakeHelper intake;
    private LaunchHelper launch;

    @Override
    public void init() {
        // BULK READS ENABLED
        List<LynxModule> hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule m : hubs) m.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);

        // Hardware Init
        Motors.init(hardwareMap);
        Servos.init(hardwareMap);
        ColorSensors.init(hardwareMap);
        DistanceSensors.init(hardwareMap);
        LEDs.init(hardwareMap);

        // State Helpers
        Launchers.INSTANCE = new Launchers();
        intake = new IntakeHelper();
        launch = new LaunchHelper();
    }

    @Override
    public void loop() {
        // Update State Machines
        intake.update();
        launch.update();

        // Control
        if (gamepad1.aWasPressed()) intake.toggleSpin();
        if (gamepad1.xWasPressed()) launch.startLaunchSequence();

        telemetry.addData("FPS", 1.0 / getRuntime()); // Simple FPS monitor
        telemetry.update();
    }
}