package tankgame.ui;

//import java.awt.Color;
//import java.awt.Font;
//import java.awt.Graphics;
//import java.awt.Point;
//import java.awt.Rectangle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;

import java.util.ArrayList;

import tankgame.TankWorld;
import tankgame.game.PlayerShip;

public class ScoreInfo extends InterfaceObject {

//    private final int x;
    PlayerShip mPlayer;
//    int playerNumber;
//    private final Point[] mScoreLocations;

    public ScoreInfo(PlayerShip playerShip) {
        mPlayer = playerShip;
//        mScoreLocations = new Point[mPlayers.length];
//        x = TankWorld.getInstance().getSizeX() / 2;
//        for (int i=0;i<mPlayers.length;i++)
//        {
//            mScoreLocations[i]=new Point(x+i*2*x, 50);
//        }
    }

    @Override
    public void draw(Canvas g2) {
//        g2.setFont(new Font("default", Font.BOLD, 30));
        Paint paint = new Paint();
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setColor(mPlayer.getColor());
        g2.drawText(Integer.toString(mPlayer.getScore()), g2.getWidth()/2, 50, paint);


    }

}
