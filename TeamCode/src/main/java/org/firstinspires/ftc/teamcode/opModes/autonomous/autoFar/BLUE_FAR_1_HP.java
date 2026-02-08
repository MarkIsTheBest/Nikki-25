package org.firstinspires.ftc.teamcode.opModes.autonomous.autoFar;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
import org.firstinspires.ftc.teamcode.constants.enums.AutoOrderFar;

@Autonomous(group = "FAR")
public class BLUE_FAR_1_HP extends LinearOpMode {

    AutoFar2 auto = new AutoFar2(this, AllianceColor.BLUE,
            new AutoOrderFar[]
                    {
                            AutoOrderFar.SPIKE_MARK_1,
                            AutoOrderFar.SPIKE_MARK_HUMAN_PLAYER
                    },
            0,
            false
    );

    @Override
    public void runOpMode() throws InterruptedException {
        auto.initialize();
        waitForStart();
        auto.play();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            auto.update();
        }
    }
}