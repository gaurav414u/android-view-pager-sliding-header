package com.gauravbhola.viewpagerslidingheader;

import android.view.MotionEvent;

public interface SlidingHeaderCallbacks {
    public boolean shouldDrawerMove();

    public void dispatchFling(MotionEvent ev1, MotionEvent ev2, float velx, float vely);
}