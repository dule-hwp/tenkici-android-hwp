package tankgame.weapons;

import android.graphics.Bitmap;
import android.graphics.Point;

import gamengine.modifiers.weapons.AbstractWeapon;

import java.util.Iterator;

import gamengine.utils.Utils;
import tankgame.TankWorld;

import tankgame.game.Bullet;
import tankgame.game.PlayerShip;
import tankgame.game.Rocket;
import tankgame.game.Ship;
import tankgame.modifiers.motions.AngledMotion;

public class WeaponRocket extends AbstractWeapon {

    int strength;

    public WeaponRocket() {
        this(5, 10);
    }

    public WeaponRocket(int reload) {
        this(5, reload);
    }

    public WeaponRocket(int strength, int reload) {
        super();
        this.reload = reload;
        this.strength = strength;
    }

    @Override
    public Bitmap getImage() {
        return Utils.getWeaponImage(0);
    }

    @Override
    public Bullet getBullet(Ship theShip) {
        Point speed = new Point(TankWorld.getDevicePixelValue(32), 0);
        Point location = theShip.getLocationPoint();
        Rocket bullet = new Rocket(location, speed, strength, new AngledMotion(), theShip);
        Iterator<PlayerShip> players = TankWorld.getInstance().getEnemies();
        while (players.hasNext())
        {
            Ship player = players.next();
            if (player != theShip)
            {
                double angle = Math.atan2(player.getLocation().centerY() - theShip.getLocation().centerY(),
                        player.getLocation().centerX() - theShip.getLocation().centerX());
                bullet.setHeading((float) Math.toDegrees(angle));
            }
        }
        if (TankWorld.getInstance().getEnemiesHashMap().size()==0)
            bullet.setHeading(theShip.getHeading());
        return bullet;
    }

    @Override
    public int getWeaponImageID() {
        return 0;
    }

}
