package app.json.com.myjson;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kofigyan.stateprogressbar.StateProgressBar;
import com.rd.PageIndicatorView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.UUID;

import app.json.com.myjson.Bluetooth.BluetoothLeService;
import app.json.com.myjson.Bluetooth.SampleGattAttributes;
import app.json.com.myjson.db.DbHelper;
import app.json.com.myjson.fragments.FragAboutUs;
import app.json.com.myjson.fragments.FragDashBoard;
import app.json.com.myjson.fragments.FragProtocalInfo;

import static android.widget.Toast.LENGTH_LONG;
import static app.json.com.myjson.Bluetooth.BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED;

public class SampleActivity extends AppCompatActivity {
    private static Context mContext;
    TextView tvDashboard, tvProtocal, tvAboutUs;
    RelativeLayout rStartStop;
    private AlertDialog mAlert;
    private ProgressDialog mProgressdialog;
    private Timer mConnectTimer;
    private boolean mConnectTimerON = false;
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static String ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL =
            "com.example.bluetooth.le.ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL";

    public final static UUID UUID_DEVICE_SERVICE_ID =
            UUID.fromString(SampleGattAttributes.DEVICE_SERVICE_ID);

    public final static UUID UUID_DEVICE_CHARACTRISTICS_ID =
            UUID.fromString(SampleGattAttributes.DEVICE_CHAR_ID);
    public final static UUID UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
    private static final long START_TIME_IN_MILLIS = 10100;//5 minutes
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtoncancel;
    private CountDownTimer mcountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private long TIME = mTimeLeftInMillis;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000000;
    //    private LeDeviceListAdapter mLeDeviceListAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String TAG = "Bluetooth2";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ProgressBar mProgressBar = null;
    String[] descriptionData;
    StateProgressBar stateProgressBar;
    BluetoothDevice smartDevice;
    String myDevice;
    String myDeviceName;
    private static BluetoothGatt mGatt;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGattCharacteristic mReadCharacteristic;
    String result, previousState;
    byte[] convertedBytes;
    private int mMaxStateNumber = 7;
    PageIndicatorView pageIndicatorView;
    private DbHelper dbHelper;

    //PageIndicatorView pageIndicatorView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);
        dbHelper = new DbHelper(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);


