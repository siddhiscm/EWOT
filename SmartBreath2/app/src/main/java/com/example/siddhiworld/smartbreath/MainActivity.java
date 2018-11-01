package com.example.siddhiworld.smartbreath;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    public void changeFragment(View view) {

        Fragment fragment;
        if (view == findViewById(R.id.btn_1)) {
            fragment = new Dashboard();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_palce, fragment);
            transaction.commit();

        }
        if (view == findViewById(R.id.btn_2)) {
            fragment = new Protocol();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_palce, fragment);
            transaction.commit();

        }
        if (view == findViewById(R.id.btn_3)) {
            fragment = new AboutUs();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_palce, fragment);
            transaction.commit();

        }
    }
}