package gamengine.modifiers.motions;

import gamengine.game.IGameWorld;
import gamengine.game.MoveableObject;
import gamengine.modifiers.AbstractGameModifier;
import java.util.Observable;
import java.util.Observer;

/*MotionControllers move around objects!*/
public abstract class MotionController extends AbstractGameModifier implements Observer {

    int fireInterval;
    IGameWorld worldInstance;

    public MotionController(IGameWorld world) {
        this.worldInstance = world;
        worldInstance.addClockObserver(this);
        fireInterval = -1;
    }

    public void delete(Observer theObject) {
        worldInstance.removeClockObserver(this);
        this.deleteObserver(theObject);
    }

    /*Motion Controllers observe the GameClock and fire on every clock tick
     * The default controller doesn't do anything though*/
    public void update(Observable o, Object arg) {
        this.setChanged();
        this.notifyObservers();
    }

    public void read(Object theObject) {
        MoveableObject object = (MoveableObject) theObject;
        object.move();

//		if(TankWorld.getInstance().getFrameNumber()%fireInterval==0){
//			object.fire();
//		}
    }
}
