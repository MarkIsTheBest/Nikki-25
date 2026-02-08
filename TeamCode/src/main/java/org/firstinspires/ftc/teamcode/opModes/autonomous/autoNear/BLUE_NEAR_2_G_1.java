package org.firstinspires.ftc.teamcode.opModes.autonomous.autoNear;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.constants.enums.AllianceColor;
import org.firstinspires.ftc.teamcode.constants.enums.AutoOrderNear;

@Autonomous(group = "NEAR")
public class BLUE_NEAR_2_G_1 extends LinearOpMode {

    AutoNear2 auto = new AutoNear2(this, AllianceColor.BLUE,
            new AutoOrderNear[]
                    {
                            AutoOrderNear.SPIKE_MARK_2,
                            AutoOrderNear.INTAKE_GATE,
                            AutoOrderNear.SPIKE_MARK_1
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