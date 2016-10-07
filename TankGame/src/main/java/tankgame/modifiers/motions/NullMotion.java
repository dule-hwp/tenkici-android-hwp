package tankgame.modifiers.motions;

import gamengine.modifiers.motions.MotionController;
import java.util.Observable;
import tankgame.TankWorld;

/*Motion controller that does nothing*/
public class NullMotion extends MotionController {

	public NullMotion() {
		super(TankWorld.getInstance());
	}

	@Override
	public void update(Observable o, Object arg) {}

}
