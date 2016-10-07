package tankgame.android;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by dusan_cvetkovic on 11/24/15.
 */
public interface INetworkable {
    byte[] toBytes();
    void fromDataInputStream(DataInputStream dis) throws IOException;
}
