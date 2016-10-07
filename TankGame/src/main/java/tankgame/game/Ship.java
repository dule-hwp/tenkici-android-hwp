package tankgame.game;

import android.graphics.Bitmap;
import android.graphics.Point;

import gamengine.game.MoveableObject;
import gamengine.game.GameObject;
//import java.awt.Bitmap;
//import java.awt.Point;
import gamengine.modifiers.weapons.NullWeapon;
import tankgame.TankWorld;

import gamengine.modifiers.motions.MotionController;
import gamengine.modifiers.weapons.AbstractWeapon;

/* Ships are things that have weapons and health */
public class Ship extends MoveableObject {

    protected AbstractWeapon weapon;
    protected int health;
    protected Point gunLocation;

    public Ship()
    {
        weapon = new NullWeapon();
    }

    public Ship(Point location, Point speed, Bitmap img) {
        this(location,speed, 100, img);
    }

    public Ship(Point location, Point speed, int strength, Bitmap img) {
        super(location, speed, img);
        this.strength = strength;
        this.health = strength;
//    	this.gunLocation = new Point(32,40);
    }

    public Ship(int x, Point speed, int strength, Bitmap img) {
        this(new Point(x, -90), speed, strength, img);
    }

    public void setWeapon(AbstractWeapon weapon) {
        if (this.weapon != null) {
            this.weapon.remove();
        }
        this.weapon = weapon;
    }

    public AbstractWeapon getWeapon() {
        return this.weapon;
    }

    public void damage(int damageDone) {
        this.health -= damageDone;
        if (health <= 0) {
            this.die();
        }
        return;
    }

    public void die() {
        this.show = false;
//        SmallExplosion explosion = new SmallExplosion(new Point(location.left, location.top));
//        TankWorld.getInstance().addBackground(explosion);
        weapon.deleteObserver(this);
        motion.deleteObserver(this);
        TankWorld.getInstance().removeClockObserver(motion);

    }

    public void collide(GameObject otherObject) {
    }

    public void fire() {
        weapon.fireWeapon(this);
    }

    /* some setters and getters!*/
    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public MotionController getMotion() {
        return this.motion;
    }

    public void setMotion(MotionController motion) {
        this.motion = motion;
    }

    public Point getGunLocation() {
        return this.gunLocation;
    }

}
