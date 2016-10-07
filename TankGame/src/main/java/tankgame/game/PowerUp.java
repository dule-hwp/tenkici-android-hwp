package tankgame.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;

import gamengine.game.GameObject;
import gamengine.modifiers.AbstractGameModifier;
//import java.awt.Bitmap;
//import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Observable;

import tankgame.TankWorld;
import tankgame.android.INetworkable;

/* PowerUp extends ship so that it can hold a weapon to give to player*/
public class PowerUp extends Ship implements INetworkable{


    private int pickUpIndex=-1;
    private int id=-1;

    public PowerUp(Point loc, int health, Bitmap powerupBitmap) {
        super(loc, new Point(0, 0), health, powerupBitmap);
    }

    public PowerUp() {
        this(new Point(0,0),100,
                Bitmap.createBitmap(TankWorld.getDevicePixelValue(40),
                        TankWorld.getDevicePixelValue(40), Bitmap.Config.ARGB_8888));
    }

    @Override
    public void update(Observable o, Object arg) {
        AbstractGameModifier modifier = (AbstractGameModifier) o;
        modifier.read(this);
    }

    @Override
    public void die() {
        this.show = false;
//    	weapon.deleteObserver(this);
//        motion.deleteObserver(this);
//        TankWorld.getInstance().removeClockObserver(motion);
    }

    public boolean checkForCollision(ArrayList<?> objects) {
        boolean collisionDetected = false;
        for (Object o : objects) {
            GameObject go = (GameObject) o;
            if (collisionDetected = this.collision(go)) {
                break;
            }
        }
        return collisionDetected;
    }

    @Override
    public void draw(Canvas g) {
        super.draw(g);
//        drawBoundingOfCollisionArea(g);
    }

    @Override
    public Region getCollisionRegion() {
        Region region = new Region();
        Path largePath = new Path();
        largePath.addCircle(location.centerX(),location.centerY(),location.width()/2, Path.Direction.CW);
        region.setPath(largePath, new Region(location));
//        return super.getCollisionRegion();
        return region;
    }

    @Override
    public byte[] toBytes() {
        Point locationPoint = getLocationPoint();
        byte[] bts = ByteBuffer.allocate(SIZE)
                .putInt(id)
                .putInt(Math.round(locationPoint.x / TankWorld.density))
                .putInt(Math.round(locationPoint.y / TankWorld.density))
                .putInt(pickUpIndex)
                .array();
        return bts;
    }
    public static final int SIZE = 16;
    @Override
    public void fromDataInputStream(DataInputStream dis) throws IOException {
        id = dis.readInt();
        int x = Math.round(dis.readInt() * TankWorld.density);
        int y = Math.round(dis.readInt()*TankWorld.density);
        this.setLocation(new Point(x, y));
        pickUpIndex = dis.readInt();
    }

    public void setPickUpIndex(int pickUpIndex) {
        this.pickUpIndex = pickUpIndex;
    }

    public int getPickUpIndex() {
        return pickUpIndex;
    }

    public int getId() {
        return id;
    }
}
