package gamengine.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
//import java.awt.Graphics;
//import java.awt.Image;
//import java.awt.Point;
//import java.awt.geom.Area;
//import java.awt.image.ImageObserver;

/* This is where the scrolling background is drawn*/
public class Background extends BackgroundObject {
	int move, w, h;

	public Background(int w, int h, Point speed, Bitmap img) {
		super(new Point(0,0), speed, img);
		this.img = img;
		this.w = w;
		this.h = h;
		move = 0;
	}
	
    public void update(int w, int h) {
    }
	
    public void draw(Canvas g) {
        int TileWidth = img.getWidth();
        int TileHeight = img.getHeight();

        int NumberX = w / TileWidth;
        int NumberY = h / TileHeight;

        //Image Buffer = GameWorld.getInstance().createImage(NumberX * TileWidth, NumberY * TileHeight);
        //Graphics BufferG = Buffer.getGraphics();

        for (int i = -1; i <= NumberY; i++) {
            for (int j = 0; j <= NumberX; j++) {
//                g.drawImage(img, j * TileWidth, i * TileHeight + (move % TileHeight), TileWidth, TileHeight, obs);
                g.drawBitmap(img, j * TileWidth, i * TileHeight + (move % TileHeight), null);
            }
        }
        move += speed.y;
        
//        drawBoundingOfCollisionArea(g);
    }

    @Override
    public Region getCollisionRegion() {
        return new Region(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
