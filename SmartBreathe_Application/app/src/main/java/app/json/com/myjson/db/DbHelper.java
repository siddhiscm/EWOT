
package app.json.com.myjson.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import app.json.com.myjson.R;

import static app.json.com.myjson.db.DbContract.ProtocolEntry.COLUMN_PHASE;
import static app.json.com.myjson.db.DbContract.ProtocolEntry.TABLE_NAME;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();
    private Resources mResources;
    private static final String DATABASE_NAME = "protocol.db";
    private static final int DATABASE_VERSION = 1;
    Context context;
    SQLiteDatabase db;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mResources = context.getResources();
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + DbContract.ProtocolEntry.TABLE_NAME + " (" +
                DbContract.ProtocolEntry._ID + " TEXT PRIMARY KEY, " +
                COLUMN_PHASE + " TEXT NOT NULL, " +
                DbContract.ProtocolEntry.COLUMN_ALTITUDE + " TEXT NOT NULL, " +
                DbContract.ProtocolEntry.COLUMN_ACTIVITY + " TEXT NOT NULL, " +
                DbContract.ProtocolEntry.COLUMN_TIME + " TEXT NOT NULL);");
        Log.e(TAG, "Database Created Successfully");
        try {
            readDatatoDB(db);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void readDatatoDB(SQLiteDatabase db) throws IOException, JSONException {


        final String PROTOCOL_PHASE = "Phase";
        final String PROTOCOL_ALTITUDE = "Altitude";
        final String PROTOCOL_ACTIVITY = "Activity";
        final String PROTOCOL_TIME = "Time";

        try {
            // String jsonDataString = readjsonDataFromFile();
            JSONObject jsonObj = new JSONObject(readjsonDataFromFile());
            JSONArray protocolJsonArray = jsonObj.getJSONArray("protocol");
            Log.e("protocolarray", "" + protocolJsonArray);
            for (int i = 0; i < protocolJsonArray.length(); i++) {
                String Phase;
                String Altitude;
                String Activity;
                String Time;

                JSONObject protocolObject = protocolJsonArray.getJSONObject(i);

                Phase = protocolObject.getString(PROTOCOL_PHASE);
                Altitude = protocolObject.getString(PROTOCOL_ALTITUDE);
                Activity = protocolObject.getString(PROTOCOL_ACTIVITY);
                Time = protocolObject.getString(PROTOCOL_TIME);
                Log.e("Phase", "" + Phase);
                Log.e("Altitude", "" + Altitude);
                Log.e("Activity", "" + Activity);
                Log.e("Time", "" + Time);

                ContentValues protocolValues = new ContentValues();
                protocolValues.put(COLUMN_PHASE, Phase);
                protocolValues.put(DbContract.ProtocolEntry.COLUMN_ALTITUDE, Altitude);
                protocolValues.put(DbContract.ProtocolEntry.COLUMN_ACTIVITY, Activity);
                protocolValues.put(DbContract.ProtocolEntry.COLUMN_TIME, Time);

                db.insert(DbContract.ProtocolEntry.TABLE_NAME, null, protocolValues);

                Log.e(TAG, "Inserted Sucessfully" + protocolValues);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private String readjsonDataFromFile() throws IOException, JSONException {
        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();
        try {
            String jsonDataString = null;
            inputStream = mResources.openRawResource(R.raw.protocol);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((jsonDataString = bufferedReader.readLine()) != null) {
                builder.append(jsonDataString);
            }

        } finally {

            if (inputStream != null) {
                inputStream.close();
            }

        }
        return new String(builder);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_NAME + "'");
        onCreate(db);

    }


}
