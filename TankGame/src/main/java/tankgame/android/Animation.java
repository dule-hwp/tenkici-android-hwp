package tankgame.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.Toast;


import gamengine.utils.Constants;
import tankgame.R;
import tankgame.TankWorld;
import tankgame.game.PlayerShip;
import tankgame.network.TCPClient;

public class Animation extends Activity {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private TankWorld tankWorld;
    private Button btnUp;
//    private Button btnDown;

    private JoystickView joystickView;
    private TCPClient mTcpClient;
    private boolean mConnected;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        btnUp = (Button) findViewById(R.id.btnFire);
//        btnDown = (Button) findViewById(R.id.btnDown);
        joystickView = (JoystickView)findViewById(R.id.joystickView);
        surfaceHolder = surfaceView.getHolder();

        tankWorld = TankWorld.getInstance();
        tankWorld.init(this, surfaceHolder, new Button[]{btnUp}, joystickView);
        surfaceHolder.addCallback(tankWorld);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.


        new connectTask().execute("");
        while (mTcpClient==null || !mTcpClient.isReady())
        {
            //waiting for tcp client to settle
            Log.d("TCP client","Connecting");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        PlayerShip player = new PlayerShip();
        player.setHeading(0);
        player.setName(getDeviceEmail());
        mTcpClient.sendMessage(NetworkUtil.marshall(player, Constants.CMSG_AUTH));
        tankWorld.setTcpClient(mTcpClient);
    }

    private String getDeviceEmail() {
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();

        for(Account account: list)
        {
            if(account.type.equalsIgnoreCase("com.google"))
            {
                return account.name;
            }
        }

        return "";
    }

    public class connectTask extends AsyncTask<String,String,Boolean> {

        @Override
        protected Boolean doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(TankWorld.getInstance());
            try {
                mTcpClient.run();
            }
            catch (Exception e)
            {
                finish();
                mConnected = false;
                return false;
            }


            mConnected = true;
            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(Animation.this);
                builder .setMessage("Something went wrong. Try again?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();
            }

        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    new connectTask().execute("");
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    finish();
                    break;
            }
        }
    };


    @Override
    public void onBackPressed() {
        TankWorld.getInstance().stop();

        if (mTcpClient!=null) {
            PlayerShip player = TankWorld.getInstance().getPlayer();
            mTcpClient.sendMessage(NetworkUtil.marshall(player, Constants.CMSG_LOG_OFF));
            mTcpClient.stopClient();
        }
        super.onBackPressed();
    }
}

