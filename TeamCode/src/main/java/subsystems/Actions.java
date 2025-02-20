package subsystems;

import static subsystems.Positions.backClaw;
import static subsystems.Positions.claw;
import static subsystems.Positions.isParallel;
import static subsystems.Positions.linkage;
import static subsystems.Positions.rotateAxis;
import static subsystems.Positions.rotateBackBody;
import static subsystems.Positions.rotateBackClaw;
import static subsystems.Positions.rotateBody;
import static subsystems.Positions.rotateClaw;
import static subsystems.Positions.rotateHead;
import static subsystems.Positions.vertical;

public class Actions {
    public static void init() {
        isParallel = false;
        extendLinkages(Constants.LINKAGE.CLOSED);
        openFrontClaw(false);
        rotateAxis = 0.23;
        rotateBody = 0.67;
        rotateHead = 0.7;
        rotateClaw = 0.2;
        rotateBackBody = 0.4;
        rotateBackClaw = 0.1;
        openBackClaw(false);
    }

    public static void prepareSample() {
        isParallel = true;
        if (State.PREPARE_SAMPLE != Positions.lastState) {
            rotateClaw = Constants.ROTATE_CLAW.INIT;
            linkage = Constants.LINKAGE.CLOSED;
            vertical = Constants.VERTICAL.MIN;
            rotateBody = 0.23;
        }
        rotateAxis = Constants.ROTATE_AXIS.MID;
        rotateHead = 0.5;//Constants.ROTATE_HEAD.INIT;

        openFrontClaw(false);
        openBackClaw(true);
        rotateBackBody = Constants.ROTATE_BACK_BODY.INIT;
        rotateBackClaw = Constants.ROTATE_BACK_CLAW.INIT;

    }

    public static void pickupSample() {
        isParallel = true;
        rotateBody = 0.15;
    }

    public static void prepareSpecimen() {
        isParallel = false;
        rotateAxis = 0.4;
        rotateBody = 0.28;
        rotateHead = 0.17;
        rotateClaw = 0.2;
        openFrontClaw(true);
        rotateBackBody = 0.2;
        rotateBackClaw = 0.46;
    }

    public static void pickupSpecimen() {
        isParallel = false;
        extendLinkages(Constants.LINKAGE.CLOSED);
        openBackClaw(true);
        rotateAxis = 1;
        rotateBody = 0.4;
        rotateHead = 0.08;
        rotateClaw = 0.2;
    }

    public static void prepareTransfer()
    {
        isParallel = false;
        rotateBackBody = 0.3;
        rotateBackClaw = 0.55;
    }

    public static void doTransfer() {
        openBackClaw(false);
        openFrontClaw(true);
    }

    public static void raiseVertical() {
        vertical = 700;
    }

    public static void leaveSpecimenRank() {
        rotateBackBody = 0.18;
        rotateBackClaw = 0.3;
    }

    public static void leaveSpecimenRankScoring() {
        rotateBackBody = 0.13;
        rotateBackClaw = 0.3;
    }

    public static void lowerVertical() {
        vertical = 0;
    }

    public static void leaveSampleBasket() {
        rotateBackBody = 0.17;
        rotateBackClaw = 0.17;
        vertical = 3250;
        linkage = Constants.LINKAGE.CLOSED;
    }

    public static void openFrontClaw(boolean value) {
        if (value) claw = 0.2;
        else claw = 0.02;
    }

    public static void openBackClaw(boolean value) {
        if (value) backClaw = 0.5;
        else backClaw = 0.3;
    }

    public static void extendLinkages(double value) {
        value = Math.max(Constants.LINKAGE.OPENED, Math.min(value, Constants.LINKAGE.CLOSED));
        linkage = value;
    }

    // Autonomous

    public static void prepareSample1()
    {
        isParallel = false;
        openFrontClaw(true);
        extendLinkages(0.53);
        rotateAxis = 0.44;
        rotateBody = 0.2;
        rotateHead = 0.25;
        rotateClaw = 0.4;
    }

    public static void prepareSample2()
    {
        isParallel = false;
        openFrontClaw(true);
        extendLinkages(0.56);
        rotateAxis = 0.4;
        rotateBody = 0.2;
        rotateHead = 0.25;
        rotateClaw = 0.34;
    }

    public static void prepareSample3()
    {
        isParallel = false;
        openFrontClaw(true);
        rotateAxis = 0.38;
        rotateBody = 0.2;
        rotateHead = 0.25;
        rotateClaw = 0.45;
    }

    public static void pickupSampleAuto()
    {
        rotateHead = 0.4;
    }

    public static void endSampleAuto()
    {
        rotateHead = 0.25;
        extendLinkages(Constants.LINKAGE.CLOSED);
    }

    public static void leaveObsvAuto()
    {
        rotateHead = 0.25;
        rotateBody = 0.2;
        extendLinkages(Constants.LINKAGE.OPENED);
        rotateAxis = 0.4;
    }

    public static void setAxisStraight()
    {
        rotateAxis = 0.4;
        rotateBody = 0.5;
        rotateHead = 0.25;
    }
}