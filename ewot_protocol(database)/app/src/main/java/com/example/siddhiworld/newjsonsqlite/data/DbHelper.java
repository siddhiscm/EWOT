
        package com.example.siddhiworld.newjsonsqlite.data;

        import android.content.ContentValues;
        import android.content.Context;
        import android.content.res.Resources;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import com.example.siddhiworld.newjsonsqlite.R;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.util.ArrayList;

        import static com.example.siddhiworld.newjsonsqlite.data.DbContract.EwotEntry.COLUMN_NAME;
        import static com.example.siddhiworld.newjsonsqlite.data.DbContract.EwotEntry.COLUMN_NAME1;

        public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();
    private Resources mResources;
    private static final String DATABASE_NAME = "EWOT.db";
    private static final int DATABASE_VERSION = 1;
    Context context;
    SQLiteDatabase db;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mResources=context.getResources();
        db=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + DbContract.EwotEntry.TABLE_NAME + " (" +
                DbContract.EwotEntry._ID + " TEXT PRIMARY KEY, " +
                DbContract.EwotEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                DbContract.EwotEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                DbContract.EwotEntry.COLUMN_NUMBEROFSTAGES + " TEXT NOT NULL, " +
                DbContract.EwotEntry.COLUMN_TOTALDURATION + " TEXT NOT NULL, " +
                DbContract.EwotEntry.COLUMN_suitablePersonality + " TEXT NOT NULL);");
        db.execSQL(" CREATE TABLE " + DbContract.EwotEntry.TABLE_NAME1 + " (" +
                DbContract.EwotEntry._ID + " TEXT PRIMARY KEY, " +
                DbContract.EwotEntry.COLUMN_NAME1 + " TEXT NOT NULL, " +
                DbContract.EwotEntry.COLUMN_POSITIVEO2 + " TEXT NOT NULL, " +
                DbContract.EwotEntry.COLUMN_NEGATIVEO2 + " TEXT NOT NULL, " +
                DbContract.EwotEntry.COLUMN_ALTITUDE + " TEXT NOT NULL, " +
                DbContract.EwotEntry.COLUMN_TOTALDURATION1 + " TEXT NOT NULL, " +
                DbContract.EwotEntry.COLUMN_METHOD + " TEXT NOT NULL, " +
                DbContract.EwotEntry.COLUMN_DESCRIPTION1 + " TEXT NOT NULL);");
        Log.e(TAG,"Database Created Successfully");
        try {
            readDatatoDB(db);
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }

    }


    private void readDatatoDB(SQLiteDatabase db) throws IOException, JSONException{


        final String PROTOCOL_NAME = "name";
        final String PROTOCOL_DESCRIPTION = "description";
        final String PROTOCOL_NUBEROFSTAGES = "numberofStages";
        final String PROTOCOL_TOTALDURATION = "totalDuration";
        final String PROTOCOL_SuitablePersonality = "suitablePersonality";
        final String STAGES_NAME = "name1";
        final String STAGES_POSITIVEO2 ="positiveO2";
        final String STAGES_NEGATIVE02="negativeO2";
        final String STAGES_ALTITUDE="altitude";
        final String STAGES_METHOD="method";
        final String STAGES_DESCRIPTION="description";





        try {
            // String jsonDataString = readjsonDataFromFile();
            String jsontext=readjsonDataFromFile(context,R.raw.protocol);
            JSONObject jsonObj = new JSONObject(jsontext);
            JSONArray protocolJsonArray = jsonObj.getJSONArray("protocol");
            Log.e("protocolarray",""+protocolJsonArray);
            for (int i = 0; i < protocolJsonArray.length(); i++) {

                String name;
                String description;
                String numberofstages;
                String totalduration;
                String suitablePersonality;

                JSONObject protocolObject = protocolJsonArray.getJSONObject(i);

                name = protocolObject.getString(PROTOCOL_NAME);
                description = protocolObject.getString(PROTOCOL_DESCRIPTION);
                numberofstages = protocolObject.getString(PROTOCOL_NUBEROFSTAGES);
                totalduration = protocolObject.getString(PROTOCOL_TOTALDURATION);
                suitablePersonality = protocolObject.getString(PROTOCOL_SuitablePersonality);
                //Log.e("name", "" + name);
               // Log.e("description", "" + description);
               // Log.e("numberofstages", "" + numberofstages);
                //Log.e("totalduration", "" + totalduration);
                //Log.e("suitablePersonality", "" + suitablePersonality);

                ContentValues protocolValues = new ContentValues();
                protocolValues.put(COLUMN_NAME, name);
               // protocolValues.put(DbContract.EwotEntry.COLUMN_DESCRIPTION, description);
               // protocolValues.put(DbContract.EwotEntry.COLUMN_NUMBEROFSTAGES, numberofstages);
               // protocolValues.put(DbContract.EwotEntry.COLUMN_TOTALDURATION, totalduration);
                //protocolValues.put(DbContract.EwotEntry.COLUMN_suitablePersonality, suitablePersonality);

                db.insert(DbContract.EwotEntry.TABLE_NAME, null, protocolValues);

                JSONArray stagesArray = protocolObject.getJSONArray("stages");
                int len = stagesArray.length();

                ArrayList<String> Satges_names = new ArrayList<>();
                for (int j = 0; j < len; j++) {
                    String name1;
                    //String description1;
                    JSONObject stagesArrayJSONObject = stagesArray.getJSONObject(j);
                     name1 = stagesArrayJSONObject.getString(STAGES_NAME);
                    ContentValues stagesValues = new ContentValues();
                    stagesValues.put(COLUMN_NAME1, name1);
                    db.insert(DbContract.EwotEntry.TABLE_NAME1, null, stagesValues);


                }


                Log.e(TAG, "Inserted Sucessfully" + protocolValues );
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private String readjsonDataFromFile(Context context, int protocol) throws IOException, JSONException{
        InputStream inputStream=null;
        StringBuilder builder = new StringBuilder();
        try {
            String jsonDataString = null;
            inputStream = mResources.openRawResource(R.raw.protocol);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            while ((jsonDataString=bufferedReader.readLine())!=null){
                builder.append(jsonDataString);
            }

        }finally {

            if(inputStream!=null){
                inputStream.close();
            }

        }
        return new String(builder);



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
