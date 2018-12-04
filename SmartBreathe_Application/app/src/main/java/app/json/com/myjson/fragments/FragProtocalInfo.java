package app.json.com.myjson.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import app.json.com.myjson.R;
import app.json.com.myjson.db.DbContract;
import app.json.com.myjson.db.DbHelper;


public class FragProtocalInfo extends Fragment {

    SQLiteDatabase db;
    private DbHelper dbHelper;


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.protocalinfofrag, container, false);

        dbHelper = new DbHelper(getContext());

        TableLayout tableLayout = (TableLayout) rootView.findViewById(R.id.tablelayout);

        TableRow rowHeader = new TableRow(getContext());
        rowHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        String[] headerText = {"Phase", "Altitude", "Activity", "Time"};
        for (String c : headerText) {
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(18);
            tv.setTextColor(getResources().getColor(R.color.white_color));
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


                    TableRow row = new TableRow(getContext());
                    row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                    String[] colText = {Phase, Altitude, Activity, Time};
                    for (String text : colText) {
                        TextView tv = new TextView(getContext());
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

        return rootView;


    }


}
