package tankgame.game;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;

import gamengine.game.GameObject;
//import java.awt.Bitmap;
//import java.awt.Point;
//import java.awt.Rectangle;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Area;

import gamengine.utils.Utils;
import tankgame.TankWorld;
import gamengine.modifiers.motions.MotionController;

/*Bullets fired by player and enemy weapons*/
public class Rocket extends Bullet {

    public static Bitmap rocketStrip = TankWorld.sprites.get("rocket");
    public static Bitmap rocketBitmap = Bitmap.createBitmap(rocketStrip,0, 0, rocketStrip.getHeight(), rocketStrip.getHeight());

    public Rocket(Point location, Point speed, int strength, MotionController motion, GameObject owner) {
        super(location, speed, strength, motion, owner, rocketBitmap);
    }

    @Override
    public Region getCollisionRegion() {
//        Rect collisonRec = new Rect(location.left, location.top + 5, (int) location.getWidth(), (int) (location.getHeight() - 10));
        Rect r = new Rect(location.left, location.top + 5, location.left + location.width(), location.top + location.height()-10);
        return Utils.getRotatedRegionForGivenRect(r, heading, new Point(location.centerX(), location.centerY()), new Region(location));

    }

    @Override
    protected short getBulletTypeId() {
        return 0;
    }
}
