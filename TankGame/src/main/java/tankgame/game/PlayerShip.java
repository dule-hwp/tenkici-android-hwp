package tankgame.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import android.widget.Button;

import gamengine.game.GameObject;
import gamengine.modifiers.AbstractGameModifier;
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Image;
//import java.awt.Point;
//import java.awt.Rect;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Area;
//import java.awt.image.AffineTransformOp;
//import java.awt.image.BufferedImage;
//import java.awt.image.ImageObserver;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;

import gamengine.utils.Constants;
import gamengine.utils.Utils;
import tankgame.TankWorld;

import tankgame.android.INetworkable;
import tankgame.android.JoystickView;
import tankgame.android.NetworkUtil;
import tankgame.modifiers.motions.InputController;
import tankgame.weapons.SimpleWeapon;

public class PlayerShip extends Ship implements Observer, INetworkable {

    private boolean canBeDrawn;
    private Point maxSpeed;
//    int lives;
    int score;
    Point resetPoint;
    int lastFired = 0;
    boolean isFiring = false;
    public int left = 0, right = 0, up = 0, down = 0;
    double rotationSpeed = Math.PI / 90;
    String name;
    int mColor;

    public int getId() {
        return id;
    }

    int id = -1;

    public PlayerShip() {
        location = new Rect();
        canBeDrawn = false;
    }

//    public PlayerShip(int id) {
//        this();
//        this.id = id;
//    }

    public void setName(String name) {
        this.name = name;
    }

    public void initPLayerByID() {
//        PlayerShip player = new PlayerShip;
        switch (id)
        {
            case 0:
                setInitData(
                        new Point(TankWorld.getDevicePixelValue(32),TankWorld.getDevicePixelValue(32)),
                        new Point(TankWorld.getDevicePixelValue(10),TankWorld.getDevicePixelValue(0)),
                        TankWorld.sprites.get("player1"), Color.RED);
                canBeDrawn = true;
                break;
            case 1:
                setInitData(
                        new Point(TankWorld.getDevicePixelValue(32), TankWorld.getInstance().getSizeY()-TankWorld.getDevicePixelValue(96)),
                        new Point(TankWorld.getDevicePixelValue(10),TankWorld.getDevicePixelValue(0)),
                        TankWorld.sprites.get("player2"), Color.BLUE);
                canBeDrawn=true;
                break;
            case 2:
                setInitData(
                        new Point(  TankWorld.getInstance().getSizeX()-TankWorld.getDevicePixelValue(96),
                                    TankWorld.getInstance().getSizeY()-TankWorld.getDevicePixelValue(96)),
                        new Point(  TankWorld.getDevicePixelValue(10),
                                    TankWorld.getDevicePixelValue(0)),
                        TankWorld.sprites.get("player2"), Color.BLUE);
                canBeDrawn=true;
                break;
            default:
                setInitData(
                        new Point(TankWorld.getDevicePixelValue(32),TankWorld.getDevicePixelValue(32)),
                        new Point(TankWorld.getDevicePixelValue(10),TankWorld.getDevicePixelValue(0)),
                        TankWorld.sprites.get("player1"), Color.RED);
                canBeDrawn = true;
        }
    }

    public void initEnemyByID()
    {
        String imgName = id%2==0 ? "player1" : "player2";
        int color = id%2 ==0 ? Color.RED : Color.BLUE;
        setInitData(new Point(getLocationPoint()),new Point(0,0), TankWorld.sprites.get(imgName), color);
    }

    private void setInitData(Point location, Point speed, Bitmap bitmap, int color)
    {
        resetPoint = new Point(location);
        mColor = color;
        this.speed = speed;
        this.img = bitmap;
        this.show = true;
        this.location = new Rect(location.x, location.y,
                location.x+this.img.getWidth(), location.y+this.img.getHeight());
        width = img.getWidth();
        height = img.getHeight();
        maxSpeed = new Point(speed);

        weapon = new SimpleWeapon();
        health = 100;
        strength = 100;
        score = 0;
    }

//    public PlayerShip(Point location, Point speed, Bitmap img, Button[] controls, JoystickView jv, int color) {
//        super(location, speed, 100, img);
//
//        maxSpeed = new Point(speed);
//        resetPoint = new Point(location);
//
//        mColor = color;
//        weapon = new SimpleWeapon();
//        motion = new InputController(this, controls, jv);
//        health = 100;
//        strength = 100;
//        score = 0;
//    }

    public void draw(Canvas g) {
        float locationX = img.getWidth() / 2;
        float locationY = img.getHeight() / 2;

        g.save();
        g.rotate(heading, location.left + locationX, location.top + locationY);
        super.draw(g);
        g.restore();

//        if (TankWorld.getInstance().getFrameNumber()%200==0)
//            Log.e(getClass().getCanonicalName(), toString() +"---" + resetPoint.x + " " + resetPoint.y);
//        drawBoundingOfCollisionArea(g);
    }

    @Override
    public void damage(int damageDone) {
//        if (respawnCounter <= 0) {
        super.damage(damageDone);
        Log.e(this.getClass().getCanonicalName(),toString() + "--- health:" + health);
//        }
    }

    @Override
    public void update(int w, int h) {
        if (isFiring) {
            int frame = TankWorld.getInstance().getFrameNumber();
            if (frame >= lastFired + weapon.reload) {
                fire();
                lastFired = frame;
            }
        }

        //rotation change
        if (right == 1 || left == 1) {
            rotateTank(right == 1);
        }

        if ((down == 1 || up == 1)) {
            Rect potPosition = getNextPotPosition();
            if (!goingOutOfBounds(w, h, potPosition)) {
                location = potPosition;
            }
        }


        TankWorld.getInstance().getTcpClient().sendMessage(NetworkUtil.marshall
                (this, Constants.CMSG_PLAYER_UPDATE));
    }

