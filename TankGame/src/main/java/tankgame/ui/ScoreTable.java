package tankgame.ui;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Observable;
import java.util.Observer;

import tankgame.R;
import tankgame.TankWorld;
import tankgame.game.PlayerShip;
import tankgame.game.UiInfo;

/**
 * Created by dusan_cvetkovic on 12/4/15.
 */
public class ScoreTable implements Observer{
    private static ScoreTable instance = new ScoreTable();
    private TextView tvPlayer1, tvPlayer2,tvPlayer3, tvPlayer4;
    private Activity mActivity;

    public static ScoreTable getInstance() {
        return instance;
    }

    private ScoreTable() {

    }

    public void init(Activity activity)
    {
        mActivity = activity;
        tvPlayer1 = (TextView) activity.findViewById(R.id.tvPlayer1);
        tvPlayer2 = (TextView) activity.findViewById(R.id.tvPlayer2);
        tvPlayer3 = (TextView) activity.findViewById(R.id.tvPlayer3);
        tvPlayer4 = (TextView) activity.findViewById(R.id.tvPlayer4);
    }


    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof UiInfo)
        {
            UiInfo uiInfo = (UiInfo) observable;
            PlayerShip player = TankWorld.getInstance().getEnemiesHashMap().get(uiInfo.getOwnerID());
            if (player==null)
                player = TankWorld.getInstance().getPlayer();
            String colorString = Integer.toHexString(player.getColor());
            colorString = colorString.replaceFirst("ff", "88");
            Field textViewField;
            TextView textView=null;
            try {
                textViewField = this.getClass().getDeclaredField("tvPlayer" + (uiInfo.getOwnerID() + 1));
                textView = (TextView) textViewField.get(this);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (textView!=null)
                setTextView(uiInfo, player, colorString, textView);

//            Log.e(getClass().getCanonicalName(),"id: " + uiInfo.getOwnerID()+" score: "+uiInfo.getScore());
        }
    }

    public void setTextView(final UiInfo uiInfo, final PlayerShip player, final String colorString, final TextView tvPlayer) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvPlayer.setVisibility(View.VISIBLE);
                tvPlayer.setTextColor(Color.parseColor("#"+colorString));
                String text = String.format(mActivity.getString(R.string.player_data), player.getName(), uiInfo.getScore());
                tvPlayer.setText(text);
            }
        });

    }
}
