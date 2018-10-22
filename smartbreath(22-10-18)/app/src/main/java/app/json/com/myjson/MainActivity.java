package app.json.com.myjson;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kofigyan.stateprogressbar.StateProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    String[] descriptionData = {"WarmUp", "CoolDown", "Oxygen", "Sprint","EPO","HGS"};
    StateProgressBar stateProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stateProgressBar = findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);

        stateProgressBar.setMaxStateNumber(StateProgressBar.StateNumber.FIVE);

        Resources res = getResources();
        InputStream stream = res.openRawResource(R.raw.protocol);
        Scanner scandata = new Scanner(stream);
        StringBuilder builder = new StringBuilder();

        while(scandata.hasNext()){
            builder.append(scandata.nextLine());
        }
        Log.e("ParseJson", "Getting data: "+builder.toString());
        jsonparsing(builder.toString());

    }
    private void jsonparsing(String s) {
        try {
            JSONObject responce = new JSONObject(s);
            JSONObject dataProtocal = responce.getJSONObject("protocol");
            Log.e("ParseJson", "Protocal data: "+dataProtocal);
            String pName = dataProtocal.getString("name");
            String pDescription = dataProtocal.getString("description");

            System.out.println("pName : " + pName );
            System.out.println("pDescription : " + pDescription );

            JSONArray stagesArray = dataProtocal.getJSONArray("stages");
            for(int i = 0 ; i < stagesArray.length() ; i++){
                JSONObject objData = stagesArray.getJSONObject(i);
                System.out.println("=================data of protocal " +i+ " ====================");

                String stageName = objData.getString("name1");
                String stagePositive= objData.getString("positiveO2");
                String stageNegative = objData.getString("negativeO2");

                System.out.println("StageName : " + stageName );
                System.out.println("StagePositive : " + stagePositive );
                System.out.println("StageNegative : " + stageNegative );


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}




