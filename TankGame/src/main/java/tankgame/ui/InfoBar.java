package tankgame.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

//import java.awt.Color;
//import java.awt.Font;
//import java.awt.Graphics;
//import java.awt.Image;
//import java.awt.Point;
//import java.awt.Rectangle;

import gamengine.utils.Utils;
import tankgame.TankWorld;
import tankgame.game.PlayerShip;
import tankgame.game.UiInfo;

public class InfoBar extends InterfaceObject {


//    private final View infoBar;

    private final UiInfo uiInfo;
    PlayerShip player;
    int playerNumber;
    private final int healthBarWidth;
    private final int healthBarHeight = TankWorld.getDevicePixelValue(5);
//    private final Point scoreLocation=new Point(0,50);
//    private Color scoreColor;

    public InfoBar(PlayerShip player, UiInfo ui) {
        uiInfo = ui;
        this.player = player;
        this.healthBarWidth = player.getCollisionRegion().getBounds().width();
//        switch (playerNum) {
//            case 1:
//                this.scoreLocation.x = GameWorld.getInstance().getWindowSize().width / 4;
//                this.scoreColor = Color.RED;
//                break;
//            case 2:
//                this.scoreLocation.x = GameWorld.getInstance().getWindowSize().width * 3 / 4;
//                this.scoreColor = Color.BLUE;
//                break;
//            default:
//                this.scoreLocation.x = 0;
//        }

    }

    @Override
    public void draw(Canvas g2) {
        Paint p = new Paint();
        if (uiInfo.getHealth() > 40) {
            p.setColor(Color.GREEN);
        } else if (uiInfo.getHealth() > 20) {
            p.setColor(Color.YELLOW);
        } else {
            p.setColor(Color.RED);
        }
        Rect tankLoc = player.getLocation();
        float healthPercentage = (float) uiInfo.getHealth() / (float) 100;
        int top = tankLoc.top - healthBarHeight;
        int left = tankLoc.centerX() - healthBarWidth / 2;
        g2.drawRect(left, top, left + Math.round(healthPercentage * healthBarWidth), top + healthBarHeight, p);

        Bitmap img = Utils.getWeaponImage(uiInfo.getWeaponId());
        if (img!=null)
            g2.drawBitmap(img, tankLoc.centerX() - healthBarWidth / 2,
                tankLoc.top + tankLoc.height(),
                null);
    }

    public UiInfo getUiInfo() {
        return uiInfo;
    }

    public void setUiInfo(UiInfo uiInfo) {
        this.uiInfo.setData(uiInfo);
    }
}
