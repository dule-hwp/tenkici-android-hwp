package tankgame.ui;

//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.Graphics;
//import java.awt.Point;
//import java.awt.event.KeyEvent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.view.KeyEvent;

import java.util.Observable;

import gamengine.modifiers.AbstractGameModifier;
import tankgame.TankWorld;
import tankgame.game.PlayerShip;
import tankgame.modifiers.motions.MenuController;

public class GameMenu extends InterfaceObject {
	int selection;
	MenuController controller;
	boolean waiting;
	
	public GameMenu(){
		selection = 1;
		controller = new MenuController(this);
		waiting = true;
	}
	public void draw(Canvas g2){
//		g2.setFont(new Font("Calibri", Font.PLAIN, 24));
//		if(selection==0)
//			g2.setColor(Color.RED);
//		else
//			g2.setColor(Color.WHITE);
//		g2.drawString("1 Player", 200,150);
//		if(selection==1)
//			g2.setColor(Color.RED);
//		else
//			g2.setColor(Color.WHITE);
//		g2.drawString("2 Player", 200, 250);
//		if(selection==2)
//			g2.setColor(Color.RED);
//		else
//			g2.setColor(Color.WHITE);
//		g2.drawString("Quit", 200, 350);
	}
	
	public void down(){
		if(selection<2)
			selection++;
	}
	
	public void up(){
		if(selection>0)
			selection--;
	}
	
	public void applySelection(){
//		TankWorld world = TankWorld.getInstance();
//		Dimension size = world.getSize();
//		int[] controls = new int[2];
//		if(selection == 0){
////			int[] controls = {KeyEvent.,KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_SPACE};
//			PlayerShip player = new PlayerShip(new Point(240, size.height-150), new Point(6,6),TankWorld.sprites.get("player1"), null, Color.RED);
//			world.addPlayer(player);
//		}
//		else if(selection == 1){
////			int[] controls = {KeyEvent.VK_A,KeyEvent.VK_W, KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_SPACE};
//			PlayerShip[] players = new PlayerShip[2];
//			players[0] = new PlayerShip(new Point(32, 32), new Point(6,6),TankWorld.sprites.get("player1"), controls, Color.RED);
////			controls = new int[] {KeyEvent.VK_LEFT,KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_ENTER};
//			players[1] = new PlayerShip(new Point(32, size.height-3*32), new Point(6,6),TankWorld.sprites.get("player2"), controls, Color.BLUE);
////                        players[1].setHeading(Math.PI);
//			world.addPlayer(players);
//
//		}
//		else{
//			System.exit(0);
//		}
		
//		GameSounds.playClip("Resources/Music.wav");
		
//		controller.deleteObservers();
//		world.removeKeyListener(controller);
//		world.level.load();
//		waiting=false;
	}
	
	public void update(Observable o, Object arg) {
		AbstractGameModifier modifier = (AbstractGameModifier) o;
		modifier.read(this);
	}
	
	public boolean isWaiting(){
		return this.waiting;
	}
}
