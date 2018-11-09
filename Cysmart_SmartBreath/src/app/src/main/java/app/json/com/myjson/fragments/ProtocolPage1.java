package app.json.com.myjson.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.json.com.myjson.R;

public class ProtocolPage1 extends Fragment {

    TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progressbarfrag, container, false);


        textView=view.findViewById(R.id.link1);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                textView.setMovementMethod(LinkMovementMethod.getInstance());
//                FragmentManager fm = getFragmentManager();
//               FragmentTransaction ft = fm.beginTransaction();
//               FragProtocalInfo ProtocolInfo = new FragProtocalInfo();
//               ft.replace(R.id.txtProtocalInfo, ProtocolInfo);
//               ft.commit();
//                Fragment fragment= new FragProtocalInfo();
//              FragmentTransaction transaction = getFragmentManager().beginTransaction();
//              transaction.replace(R.id.txtProtocalInfo, fragment);
//             transaction.addToBackStack(null);  // this will manage backstack
//            transaction.commit();


//                Fragment fragment= new FragProtocalInfo();
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.txtProtocalInfo, fragment);
//                fragmentTransaction.commit();
            }
        });

        return view;
    }
}