        initialize();
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "bluetoothManager.getAdapter()==null.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
//            mBluetoothAdapter.enable();
//        }
        // Initializes list view adapter.
//        mLeDeviceListAdapter = new LeDeviceListAdapter();
        //setListAdapter(mLeDeviceListAdapter);
        mHandler = new Handler();
        //scanLeDevice(true);

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Constant.STATE == 1) {
                    //   mTimeLeftInMillis=10000;
                    descriptionData = new String[]{"Prep\r\nTime", "Warm\r\nUp", "EPO", "Oxygen\r\nWash", "HGS\r\nSprint", "HGS\r\nSprint", "Cool\r\nDown"};
                    initializeStateProgress(StateProgressBar.StateNumber.SEVEN, descriptionData);
                } else if (Constant.STATE == 2) {
                    //  mTimeLeftInMillis=30000;
                    descriptionData = new String[]{"Prep\r\nTime", "Warm\r\nUp", "Sprint", "Sprint", "Cool\r\nDown"};
                    initializeStateProgress(StateProgressBar.StateNumber.FIVE, descriptionData);
                } else if (Constant.STATE == 3) {
                    //    mTimeLeftInMillis=20000;
                    descriptionData = new String[]{"Prep\r\nTime", "Warm\r\nUp", "Sprint", "Sprint", "Cool\r\nDown"};
                    initializeStateProgress(StateProgressBar.StateNumber.FIVE, descriptionData);
                } else if (Constant.STATE == 4) {
                    //  mTimeLeftInMillis=40000;
                    descriptionData = new String[]{"Prep\r\nTime", "Warm\r\nUp4", "Oxygen\r\nWash", "Cool\r\nDown"};

                    initializeStateProgress(StateProgressBar.StateNumber.FOUR, descriptionData);
                } else if (Constant.STATE == 5) {
                    //  mTimeLeftInMillis=10000;
                    descriptionData = new String[]{"Prep\r\nTime", "Warm\r\nUp5", "EPO", "Oxygen\r\nWash", "Cool\r\nDown"};
                    initializeStateProgress(StateProgressBar.StateNumber.FIVE, descriptionData);
                } else if (Constant.STATE == 6) {
                    //  mTimeLeftInMillis=10000;
                    descriptionData = new String[]{"Prep\r\nTime", "Warm\r\nUp"};
                    initializeStateProgress(StateProgressBar.StateNumber.TWO, descriptionData);
                }


                String buttonState = mButtonStartPause.getText().toString().trim();

                if (buttonState.equals("Start")) {
                    result = "0x00 0x00 0x00 0x00 ";//empty
                    convertedBytes = convertingTobyteArray(result);
                    writeCharaValue(convertedBytes);
                    mButtonStartPause.setEnabled(false);
                    tvProtocal.setEnabled(false);
                    tvDashboard.setEnabled(false);
                    tvAboutUs.setEnabled(false);
                    stateProgressBar.setVisibility(View.VISIBLE);
                    tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_disable));
                    tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_disable));
                    mButtoncancel.setBackgroundColor(getResources().getColor(R.color.button_enable));
                    ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
                    viewPager.setVisibility(View.GONE);
                    mButtonStartPause.setBackgroundColor(getResources().getColor(R.color.button_disable));
                    mButtonStartPause.setTextColor(getResources().getColor(R.color.background_color));
                    mProgressBar.setVisibility(View.VISIBLE);
                    mTextViewCountDown.setVisibility(View.VISIBLE);
                    setProgressBarValues();
                    startTimer(START_TIME_IN_MILLIS);
                    //pageIndicatorView.setVisibility(View.INVISIBLE);
                    pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
                    pageIndicatorView.setVisibility(View.GONE);


                } else if (buttonState.equals("Resume")) {

                    result = previousState; //red
                    convertedBytes = convertingTobyteArray(result);
                    writeCharaValue(convertedBytes);
                    mButtonStartPause.setEnabled(false);
                    tvProtocal.setEnabled(false);
                    tvDashboard.setEnabled(false);
                    tvAboutUs.setEnabled(false);
                    stateProgressBar.setVisibility(View.VISIBLE);
                    tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_disable));
                    tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_disable));
                    mButtoncancel.setBackgroundColor(getResources().getColor(R.color.button_enable));
                    ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
                    viewPager.setVisibility(View.GONE);
                    mButtonStartPause.setBackgroundColor(getResources().getColor(R.color.button_disable));
                    mButtonStartPause.setTextColor(getResources().getColor(R.color.background_color));
                    mProgressBar.setVisibility(View.VISIBLE);
                    mTextViewCountDown.setVisibility(View.VISIBLE);
                    setProgressBarValues();
                    startTimer(mTimeLeftInMillis);
                    //pageIndicatorView.setVisibility(View.INVISIBLE);
                    pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
                    pageIndicatorView.setVisibility(View.GONE);


                }

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
                mButtonStartPause.setBackgroundColor(getResources().getColor(R.color.button_enable));
                stateProgressBar.setVisibility(View.GONE);
                mButtoncancel.setBackgroundColor(getResources().getColor(R.color.button_disable));
                replacefragment(new FragDashBoard());
                ImageView imageView = (ImageView) findViewById(R.id.smartbreath);
                imageView.setVisibility(View.GONE);
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
                ImageView imageView = (ImageView) findViewById(R.id.smartbreath);
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
                ImageView imageView = (ImageView) findViewById(R.id.smartbreath);
                imageView.setVisibility(View.GONE);
            }
        });
    }

    private void initializeStateProgress(StateProgressBar.StateNumber state, String[] descriptionData) {

        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setMaxStateNumber(state);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setCancelable(false);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
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
                if (Constant.STATE == 1) {
                    vibrator();
                    tone();

                    switch (stateProgressBar.getCurrentStateNumber()) {
                        case 1:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x00 0x10 0x00 0x00";//red
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 2:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x00 0x10 0x00 0x00"; //red
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 3:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_green));
                            result = "0x10 0x00 0x00 0x00"; //green
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 4:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_green));
                            result = "0x00 0x10 0x00 0x00"; //red
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 5:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.SIX);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_green));
                            result = "0x10 0x00 0x00 0x00"; //green
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 6:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.SEVEN);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_green));
                            result = "0x10 0x00 0x00 0x00"; //green
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 7:
                            stateProgressBar.setAllStatesCompleted(true);
                            result = "0x00 0x00 0x00 0x00 ";//empty
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stateProgressBar.setVisibility(View.GONE);
                            mButtonStartPause.setVisibility(View.GONE);
                            mButtoncancel.setVisibility(View.GONE);
                            tvDashboard.setEnabled(true);
                            resetState();
                            vibrator();
                            alert_finish();
                            tone();
                            mTimeLeftInMillis = START_TIME_IN_MILLIS;
                            TIME = START_TIME_IN_MILLIS;
                            mButtonStartPause.setText("Start");
                            tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_enable));
                            tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_enable));
                            tvAboutUs.setEnabled(true);
                            tvProtocal.setEnabled(true);
                            tvProtocal.setEnabled(true);
                            break;
                    }
                } else if (Constant.STATE == 2) {
                    vibrator();
                    tone();
                    switch (stateProgressBar.getCurrentStateNumber()) {
                        case 1:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x00 0x00 0x00 0x00";//empty
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 2:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x00 0x10 0x00 0x00"; //red
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 3:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x10 0x00 0x00 0x00"; //green
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 4:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x10 0x00 0x00 0x00"; //green
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 5:
                            stateProgressBar.setAllStatesCompleted(true);
                            result = "0x00 0x00 0x00 0x00 ";//empty
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stateProgressBar.setVisibility(View.GONE);
                            mButtonStartPause.setVisibility(View.GONE);
                            mButtoncancel.setVisibility(View.GONE);
                            tvDashboard.setEnabled(true);
                            resetState();
                            vibrator();
                            alert_finish();
                            tone();
                            mTimeLeftInMillis = START_TIME_IN_MILLIS;
                            TIME = START_TIME_IN_MILLIS;
                            mButtonStartPause.setText("Start");
                            tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_enable));
                            tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_enable));
                            tvAboutUs.setEnabled(true);
                            tvProtocal.setEnabled(true);
                            tvProtocal.setEnabled(true);
                            break;
                    }
                } else if (Constant.STATE == 3) {
                    vibrator();
                    tone();
                    switch (stateProgressBar.getCurrentStateNumber()) {
                        case 1:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x10 0x00 0x00 0x00";//green
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 2:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x00 0x10 0x00 0x00"; //red
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 3:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x10 0x00 0x00 0x00"; //green
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 4:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x10 0x00 0x00 0x00"; //green
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 5:
                            stateProgressBar.setAllStatesCompleted(true);
                            result = "0x00 0x00 0x00 0x00 ";//empty
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stateProgressBar.setVisibility(View.GONE);
                            mButtonStartPause.setVisibility(View.GONE);
                            mButtoncancel.setVisibility(View.GONE);
                            tvDashboard.setEnabled(true);
                            resetState();
                            vibrator();
                            alert_finish();
                            tone();
                            mTimeLeftInMillis = START_TIME_IN_MILLIS;
                            TIME = START_TIME_IN_MILLIS;
                            mButtonStartPause.setText("Start");
                            tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_enable));
                            tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_enable));
                            tvAboutUs.setEnabled(true);
                            tvProtocal.setEnabled(true);
                            tvProtocal.setEnabled(true);
                            break;
                    }
                } else if (Constant.STATE == 4) {
                    vibrator();
                    tone();
                    switch (stateProgressBar.getCurrentStateNumber()) {
                        case 1:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x00 0x10 0x00 0x00";//red
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 2:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x00 0x10 0x00 0x00"; //red
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 3:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_green));
                            result = "0x10 0x00 0x00 0x00"; //green
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 4:
                            stateProgressBar.setAllStatesCompleted(true);
                            result = "0x00 0x00 0x00 0x00 ";//empty
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stateProgressBar.setVisibility(View.GONE);
                            mButtonStartPause.setVisibility(View.GONE);
                            mButtoncancel.setVisibility(View.GONE);
                            tvDashboard.setEnabled(true);
                            resetState();
                            vibrator();
                            alert_finish();
                            tone();
                            mTimeLeftInMillis = START_TIME_IN_MILLIS;
                            TIME = START_TIME_IN_MILLIS;
                            mButtonStartPause.setText("Start");
                            tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_enable));
                            tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_enable));
                            tvAboutUs.setEnabled(true);
                            tvProtocal.setEnabled(true);
                            tvProtocal.setEnabled(true);
                            break;
                    }
                } else if (Constant.STATE == 5) {
                    vibrator();
                    tone();

                    switch (stateProgressBar.getCurrentStateNumber()) {
                        case 1:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x00 0x10 0x00 0x00";//red
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 2:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x00 0x10 0x00 0x00"; //red
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 3:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_green));
                            result = "0x10 0x00 0x00 0x00"; //green
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 4:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_green));
                            result = "0x10 0x00 0x00 0x00"; //green
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 5:
                            stateProgressBar.setAllStatesCompleted(true);
                            result = "0x00 0x00 0x00 0x00 ";//empty
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stateProgressBar.setVisibility(View.GONE);
                            mButtonStartPause.setVisibility(View.GONE);
                            mButtoncancel.setVisibility(View.GONE);
                            tvDashboard.setEnabled(true);
                            resetState();
                            vibrator();
                            alert_finish();
                            tone();
                            mTimeLeftInMillis = START_TIME_IN_MILLIS;
                            TIME = START_TIME_IN_MILLIS;
                            mButtonStartPause.setText("Start");
                            tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_enable));
                            tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_enable));
                            tvAboutUs.setEnabled(true);
                            tvProtocal.setEnabled(true);
                            tvProtocal.setEnabled(true);
                            break;
                    }

                } else if (Constant.STATE == 6) {

                    vibrator();
                    tone();
                    switch (stateProgressBar.getCurrentStateNumber()) {
                        case 1:
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                            mTimeLeftInMillis = 30100;//30 sec
                            mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
                            result = "0x00 0x10 0x00 0x00";//red
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stages();
                            break;
                        case 2:
                            stateProgressBar.setAllStatesCompleted(true);
                            result = "0x00 0x00 0x00 0x00 ";//empty
                            convertedBytes = convertingTobyteArray(result);
                            writeCharaValue(convertedBytes);
                            stateProgressBar.setVisibility(View.GONE);
                            mButtonStartPause.setVisibility(View.GONE);
                            mButtoncancel.setVisibility(View.GONE);
                            tvDashboard.setEnabled(true);
                            resetState();
                            vibrator();
                            alert_finish();
                            tone();
                            mTimeLeftInMillis = START_TIME_IN_MILLIS;
                            TIME = START_TIME_IN_MILLIS;
                            mButtonStartPause.setText("Start");
                            tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_enable));
                            tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_enable));
                            tvAboutUs.setEnabled(true);
                            tvProtocal.setEnabled(true);
                            tvProtocal.setEnabled(true);
                            break;
                    }

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
        mTextViewCountDown.setTextColor(getResources().getColor(R.color.text_color));
    }

    private void setProgressBarValues() {
        mProgressBar.setMax((int) TIME / 1000);
        mProgressBar.setProgress((int) this.mTimeLeftInMillis / 1000);
    }

