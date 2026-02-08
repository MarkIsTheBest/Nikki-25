package org.firstinspires.ftc.teamcode.opModes.autonomous.autoNear;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
import org.firstinspires.ftc.teamcode.constants.enums.AutoOrderNear;

@Autonomous(group = "NEAR")
public class BLUE_NEAR_1_2_3 extends LinearOpMode {

    AutoNear2 auto = new AutoNear2(this, AllianceColor.BLUE,
            new AutoOrderNear[]
                    {
                            AutoOrderNear.SPIKE_MARK_1,
                            AutoOrderNear.SPIKE_MARK_2,
                            AutoOrderNear.SPIKE_MARK_3
                    }
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