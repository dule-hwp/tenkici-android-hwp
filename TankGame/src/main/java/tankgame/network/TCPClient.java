package tankgame.network;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {

//    private String serverMessage;
    private static final String SERVERIP = "192.168.0.18"; //your computer IP address
//    public static final String SERVERIP = "10.143.63.199"; //your computer IP address
//    public static final String SERVERIP = "thecity.sfsu.edu"; //your computer IP address

    private static final int SERVERPORT = 9252;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;

    OutputStream out;
    InputStream in;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(byte[] message) {

        try {
            if (out != null) {
                out.write(message);
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void stopClient(){
        mRun = false;
    }

    public void run() throws Exception {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server

            Socket socket= null;
            try {
                socket = new Socket(serverAddr, SERVERPORT);
                //send the message to the server
                out = socket.getOutputStream();

                Log.e("TCP Client", "C: Sent.");

                Log.e("TCP Client", "C: Done.");

                //receive the message which the server sends back
                in = socket.getInputStream();

                //in this while the client listens for the messages sent by the server
                while (mRun) {
//                    serverMessage = in.readLine();

                    if (in.available()>0 && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(in);
                    }
//                    serverMessage = null;

                }

//                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);
                throw new Exception(e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                if (socket!=null)
                    socket.close();
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);
            throw new Exception(e);
        }

    }

    public boolean isReady()
    {
        return in!=null && out!=null;
    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
         void messageReceived(InputStream message);
    }
}