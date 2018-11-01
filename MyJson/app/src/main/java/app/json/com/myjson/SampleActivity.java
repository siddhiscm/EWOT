package app.json.com.myjson;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import app.json.com.myjson.fragments.FragAboutUs;
import app.json.com.myjson.fragments.FragDashBoard;
import app.json.com.myjson.fragments.FragProtocalInfo;



public class SampleActivity extends AppCompatActivity {

    TextView tvDashboard,tvProtocal,tvAboutUs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();

        tvDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDashboard.setTextColor(getResources().getColor(R.color.white_color));
                tvDashboard.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tvProtocal.setTextColor(getResources().getColor(R.color.app_black));
                tvProtocal.setBackgroundColor(getResources().getColor(R.color.white_color));
                tvAboutUs.setTextColor(getResources().getColor(R.color.app_black));
                tvAboutUs.setBackgroundColor(getResources().getColor(R.color.white_color));

                replacefragment(new FragDashBoard());
            }
        });

        tvProtocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDashboard.setTextColor(getResources().getColor(R.color.app_black));
                tvDashboard.setBackgroundColor(getResources().getColor(R.color.white_color));
                tvProtocal.setTextColor(getResources().getColor(R.color.white_color));
                tvProtocal.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tvAboutUs.setTextColor(getResources().getColor(R.color.app_black));
                tvAboutUs.setBackgroundColor(getResources().getColor(R.color.white_color));

                replacefragment(new FragProtocalInfo());
            }
        });
        tvAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvDashboard.setTextColor(getResources().getColor(R.color.app_black));
                tvDashboard.setBackgroundColor(getResources().getColor(R.color.white_color));
                tvProtocal.setTextColor(getResources().getColor(R.color.app_black));
                tvProtocal.setBackgroundColor(getResources().getColor(R.color.white_color));
                tvAboutUs.setTextColor(getResources().getColor(R.color.white_color));
                tvAboutUs.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                replacefragment(new FragAboutUs());
            }
        });
    }

    private void initialize() {

        tvDashboard = findViewById(R.id.txtDashboard);
        tvProtocal = findViewById(R.id.txtProtocalInfo);
        tvAboutUs = findViewById(R.id.txtAboutUs);

    }

    public void replacefragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();

    }
}
