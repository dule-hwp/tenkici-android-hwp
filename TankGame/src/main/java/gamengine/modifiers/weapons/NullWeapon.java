package gamengine.modifiers.weapons;

import android.graphics.Bitmap;

import gamengine.modifiers.weapons.AbstractWeapon;
//import java.awt.Image;
import tankgame.game.*;

public class NullWeapon extends AbstractWeapon {

    @Override
    public void fireWeapon(Ship theShip) {
        return;
    }

    @Override
    public void read(Object theObject) {
    }

    @Override
    public Bitmap getImage() {
        return null;
    }

    @Override
    public Bullet getBullet(Ship theShip) {
        return null;
    }

    @Override
    public int getWeaponImageID() {
        return -1;
    }

}
