package app.json.com.myjson;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Image;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.rd.PageIndicatorView;
//import com.rd.PageIndicatorView;

import java.util.Locale;
import app.json.com.myjson.fragments.FragAboutUs;
import app.json.com.myjson.fragments.FragDashBoard;
import app.json.com.myjson.fragments.FragProtocalInfo;
import static android.widget.Toast.LENGTH_LONG;
public class SampleActivity extends AppCompatActivity {
    TextView tvDashboard, tvProtocal, tvAboutUs;
    RelativeLayout rStartStop;

    private static final long START_TIME_IN_MILLIS =10100;//10 sec
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtoncancel;
    private CountDownTimer mcountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private  long TIME=mTimeLeftInMillis;
    //  private long mTimeLeftInMillis1=mTimeLeftInMillis;
    ProgressBar mProgressBar = null;
    private int mMaxStateNumber=5;
    String[] descriptionData = {"Prep\r\nTime", "Warm\r\nUp","EPO","Oxygen\r\nWash","Cool\r\nDown"};
    StateProgressBar stateProgressBar;
    PageIndicatorView pageIndicatorView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main_sb);
        initialize();
        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonStartPause.setEnabled(false);
                tvProtocal.setEnabled(false);
                tvDashboard.setEnabled(false);
                tvAboutUs.setEnabled(false);
                stateProgressBar.setVisibility(View.VISIBLE);
                tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_disable));
                tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_disable));
                mButtoncancel.setBackgroundColor(getResources().getColor(R.color.button_enable));
                ViewPager viewPager=(ViewPager)findViewById(R.id.viewPager);
                viewPager.setVisibility(View.GONE);
                mButtonStartPause.setBackgroundColor(getResources().getColor(R.color.button_disable));
                mButtonStartPause.setTextColor(getResources().getColor(R.color.background_color));
                mProgressBar.setVisibility(View.VISIBLE);
                mTextViewCountDown.setVisibility(View.VISIBLE);
                setProgressBarValues();
                startTimer(mTimeLeftInMillis);
                //pageIndicatorView.setVisibility(View.INVISIBLE);
                pageIndicatorView=(PageIndicatorView)findViewById(R.id.pageIndicatorView);
                pageIndicatorView.setVisibility(View.GONE);







            }
        });




        updateCountDownText(mTimeLeftInMillis);


        tvDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDashboard.setTextColor(getResources().getColor(R.color.background_color));
                tvDashboard.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tvProtocal.setTextColor(getResources().getColor(R.color.background_color));
                tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_disable));
                tvAboutUs.setTextColor(getResources().getColor(R.color.background_color));
                tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_disable));
                rStartStop.setVisibility(View.VISIBLE);
                mButtonStartPause.setVisibility(View.VISIBLE);
                mButtoncancel.setVisibility(View.VISIBLE);
                mButtoncancel.setEnabled(false);
                //stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                mButtonStartPause.setBackgroundColor(getResources().getColor(R.color.button_enable));
                stateProgressBar.setVisibility(View.GONE);
                mButtoncancel.setBackgroundColor(getResources().getColor(R.color.button_disable));
                replacefragment(new FragDashBoard());
                ImageView imageView=(ImageView)findViewById(R.id.smartbreath);
                imageView.setVisibility(View.GONE);


                //stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);

            }
        });

        tvProtocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDashboard.setTextColor(getResources().getColor(R.color.background_color));
                tvDashboard.setBackgroundColor(getResources().getColor(R.color.button_disable));
                tvProtocal.setTextColor(getResources().getColor(R.color.background_color));
                tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_enable));
                tvAboutUs.setTextColor(getResources().getColor(R.color.background_color));
                tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_disable));
                rStartStop.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mTextViewCountDown.setVisibility(View.GONE);
                stateProgressBar.setVisibility(View.GONE);
                replacefragment(new FragProtocalInfo());
                ImageView imageView=(ImageView)findViewById(R.id.smartbreath);
                imageView.setVisibility(View.GONE);

            }
        });
        tvAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDashboard.setTextColor(getResources().getColor(R.color.background_color));
                tvDashboard.setBackgroundColor(getResources().getColor(R.color.button_disable));
                tvProtocal.setTextColor(getResources().getColor(R.color.background_color));
                tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_disable));
                tvAboutUs.setTextColor(getResources().getColor(R.color.white_color));
                tvAboutUs.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                rStartStop.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mTextViewCountDown.setVisibility(View.GONE);
                stateProgressBar.setVisibility(View.GONE);
                replacefragment(new FragAboutUs());
                ImageView imageView=(ImageView)findViewById(R.id.smartbreath);
                imageView.setVisibility(View.GONE);

            }
        });
    }

    private void startTimer(final long startTimeInMillis) {
        mcountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText(startTimeInMillis);
            }
            @Override
            public void onFinish() {
                mTimerRunning = true;
                mTimeLeftInMillis = startTimeInMillis;
                mTextViewCountDown.setVisibility(View.INVISIBLE);
                mButtonStartPause.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mButtonStartPause.setEnabled(true);
                stagesTime();

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
        mTextViewCountDown.setTextColor(getResources().getColor(R.color.text_color));
    }
    private void setProgressBarValues() {
        mProgressBar.setMax((int) TIME/ 1000);
        mProgressBar.setProgress((int) this.mTimeLeftInMillis / 1000);
    }

    private void stages(){
        startTimer(mTimeLeftInMillis);
        TIME=mTimeLeftInMillis;
        mProgressBar.setMax((int) this.mTimeLeftInMillis / 1000);
        mProgressBar.setProgress((int) this.mTimeLeftInMillis / 1000);
        mProgressBar.setVisibility(View.VISIBLE);
        mButtonStartPause.setEnabled(false);
        mTextViewCountDown.setVisibility(View.VISIBLE);
    }
    @Override
    public void onBackPressed() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        mcountDownTimer.cancel();

                        finish();
                        //close();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();

    }


    @SuppressLint("WrongConstant")
    public void stagesTime(){
        switch (stateProgressBar.getCurrentStateNumber()) {
            case 1:
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                mTimeLeftInMillis=30100;//30 sec
                stages();
                tone();

                break;
            case 2:
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                mTimeLeftInMillis=30100;//30 sec
                stages();
                tone();
                break;
            case 3:
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                mTimeLeftInMillis=30100;//30 sec
                stages();
                tone();
                break;
            case 4:
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
                mTimeLeftInMillis=30100;//30 sec
                stages();
                tone();
                break;

            case 5:
                stateProgressBar.setAllStatesCompleted(true);
                stateProgressBar.setVisibility(View.GONE);
                mButtonStartPause.setVisibility(View.GONE);
                mButtoncancel.setVisibility(View.GONE);
                tvDashboard.setEnabled(true);
                resetState();
                vibrator();
                alert_finish();
                tone();
                tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_enable));
                tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_enable));
                tvAboutUs.setEnabled(true);
                tvProtocal.setEnabled(true);
                tvProtocal.setEnabled(true);
                break;
        }
    }
    public void alert_finish(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
        builder1.setMessage("Congratulations! You have successfully finished your training session.");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    public void alert_Terminated(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
        builder1.setMessage("Your Session is Terminated");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }






    public void vibrator() {
        try {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        } catch (Exception e) {
            Log.d("hgj", "vibrator exception: " + e);
        }

    }
    public void tone(){
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,200);

    }

    public void resetState(){

        if(stateProgressBar.getCurrentStateNumber()>=mMaxStateNumber){
            stateProgressBar.setAllStatesCompleted(Boolean.FALSE);
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
            //mStartButtonSetup();
        }
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
        stateProgressBar.setMaxStateNumber(StateProgressBar.StateNumber.FIVE);
    }
    public void replacefragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }
    public void alert(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to End the session?");
        mcountDownTimer.cancel();
        AlertDialog.OnClickListener listener = new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == dialogInterface.BUTTON_POSITIVE) {
                    mTimeLeftInMillis = START_TIME_IN_MILLIS;
                    TIME=START_TIME_IN_MILLIS;
                    //resetState();
                    mTextViewCountDown.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mButtonStartPause.setEnabled(true);
                    mButtonStartPause.setText("Start");
                    tvDashboard.setEnabled(true);
                    stateProgressBar.setVisibility(View.GONE);
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                    ViewPager viewPager=(ViewPager)findViewById(R.id.viewPager);
                    viewPager.setVisibility(View.GONE);
                    tvProtocal.setEnabled(true);
                    mButtoncancel.setEnabled(false);
                    mButtoncancel.setBackgroundColor(getResources().getColor(R.color.button_disable));
                    tvAboutUs.setEnabled(true);
                    tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_enable));
                    tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_enable));
                    mButtonStartPause.setBackgroundColor(getResources().getColor(R.color.button_enable));
                    // PageIndicatorView pageIndicatorView=(PageIndicatorView)findViewById(R.id.pageIndicatorView);
                    //pageIndicatorView.setVisibility(View.GONE);
                    alert_Terminated();
                    mButtonStartPause.setVisibility(View.GONE);
                    mButtoncancel.setVisibility(View.GONE);
                    ImageView imageView=(ImageView)findViewById(R.id.smartbreath);
                    imageView.setVisibility(View.VISIBLE);
                } else if (i == dialogInterface.BUTTON_NEGATIVE) {
                    mButtonStartPause.setEnabled(true);
                    mButtonStartPause.setText("Resume");
                    mButtoncancel.setEnabled(false);
                    vibrator();
                    tone();
                    mButtoncancel.setBackgroundColor(getResources().getColor(R.color.button_disable));
                    mButtonStartPause.setBackgroundColor(getResources().getColor(R.color.button_enable));
                    mButtonStartPause.setTextColor(getResources().getColor(R.color.background_color));
                }
            }
        };
        builder.setPositiveButton(" Ok", listener);
        builder.setNegativeButton("Pause", listener);
        builder.setCancelable(false);
        builder.show();


    }
}

