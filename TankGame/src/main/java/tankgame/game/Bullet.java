package tankgame.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Observable;

import gamengine.game.GameObject;
import gamengine.game.MoveableObject;
import gamengine.modifiers.motions.MotionController;
import gamengine.utils.Utils;
import tankgame.TankWorld;
import tankgame.android.INetworkable;
import tankgame.modifiers.motions.AngledMotion;

//import java.awt.Graphics;
//import java.awt.Image;
//import java.awt.Point;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Area;
//import java.awt.image.AffineTransformOp;
//import java.awt.image.ImageObserver;

/*Bullets fired by player and enemy weapons*/
public class Bullet extends MoveableObject implements INetworkable{
	PlayerShip owner;
	boolean friendly;
	int id = -1;
    private short bulletTypeId;

    public Bullet() {
        location = new Rect();
        motion = new AngledMotion();   //this should be based on type of weapon (bullet, rocket)
        speed = new Point(TankWorld.getDevicePixelValue(25), 0);
    }

    public Bullet(int id) {
        this();
        this.id = id;
    }

    public Bullet(Point location, Point speed, int strength, MotionController motion, GameObject owner, Bitmap img){
		super(location, speed, img);
//		super(location, new Point(TankWorld.getDevicePixelValue(35),0), img);
		this.strength=strength;
		this.setImage(img);
		this.owner = (PlayerShip) owner;
		this.motion = motion;
		motion.addObserver(this);
	}
	
	public PlayerShip getOwner(){
		return owner;
	}
	
	public boolean isFriendly(){
		if(friendly){
			return true;
		}
		return false;
	}

    @Override
    public void draw(Canvas g) {
        float locationX = img.getWidth() / 2;
        float locationY = img.getHeight() / 2;
		g.save();
		g.rotate(heading, location.left + locationX, location.top + locationY);
		super.draw(g);
		g.restore();
//        super.draw(g);
//        drawBoundingOfCollisionArea(g);
    }

    @Override
    public Region getCollisionRegion() {

		Rect r = new Rect(location.left, location.top, location.left + location.width(), location.top + location.height());;
		return Utils.getRotatedRegionForGivenRect(r, heading, new Point(location.centerX(), location.centerY()), new Region(location));
    }


	@Override
	public byte[] toBytes() {
        Point locationPoint = getLocationPoint();
        byte[] bts = ByteBuffer.allocate(SIZE)
                .putInt(id)
                .putInt(Math.round(locationPoint.x/TankWorld.density))
                .putInt(Math.round(locationPoint.y/TankWorld.density))
                .putFloat(heading)
                .putShort((short) (show ? 1 : 0))
                .putInt(this.getOwner().getId())
                .putShort(this.getBulletTypeId())
                .array();
        return bts;
	}

    protected short getBulletTypeId() {
        return 3;
    }

    @Override
    public void update(Observable o, Object arg) {
        super.update(o, arg);
    }

    public static final int SIZE = 24;

    public void fromDataInputStream (DataInputStream dis) throws IOException {
        if (id==-1)     //if id is not already set, read it and set it
            this.id = dis.readInt();
        int x = Math.round(dis.readInt()*TankWorld.density);
        int y = Math.round(dis.readInt()*TankWorld.density);
        this.setLocation(new Point(x, y));
        this.setHeading(dis.readFloat());
        this.show = dis.readShort()==1;
        int ownerId = dis.readInt();
        PlayerShip ownerEnemy = TankWorld.getInstance().getEnemiesHashMap().get(ownerId);
        this.owner = ownerEnemy==null ? TankWorld.getInstance().getPlayer() : ownerEnemy;
        this.bulletTypeId = dis.readShort();
    }

    public void initBulletByID()
    {

//        String imgName = bulletTypeId==0 ? "rocket" : "bullet";
        this.img = bulletTypeId==0 ? Rocket.rocketBitmap : TankWorld.sprites.get("bullet");
        Point lp = this.getLocationPoint();
        this.location = new Rect(lp.x, lp.y, lp.x+this.img.getWidth(), lp.y+this.img.getHeight());
        this.show = true;
        strength = 10;
    }

    public int getId() {
        return id;
    }

    public void setData(Bullet data) {
        setLocation(data.getLocationPoint());
        this.heading = data.getHeading();
    }
}
