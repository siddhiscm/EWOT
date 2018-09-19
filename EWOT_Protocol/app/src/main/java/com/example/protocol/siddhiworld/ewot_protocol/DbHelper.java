package com.example.protocol.siddhiworld.ewot_protocol;

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

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG=DbHelper.class.getSimpleName();

    private Resources mResources;
    private static final String DATABASE_NAME="EWOT12";
    private static final int DATABASE_VERSION=1;
    public static final String TABLE_NAME="protocol12_table";
    Context context;


    DbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        mResources = context.getResources();
        SQLiteDatabase db;
        db = this.getWritableDatabase();
    }


    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_BUGS_TABLE="CREATE TABLE " + Dbcontract.MenuEntry.TABLE_NAME + "(" +
                Dbcontract.MenuEntry._ID + "INTEGER PRIMARY KEY AUTOINCREMENT,"+
                Dbcontract.MenuEntry.COL_1 + "INTEGER UNIQUE NOT NULL,"+
                Dbcontract.MenuEntry.COL_2 +"TEXT NOT NULL "+");";

        db.execSQL(SQL_CREATE_BUGS_TABLE);
        Log.d(TAG,"Database created successfully");

        try {
            readDataToDb(db);
        }catch (IOException | JSONException e){
            e.printStackTrace();
        }

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    private void readDataToDb(SQLiteDatabase db) throws IOException , JSONException{
        final String MNU_ID="id";
        final String MNU_NAME="name";

        try {
            String jsonDatastring=readJsonDataFromFile();
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> 530f9683f3443a79f2e58255080a44f08ce4caea
            JSONArray menuItemsJsonArray = new JSONArray(jsonDatastring);

            for (int i=0;i<menuItemsJsonArray.length();++i){
                String id;
                String name;

                JSONObject menuItemobject = menuItemsJsonArray.getJSONObject(i);
<<<<<<< HEAD
=======
=======
            JSONObject jsonObject =new JSONObject();
            JSONArray protocols = jsonObject.getJSONArray("protocols");

            for (int i=0;i<protocols.length();i++){
                String id;
                String name;

                JSONObject menuItemobject = protocols.getJSONObject(i);
>>>>>>> 4674ac6f409d28665d491131adec1a5485d80502
>>>>>>> 530f9683f3443a79f2e58255080a44f08ce4caea

                id = menuItemobject.getString(MNU_ID);
                name = menuItemobject.getString(MNU_NAME);
            }
        }catch (JSONException e){
            Log.e(TAG,e.getMessage(),e);
            e.printStackTrace();
        }

    }

    private String readJsonDataFromFile()throws IOException {
        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();
        try {
            String jsonDataString= null;
<<<<<<< HEAD
            inputStream = mResources.openRawResource(R.raw.protocol_info);
=======
<<<<<<< HEAD
            inputStream = mResources.openRawResource(R.raw.protocol_info);
=======
            inputStream = mResources.openRawResource(R.raw.protocols);
>>>>>>> 4674ac6f409d28665d491131adec1a5485d80502
>>>>>>> 530f9683f3443a79f2e58255080a44f08ce4caea
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream,"UTF-8"));
            while ((jsonDataString = bufferedReader.readLine()) !=null){
                builder.append(jsonDataString);
            }
        }finally {
            if (inputStream!=null){
                inputStream.close();
            }
        }
        return new String(builder);
    }

}
