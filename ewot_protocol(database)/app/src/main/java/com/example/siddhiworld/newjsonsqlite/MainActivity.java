package com.example.siddhiworld.newjsonsqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.siddhiworld.newjsonsqlite.data.DbHelper;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    private DbHelper dbHelper;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DbHelper dbHelper= new DbHelper(this);
    }
}
