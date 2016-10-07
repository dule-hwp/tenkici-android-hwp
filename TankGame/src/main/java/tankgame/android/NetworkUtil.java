package tankgame.android;

import java.nio.ByteBuffer;

/**
 * Created by dusan_cvetkovic on 11/24/15.
 */
public class NetworkUtil {
    public static byte[] marshall(INetworkable in, short reqCode) {
        byte[] bytes = in==null ? new byte[0] : in.toBytes();
        int msgSize = bytes.length;
        byte[] baToSend = ByteBuffer.allocate(4+msgSize)
                .putShort((short) (msgSize+2))
                .putShort(reqCode)
                .put(bytes)
                .array();
        return baToSend;
    }

    public static byte[] marshall(byte[] bytes, short reqCode) {
        int msgSize = bytes.length;
        byte[] baToSend = ByteBuffer.allocate(4+msgSize)
                .putShort((short) (msgSize+2))
                .putShort(reqCode)
                .put(bytes)
                .array();
        return baToSend;
    }

}
