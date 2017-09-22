package com.example.heshammostafa.mykeystrok.Event;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.SensorEvent;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.example.heshammostafa.mykeystrok.biokeyboard.LatinKeyboard;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by guinness on 21/07/17.
 */public class BioEvent {
    private final Action mAction; // Press or release event
    private final int mEntityId; // Unique ID of the button within the keyboard type
    private final LatinKeyboard.Type mKeyboardType; //The screen layout: i.e. qwerty, symbols, etc.
    private final ScreenOrientation mScreenOrientation; //The screen orientation: portrait, landscape

    private final MotionEventData mMotionEventData; //Contains most of the touch info and timestamp
    private final DisplayMetrics mDisplayMetrics; //Contains needed hardware info

    private final float mAccelX; //Acceleration of the device
    private final float mAccelY;
    private final float mAccelZ;

    private final float mRotationX; //Rotation of the device, x*sin(θ/2)
    private final float mRotationY;
    private final float mRotationZ;

    public BioEvent(Action action, int entityId, LatinKeyboard.Type keyboardType, ScreenOrientation screenOrientation,
                    MotionEventData motionEventData, DisplayMetrics displayMetrics, float accelX, float accelY, float accelZ,
                    float rotationX, float rotationY, float rotationZ) {
        this.mAction = action;
        this.mEntityId = entityId;
        this.mKeyboardType = keyboardType;
        this.mScreenOrientation = screenOrientation;
        this.mMotionEventData = motionEventData;
        this.mAccelX = accelX;
        this.mAccelY = accelY;
        this.mAccelZ = accelZ;
        this.mRotationX = rotationX;
        this.mRotationY = rotationY;
        this.mRotationZ = rotationZ;

        this.mDisplayMetrics = new DisplayMetrics();
        this.mDisplayMetrics.setTo(displayMetrics);
    }

    public BioEvent(Context context, Action action, int entityId,
                    LatinKeyboard.Type keyboardType, MotionEvent motionEvent,
                    SensorEvent acceleration, SensorEvent rotation, DisplayMetrics displayMetrics) {

        this.mAction = action;
        this.mEntityId = entityId;
        this.mKeyboardType = keyboardType;
        this.mScreenOrientation = ScreenOrientation.fromContext(context);

        this.mAccelX = acceleration == null ? 0 : acceleration.values[0];
        this.mAccelY = acceleration == null ? 0 : acceleration.values[1];
        this.mAccelZ = acceleration == null ? 0 : acceleration.values[2];

        this.mRotationX = rotation == null ? 0 : rotation.values[0];
        this.mRotationY = rotation == null ? 0 : rotation.values[1];
        this.mRotationZ = rotation == null ? 0 : rotation.values[2];

        this.mMotionEventData = new MotionEventData(motionEvent);
        this.mDisplayMetrics = displayMetrics;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new LinkedHashMap<String, Object>(); //Maintain field order with a linked hash map

        values.put("action", mAction.toString());
        values.put("entity", mEntityId);
        values.put("keyboard", mKeyboardType.toString());
        values.put("orientation", mScreenOrientation.toString());

        values.put("x_acceleration", mAccelX);
        values.put("y_acceleration", mAccelY);
        values.put("z_acceleration", mAccelZ);

        values.put("x_rotation", mRotationX);
        values.put("y_rotation", mRotationY);
        values.put("z_rotation", mRotationZ);

        values.put("time", mMotionEventData.getTime());
        values.put("x", mMotionEventData.getX());
        values.put("y", mMotionEventData.getY());
        values.put("pressure", mMotionEventData.getPressure());
        values.put("touch_major", mMotionEventData.getTouchMajor());
        values.put("touch_minor", mMotionEventData.getTouchMinor());
        values.put("tool_major", mMotionEventData.getToolMajor());
        values.put("tool_minor", mMotionEventData.getToolMinor());

        values.put("dpi", mDisplayMetrics.densityDpi);
        values.put("x_dpi", mDisplayMetrics.xdpi);
        values.put("y_dpi", mDisplayMetrics.ydpi);
        values.put("x_max", mDisplayMetrics.widthPixels);
        values.put("y_max", mDisplayMetrics.heightPixels);

        return values;
    }

    public JSONObject toJSON() {
        return new JSONObject(toMap());
    }

    @Override
    public String toString() {
        return String.format("BioEvent: %s %d", mAction, mEntityId);
    }

    public String dump() {
        StringBuilder builder = new StringBuilder();
        Map<String, Object> data = toMap();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (builder.length() > 0) builder.append('\n');
            builder
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue());
        }
        return builder.toString();
    }

    public int getEntityId() {
        return mEntityId;
    }

    public LatinKeyboard.Type getKeyboardType() {
        return mKeyboardType;
    }

    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    public ScreenOrientation getScreenOrientation() {
        return mScreenOrientation;
    }

    public Action getAction() {
        return mAction;
    }

    public MotionEventData getMotionEvent() {
        return mMotionEventData;
    }

    public float getAccelX() {
        return mAccelX;
    }

    public float getAccelY() {
        return mAccelY;
    }

    public float getAccelZ() {
        return mAccelZ;
    }

    public float getRotationX() {
        return mRotationX;
    }

    public float getRotationY() {
        return mRotationY;
    }

    public float getRotationZ() {
        return mRotationZ;
    }

    public enum Action {
        PRESS("press"), RELEASE("release");

        public final String id;

        private Action(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return id;
        }

        public static Action fromId(String id) {
            for (Action action : values()) {
                if (action.id.equals(id)) return action;
            }
            return null;
        }
    }

    public enum ScreenOrientation {
        PORTRAIT("portrait"), LANDSCAPE("landscape");

        public final String id;

        private ScreenOrientation(String id) {
            this.id = id;
        }

        public static ScreenOrientation fromContext(Context context) {
            int orientation = context.getResources().getConfiguration().orientation;
            return orientation == Configuration.ORIENTATION_LANDSCAPE ? LANDSCAPE : PORTRAIT;
        }

        @Override
        public String toString() {
            return id;
        }

        public static ScreenOrientation fromId(String id) {
            for (ScreenOrientation orientation : values()) {
                if (orientation.id.equals(id)) return orientation;
            }
            return null;
        }
    }
}