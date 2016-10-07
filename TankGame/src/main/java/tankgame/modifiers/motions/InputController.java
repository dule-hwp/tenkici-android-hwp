package tankgame.modifiers.motions;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


import gamengine.modifiers.motions.MotionController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Observable;

import tankgame.R;
import tankgame.TankWorld;
import tankgame.android.JoystickMovedListener;
import tankgame.android.JoystickView;
import tankgame.game.PlayerShip;

public class InputController extends MotionController implements View.OnTouchListener, JoystickMovedListener {

    Field field;
    Method action;
    int moveState;
    Button[] keys;
    PlayerShip mPlayer;
//    boolean player;

//    public InputController(PlayerShip player) {
//        this(player, new Button[]{}, null);
//        moveState = 0;
//        this.player = true;
//    }

    public InputController(PlayerShip player, Button[] keys, JoystickView jv) {
        super(TankWorld.getInstance());
        this.addObserver(player);
        mPlayer = player;
        this.action = null;
        this.field = null;
        this.keys = keys;
        if (keys!=null)
        {
            for (View b : keys) {
                b.setOnTouchListener(this);
            }
        }
        if (jv!=null)
            jv.setOnJostickMovedListener(this);
    }


    private void setMove(String direction) {
        try {
            field = PlayerShip.class.getDeclaredField(direction);
            moveState = 1;
            this.setChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyObservers();
    }

    private void setFire() {
        field = null;
        try {
            action = PlayerShip.class.getMethod("startFiring");
            this.setChanged();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        notifyObservers();
    }

    private void unsetMove(String direction) {
        try {
            field = PlayerShip.class.getDeclaredField(direction);
            moveState = 0;
            this.setChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyObservers();
    }

    private void unsetFire() {
        field = null;
        try {
            action = PlayerShip.class.getMethod("stopFiring");
            this.setChanged();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        notifyObservers();
    }

    public void read(Object theObject) {
        PlayerShip player = (PlayerShip) theObject;

        try {
            field.setInt(player, moveState);
        } catch (Exception e) {
            //e.printStackTrace();
            try {
                action.invoke(player);
            } catch (Exception e2) {
            }
        }
    }

    public void clearChanged() {
        super.clearChanged();
    }

    @Override
    public void update(Observable o, Object arg) {
    }

//    public void keyPressed(KeyEvent e) {
//        int code = e.getKeyCode();
//        // left
//        if (code == keys[0]) {
//            this.setMove("left");
//        } // up
//        else if (code == keys[1]) {
//            this.setMove("up");
//        } // right
//        else if (code == keys[2]) {
//            this.setMove("right");
//        } // down
//        else if (code == keys[3]) {
//            this.setMove("down");
//        } // fire
//        else if (code == keys[4]) {
//            this.setFire();
//        }
//        setChanged();
//        this.notifyObservers();
//    }

//    public void keyReleased(KeyEvent e) {
//        int code = e.getKeyCode();
//        if (code == keys[0]) {		//
//            this.unsetMove("left");
//        } else if (code == keys[1]) {
//            this.unsetMove("up");
//        } else if (code == keys[2]) {
//            this.unsetMove("right");
//        } else if (code == keys[3]) {
//            this.unsetMove("down");
//        } else if (code == keys[4]) {
//            this.unsetFire();
//        }
//        setChanged();
//        this.notifyObservers();
//    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_UP:     //button released
                switch (v.getId())
                {
                    case R.id.btnFire:
                        this.unsetFire();
                        break;
//                    case R.id.btnDown:
//                        this.unsetMove("down");
//                        break;
                }

                break;
            case MotionEvent.ACTION_DOWN:     //button pressed
                switch (v.getId())
                {
                    case R.id.btnFire:
                        this.setFire();
                        break;
//                    case R.id.btnDown:
//                        this.setMove("down");
//                        break;
                }
        }
        setChanged();
        notifyObservers();
        return true;
    }

    @Override
    public void OnMoved(double angle, double radius, double maxRadius) {
        try {
            Method method = PlayerShip.class.getMethod("setHeading", float.class);
//            PlayerShip player = TankWorld.getInstance().getEnemies().next();
            method.invoke(mPlayer, (float) angle);
            //AS (inteliJ) provide mechanism for checking reflection method!!!
            Method setSpeed = PlayerShip.class.getMethod("setGear", double.class);
            double gear = calculateGear(radius, maxRadius);
            setSpeed.invoke(mPlayer, gear);


//            if (angle<0) {
                setMove("up");
//                unsetMove("down");
//            }
//            else {
//                setMove("down");
//                unsetMove("up");
//            }
//            Log.d("radiusTag", radius + " is radius||||" + angle + " is angle |||" + " is gear" + "up down"+player.up+" "+player.down);
            setChanged();
            notifyObservers();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private double calculateGear(double radius, double maxRadius)
    {
        double noSpeed = maxRadius/3;
        double speed = maxRadius-noSpeed;
        if (radius<noSpeed)
            return 0;
        else if (radius < noSpeed+speed/3)
            return 0.33;
        else if (radius < noSpeed+2*speed/3)
            return 0.66;
        else //(radius < noSpeed+3*speed/3)
            return 1;

    }

    @Override
    public void OnReleased() {
        this.unsetMove("up");
        this.unsetMove("down");
    }
}