//    @SuppressLint("WrongConstant")
//    public void stagesTime() {
//
//

//        switch (stateProgressBar.getCurrentStateNumber()) {
//            case 1:
//                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
//                mTimeLeftInMillis = 30100;//30 sec
//                mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
//                result = "0x00 0x10 0x00 0x00";//red
//                convertedBytes = convertingTobyteArray(result);
//                writeCharaValue(convertedBytes);
//                stages();
//                break;
//            case 2:
//                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
//                mTimeLeftInMillis = 30100;//30 sec
//                mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_red));
//                result = "0x00 0x10 0x00 0x00"; //red
//                convertedBytes = convertingTobyteArray(result);
//                writeCharaValue(convertedBytes);
//                stages();
//                break;
//            case 3:
//                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
//                mTimeLeftInMillis = 30100;//30 sec
//                mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_green));
//                result = "0x10 0x00 0x00 0x00"; //green
//                convertedBytes = convertingTobyteArray(result);
//                writeCharaValue(convertedBytes);
//                stages();
//                break;
//            case 4:
//                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
//                mTimeLeftInMillis = 30100;//30 sec
//                mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_green));
//                result = "0x10 0x00 0x00 0x00"; //green
//                convertedBytes = convertingTobyteArray(result);
//                writeCharaValue(convertedBytes);
//                stages();
//                break;
//            case 5:
//                stateProgressBar.setAllStatesCompleted(true);
//                result = "0x00 0x00 0x00 0x00 ";//empty
//                convertedBytes = convertingTobyteArray(result);
//                writeCharaValue(convertedBytes);
//                stateProgressBar.setVisibility(View.GONE);
//                mButtonStartPause.setVisibility(View.GONE);
//                mButtoncancel.setVisibility(View.GONE);
//                tvDashboard.setEnabled(true);
//                resetState();
//                vibrator();
//                alert_finish();
//                tone();
//                mTimeLeftInMillis = START_TIME_IN_MILLIS;
//                TIME = START_TIME_IN_MILLIS;
//                mButtonStartPause.setText("Start");
//                tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_enable));
//                tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_enable));
//               tvAboutUs.setEnabled(true);
//                tvProtocal.setEnabled(true);
//                tvProtocal.setEnabled(true);
//                break;
//        }

    //   }

    private void stages() {
        startTimer(START_TIME_IN_MILLIS);
        TIME = mTimeLeftInMillis;
        mProgressBar.setMax((int) this.mTimeLeftInMillis / 1000);
        mProgressBar.setProgress((int) this.mTimeLeftInMillis / 1000);
        mProgressBar.setVisibility(View.VISIBLE);
        mButtonStartPause.setEnabled(false);
        mTextViewCountDown.setVisibility(View.VISIBLE);
    }

    @SuppressLint("MissingPermission")
    public void vibrator() {
        try {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        } catch (Exception e) {
            Log.d("hgj", "vibrator exception: " + e);
        }
    }

    public void alert_finish() {
        mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_blue));
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder1.setMessage("Congratulations! You have successfully finished your training session.");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ImageView imageView = (ImageView) findViewById(R.id.smartbreath);
                        imageView.setVisibility(View.VISIBLE);
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void alert_Terminated() {
        mProgressBar.setBackground(getResources().getDrawable(R.drawable.drawable_circle_dark_blue));
        result = "0x00 0x00 0x00 0x00 "; // result is nothing 
        convertedBytes = convertingTobyteArray(result);
        writeCharaValue(convertedBytes);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder1.setMessage("Your Session is Terminated");
        builder1.setCancelable(true);
        builder1.setPositiveButton(

                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        ImageView imageView = (ImageView) findViewById(R.id.smartbreath);
                        imageView.setVisibility(View.VISIBLE);

                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
        vibrator();
        tone();
    }

    public void tone() {
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 100);

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//;                mGatt.disconnect();
                // SampleActivity.super.onBackPressed();
                //if user pressed "yes", then he is allowed to exit from application
                finish();
                result = "0x00 0x00 0x00 0x00 ";//empty
                convertedBytes = convertingTobyteArray(result);
                writeCharaValue(convertedBytes);

///               mcountDownTimer.cancel();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        vibrator();
        tone();
    }


    public static byte[] convertingTobyteArray(String result) {
        String[] splited = result.split("\\s+");
        byte[] valueByte = new byte[splited.length];
        for (int i = 0; i < splited.length; i++) {
            if (splited[i].length() > 2) {
                String trimmedByte = splited[i].split("x")[1];
                valueByte[i] = (byte) convertstringtobyte(trimmedByte);
            }

        }
        return valueByte;
    }


    private static int convertstringtobyte(String string) {
        return Integer.parseInt(string, 16);
    }


    public void resetState() {
        if (stateProgressBar.getCurrentStateNumber() >= mMaxStateNumber) {
            stateProgressBar.setAllStatesCompleted(Boolean.FALSE);
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
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
        // stateProgressBar.setMaxStateNumber(StateProgressBar.StateNumber.SEVEN);
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
                    result = "0x00 0x00 0x00 0x00"; //null
                    convertedBytes = convertingTobyteArray(result);
                    writeCharaValue(convertedBytes);
                    mTimeLeftInMillis = START_TIME_IN_MILLIS;
                    TIME = START_TIME_IN_MILLIS;
                    mTextViewCountDown.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mButtonStartPause.setEnabled(true);
                    mButtonStartPause.setText("Start");
                    tvDashboard.setEnabled(true);
                    stateProgressBar.setVisibility(View.GONE);
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                    ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
                    viewPager.setVisibility(View.GONE);
                    tvProtocal.setEnabled(true);
                    mButtoncancel.setEnabled(false);
                    mButtoncancel.setBackgroundColor(getResources().getColor(R.color.button_disable));
                    tvAboutUs.setEnabled(true);
                    tvProtocal.setBackgroundColor(getResources().getColor(R.color.button_enable));
                    tvAboutUs.setBackgroundColor(getResources().getColor(R.color.button_enable));
                    mButtonStartPause.setBackgroundColor(getResources().getColor(R.color.button_enable));
                    PageIndicatorView pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
                    pageIndicatorView.setVisibility(View.GONE);
                    alert_Terminated();
                    mButtonStartPause.setVisibility(View.GONE);
                    mButtoncancel.setVisibility(View.GONE);
                    ImageView imageView = (ImageView) findViewById(R.id.smartbreath);
                    imageView.setVisibility(View.VISIBLE);
                } else if (i == dialogInterface.BUTTON_NEGATIVE) {
                    previousState = result;
                    result = "0x00 0x00 0x00 0x00"; //null
                    convertedBytes = convertingTobyteArray(result);
                    writeCharaValue(convertedBytes);
                    mButtonStartPause.setEnabled(true);
                    mButtoncancel.setEnabled(false);
                    mButtonStartPause.setText("Resume");
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


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:

                        mGatt = null;

                        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                            turnOnBluetooth();
                        }

                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        // Toast.makeText(getApplicationContext(), "BL Start OFF", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
//                        if (mBluetoothAdapter.isEnabled()) {
//                            scan();
//                        }
                        //Toast.makeText(getApplicationContext(), "BL is ON", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        //Toast.makeText(getApplicationContext(), "BL Start OFF", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };


    void turnOnBluetooth() {
        Log.i("turnon Bluettoth", "turnon");
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Log.i("turnon Bluettoth1", "turnon");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    void scan() {
        Log.i("scan1", "turnon");
        if (Build.VERSION.SDK_INT >= 21) {
            Log.i("scan", "turnon");
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<ScanFilter>();
            Log.d("BLE_SCAN", "scannig ");
        }
        scanLeDevice(true);
        Log.d("BLE TRUE", "TRUE");
    }


    @Override
    protected void onResume() {
        super.onResume();


        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            turnOnBluetooth();
        }

        if (mBluetoothAdapter.isEnabled()) {
            scan();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
//            scanLeDevice(false);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("BLES", "SJABAFJ");
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
//    void scanLeDevice(final boolean enable){
//        if (enable) {
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mScanning = false;
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    invalidateOptionsMenu();
//                }
//            }, SCAN_PERIOD);
//
//
//            mScanning = true;
//            mBluetoothAdapter.startLeScan(mLeScanCallback);
//        } else {
//            mScanning = false;
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//
//        }
//        invalidateOptionsMenu();
//
//
//    }
//

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    //String SmartBrethUIID = "00:A0:50";
                    final String[] macAddressParts = device.getAddress().split(":");
                    // convert hex string to byte values
                    Integer[] macAddressBytes = new Integer[6];
                    for (int i = 0; i < 6; i++) {
                        Integer hex = Integer.parseInt(macAddressParts[i], 16);
                        macAddressBytes[i] = hex;
                        //Logger.d("abc",String.valueOf(macAddressBytes[i]));
                    }
                    if ((macAddressBytes[0] == 0x00) && (macAddressBytes[1] == 0xA0) && (macAddressBytes[2] == 0x50) && device.getName() == "B") {

                        Log.d("SMARTBREATH DEVICe", device.getName());
                        connectToDevice(device);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("onLeScan", device.toString());

                            }

                        });
                    }


                }
            };

    public void connectToDevice(BluetoothDevice device) {

        if (mGatt == null) {

            mGatt = device.connectGatt(this, true, gattCallback);
            scanLeDevice(false);// will stop after first device detection
            Toast toast = Toast.makeText(getApplicationContext(), "Device Connected ", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        private boolean enabled;

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");

                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {

//                          Log.i("onServiceDicovered", "Device address" + myDevice);
//                            Log.i("onServiceDicovered", "DeviceName" + myDeviceName);

                int count = 0;
                for (BluetoothGattService service : gatt.getServices()) {
//                                UUID DebugUUID=service.getUuid();
//                                Log.i("onSunsccessful", "Device address"+DebugUUID);
//                                BluetoothGattCharacteristic mCharacteristic;
//
//                                        mCharacteristic = (BluetoothGattCharacteristic) service.getCharacteristics();

//                                if (UUID_DEVICE_CHARACTRISTICS_ID.equals(service.getUuid())) {
//                                    if (!service.getCharacteristics().isEmpty()) {
//                                        UUID m=service.getUuid();
//                                        Log.i("onSunsccessful", "Device address"+m);
////                                        buttonCharacteristic = service.getCharacteristics().get(0);
////                                        setCharacteristicNotification(gatt, buttonCharacteristic, true);
//                                    }
//                                }
                    if (!service.getCharacteristics().isEmpty()) {
                        UUID m = service.getUuid();
                        Log.i("onSunsccessful", "Device address" + m);
                        BluetoothGattCharacteristic buttonCharacteristic = service.getCharacteristics().get(0);
                        mReadCharacteristic = buttonCharacteristic;
                        gatt.setCharacteristicNotification(buttonCharacteristic, true);

                        BluetoothGattDescriptor descriptor = buttonCharacteristic.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG);
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }

                    }
                    count++;
                }
//                            Log.i("onServiceDicovered", "Device address" + service);
//                            BluetoothGattCharacteristic Characteristic =
//                                    service.getCharacteristic(UUID_DEVICE_CHARACTRISTICS_ID);
//                            Log.i("onServiceDicovered", "Device address" + Characteristic);
                // gatt.readCharacteristic(Characteristic);
                //  super.onServicesDiscovered(gatt, status);

            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION ||
                    status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION) {
                //bondDevice();
                broadcastUpdate(ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL);
            } else {
                broadcastUpdate(ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL);
            }

        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            BluetoothGattService service =
                    gatt.getService(UUID_DEVICE_SERVICE_ID);
            Log.i("onServiceDicovered", "Read service address" + service);
            BluetoothGattCharacteristic Characteristic =
                    service.getCharacteristic(UUID_DEVICE_CHARACTRISTICS_ID);
            Log.i("onServiceDicovered", "Read service address" + Characteristic);
            //readNextCharacteristic(gatt, characteristic);
            super.onCharacteristicRead(gatt, characteristic, status);
//            Log.i("onCharacteristicRead", characteristic.toString());
//            gatt.disconnect();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            String serviceUUID = characteristic.getService().getUuid().toString();
            String serviceName = SampleGattAttributes.lookupUUID(characteristic.getService().getUuid(), serviceUUID);

            String characteristicUUID = characteristic.getUuid().toString();
            String characteristicName = SampleGattAttributes.lookupUUID(characteristic.getUuid(), characteristicUUID);

        }
    };

    private BluetoothGattCharacteristic getCharacteristic(
            @NonNull BluetoothGatt bluetoothgatt,
            @NonNull UUID serviceUuid,
            @NonNull UUID characteristicUuid
    ) {
        BluetoothGattService service = bluetoothgatt.getService(serviceUuid);
        if (service != null)
            return service.getCharacteristic(characteristicUuid);
        return null;
    }


    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }


    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is carried out as per profile specifications.
        if (UUID_DEVICE_CHARACTRISTICS_ID.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                        stringBuilder.toString());
            }
        }

        sendBroadcast(intent);
    }

