package tankgame.ui;

import android.graphics.Canvas;
import android.graphics.Point;

//import java.awt.Graphics;
//import java.awt.Point;
//import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;

public abstract class InterfaceObject implements Observer{
	Point location;
	
	public abstract void draw(Canvas g);

	public void update(Observable o, Object arg) {
	}
}
