package com.javacodegeeks.android;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by dusan_cvetkovic on 11/12/15.
 */
public class AnimView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private AnimThread animThread;

    public AnimView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        animThread = new AnimThread(holder);
        animThread.setRunning(true);
        animThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        animThread.setRunning(false);
        while (retry) {
            try {
                animThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}
