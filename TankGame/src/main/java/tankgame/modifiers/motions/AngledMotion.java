package tankgame.modifiers.motions;

import gamengine.modifiers.motions.MotionController;
import gamengine.game.MoveableObject;
import tankgame.TankWorld;

public class AngledMotion extends MotionController {

    double angle;

    public AngledMotion() {
        super(TankWorld.getInstance());
    }

    public AngledMotion(double angle) {
        this();
        this.angle = angle;
    }

    public void read(Object theObject) {
        MoveableObject object = (MoveableObject) theObject;
        object.moveByHeading(object.getHeading());
    }
}