//    private static void broadcastConnectionUpdate(final String action) {
//      //  Logger.i("action :" + action);
//        final Intent intent = new Intent(action);
//        mContext.sendBroadcast(intent);
//    }

//    public static void bondDevice() {
//        try {
//            Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
//            Method createBondMethod = class1.getMethod("createBond");
//            Boolean returnValue = (Boolean) createBondMethod.invoke(mGatt.getDevice());
//            Log.i("BondDivce","Pair initiate status"+returnValue);
//           // Logger.e("Pair initates status-->" + returnValue);
//        } catch (Exception e) {
//          //  Logger.e("Exception Pair" + e.getMessage());
//        }
//
//    }

    private void writeCharaValue(byte[] value) {
//        displayTimeandDate();
        // Writing the hexValue to the characteristic
        try {
            writeCharacteristicGattDb(mReadCharacteristic,
                    value);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void writeCharacteristicGattDb(
            BluetoothGattCharacteristic characteristic, byte[] byteArray) {


        String serviceUUID = characteristic.getService().getUuid().toString();
        String serviceName = SampleGattAttributes.lookupUUID(characteristic.getService().getUuid(), serviceUUID);
        String characteristicUUID = characteristic.getUuid().toString();
        String characteristicName = SampleGattAttributes.lookupUUID(characteristic.getUuid(), characteristicUUID);

        String characteristicValue = ByteArraytoHex(byteArray);
        if (mBluetoothAdapter == null || mGatt == null) {
            return;
        } else {
            byte[] valueByte = byteArray;
            characteristic.setValue(valueByte);
            mGatt.writeCharacteristic(characteristic);

        }

    }


    public String ByteArraytoHex(byte[] bytes) {
        if (bytes != null) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02X ", b));
            }
            return sb.toString();
        }
        return "";
    }

//                private final BroadcastReceiver mGattConnectReceiver = new BroadcastReceiver() {
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                        final String action = intent.getAction();
//                        // Status received when connected to GATT Server
//                        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                            //  mProgressdialog.setMessage(getString(R.string.alert_message_bluetooth_connect));
//                            if (mScanning) {
//                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                                mScanning = false;
//                            }
//                            //   mProgressdialog.dismiss();
//                            //   mLeDevices.clear();
//                            if (mConnectTimer != null)
//                                mConnectTimer.cancel();
//                            mConnectTimerON = false;
//                            //   updateWithNewFragment();
//                        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                            /**
//                             * Disconnect event.When the connect timer is ON,Reconnect the device
//                             * else show disconnect message
//                             */
//                            if (mConnectTimerON) {
//                                //    BluetoothLeService.reconnect();
//                            } else {
////                    Toast.makeText(getActivity(),
////                            R.string.profile_cannot_connect_message,
////                            Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                };


    public void openDialog(final BluetoothDevice device) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure,You wanted to make decision");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                device.getName();
                Toast.makeText(SampleActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


}