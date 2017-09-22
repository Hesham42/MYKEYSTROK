package com.example.heshammostafa.mykeystrok.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.heshammostafa.mykeystrok.BuildConfig;
import com.example.heshammostafa.mykeystrok.Event.BioEvent;
import com.example.heshammostafa.mykeystrok.Event.MotionEventData;
import com.example.heshammostafa.mykeystrok.biokeyboard.LatinKeyboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by HeshamMostafa on 7/28/2017.
 */

public class Database
        extends SQLiteOpenHelper
        implements BioEventConsumer {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "EventsDatabase";

    private static final String TABLE_EVENTS = "events";
    private static final String TABLE_SESSIONS = "sessions";
    private static final String TABLE_SESSION_TAGS = "sessionTags";

    private static final String EVENTS_ID = "_id";
    private static final String EVENTS_SESSION_UUID = "session_uuid";
    private static final String EVENTS_ACTION = "action";
    private static final String EVENTS_ENTITY = "entity";
    private static final String EVENTS_KEYBOARD = "keyboard";
    private static final String EVENTS_ORIENTATION = "orientation";
    private static final String EVENTS_X_ACCEL = "x_accel";
    private static final String EVENTS_Y_ACCEL = "y_accel";
    private static final String EVENTS_Z_ACCEL = "z_accel";
    private static final String EVENTS_X_ROTATION = "x_rotation";
    private static final String EVENTS_Y_ROTATION = "y_rotation";
    private static final String EVENTS_Z_ROTATION = "z_rotation";
    private static final String EVENTS_MOTION_TIME = "motion_time";
    private static final String EVENTS_MOTION_X = "motion_x";
    private static final String EVENTS_MOTION_Y = "motion_y";
    private static final String EVENTS_MOTION_PRESSURE = "motion_pressure";
    private static final String EVENTS_MOTION_TOUCH_MAJOR = "motion_touch_major";
    private static final String EVENTS_MOTION_TOUCH_MINOR = "motion_touch_minor";
    private static final String EVENTS_MOTION_TOOL_MAJOR = "motion_tool_major";
    private static final String EVENTS_MOTION_TOOL_MINOR = "motion_tool_minor";
    private static final String EVENTS_DISPLAY_DPI = "display_dpi";
    private static final String EVENTS_DISPLAY_X_DPI = "display_x_dpi";
    private static final String EVENTS_DISPLAY_Y_DPI = "display_y_dpi";
    private static final String EVENTS_DISPLAY_WIDTH = "display_x_max";
    private static final String EVENTS_DISPLAY_HEIGHT = "display_y_max";

    //UUID is assigned externally because we need to have the session id before we insert the entry into the table
    private static final String SESSIONS_UUID = "uuid";
    private static final String SESSIONS_IDENTITY = "identity";
    private static final String SESSIONS_KEY = "key";
    private static final String SESSIONS_OS_NAME = "os_name";
    private static final String SESSIONS_OS_ARCH = "os_arch";
    private static final String SESSIONS_OS_VERSION = "os_version";
    private static final String SESSIONS_START_TIME = "start_time";
    private static final String SESSIONS_LOCALE = "locale";

    private static final String SESSION_TAGS_SESSION_UUID = "session_uuid";
    private static final String SESSION_TAGS_TAG = "tag";

    private static final String SQL_CREATE_EVENTS = "CREATE TABLE " + TABLE_EVENTS + " (" +
            EVENTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            EVENTS_SESSION_UUID + " TEXT " +
            "REFERENCES " + TABLE_SESSIONS + " (" + SESSIONS_UUID + ") ON DELETE CASCADE, " +
            EVENTS_ACTION + " TEXT, " +
            EVENTS_ENTITY + " INTEGER, " +
            EVENTS_KEYBOARD + " TEXT, " +
            EVENTS_ORIENTATION + " TEXT, " +
            EVENTS_X_ACCEL + " REAL, " +
            EVENTS_Y_ACCEL + " REAL, " +
            EVENTS_Z_ACCEL + " REAL, " +
            EVENTS_X_ROTATION + " REAL, " +
            EVENTS_Y_ROTATION + " REAL, " +
            EVENTS_Z_ROTATION + " REAL, " +
            EVENTS_MOTION_TIME + "" +
            "" +
            EVENTS_MOTION_X + " REAL, " +
            EVENTS_MOTION_Y + " REAL, " +
            EVENTS_MOTION_PRESSURE + " REAL, " +
            EVENTS_MOTION_TOUCH_MAJOR + " REAL, " +
            EVENTS_MOTION_TOUCH_MINOR + " REAL, " +
            EVENTS_MOTION_TOOL_MAJOR + " REAL, " +
            EVENTS_MOTION_TOOL_MINOR + " REAL, " +
            EVENTS_DISPLAY_DPI + " INTEGER, " +
            EVENTS_DISPLAY_X_DPI + " REAL, " +
            EVENTS_DISPLAY_Y_DPI + " REAL, " +
            EVENTS_DISPLAY_WIDTH + " INTEGER, " +
            EVENTS_DISPLAY_HEIGHT + " INTEGER);";

    private static final String[] EVENTS_VALUE_FIELDS = {
            EVENTS_ACTION,
            EVENTS_ENTITY,
            EVENTS_KEYBOARD,
            EVENTS_ORIENTATION,
            EVENTS_X_ACCEL,
            EVENTS_Y_ACCEL,
            EVENTS_Z_ACCEL,
            EVENTS_X_ROTATION,
            EVENTS_Y_ROTATION,
            EVENTS_Z_ROTATION,
            EVENTS_MOTION_TIME,
            EVENTS_MOTION_X,
            EVENTS_MOTION_Y,
            EVENTS_MOTION_PRESSURE,
            EVENTS_MOTION_TOUCH_MAJOR,
            EVENTS_MOTION_TOUCH_MINOR,
            EVENTS_MOTION_TOOL_MAJOR,
            EVENTS_MOTION_TOOL_MINOR,
            EVENTS_DISPLAY_DPI,
            EVENTS_DISPLAY_X_DPI,
            EVENTS_DISPLAY_Y_DPI,
            EVENTS_DISPLAY_WIDTH,
            EVENTS_DISPLAY_HEIGHT
    };

    private static final String SQL_CREATE_SESSIONS = "CREATE TABLE " + TABLE_SESSIONS + " (" +
            SESSIONS_UUID + " TEXT PRIMARY KEY, " +
            SESSIONS_IDENTITY + " TEXT, " +
            SESSIONS_KEY + " TEXT, " +
            SESSIONS_OS_NAME + " TEXT, " +
            SESSIONS_OS_ARCH + " TEXT, " +
            SESSIONS_OS_VERSION + " TEXT, " +
            SESSIONS_START_TIME + " INTEGER, " +
            SESSIONS_LOCALE + " TEXT);";

    private static final String SQL_CREATE_SESSION_TAGS = "CREATE TABLE " + TABLE_SESSION_TAGS + " (" +
            SESSION_TAGS_SESSION_UUID + " TEXT " +
            "REFERENCES " + TABLE_SESSIONS + " (" + SESSIONS_UUID + ") ON DELETE CASCADE, " +
            SESSION_TAGS_TAG + " TEXT, " +
            "PRIMARY KEY (" + SESSION_TAGS_SESSION_UUID + ", " + SESSION_TAGS_TAG + "));";

    private static final String TAG = "BioKeyboard/Database";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SESSIONS);
        db.execSQL(SQL_CREATE_SESSION_TAGS);
        db.execSQL(SQL_CREATE_EVENTS);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {
    }

    public long getEventsCount() {
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_EVENTS);
    }

    public List<BioEvent> getAllEvents() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, EVENTS_VALUE_FIELDS, null, null, null, null, EVENTS_ID + " ASC");

        try {
            ContentValues values = new ContentValues();
            return readEventsFromCursor(cursor, values);
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public Map<SessionData, List<BioEvent>> getAllSessions() {
        final Map<SessionData, List<BioEvent>> sessionMap = new LinkedHashMap<SessionData, List<BioEvent>>();
        processSessions(new SessionProcessor() {
            @Override
            public void processSession(SessionData session, List<BioEvent> events) {
                sessionMap.put(session, events);
            }
        });
        return sessionMap;
    }

    public void processSessions(SessionProcessor processor) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor sessionsCursor = db.query(TABLE_SESSIONS, null, null, null, null, null, SESSIONS_START_TIME + " ASC");
        try {
            if (sessionsCursor == null || !sessionsCursor.moveToFirst()) return;

            ContentValues values = new ContentValues();
            ContentValues eventValues = new ContentValues();
            ContentValues tagValues = new ContentValues();
            do {
                values.clear();
                DatabaseUtils.cursorRowToContentValues(sessionsCursor, values);

                UUID uuid = UUID.fromString(values.getAsString(SESSIONS_UUID));

                Set<String> tagList = new HashSet<String>();
                Cursor tagsCursor = db.query(TABLE_SESSION_TAGS, null, SESSION_TAGS_SESSION_UUID + "=?",
                        new String[]{uuid.toString()}, null, null, null);
                if (tagsCursor != null && tagsCursor.moveToFirst()) {
                    do {
                        tagValues.clear();
                        DatabaseUtils.cursorRowToContentValues(tagsCursor, tagValues);
                        String tag = tagValues.getAsString(SESSION_TAGS_TAG);
                        tagList.add(tag);
                    } while (tagsCursor.moveToNext());
                }

                SessionData session = SessionData.fromRawValues(uuid,
                        values.getAsString(SESSIONS_IDENTITY),
                        values.getAsString(SESSIONS_KEY),
                        values.getAsString(SESSIONS_OS_NAME),
                        values.getAsString(SESSIONS_OS_ARCH),
                        values.getAsString(SESSIONS_OS_VERSION),
                        values.getAsLong(SESSIONS_START_TIME),
                        values.getAsString(SESSIONS_LOCALE),
                        tagList);

                Cursor eventsCursor = db.query(TABLE_EVENTS, null, EVENTS_SESSION_UUID + "=?",
                        new String[]{uuid.toString()}, null, null, EVENTS_MOTION_TIME + " ASC");
                List<BioEvent> eventsFromSession;
                try {
                    eventsFromSession = readEventsFromCursor(eventsCursor, eventValues);
                } finally {
                    if (eventsCursor != null) eventsCursor.close();
                }

                processor.processSession(session, eventsFromSession);
            } while (sessionsCursor.moveToNext());
        } finally {
            if (sessionsCursor != null) sessionsCursor.close();
        }
    }

    private static List<BioEvent> readEventsFromCursor(Cursor cursor, ContentValues values) {
        List<BioEvent> eventList = new ArrayList<BioEvent>();

        if (cursor == null || !cursor.moveToFirst()) return eventList;

        do {
            values.clear();
            DatabaseUtils.cursorRowToContentValues(cursor, values);

            BioEvent.Action action = BioEvent.Action.fromId(values.getAsString(EVENTS_ACTION));
            LatinKeyboard.Type keyboardType = LatinKeyboard.Type.fromId(values.getAsString(EVENTS_KEYBOARD));
            BioEvent.ScreenOrientation orientation = BioEvent.ScreenOrientation.fromId(values.getAsString(EVENTS_ORIENTATION));

            DisplayMetrics displayMetrics = new DisplayMetrics();
            displayMetrics.densityDpi = values.getAsInteger(EVENTS_DISPLAY_DPI);
            displayMetrics.density = (float) displayMetrics.densityDpi / DisplayMetrics.DENSITY_MEDIUM;
            displayMetrics.scaledDensity = displayMetrics.density;
            displayMetrics.xdpi = values.getAsFloat(EVENTS_DISPLAY_X_DPI);
            displayMetrics.ydpi = values.getAsFloat(EVENTS_DISPLAY_Y_DPI);
            displayMetrics.widthPixels = values.getAsInteger(EVENTS_DISPLAY_WIDTH);
            displayMetrics.heightPixels = values.getAsInteger(EVENTS_DISPLAY_HEIGHT);

            MotionEventData motionEventData = new MotionEventData(
                    values.getAsLong(EVENTS_MOTION_TIME),
                    values.getAsFloat(EVENTS_MOTION_X),
                    values.getAsFloat(EVENTS_MOTION_Y),
                    values.getAsFloat(EVENTS_MOTION_PRESSURE),
                    values.getAsFloat(EVENTS_MOTION_TOUCH_MAJOR),
                    values.getAsFloat(EVENTS_MOTION_TOUCH_MINOR),
                    values.getAsFloat(EVENTS_MOTION_TOOL_MAJOR),
                    values.getAsFloat(EVENTS_MOTION_TOOL_MINOR));

            BioEvent event = new BioEvent(action, values.getAsInteger(EVENTS_ENTITY), keyboardType, orientation,
                    motionEventData, displayMetrics, values.getAsFloat(EVENTS_X_ACCEL),
                    values.getAsFloat(EVENTS_Y_ACCEL), values.getAsFloat(EVENTS_Z_ACCEL),
                    values.getAsFloat(EVENTS_X_ROTATION), values.getAsFloat(EVENTS_Y_ROTATION),
                    values.getAsFloat(EVENTS_Z_ROTATION));

            eventList.add(event);
        } while (cursor.moveToNext());

        return eventList;
    }

    @Override
    public void onEventsReceived(java.nio.Buffer buffer, List<? extends BioEvent> events) {

    }

    @Override
    public void onSessionEnd(java.nio.Buffer buffer) {

    }

    @Override
    public void onEventsReceived(Buffer buffer, List<? extends BioEvent> eventList) {
        if (BuildConfig.DEBUG) Log.v(TAG, "Received " + eventList.size() + " events");

        SQLiteDatabase db = getWritableDatabase();

        SessionData sessionData = buffer.getSessionData();
        String uuid = sessionData.getUuid().toString();
        Cursor sessions = db.query(TABLE_SESSIONS, new String[]{SESSIONS_UUID}, SESSIONS_UUID + "=?",
                new String[]{uuid}, null, null, null);

        db.beginTransaction();
        try {
            if (sessions == null || !sessions.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put(SESSIONS_UUID, uuid);
                values.put(SESSIONS_IDENTITY, sessionData.getIdentity());
                values.put(SESSIONS_KEY, sessionData.getKey());
                values.put(SESSIONS_OS_NAME, sessionData.getOsName());
                values.put(SESSIONS_OS_ARCH, sessionData.getOsArch());
                values.put(SESSIONS_OS_VERSION, sessionData.getOsVersion());
                values.put(SESSIONS_START_TIME, sessionData.getStartTime());
                values.put(SESSIONS_LOCALE, sessionData.getLocale());
                db.insert(TABLE_SESSIONS, null, values);

                Set<String> tagList = sessionData.getTags();
                for (String tag : tagList) {
                    values.clear();
                    values.put(SESSION_TAGS_SESSION_UUID, uuid);
                    values.put(SESSION_TAGS_TAG, tag);
                    db.insert(TABLE_SESSION_TAGS, null, values);
                }
            }

            for (BioEvent event : eventList) {
                ContentValues values = new ContentValues();
                values.put(EVENTS_SESSION_UUID, uuid);

                values.put(EVENTS_ACTION, event.getAction().id);
                values.put(EVENTS_ENTITY, event.getEntityId());
                values.put(EVENTS_KEYBOARD, event.getKeyboardType().id);
                values.put(EVENTS_ORIENTATION, event.getScreenOrientation().id);

                MotionEventData motionEvent = event.getMotionEvent();
                values.put(EVENTS_MOTION_TIME, motionEvent.getTime());
                values.put(EVENTS_MOTION_X, motionEvent.getX());
                values.put(EVENTS_MOTION_Y, motionEvent.getY());
                values.put(EVENTS_MOTION_PRESSURE, motionEvent.getPressure());
                values.put(EVENTS_MOTION_TOUCH_MAJOR, motionEvent.getTouchMajor());
                values.put(EVENTS_MOTION_TOUCH_MINOR, motionEvent.getTouchMinor());
                values.put(EVENTS_MOTION_TOOL_MAJOR, motionEvent.getToolMajor());
                values.put(EVENTS_MOTION_TOOL_MINOR, motionEvent.getToolMinor());

                DisplayMetrics displayMetrics = event.getDisplayMetrics();
                values.put(EVENTS_DISPLAY_DPI, displayMetrics.densityDpi);
                values.put(EVENTS_DISPLAY_X_DPI, displayMetrics.xdpi);
                values.put(EVENTS_DISPLAY_Y_DPI, displayMetrics.ydpi);
                values.put(EVENTS_DISPLAY_WIDTH, displayMetrics.widthPixels);
                values.put(EVENTS_DISPLAY_HEIGHT, displayMetrics.heightPixels);

                values.put(EVENTS_X_ACCEL, event.getAccelX());
                values.put(EVENTS_Y_ACCEL, event.getAccelY());
                values.put(EVENTS_Z_ACCEL, event.getAccelZ());
                values.put(EVENTS_X_ROTATION, event.getRotationX());
                values.put(EVENTS_Y_ROTATION, event.getRotationY());
                values.put(EVENTS_Z_ROTATION, event.getRotationZ());
                Log.d("Guinness", event.toString());
                db.insert(TABLE_EVENTS, null, values);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onSessionEnd(Buffer buffer) {
        close();
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, null, null);
        db.delete(TABLE_SESSIONS, null, null);
        db.delete(TABLE_SESSION_TAGS, null, null);
    }

    public interface SessionProcessor {
        void processSession(SessionData session, List<BioEvent> eventsFromSession);
    }
}
