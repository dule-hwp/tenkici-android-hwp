package gamengine.game;

import android.graphics.Bitmap;
import android.graphics.Point;

/*BackgroundObjects move at speed of 1 and are not collidable*/
public class BackgroundObject extends GameObject {
	public BackgroundObject(Point location, Bitmap img){
		super(location, new Point(0,1), img);
	}
	
	public BackgroundObject(Point location, Point speed, Bitmap img){
		super(location, speed, img);
	}
        
        public BackgroundObject(Point location,int w, int h, Point speed, Bitmap img){
		super(location,w,h, speed, img);
	}

        
}
