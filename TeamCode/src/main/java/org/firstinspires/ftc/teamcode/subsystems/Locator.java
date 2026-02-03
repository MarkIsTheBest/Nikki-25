package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.constants.Positions;
import org.firstinspires.ftc.teamcode.constants.enums.Location;
import org.firstinspires.ftc.teamcode.helper.general.MathHelper;
import org.firstinspires.ftc.teamcode.helper.general.Vector2D;

public class Locator {

    private final Follower follower;

    public Locator(Follower PPfollower) {
        follower = PPfollower;
    }

    public Location getLocation() {
        Pose curPos = follower.getPose();

        if(MathHelper.pointInTriangle(
                new Vector2D(curPos.getX()-9, curPos.getY()-9),
                new Vector2D(0,144),
                new Vector2D(72,72),
                new Vector2D(144,144))
        ) {
            return Location.LAUNCH_ZONE_NEAR;
        }

        if(MathHelper.pointInTriangle(
                new Vector2D(curPos.getX()-9, curPos.getY()-9),
                new Vector2D(48,0),
                new Vector2D(72,24),
                new Vector2D(96,0))
        ) {
            return Location.LAUNCH_ZONE_FAR;
        }

        if(MathHelper.rotatedSquareInsideSquare(
                new Vector2D(curPos.getX(), curPos.getY()),
                8.5,
                curPos.getHeading(),
                new Vector2D(Positions.Field.BLUE_BASE.getX(), Positions.Field.BLUE_BASE.getY()),
                9)
        ) {
            return Location.FULL_BASE_BLUE;
        }
        else if (MathHelper.rotatedSquareIntersectsSquare(
                new Vector2D(curPos.getX(), curPos.getY()),
                8.5,
                curPos.getHeading(),
                new Vector2D(Positions.Field.BLUE_BASE.getX(), Positions.Field.BLUE_BASE.getY()),
                9)
        ) {
            return Location.PARTIAL_BASE_BLUE;
        }

        if(MathHelper.rotatedSquareInsideSquare(
                new Vector2D(curPos.getX(), curPos.getY()),
                8.5,
                curPos.getHeading(),
                new Vector2D(Positions.Field.RED_BASE.getX(), Positions.Field.RED_BASE.getY()),
                9)
        ) {
            return Location.FULL_BASE_RED;
        }
        else if (MathHelper.rotatedSquareIntersectsSquare(
                new Vector2D(curPos.getX(), curPos.getY()),
                8.5,
                curPos.getHeading(),
                new Vector2D(Positions.Field.RED_BASE.getX(), Positions.Field.RED_BASE.getY()),
                9)
        ) {
            return Location.PARTIAL_BASE_RED;
        }

        return Location.FIELD;
    }
}
