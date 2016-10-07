/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamengine.game;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dusan_cvetkovic
 */
public class ObjectManager implements Observer{

    ArrayList<SchedulingObject> objects; 
    IGameWorld world;

    public ObjectManager(IGameWorld gw) {
        objects = new ArrayList<>();
        world = gw;
    }
    
    @Override
    public void update(Observable o, Object arg) {
        checkScheduledObjects();
    }
    
    private void checkScheduledObjects()
    {
        int frame = world.getFrameNumber();
        ListIterator<SchedulingObject> listIterator = objects.listIterator();
        while (listIterator.hasNext())
        {
            SchedulingObject next = listIterator.next();
            if (frame>=next.frameToAppear)
            {
                GameObject go = (GameObject) next.obj;
                go.show=true;
                try {
                    //                TankWorld.getInstance().addBackground(bo);
                    next.action.invoke(world, go);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(ObjectManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(ObjectManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(ObjectManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                listIterator.remove();
            }
        }
//        if (frame%100==0)
//        {
//            if (TankWorld.getInstance().getPowerups().size()<=10)
//                TankWorld.getInstance().addRandomPowerUp();
//        }
    }
    
    public void addObject(SchedulingObject so)
    {
        this.objects.add(so);
    }
}


