/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame.game;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Observable;

import tankgame.android.INetworkable;
import tankgame.ui.ScoreTable;

/**
 *
 * @author dusan_cvetkovic
 */
public class UiInfo extends Observable implements INetworkable {
    int score;
    int health;
    int weaponId;
    private int ownerID;

    public UiInfo(int ownerId, int score, int health, int weaponId) {
        this.ownerID = ownerId;
        this.score = score;
        this.health = health;
        this.weaponId = weaponId;
        this.addObserver(ScoreTable.getInstance());
        setChanged();
        notifyObservers();
    }

    public UiInfo() {
    }

    public int getOwnerID() {
        return ownerID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        setChanged();
        notifyObservers();
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getWeaponId() {
        return weaponId;
    }

    public void setWeaponId(int weaponId) {
        this.weaponId = weaponId;
    }
    public static final int SIZE = 16;
    @Override
    public byte[] toBytes() {
        byte[] bts = ByteBuffer.allocate(SIZE)
                .putInt(ownerID)
                .putInt(score)
                .putInt(health)
                .putInt(weaponId)
                .array();
        return bts;
    }

    @Override
    public void fromDataInputStream(DataInputStream dataInput) throws IOException {
        ownerID = dataInput.readInt();
        int newScore = dataInput.readInt();
//        if (score!=newScore) {
            this.score = newScore;
//            setChanged();
//            notifyObservers();
//        }
        this.health = dataInput.readInt();
        this.weaponId = dataInput.readInt();
    }

    @Override
    public String toString() {
        return "oid: "+ownerID+" score: "+score+" health:"+health;
    }

    public void setData (UiInfo ui)
    {
        ownerID = ui.getOwnerID();
        score = ui.getScore();
        health = ui.health;
        weaponId = ui.weaponId;
        setChanged();
        notifyObservers();
    }
}
