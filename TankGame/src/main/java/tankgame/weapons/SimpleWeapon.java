package tankgame.weapons;

import android.graphics.Bitmap;
import android.graphics.Point;

import gamengine.modifiers.weapons.AbstractWeapon;
import tankgame.TankWorld;

import tankgame.game.Bullet;
import tankgame.game.Ship;
import tankgame.modifiers.motions.AngledMotion;

public class SimpleWeapon extends AbstractWeapon {

    int strength;

    public SimpleWeapon() {
        this(5, 10);
    }

    public SimpleWeapon(int reload) {
        this(5, reload);
    }

    public SimpleWeapon(int strength, int reload) {
        super();
        this.reload = reload;
        this.strength = strength;
    }

    @Override
    public Bitmap getImage() {
        return null;
    }

    @Override
    public Bullet getBullet(Ship theShip) {
        Point speed = new Point(TankWorld.getDevicePixelValue(30), 0);
        Point location = theShip.getLocationPoint();
        Bullet b = new Bullet(location, speed, strength, new AngledMotion(), theShip, TankWorld.sprites.get("bullet"));
        b.setHeading(theShip.getHeading());
        return b;
    }

    @Override
    public int getWeaponImageID() {
        return -1;
    }

}
