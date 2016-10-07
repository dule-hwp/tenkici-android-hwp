/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamengine.game;

import java.lang.reflect.Method;

/**
 *
 * @author dusan_cvetkovic
 */
public class SchedulingObject {
    public GameObject obj;
    public int frameToAppear;
    public Method action;

    public SchedulingObject(GameObject obj, Method action, int frameToAppear) {
        this.obj = obj;
        this.action = action;
        this.frameToAppear = frameToAppear;
    }
    
}
