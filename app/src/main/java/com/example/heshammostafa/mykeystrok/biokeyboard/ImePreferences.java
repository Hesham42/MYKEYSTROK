package com.example.heshammostafa.mykeystrok.biokeyboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.heshammostafa.mykeystrok.BuildConfig;
import com.example.heshammostafa.mykeystrok.DataBase.Database;
import com.example.heshammostafa.mykeystrok.DataBase.SessionData;
import com.example.heshammostafa.mykeystrok.Event.BioEvent;
import com.example.heshammostafa.mykeystrok.R;
import com.example.heshammostafa.mykeystrok.inputmethodcommon.InputMethodSettingsFragment;

import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by HeshamMostafa on 7/28/2017.
 */
public class ImePreferences extends PreferenceActivity {
    private static final String TAG = "BioKeyboard/Preferences";

    @Override
    public Intent getIntent() {
        final Intent modIntent = new Intent(super.getIntent());
        modIntent.putExtra(EXTRA_SHOW_FRAGMENT, Settings.class.getName());
        modIntent.putExtra(EXTRA_NO_HEADERS, true);
        return modIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We overwrite the title of the activity, as the default one is "Voice Search".
        setTitle(R.string.settings_name);
    }

    @Override
    protected boolean isValidFragment(final String fragmentName) {
        return Settings.class.getName().equals(fragmentName);
    }

    public static class Settings extends InputMethodSettingsFragment
            implements
            Preference.OnPreferenceClickListener,
            DialogConfirmation.ConfirmationListener,
            SharedPreferences.OnSharedPreferenceChangeListener
    {
        private Context mContext;

        private volatile Database mDatabase;
        private int mDatabaseTasksCount = 0;
        private boolean mDestroyed;

        private volatile String mKeyIdentity;
        private volatile String mKeyExportOverNetwork;
        private volatile String mKeyExportToStorage;
        private volatile String mKeyClearDatabase;

        private static final int REQUEST_CLEAR_DATABASE = 1;

        private static final String DIALOG_CONFIRMATION = "ConfirmationDialog";

        private static final String[] SESSION_HEADERS = {
                "session_uuid",
                "session_time",
                "session_identity"
        };

        private static final URL SERVER_URL;
        private static final String ENCODING = "UTF-8";
        private static final String KEY_IDENTITY = "identity";
        private static final String KEY_SESSION= "session";
        private static final String KEY_PLATFORM = "platform";
        private static final String KEY_TASK = "task";
        private static final String KEY_QUANTITY = "quantity";
        private static final String KEY_EVENTS = "events";

        static {
            try {
                SERVER_URL = new URL("http://moodle.vmonaco.com/local/bioauth/enroll_mobile.ajax.php");
            } catch (MalformedURLException ex) {
                throw new AssertionError("Server URL is invalid");
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mDatabase = new Database(getActivity());

            mKeyIdentity = getString(R.string.pref_identity_key);
            mKeyExportOverNetwork = getString(R.string.pref_export_over_network_key);
            mKeyExportToStorage = getString(R.string.pref_export_to_storage_key);
            mKeyClearDatabase = getString(R.string.pref_clear_database_key);

            setSubtypeEnablerTitle(R.string.select_language);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.ime_preferences);

            Preference clearDatabasePref = findPreference(mKeyClearDatabase);
            clearDatabasePref.setOnPreferenceClickListener(this);

            findPreference(mKeyIdentity).setOnPreferenceClickListener(this);
            findPreference(mKeyExportOverNetwork).setOnPreferenceClickListener(this);
            findPreference(mKeyExportToStorage).setOnPreferenceClickListener(this);

            SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
            prefs.registerOnSharedPreferenceChangeListener(this);

            updatePreferencesState();
        }


        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mContext = getActivity().getApplicationContext();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            mDestroyed = true;
            closeDatabaseIfNeeded();
        }

