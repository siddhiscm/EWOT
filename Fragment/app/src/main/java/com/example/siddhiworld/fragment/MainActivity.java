package com.example.siddhiworld.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    FragmentManager fmanager;
    FragmentTransaction tx;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fmanager=getFragmentManager();
        tx=fmanager.beginTransaction();
        tx.add(R.id.frame1,new HomeFragment());
        tx.commit();
    }

    public void home(View v) {
        tx=fmanager.beginTransaction();
        tx.replace(R.id.frame1,new HomeFragment());
        tx.commit();


    }

    public void Gallery(View v) {
        tx=fmanager.beginTransaction();
        tx.replace(R.id.frame1,new GalleryFragment());
        tx.commit();
    }

    public void ContactUs(View v) {
        tx=fmanager.beginTransaction();
        tx.replace(R.id.frame1,new ContactusFragment());
        tx.commit();
    }

}

