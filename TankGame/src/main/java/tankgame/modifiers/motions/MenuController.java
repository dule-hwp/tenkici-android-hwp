package tankgame.modifiers.motions;

import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;

import gamengine.modifiers.motions.MotionController;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Observable;
import java.util.Observer;
import tankgame.game.PlayerShip;
import tankgame.ui.GameMenu;

import tankgame.TankWorld;

public class MenuController extends MotionController implements KeyListener {
	KeyListener listener;
	Field field;
	Method action;
	int moveState;
	int[] keys;
	boolean player;
	
	public MenuController(GameMenu menu){
		this(menu, new int[] {KeyEvent.ACTION_DOWN});
		this.player = false;
	}
	
	public MenuController(PlayerShip player){
		this(player, new int[] {KeyEvent.ACTION_DOWN});
		moveState = 0;
		this.player = true;
	}
	
	public MenuController(Observer theObject, int[] keys){
		super(TankWorld.getInstance());
		this.addObserver(theObject);
		this.action = null;
		this.field = null;
		this.keys = keys;
		TankWorld world = TankWorld.getInstance();
//		world.addKeyListener(this);
	}
	
	public void signalKeyPress(KeyEvent e){

	}
	
	private void setMove(String direction) {
		try{
			action = GameMenu.class.getMethod(direction);
			this.setChanged();
		} catch (Exception e){}
	}
	
	private void setFire(){
		try {
			action = GameMenu.class.getMethod("applySelection");
		} catch (NoSuchMethodException | SecurityException e1) {}
		notifyObservers();
	}
	
	public void read(Object theObject) {
			try{
				action.invoke(theObject);
				action = null;
			} catch (Exception e) {}
	}
	
	public void clearChanged(){
		super.clearChanged();
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
	
    public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		// left
		if(code==keys[0]) {
			this.setMove("left");
		}
		// up
		else if(code==keys[1]) {
			this.setMove("up");
		}
		// right
		else if(code==keys[2]) {
			this.setMove("right");
		}
		// down
		else if(code==keys[3]) {
			this.setMove("down");
		}
		// fire
		else if(code==keys[4]){
			this.setFire();
		}
		// also map to enter key!
//		else if(code==KeyEvent.VK_ENTER){
//			this.setFire();
//		}
		setChanged();
		this.notifyObservers();
    }
    
    public void keyReleased(KeyEvent e) {
    }

//	@Override
//	public void keyTyped(KeyEvent e) {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public int getInputType() {
		return 0;
	}

	@Override
	public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyOther(View view, Editable text, KeyEvent event) {
		return false;
	}

	@Override
	public void clearMetaKeyState(View view, Editable content, int states) {

	}
}