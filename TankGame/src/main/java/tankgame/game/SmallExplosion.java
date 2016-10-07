package tankgame.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import gamengine.game.BackgroundObject;
import gamengine.game.GameObject;
//import gamengine.utils.BitmapUtils;
//import java.awt.Graphics;
//import java.awt.Bitmap;
//import java.awt.Point;
//import java.awt.geom.Area;
//import java.awt.Bitmap.BitmapObserver;

import tankgame.TankWorld;
import static tankgame.game.BigExplosion.sprite;

/* Small explosions happen whenever an enemy dies */
public class SmallExplosion extends BackgroundObject {

    int timer;
    int frame;
    Bitmap animation[] = new Bitmap[6];
    static Bitmap sprite = TankWorld.sprites.get("explosion1_1");

    public SmallExplosion(Point location) {
        super(location, new Point(0, 0), sprite);
        timer = 0;
        frame = 0;
//        TankWorld.sound.play("Resources/Explosion_small.wav");
        for (int i = 1; i < 7; i++) {
            animation[i - 1] = TankWorld.sprites.get("explosion1_" + i);
        }
    }

    public void update(int w, int h) {
        super.update(w, h);
        timer++;
        if (timer % 2 == 0) {
            frame++;
            if (frame < 6) {
                this.img = animation[frame];
            } else {
                this.show = false;
            }
        }

    }
    
    public void draw(Canvas g) {
        if (show) {
            g.drawBitmap(img, location.left - sprite.getWidth() / 2, location.top - sprite.getHeight() / 2, new Paint());
        }
    }
}
