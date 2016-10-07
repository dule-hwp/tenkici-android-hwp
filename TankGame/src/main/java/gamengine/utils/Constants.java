package gamengine.utils;

public class Constants {



    // Constants
    public static String CLIENT_VERSION = "1.00";
    public static String REMOTE_HOST = "localhost";
    public static int REMOTE_PORT = 9252;

    // Request (1xx) + Response (2xx)
    public static final short CMSG_AUTH = 101;
    public static final short SMSG_AUTH = 201;

    public static final short CMSG_HEARTBEAT = 102;
    public static final short SMSG_HEARTBEAT = 202;

    public static final short CMSG_ENEMIES = 103;
    public static final short SMSG_ENEMIES = 203;

    public static final short CMSG_TEST = 104;
    public static final short SMSG_TEST = 204;

    public static final short CMSG_ADD_BULLET = 105;
    public static final short SMSG_ADD_BULLET = 205;

    public static final short CMSG_PLAYER_UPDATE = 106;
    public static final short SMSG_PLATERUPDATE = 206;

    public static final short CMSG_UPDATE_BULLET = 107;
    public static final short SMSG_UPDATE_BULLET = 207;

    public static final short CMSG_UI_INFO_UPDATE = 108;
    public static final short SMSG_UI_INFO_UPDATE = 208;

    public static final short CMSG_REMOVE_POWER_UP = 109;
    public static final short SMSG_REMOVE_POWER_UP = 209;

    public static final short CMSG_ADD_POWER_UP = 110;
    public static final short SMSG_ADD_POWER_UP = 210;

    public static final short CMSG_LOG_OFF = 111;



    // GUI Window IDs
    public enum GUI_ID {
        Login
    }

    ;

    public static int USER_ID = -1;
}