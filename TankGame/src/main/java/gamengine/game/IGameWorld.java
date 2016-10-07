/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamengine.game;

import java.util.Observer;

/**
 *
 * @author dusan_cvetkovic
 */
public interface IGameWorld {
    int getFrameNumber();
    void addClockObserver(Observer o);
    void removeClockObserver(Observer o);
    
}