    public Rect getNextPotPosition() {
        Rect temp = new Rect(location);
        double headingLoc = Math.toRadians(heading);
        headingLoc += down == 1 ? Math.PI : 0;
        long yOffset = Math.round(speed.x * Math.sin(headingLoc));
        long xOffset = Math.round(speed.x * Math.cos(headingLoc));
        temp.offset((int)xOffset, (int) yOffset);
        return temp;
    }

    @Override
    public boolean collision(GameObject otherObject) {
        if (down == 1 || up==1) {
            return super.collision(otherObject);
        } else {
            return false;
        }
    }

    public void startFiring() {
        isFiring = true;
    }

    public void stopFiring() {
        isFiring = false;
    }

    public void rotateTank(boolean rotateRight) {
        heading += rotateRight ? rotationSpeed : -rotationSpeed;
        if (heading >= 360 || heading < -360)
            heading = 0;
//        updateGunLocation();
    }

    @Override
    public void fire() {
        weapon.fireWeapon(this);
    }

    public void die() {
        this.show = false;
        BigExplosion explosion = new BigExplosion(new Point(location.left, location.top));
        TankWorld.getInstance().addBackground(explosion);
            TankWorld.getInstance().removeClockObserver(this.motion);
            reset();
    }

    public void reset() {
        this.setLocation(resetPoint);
        health = 100;
        heading = 0;
        TankWorld.getInstance().getTcpClient().sendMessage(NetworkUtil.marshall
                (this, Constants.CMSG_PLAYER_UPDATE));
    }

//    public int getLives() {
//        return this.lives;
//    }

    public int getScore() {
        return this.score;
    }

    public int getColor() {
        return mColor;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setHeading(float heading) {
        super.setHeading(heading);
    }

    //    public String getName() {
//        return this.name;
//    }

    public void incrementScore(int increment) {
        score += increment;
    }

    public boolean isDead() {
        return health <= 0;
    }

    @Override
    public void update(Observable o, Object arg) {
        AbstractGameModifier modifier = (AbstractGameModifier) o;
        modifier.read(this);
    }

    private boolean goingOutOfBounds(int w, int h, Rect potLocation) {
        if ((potLocation.top < h - height && potLocation.top > 0) && (potLocation.left > 0 && potLocation.left < w - width)) {
            return false;
        }
        return true;
    }

    public void bounce(int w, int h) {

        if (down == 1) {
            down = 0;
            up = 1;
            update(w, h);

            up = 0;
            down = 1;
        } else {
            up = 0;
            down = 1;
            update(w, h);
            down = 0;
            up = 1;
        }
    }

    @Override
    public Region getCollisionRegion() {
//        heading = heading<0 ? heading+360: heading;
        Rect r;
        if (down==1)
            r = new Rect(location.left + toDPV(7), location.top + toDPV(9), location.left + toDPV(7+ 27), location.top + toDPV(9+45));
        else if (up==1)
            r = new Rect(location.left + toDPV(7+27), location.top + toDPV(9), location.left + toDPV(7+ 54), location.top + toDPV(9+45));
        else
            r = new Rect(location.left + toDPV(7), location.top + toDPV(9), location.left + toDPV(7+ 54), location.top + toDPV(9+45));

        return Utils.getRotatedRegionForGivenRect(r, heading, new Point(location.centerX(), location.centerY()), new Region(location));
    }

    int toDPV(int valueInPiexels)
    {
        return TankWorld.getDevicePixelValue(valueInPiexels);
    }

    @Override
    public String toString() {
        return name +" "+ id;
    }

    public void setGear(double gearNum)
    {
        speed.x = (int) Math.round(gearNum * maxSpeed.x);
//        Log.d("as", speed.x+"");
    }

    public byte[] toBytes()
    {
        byte[] nameBytes = name.getBytes();
        Point locationPoint = getLocationPoint();
        byte[] bts = ByteBuffer.allocate(SIZE+2+nameBytes.length)
                .putInt(id)
                .putInt(Math.round(locationPoint.x / TankWorld.density))
                .putInt(Math.round(locationPoint.y / TankWorld.density))
                .putFloat(heading)
                .putShort((short) nameBytes.length)
                .put(nameBytes)
                .array();
        return bts;
    }

    private static final int SIZE = 16;

    public void fromDataInputStream (DataInputStream dis) throws IOException {
        int id = dis.readInt();
        if (this.id==-1)     //if id is not already set, read it and set it
            this.id = id;
        int x = Math.round(dis.readInt() * TankWorld.density);
        int y = Math.round(dis.readInt()*TankWorld.density);
        this.setLocation(new Point(x, y));
        this.setHeading(dis.readFloat());
        short nameBytesSize = dis.readShort();
        byte [] nameBytes = new byte[nameBytesSize];
        dis.read(nameBytes);
        name = new String(nameBytes);
//        this.health = dis.readInt();
//        this.score = dis.readInt();
    }

    public void setData(PlayerShip data) {
        this.id = data.id;
        this.setLocation(data.getLocationPoint());
        this.setHeading(data.getHeading());
        name = data.getName();
//        this.health = data.getHealth();
//        this.score = data.getScore();
    }

    public void attachInputController(Button[] controls, JoystickView jv) {
        motion = new InputController(this, controls, jv);
    }

    public boolean canBeDrawn() {
        return canBeDrawn;
    }

    public void setCanBeDrawn(boolean canBeDrawn) {
        this.canBeDrawn = canBeDrawn;
    }


}
