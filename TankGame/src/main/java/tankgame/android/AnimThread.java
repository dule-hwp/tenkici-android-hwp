package com.javacodegeeks.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

/**
 * Created by dusan_cvetkovic on 11/12/15.
 */
public class AnimThread extends Thread{
    private SurfaceHolder holder;
    private boolean running = true;
    private int i;

    public AnimThread(SurfaceHolder holder) {
        this.holder = holder;
    }

    @Override
    public void run() {
        while(running ) {
            Canvas canvas = null;

            try {
                canvas = holder.lockCanvas();
                synchronized (holder) {
                    // draw
                    canvas.drawColor(Color.BLACK);
                    Paint paint = new Paint();
                    paint.setColor(Color.WHITE);
                    canvas.drawCircle(i++, 100, 50, paint);

                }
            }
            finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public void setRunning(boolean b) {
        running = b;
    }
}
