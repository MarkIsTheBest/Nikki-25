package org.firstinspires.ftc.teamcode.opModes.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.helper.general.Debug;
import org.firstinspires.ftc.teamcode.helper.general.FpsCounter;
import org.firstinspires.ftc.teamcode.helper.hardware.Servos;

@TeleOp
public class ResetServoPos extends LinearOpMode {

    FpsCounter fps = new FpsCounter();

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }

    private void initialize() {
        Servos.init();
    }

    private void play() {
        Servos.setPosition(Servos.Holder1(),0.5);
        Servos.setPosition(Servos.Holder2(),0.5);
        Servos.setPosition(Servos.Holder3(),0.5);
    }

    private void update() {
        // loop logic
        fps.update();
        Debug.INSTANCE.addData("fps", fps.getFps());
        Debug.INSTANCE.update();
    }
}