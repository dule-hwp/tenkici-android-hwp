package tankgame.weapons;

import android.graphics.Bitmap;
import android.graphics.Point;

import gamengine.modifiers.weapons.AbstractWeapon;
import gamengine.utils.Utils;
import tankgame.TankWorld;
import tankgame.game.Bullet;
import tankgame.game.Ship;
import tankgame.modifiers.motions.AngledMotion;

public class WeaponBounceMissile extends AbstractWeapon {

    int strength;

    public WeaponBounceMissile() {
        this(5, 10);
    }

    public WeaponBounceMissile(int reload) {
        this(5, reload);
    }

    public WeaponBounceMissile(int strength, int reload) {
        super();
        this.reload = reload;
        this.strength = strength;
    }
    
    @Override
    public Bitmap getImage() {
        return Utils.getWeaponImage(1);
    }



    @Override
    public Bullet getBullet(Ship theShip) {
        Point speed = new Point(TankWorld.getDevicePixelValue(18), 0);
        Point location = theShip.getLocationPoint();
        Bullet b = new Bullet(location, speed, strength, new AngledMotion(), theShip, TankWorld.sprites.get("bullet"));
        b.setHeading(theShip.getHeading());
        return b;
        ///should changed for specific behaviour
    }

    @Override
    public int getWeaponImageID() {
        return 1;
    }

}
