package gamengine.modifiers.motions;

import gamengine.game.IGameWorld;
import gamengine.game.MoveableObject;

public class SimpleMotion extends MotionController {

    public SimpleMotion(IGameWorld world) {
        super(world);
    }

    public void read(Object theObject) {
        MoveableObject object = (MoveableObject) theObject;
        object.move();
    }
}
