package com.example.siddhiworld.jsonparsestages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView outputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputText = (TextView) this.findViewById(R.id.editText);
    }

    public void runExample(View view)  {
        try {

            Protocol protocol = ReadJSONExample.readProtocolJSONFile(this);

            outputText.setText(protocol.toString());
        } catch(Exception e)  {
            outputText.setText(e.getMessage());
            e.printStackTrace();
        }
    }
}
