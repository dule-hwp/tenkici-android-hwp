package gamengine.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import gamengine.utils.ImageUtils;
import tankgame.TankWorld;
//import java.awt.Graphics;
//import java.awt.Image;
//import java.awt.Point;
//import java.awt.image.ImageObserver;

public class Wall extends BackgroundObject {

    private final boolean mIsBreakabe;
//    public int destroyedInFrame = 0;

    public Wall(int w, int h, Point startingPoint, Bitmap img, boolean isBreakable) {
        super(startingPoint, w, h, new Point(0, 0), img);
//        this.img = img;
        mIsBreakabe = isBreakable;
    }


    @Override
    public void update(int w, int h) {
    }
    

    public void draw(Canvas g) {
        if (mIsBreakabe) {
            super.draw(g);
//            drawBoundingOfCollisionArea(g);
            return;
        }
        int tileWidth = img.getWidth();
        int tileHeight = img.getHeight();

        int NumberX = width / tileWidth;
        int NumberY = height / tileHeight;

        for (int i = 0; i < NumberY; i++) {
            for (int j = 0; j < NumberX; j++) {
                int left = location.left + j * tileWidth;
                int top = location.top + i * tileHeight;
                g.drawBitmap(img, left, top, null);
            }
        }

//        drawBoundingOfCollisionArea(g);
    }

    @Override
    public String toString() {
        return "Wall: w:" + this.width + " h:" + this.height;
    }

    public boolean isBreakable() {
        return mIsBreakabe;
    }

}