        private void closeDatabaseIfNeeded() {
            if (mDestroyed && mDatabaseTasksCount <= 0) mDatabase.close();
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            if (key.equals(mKeyClearDatabase)) {
                DialogConfirmation dialog = DialogConfirmation.newInstance(this, REQUEST_CLEAR_DATABASE,
                        mContext.getString(R.string.dialog_confirmation_clear_database),
                        mContext.getString(R.string.dialog_confirmation_clear_database_ok),
                        mContext.getString(R.string.dialog_confirmation_clear_database_cancel));
                dialog.show(getFragmentManager(), DIALOG_CONFIRMATION);
            } else if (key.equals(mKeyExportToStorage)) {
                saveEventsToFile(Environment.getExternalStorageDirectory());
                Log.d("Guinness", String.valueOf(Environment.getExternalStorageDirectory()));

            } else if (key.equals(mKeyExportOverNetwork)) {
                sendData();
            }

            return true;
        }

        @Override
        public void onResponseReceived(int requestId, boolean confirmed) {
            switch (requestId) {
                case REQUEST_CLEAR_DATABASE:
                    if (confirmed) {
                        new DatabaseTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                mDatabase.clear();
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                super.onPostExecute(result);
                                updatePreferencesState();
                            }
                        }.execute();
                    }
                    break;
            }
        }

        private void updatePreferencesState() {
            new DatabaseTask<Void, Void, Long>() {
                @Override
                protected Long doInBackground(Void... params) {
                    return mDatabase.getEventsCount();
                }

                @Override
                protected void onPostExecute(Long result) {
                    super.onPostExecute(result);

                    if (result == null || result > Integer.MAX_VALUE) return;

                    int resultInt = result.intValue();

                    Preference clearDatabasePref = findPreference(mKeyClearDatabase);
                    clearDatabasePref.setSummary(mContext.getResources().getQuantityString(
                            R.plurals.pref_clear_database_summary, resultInt, resultInt));
                    clearDatabasePref.setEnabled(result > 0);

                    findPreference(mKeyExportOverNetwork).setEnabled(result > 0);
                    findPreference(mKeyExportToStorage).setEnabled(result > 0);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String identity = prefs.getString(mKeyIdentity, "");
                    findPreference(mKeyIdentity).setSummary(identity);
                }
            }.execute();
        }

        void sendData() {
            new DatabaseTask<Void, Object, Boolean>() {
                private Exception exception = null;

                @Override
                protected Boolean doInBackground(Void... params) {
                    mDatabase.processSessions(new Database.SessionProcessor() {
                        boolean headersWritten = false;

                        @Override
                        public void processSession(SessionData session, List<BioEvent> eventsFromSession) {
                            if (eventsFromSession.isEmpty()) return;

                            HttpURLConnection urlConnection = null;
                            try {
                                urlConnection = (HttpURLConnection) SERVER_URL.openConnection();
                                urlConnection.setRequestMethod("POST");
                                urlConnection.setDoOutput(true);
                                Writer outStream = null;
                                try {
                                    outStream = new OutputStreamWriter(urlConnection.getOutputStream(), ENCODING);
                                    outStream.write(KEY_IDENTITY + "=" + URLEncoder.encode(session.getIdentity(), ENCODING));
                                    outStream.write("&" + KEY_SESSION+ "=" + URLEncoder.encode(session.getUuid().toString(), ENCODING));
                                    outStream.write("&" + KEY_PLATFORM + "=" + URLEncoder.encode(
                                            session.getPlatformDescription(), ENCODING));

                                    Set<String> tags = session.getTags();
                                    StringBuilder taskBuilder = new StringBuilder();
                                    for (String tag : tags) {
                                        if (taskBuilder.length() > 0) taskBuilder.append(", ");
                                        taskBuilder.append(tag);
                                    }
                                    outStream.write("&" + KEY_TASK + "=" + URLEncoder.encode(taskBuilder.toString(),
                                            ENCODING));

                                    JSONArray json = new JSONArray();
                                    for (BioEvent bioEvent : eventsFromSession) {
                                        json.put(bioEvent.toJSON());
                                    }
                                    outStream.write("&" + KEY_QUANTITY+ "=" + URLEncoder.encode(""+eventsFromSession.size(), ENCODING));
                                    outStream.write("&" + KEY_EVENTS + "=" + URLEncoder.encode(json.toString(), ENCODING));
                                } finally {
                                    if (outStream != null) outStream.close();
                                }
                                urlConnection.getResponseCode(); //This line is required to make the actual request!
                            } catch (IOException ex) {
                                exception = ex;
                            } finally {
                                if (urlConnection != null) urlConnection.disconnect();
                            }
                        }
                    });

                    return exception == null;
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    super.onPostExecute(success);

                    int messageResId = success ?
                            R.string.toast_export_over_network_success :
                            R.string.toast_export_over_network_failure;
                    Toast.makeText(getActivity(), messageResId, Toast.LENGTH_SHORT).show();
                }
            }.execute();
        }

        private void saveEventsToFile(final File file) {
            new DatabaseTask<Void, Object, Boolean>() {
                private Exception exception = null;

                @Override
                protected Boolean doInBackground(Void... params) {
                    CSVWriter writer = null;
                    File outputFile = new File(file, "events.csv");
                    Log.d("Guinness ","Save at file data   events.csv");
                    try {
                        writer = new CSVWriter(new FileWriter(outputFile), '\t');
                        final CSVWriter finalWriter = writer;

                        mDatabase.processSessions(new Database.SessionProcessor() {
                            boolean headersWritten = false;

                            @Override
                            public void processSession(SessionData session, List<BioEvent> eventsFromSession) {
                                if (eventsFromSession.isEmpty()) return;

                                if (!headersWritten) {
                                    writeHeaders(finalWriter, session, eventsFromSession);
                                  Log.d("Guinness",session.toString()+eventsFromSession.toString());
                                    headersWritten = true;
                                }

                                for (BioEvent bioEvent : eventsFromSession) {
                                    Collection<Object> valuesList = bioEvent.toMap().values();
                                    List<String> valueStrings = new ArrayList<String>(valuesList.size());
                                    for (Object value : valuesList) {
                                        valueStrings.add(value.toString());
                                    }

                                    String[] valuesArr = valueStrings.toArray(new String[valueStrings.size() +
                                            SESSION_HEADERS.length]);
                                    valuesArr[valueStrings.size()] = session.getUuid().toString();
                                    valuesArr[valueStrings.size() + 1] = new Date(session.getStartTime()).toString();
                                    valuesArr[valueStrings.size() + 2] = session.getIdentity().trim();

                                    finalWriter.writeNext(valuesArr);
                                }
                            }
                        });
                    } catch (IOException ex) {
                        exception = ex;
                    } finally {
                        try {
                            if (writer != null) writer.close();
                        } catch (IOException ex) {
                            if (exception != null) exception = ex;
                        }
                    }
                    if (BuildConfig.DEBUG && exception != null) Log.v(TAG, "Couldn't save data in CSV", exception);

                    return exception == null;
                }

                private void writeHeaders(CSVWriter writer, SessionData session, List<BioEvent> events) {
                    Set<String> eventColNames = events.get(0).toMap().keySet();
                    String[] columnNamesArr = eventColNames.toArray(new String[eventColNames.size() + SESSION_HEADERS.length]);
                    System.arraycopy(SESSION_HEADERS, 0, columnNamesArr, eventColNames.size(), SESSION_HEADERS.length);

                    writer.writeNext(columnNamesArr);
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    super.onPostExecute(success);

                    int messageResId = success ?
                            R.string.toast_export_to_storage_success :
                            R.string.toast_export_to_storage_failure;
                    Toast.makeText(getActivity(), messageResId, Toast.LENGTH_SHORT).show();
                }
            }.execute();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            updatePreferencesState();
        }

        private abstract class DatabaseTask<P, U, R> extends AsyncTask<P, U, R> {
            @Override
            protected void onPreExecute() {
                mDatabaseTasksCount++;
            }

            @Override
            protected void onCancelled(R result) {
                checkDatabaseTaskCount();
            }

            @Override
            protected void onPostExecute(R result) {
                checkDatabaseTaskCount();
            }

            private void checkDatabaseTaskCount() {
                mDatabaseTasksCount--;
                closeDatabaseIfNeeded();
            }
        }
    }
}
