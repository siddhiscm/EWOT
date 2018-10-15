package app.json.com.myjson;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.Locale;
import app.json.com.myjson.fragments.FragAboutUs;
import app.json.com.myjson.fragments.FragDashBoard;
import app.json.com.myjson.fragments.FragProtocalInfo;
import static android.widget.Toast.LENGTH_LONG;
public class SampleActivity extends AppCompatActivity {
    TextView tvDashboard, tvProtocal, tvAboutUs;
    RelativeLayout rStartStop;
    private static final long START_TIME_IN_MILLIS = 10000;
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtoncancel;
    private CountDownTimer mcountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    ProgressBar mProgressBar = null;

    String[] descriptionData = {"WarmUp","CoolDown","Oxygen","Sprint","EPO","HGS"};
    StateProgressBar stateProgressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonStartPause.setEnabled(false);
                tvProtocal.setEnabled(false);
                tvDashboard.setEnabled(false);
                tvAboutUs.setEnabled(false);
                stateProgressBar.setVisibility(View.VISIBLE);

                tvProtocal.setBackgroundColor(getResources().getColor(R.color.grey));
                tvAboutUs.setBackgroundColor(getResources().getColor(R.color.grey));
                mButtoncancel.setBackgroundColor(getResources().getColor(R.color.white_color));
                ViewPager viewPager=(ViewPager)findViewById(R.id.viewPager);
                viewPager.setVisibility(View.GONE);

                //TextView textview = (TextView) findViewById(R.id.P1);
                //textview.setVisibility(View.GONE);
                mButtonStartPause.setBackgroundColor(getResources().getColor(R.color.grey));
                mButtonStartPause.setTextColor(getResources().getColor(R.color.app_black));
                mProgressBar.setVisibility(View.VISIBLE);
                mTextViewCountDown.setVisibility(View.VISIBLE);
                // reset the value to 100000 if the previous state is cancel, else dont reset
               // if() {
                    //mTimeLeftInMillis = START_TIME_IN_MILLIS;
               // }
                setProgressBarValues();
                //   if (mTimerRunning){
                // pauseTimer();
                startTimer(START_TIME_IN_MILLIS);

                //  }
                //else {
                //    startTimer();

//                }
            }
        });

        /*mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();

            }
        }); */

        updateCountDownText(START_TIME_IN_MILLIS);

        tvDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDashboard.setTextColor(getResources().getColor(R.color.white_color));
                tvDashboard.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tvProtocal.setTextColor(getResources().getColor(R.color.app_black));
                tvProtocal.setBackgroundColor(getResources().getColor(R.color.white_color));
                tvAboutUs.setTextColor(getResources().getColor(R.color.app_black));
                tvAboutUs.setBackgroundColor(getResources().getColor(R.color.white_color));
                rStartStop.setVisibility(View.VISIBLE);
                //mProgressBar.setVisibility(View.VISIBLE);
                //mTextViewCountDown.setVisibility(View.VISIBLE);
                mButtonStartPause.setVisibility(View.VISIBLE);
                mButtoncancel.setVisibility(View.VISIBLE);
                mButtoncancel.setEnabled(false);
                mButtoncancel.setBackgroundColor(getResources().getColor(R.color.grey));
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
                rStartStop.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mTextViewCountDown.setVisibility(View.GONE);
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
                rStartStop.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mTextViewCountDown.setVisibility(View.GONE);
                replacefragment(new FragAboutUs());
            }
        });
    }
    private void startTimer(final long startTimeInMillis) {
        mcountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText(startTimeInMillis);
                //  mTextViewCountDown.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                mTimerRunning = true;
                mTimeLeftInMillis = startTimeInMillis;
                //mButtonStartPause.setText("Start");
                mTextViewCountDown.setVisibility(View.INVISIBLE);
                mButtonStartPause.setVisibility(View.VISIBLE);
                // mButtonReset.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mButtonStartPause.setEnabled(true);
                switch (stateProgressBar.getCurrentStateNumber()) {
                    case 1:
                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                        startTimer(20000);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mButtonStartPause.setEnabled(false);
                        mTextViewCountDown.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                        startTimer(1001011);
                        mButtonStartPause.setEnabled(false);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mTextViewCountDown.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                        startTimer(1001010);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mButtonStartPause.setEnabled(false);
                        mTextViewCountDown.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
                        startTimer(10010);
                        mButtonStartPause.setEnabled(false);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mTextViewCountDown.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.SIX);
                        startTimer(10010);
                        mButtonStartPause.setEnabled(false);
                        mProgressBar.setVisibility(View.VISIBLE);
                        mTextViewCountDown.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        stateProgressBar.setAllStatesCompleted(true);
                        break;
                }

            }
        }.start();
    }
    private void updateCountDownText(long time) {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        mProgressBar.setProgress((int) (mTimeLeftInMillis / 1000));
        mButtoncancel.setEnabled(true);

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }
    private void setProgressBarValues() {
        mProgressBar.setMax((int) START_TIME_IN_MILLIS / 1000);
        mProgressBar.setProgress((int) mTimeLeftInMillis / 1000);
    }
    private void initialize() {
        tvDashboard = findViewById(R.id.txtDashboard);
        tvProtocal = findViewById(R.id.txtProtocalInfo);
        tvAboutUs = findViewById(R.id.txtAboutUs);
        rStartStop = findViewById(R.id.relBtn);
        mProgressBar = findViewById(R.id.progressBar);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtoncancel = findViewById(R.id.cancel);
        mProgressBar.setVisibility(View.GONE);
        stateProgressBar = findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setMaxStateNumber(StateProgressBar.StateNumber.SIX);
    }
    public void replacefragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }
    public void alert(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message");
        builder.setMessage("Do you really want to End the session?");
        builder.setIcon(R.drawable.alert);
        AlertDialog.OnClickListener listener = new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mcountDownTimer.cancel();
                if (i == dialogInterface.BUTTON_POSITIVE) {
                    mTimeLeftInMillis = START_TIME_IN_MILLIS;
                    mTextViewCountDown.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mButtonStartPause.setEnabled(true);
                    mButtonStartPause.setText("Start");
                    tvDashboard.setEnabled(true);
                    ViewPager viewPager=(ViewPager)findViewById(R.id.viewPager);
                    viewPager.setVisibility(View.VISIBLE);
                    stateProgressBar.setVisibility(View.GONE);
                    tvProtocal.setEnabled(true);
                    mButtoncancel.setEnabled(false);
                    mButtoncancel.setBackgroundColor(getResources().getColor(R.color.grey
                    ));
                    tvAboutUs.setEnabled(true);
                    tvProtocal.setBackgroundColor(getResources().getColor(R.color.white_color));
                    tvAboutUs.setBackgroundColor(getResources().getColor(R.color.white_color));
                    mButtonStartPause.setBackgroundColor(getResources().getColor(R.color.white_color));
                } else if (i == dialogInterface.BUTTON_NEGATIVE) {
                    mButtonStartPause.setEnabled(true);
                    mButtonStartPause.setText("Resume");
                    mButtonStartPause.setBackgroundColor(getResources().getColor(R.color.white_color));
                    mButtonStartPause.setTextColor(getResources().getColor(R.color.app_black));
                }
            }
        };
        builder.setPositiveButton(" Ok", listener);
        builder.setNegativeButton("Pause", listener);
        builder.show();


    }
    }

