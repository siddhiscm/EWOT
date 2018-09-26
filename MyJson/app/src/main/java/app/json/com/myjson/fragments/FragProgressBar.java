package app.json.com.myjson.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import app.json.com.myjson.R;



public class FragProgressBar extends Fragment {

    ProgressBar mProgressBar = null;
    Button btStart,btStop;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progressbarfrag, container, false);

        mProgressBar = view.findViewById(R.id.progressBar);
        btStart = view.findViewById(R.id.btnStart);
        btStop = view.findViewById(R.id.btnStop);
        mProgressBar.setVisibility(View.GONE);


        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.GONE);
            }
        });

        return view;
    }
}
