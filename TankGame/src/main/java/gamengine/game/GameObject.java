/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamengine.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;

//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Image;
//import java.awt.Point;
//import java.awt.Rectangle;
//import java.awt.geom.Area;
//import java.awt.image.BufferedImage;
//import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;

import gamengine.modifiers.AbstractGameModifier;
import tankgame.TankWorld;

public class GameObject implements Observer {

    private final Point mLocationPoint;
    protected Point speed;
    protected Rect location;
    protected Bitmap img;
    protected int height, width;
    protected float heading = 0;
//    protected ImageObserver observer;
    public boolean show;
    private Point lastCollision;
    private Region mCollisionRegion;

    public GameObject() {
        this(new Point(0,0), Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
    }

    public GameObject(Point location, Point speed, Bitmap img) {
        this(location,img.getWidth(),img.getHeight(),speed,img);
    }

    public GameObject(Point location, int w, int h, Point speed, Bitmap img) {
        this.speed = speed;
//        float density = TankWorld.density;
//        this.img = Bitmap.createScaledBitmap(img, (int) (img.getWidth() / density), (int) (img.getHeight() / density), false);
        this.img = img;
        this.show = true;
        this.location = new Rect(location.x, location.y,
                location.x+this.img.getWidth(), location.y+this.img.getHeight());
        width = w;
        height = h;
        mCollisionRegion = new Region(this.location);
        mLocationPoint = location;
    }

    public void setSpeed(Point speed) {
        this.speed = speed;
    }

    public GameObject(Point location, Bitmap img) {
        this(location, new Point(0, 0), img);
    }

    public void draw(Canvas g) {
        if (show) {
//            g.drawImage(img, location.x, location.y, obs);
            g.drawBitmap(img, location.left, location.top, null);
        }
    }

    public void setImage(Bitmap img) {
        this.img = img;
        Point location = getLocationPoint();
        if (location!=null)
//            this.location = new Rect(location.x, location.y,
//                    location.x+this.img.getWidth(), location.y+this.img.getHeight());
            this.location.set(location.x, location.y,
            location.x+this.img.getWidth(), location.y+this.img.getHeight());
    }

    public Bitmap getImg() {
        return img;
    }

    public void update(int w, int h) {
        location.left += speed.x;
        location.top += speed.y;

        if (location.top < -100 || location.top == h + 100) {
            this.show = false;
        }

    }

    @Override
    public void update(Observable o, Object arg) {
        AbstractGameModifier modifier = (AbstractGameModifier) o;
        modifier.read(this);
    }

    public int getX() {
        return location.left;
    }

    public int getY() {
        return location.top;
    }

    public int getCenterX(){
        return location.centerX();
    }

    public int getSizeX() {
        return width;
    }

    public int getSizeY() {
        return height;
    }

    public Point getSpeed() {
        return speed;
    }

    public void setLocation(Point newLocation) {
//        location.setLocation(newLocation);
        location.offsetTo(newLocation.x, newLocation.y);
//        location.set(newLocation.x, newLocation.y, newLocation.x+width, newLocation.y+height);
    }

    public Rect getLocation() {
//        return new Rect(this.location);
        return this.location;
    }

    public Point getLocationPoint() {
        mLocationPoint.set(location.left,location.top);
        return mLocationPoint;
//        return new Point(location.left, location.top);
    }

    public void move(int dx, int dy) {
//        location.translate(dx, dy);
        location.offset(dx, dy);
    }

    public void move() {
        move(speed.x, speed.y);
    }

    public void moveByHeading(double angle) {

        int dy = (int) Math.round(speed.x * Math.sin(Math.toRadians(angle)));
        int dx = (int) Math.round(speed.x * Math.cos(Math.toRadians(angle)));
        move(dx, dy);
    }

    public boolean collision(GameObject otherObject) {
        Region region1 = getCollisionRegion();
        Region region2 = otherObject.getCollisionRegion();
        if (!region1.quickReject(region2) && region1.op(region2, Region.Op.INTERSECT)) {
            return true; //There is a collision
        }
        return false;
    }

    public float getHeading() {
        return heading;
    }

    public void setHeading(float heading) {
        this.heading = heading;
    }

    public Region getCollisionRegion(){
//        Rect rect = new Rect(location.left, location.top,
//                location.left + width, location.top + height);
//        return new Region(rect);
//        mCollisionRegion.set(location);
        return mCollisionRegion;
    }
    
    protected void drawBoundingOfCollisionArea(Canvas g) {
        Region a = getCollisionRegion();
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        g.drawPath(a.getBoundaryPath(), paint);
    }

}
