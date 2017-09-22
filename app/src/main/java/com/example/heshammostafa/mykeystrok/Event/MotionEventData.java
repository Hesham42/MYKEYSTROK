package com.example.heshammostafa.mykeystrok.Event;

import android.view.MotionEvent;

/**
 * Created by HeshamMostafa on 7/28/2017.
 */
//Motion events can be recycled, so it's best to have a separate class to contain all the data from the motion event.

public class MotionEventData {
    private final long mTime;
    private final float mX;
    private final float mY;
    private final float mPressure;
    private final float mTouchMajor;
    private final float mTuchMinor;
    private final float mToolMajor;
    private final float mToolMinor;

    public MotionEventData(long time, float x, float y, float pressure, float touchMajor, float touchMinor,
                           float toolMajor, float toolMinor) {
        this.mTime = time;
        this.mX = x;
        this.mY = y;
        this.mPressure = pressure;
        this.mTouchMajor = touchMajor;
        this.mTuchMinor = touchMinor;
        this.mToolMajor = toolMajor;
        this.mToolMinor = toolMinor;
    }

    MotionEventData(MotionEvent event) {
        mTime = event.getEventTime();
        mX = event.getRawX();
        mY = event.getRawY();
        mPressure = event.getPressure();
        mTouchMajor = event.getTouchMajor();
        mTuchMinor = event.getTouchMinor();
        mToolMajor = event.getToolMajor();
        mToolMinor = event.getToolMinor();
    }

    public long getTime() {
        return mTime;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public float getPressure() {
        return mPressure;
    }

    public float getTouchMajor() {
        return mTouchMajor;
    }

    public float getTouchMinor() {
        return mTuchMinor;
    }

    public float getToolMajor() {
        return mToolMajor;
    }

    public float getToolMinor() {
        return mToolMinor;
    }
}