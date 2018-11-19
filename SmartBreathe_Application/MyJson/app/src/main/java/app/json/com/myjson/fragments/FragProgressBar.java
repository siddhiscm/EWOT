package app.json.com.myjson.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

import app.json.com.myjson.R;



public class FragProgressBar extends Fragment {
    private static final long START_TIME_IN_MILLIS=11000;
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mcountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis=START_TIME_IN_MILLIS;


    ProgressBar mProgressBar = null;
    Button btStart,btStop;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progressbarfrag, container, false);

        mProgressBar = view.findViewById(R.id.progressBar);
        mTextViewCountDown=view.findViewById(R.id.text_view_countdown);
        mButtonStartPause=view.findViewById(R.id.button_start_pause);
        mButtonReset=view.findViewById(R.id.button_reset);

        //btStart = view.findViewById(R.id.btnStart);
       //btStop = view.findViewById(R.id.btnStop);
        mProgressBar.setVisibility(View.GONE);
     /*   btStart.setOnClickListener(new View.OnClickListener() {
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
        });*/

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                mTextViewCountDown.setVisibility(View.VISIBLE);

                setProgressBarValues();
                if (mTimerRunning){
                   // pauseTimer();
                    startTimer();

                }else {
                    startTimer();

               }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
                mcountDownTimer.cancel();
            }
        });

        updateCountDownText();


        return view;
    }

    private void startTimer(){
        mcountDownTimer=new CountDownTimer(mTimeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
                mTextViewCountDown.setVisibility(View.VISIBLE);

            }
            @Override
            public void onFinish() {

                mTimerRunning=false;
                mTimeLeftInMillis=START_TIME_IN_MILLIS;
                //mButtonStartPause.setText("Start");
                mTextViewCountDown.setVisibility(View.INVISIBLE);
                mButtonStartPause.setVisibility(View.VISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);

            }
        }.start();

        mTimerRunning=true;
       // mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.VISIBLE);
    }
   /* private void pauseTimer(){

        mcountDownTimer.cancel();
        mTimerRunning=false;
        mButtonStartPause.setText("start");
        mButtonReset.setVisibility(View.VISIBLE);
    }*/
    private void resetTimer(){

      //  mTimeLeftInMillis=START_TIME_IN_MILLIS;
       // updateCountDownText();
       mTextViewCountDown.setVisibility(View.GONE);
       mProgressBar.setVisibility(View.INVISIBLE);
        mButtonReset.setVisibility(View.VISIBLE);
       // mButtonStartPause.setVisibility(View.VISIBLE);

    }
    private void updateCountDownText(){
        int minutes=(int)(mTimeLeftInMillis/1000)/1000;
        int seconds=(int)(mTimeLeftInMillis/1000)%1000;
      mProgressBar.setProgress((int) (mTimeLeftInMillis / 1000));

        String timeLeftFormatted=String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }
    private void setProgressBarValues() {

        mProgressBar.setMax((int) mTimeLeftInMillis / 1000);
        mProgressBar.setProgress((int) mTimeLeftInMillis / 1000);
    }

}
