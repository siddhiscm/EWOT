package app.json.com.myjson;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import app.json.com.myjson.db.DbContract;
import app.json.com.myjson.db.DbHelper;

public class SmartBreatheApplication extends AppCompatActivity {


    SQLiteDatabase db;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.protocalinfofrag);
        dbHelper = new DbHelper(this);

        TableLayout tableLayout = (TableLayout) findViewById(R.id.tablelayout);

        TableRow rowHeader = new TableRow(this);
        rowHeader.setBackgroundColor(Color.parseColor("#c0c0c0"));
        rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        String[] headerText = {"Phase", "Altitude", "Activity", "Time"};
        for (String c : headerText) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(18);
            tv.setPadding(5, 5, 5, 5);
            tv.setText(c);
            rowHeader.addView(tv);
        }
        tableLayout.addView(rowHeader);

        // Get data from sqlite database and add them to the table
        // Open the database for reading
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Start the transaction.
        db.beginTransaction();


        try {
            String selectQuery = "SELECT * FROM " + DbContract.ProtocolEntry.TABLE_NAME;
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // Read columns data
                    // int outlet_id= cursor.getInt(cursor.getColumnIndex("outlet_id"));
                    String Phase = cursor.getString(cursor.getColumnIndex("Phase"));
                    String Altitude = cursor.getString(cursor.getColumnIndex("Altitude"));
                    String Activity = cursor.getString(cursor.getColumnIndex("Activity"));
                    String Time = cursor.getString(cursor.getColumnIndex("Time"));


                    TableRow row = new TableRow(this);
                    row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                    String[] colText = {Phase, Altitude, Activity, Time};
                    for (String text : colText) {
                        TextView tv = new TextView(this);
                        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));

                        tv.setGravity(Gravity.CENTER);
                        tv.setTextSize(16);
                        tv.setPadding(5, 50, 5, 50);
                        tv.setText(text);
                        //  tv.setPaddingRelative(50,50,50,50);
                        tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                        row.addView(tv);
                    }
                    tableLayout.addView(row);
                }

            }
            db.setTransactionSuccessful();

        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
    }
}
