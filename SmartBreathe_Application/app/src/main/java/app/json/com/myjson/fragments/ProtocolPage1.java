package app.json.com.myjson.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import app.json.com.myjson.R;

public class ProtocolPage1 extends Fragment {

    Button seeMore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progressbarfrag, container, false);


        seeMore = view.findViewById(R.id.see_more);


        seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("EVENT", "EVENT");

                Toast.makeText(getContext(), "EVENT", Toast.LENGTH_SHORT).show();

                FragProtocalInfo someFragment = new FragProtocalInfo();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, someFragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });


        return view;
    }
}