package tankgame.game;

import android.graphics.Bitmap;
import android.graphics.Point;


import gamengine.game.BackgroundObject;

import tankgame.TankWorld;


/* BigExplosion plays when player dies*/
public class BigExplosion extends BackgroundObject {

    int timer;
    int frame;
    Bitmap animation[] = new Bitmap[7];
    static Bitmap sprite = TankWorld.sprites.get("explosion2_1");

    public BigExplosion(Point location) {
        super(location, new Point(0, 0), sprite);
        timer = 0;
        frame = 0;
//        TankWorld.sound.play("Resources/Explosion_large.wav");
        for (int i = 1; i < 8; i++) {
            animation[i - 1] = TankWorld.sprites.get("explosion2_" + i);
        }
    }

    public void update(int w, int h) {
        super.update(w, h);
        timer++;
        if (timer % 2 == 0) {
            frame++;
            if (frame < 7) {
                this.img = animation[frame];
            } else {
                this.show = false;
            }
        }

    }
    
//    public void draw(Graphics g, BitmapObserver obs) {
//        if (show) {
//            g.drawBitmap(img, location.x-sprite.getWidth(observer)/2, location.y-sprite.getHeight(observer)/2, obs);
//        }
//    }

}
