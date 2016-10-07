package tankgame.network;

import org.apache.http.util.ByteArrayBuffer;

import gamengine.game.GameObject;

/**
 * Created by dusan_cvetkovic on 11/23/15.
 */
public class GamePacket {

    private ByteArrayBuffer buffer;



    public int size() {
        return buffer.length();
    }

    public byte[] getBytes() {
        return buffer.toByteArray();
    }
}
