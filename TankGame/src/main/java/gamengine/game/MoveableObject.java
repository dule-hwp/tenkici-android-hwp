package gamengine.game;

//import java.awt.Image;
//import java.awt.Point;

import android.graphics.Bitmap;
import android.graphics.Point;

import gamengine.modifiers.motions.MotionController;
import tankgame.modifiers.motions.NullMotion;

/*MoveableObjects have movement behaviors*/
public class MoveableObject extends GameObject {

    protected int strength;
    protected MotionController motion;

    public MoveableObject()
    {
        motion = new NullMotion();
    }

    public MoveableObject(Point location, Point speed, Bitmap img) {
        super(location, speed, img);
        this.strength = 0;
        this.motion = new NullMotion();
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void update(int w, int h) {
        motion.read(this);
    }

    public void start() {
        motion.addObserver(this);
    }
    
    public void stop() {
        motion.delete(this);
    }
}
