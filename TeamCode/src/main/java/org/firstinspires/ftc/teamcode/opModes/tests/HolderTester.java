package org.firstinspires.ftc.teamcode.opModes.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.helper.MotorHelper;
import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.hardware.Motors;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;

@TeleOp
@Configurable
public class HolderTester extends LinearOpMode {

    public static double position;
    public static boolean holder1;
    public static boolean holder2;
    public static boolean holder3;

    Debug debug = new Debug(telemetry);

    Servos servos = new Servos();

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        servos.init();
    }

    private void play() {

    }

    private void update() {
        if (holder1) {
            servos.setPosition(servos.Holder1(), position);
        }

        if (holder2) {
            servos.setPosition(servos.Holder2(), position);
        }

        if (holder3) {
            servos.setPosition(servos.Holder3(), position);
        }

        debug.addData("Position", position);
        debug.update();
    }
}