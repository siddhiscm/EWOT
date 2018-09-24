package com.example.siddhiworld.jsonparsestages;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadJSONExample {
    public static Protocol readProtocolJSONFile(Context context) throws IOException,JSONException {
        String jsonText =readText(context,R.raw.protocol);//read the data from Json file
        JSONObject jsonRoot = new JSONObject(jsonText);

        //get the  data from Json file
        String name = jsonRoot.getString("name");
        String description = jsonRoot.getString("description");
        String totalDuration = jsonRoot.getString("totalDuration");
        String numberofstages = jsonRoot.getString("numberofStages");
        String suitablePersonality = jsonRoot.getString("suitablePersonality");

        //Json obeject of stages
        JSONObject jsonStages = jsonRoot.getJSONObject("stages");
        String name1=jsonStages.getString("name1");
        String positiveO2=jsonStages.getString("positiveO2");
        String negativeO2=jsonStages.getString("negativeO2");
        String altitude=jsonStages.getString("altitude");
        String method=jsonStages.getString("method");
        String description1=jsonStages.getString("description1");

        //create the protocol and stages object and set the values
        Stages stages=new Stages(name1,positiveO2,negativeO2,altitude,method,description1);
        Protocol protocol=new Protocol();
        protocol.setName(name);
        protocol.setDescription(description);
        protocol.setTotalDuration(totalDuration);
        protocol.setNumberofStages(numberofstages);
        protocol.setSuitablePersonality(suitablePersonality);
        protocol.setStages(stages);
        return protocol;
    }

    private static String readText(Context context, int protocol) throws IOException{
        InputStream is = context.getResources().openRawResource(protocol);
        BufferedReader br= new BufferedReader(new InputStreamReader(is));
        StringBuilder sb= new StringBuilder();
        String s= null;
        while((  s = br.readLine())!=null) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }
}

