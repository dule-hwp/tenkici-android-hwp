package gamengine.modifiers.weapons;

import android.graphics.Bitmap;
import android.graphics.Point;

import gamengine.modifiers.AbstractGameModifier;
import gamengine.utils.Constants;
import tankgame.TankWorld;
import tankgame.android.NetworkUtil;
import tankgame.game.Bullet;
import tankgame.game.Ship;

/*Weapons are fired by motion controllers on behalf of players or ships
 * They observe motions and are observed by the Game World
 */
public abstract class AbstractWeapon extends AbstractGameModifier {

    Bullet[] bullets;
    boolean friendly;
    int lastFired = 0, reloadTime;
    protected int direction;
    public int reload = 5;
    private int weaponImageID;

    public AbstractWeapon() {
        super();
        this.addObserver(TankWorld.getInstance());
    }

    public void fireWeapon(Ship theShip) {
          fireWeapon(theShip, theShip.getHeading());
    }

    public void fireWeapon(Ship theShip, float heading) {
        Point location = theShip.getLocationPoint();
        Bullet bullet = getBullet(theShip);
        bullets = new Bullet[1];
        bullets[0] = bullet;
//        bullet.setHeading(heading);

        int halfHeight = theShip.getWeapon().bullets[0].getSizeY() / 2;
        int halfWidth = theShip.getWeapon().bullets[0].getSizeX() / 2;

        location.x = theShip.getLocation().centerX() - halfWidth;
        location.y = theShip.getLocation().centerY() - halfWidth;
        bullet.setLocation(location);

        this.setChanged();
        this.notifyObservers();
    }

    /* read is called by Observers when they are notified of a change */
    public void read(Object theObject) {
//        TankWorld world = (TankWorld) theObject;
//        world.addBullet(bullets);
        for (Bullet b : bullets) {
            TankWorld.getInstance().getTcpClient().
                    sendMessage(NetworkUtil.marshall(b, Constants.CMSG_ADD_BULLET));
        }

    }

    public void remove() {
        this.deleteObserver(TankWorld.getInstance());
    }

    public abstract Bitmap getImage();
    public abstract Bullet getBullet(Ship theShip);

    public abstract int getWeaponImageID();
}
