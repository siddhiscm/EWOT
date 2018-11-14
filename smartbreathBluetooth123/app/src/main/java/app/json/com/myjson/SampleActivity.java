package app.json.com.myjson;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kofigyan.stateprogressbar.StateProgressBar;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.json.com.myjson.fragments.FragAboutUs;
import app.json.com.myjson.fragments.FragDashBoard;
import app.json.com.myjson.fragments.FragProtocalInfo;

import static android.widget.Toast.LENGTH_LONG;
public class SampleActivity extends AppCompatActivity  {

    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private boolean mScanning;
    private static final long SCAN_PERIOD = 10000;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private BluetoothGattCharacteristic mReadCharacteristic;

    TextView tvDashboard, tvProtocal, tvAboutUs;
    RelativeLayout rStartStop;

    private static final long START_TIME_IN_MILLIS = 10000;//5 minutes
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtoncancel;
    private CountDownTimer mcountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private  long TIME=mTimeLeftInMillis;
  //  private long mTimeLeftInMillis1=mTimeLeftInMillis;
    ProgressBar mProgressBar = null;
    String[] descriptionData = {"   Warm Up","EPO","Oxygen\r\nWash","HGS \r\nSprint","Cool Down","HGS"};
    StateProgressBar stateProgressBar;
    //PageIndicatorView pageIndicatorView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            // Android M Permission check 
            if(this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();







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
                setProgressBarValues(mTimeLeftInMillis);
                startTimer(mTimeLeftInMillis);
                //pageIndicatorView.setVisibility(View.INVISIBLE);
                PageIndicatorView pageIndicatorView=(PageIndicatorView)findViewById(R.id.pageIndicatorView);
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
            }
        });
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
    private void setProgressBarValues(long mTimeLeftInMillis) {
        mProgressBar.setMax((int) TIME/ 1000);
        mProgressBar.setProgress((int) this.mTimeLeftInMillis / 1000);
    }


    @SuppressLint("WrongConstant")
    public void stagesTime(){
        switch (stateProgressBar.getCurrentStateNumber()) {
            case 1:
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                mTimeLeftInMillis=10000;//1 minutes
                startTimer(mTimeLeftInMillis);
                TIME=mTimeLeftInMillis;
                String result;
                result="0x01 0x00 0x00 0x00";
                byte[] convertedBytes = Utils.convertingTobyteArray(result);
                writeCharaValue(convertedBytes);
                Log.d("switch","enablead");
                mProgressBar.setMax((int) this.mTimeLeftInMillis / 1000);
                mProgressBar.setProgress((int) this.mTimeLeftInMillis / 1000);
                mProgressBar.setVisibility(View.VISIBLE);
                mButtonStartPause.setEnabled(false);
                mTextViewCountDown.setVisibility(View.VISIBLE);
                break;
            case 2:
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                mTimeLeftInMillis=240100;//4 minutes
                startTimer(mTimeLeftInMillis);
                TIME=mTimeLeftInMillis;
                //mTimeLeftInMillis1=mTimeLeftInMillis;
                mProgressBar.setMax((int) this.mTimeLeftInMillis / 1000);
                mProgressBar.setProgress((int) this.mTimeLeftInMillis / 1000);
                // startTimer(1001011);
                mButtonStartPause.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
                mTextViewCountDown.setVisibility(View.VISIBLE);
                break;
            case 3:
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                mTimeLeftInMillis=240100; //4 minutes
                TIME=mTimeLeftInMillis;
              //  mTimeLeftInMillis1=mTimeLeftInMillis;
                startTimer(mTimeLeftInMillis);
                mProgressBar.setMax((int) this.mTimeLeftInMillis / 1000);
                mProgressBar.setProgress((int) this.mTimeLeftInMillis / 1000);
                //startTimer(1001010);
                mProgressBar.setVisibility(View.VISIBLE);
                mButtonStartPause.setEnabled(false);
                mTextViewCountDown.setVisibility(View.VISIBLE);
                break;
            case 4:
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FIVE);
                mTimeLeftInMillis=180100; //3 minutes
                TIME=mTimeLeftInMillis;
                startTimer(mTimeLeftInMillis);
               // mTimeLeftInMillis1=mTimeLeftInMillis;
                mProgressBar.setMax((int) this.mTimeLeftInMillis / 1000);
                mProgressBar.setProgress((int) this.mTimeLeftInMillis / 1000);
                //startTimer(1001010);
                mProgressBar.setVisibility(View.VISIBLE);
                mButtonStartPause.setEnabled(false);
                mTextViewCountDown.setVisibility(View.VISIBLE);
                break;
            case 5:
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.SIX);
                mTimeLeftInMillis=180100;  //3 minutes
                TIME=mTimeLeftInMillis;
               // mTimeLeftInMillis1=mTimeLeftInMillis;
                startTimer(mTimeLeftInMillis);
                mProgressBar.setMax((int) this.mTimeLeftInMillis / 1000);
                mProgressBar.setProgress((int) this.mTimeLeftInMillis / 1000);
                // startTimer(1001010);
                mProgressBar.setVisibility(View.VISIBLE);
                mButtonStartPause.setEnabled(false);
                mTextViewCountDown.setVisibility(View.VISIBLE);
                break;
            case 6:
                stateProgressBar.setAllStatesCompleted(true);
                stateProgressBar.setVisibility(View.GONE);
                mButtonStartPause.setVisibility(View.GONE);
                mButtoncancel.setVisibility(View.GONE);
                Toast toast=Toast.makeText(getApplicationContext(),"Congratulations! You have successfully \n finished your training session.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0); // last two args are X and Y are used for setting position
                //toast.setDuration(100000);//you can even use milliseconds to display toast
                toast.setDuration(LENGTH_LONG);
                toast.show();
                tvDashboard.setEnabled(true);
                resetState();
               // mButtonStartPause.setBackgroundColor(getResources().getColor(R.color.white_color));
                break;
        }

    }


    private void writeCharaValue(byte[] value) {
//        displayTimeandDate();
        // Writing the hexValue to the characteristic
        try {
            BluetoothLeService.writeCharacteristicGattDb(mReadCharacteristic,
                    value);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
   private int mMaxStateNumber=6;
    public void resetState(){

        if(stateProgressBar.getCurrentStateNumber()>=mMaxStateNumber){
            stateProgressBar.setAllStatesCompleted(Boolean.FALSE);
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
           //mStartButtonSetup();
       }
//
    }
//
//    void mStartButtonSetup(){
//        mButtonStartPause.setVisibility(View.VISIBLE);
//        mButtoncancel.setVisibility(View.VISIBLE);
//        mButtoncancel.setEnabled(true);
//        mButtoncancel.setBackgroundColor(getResources().getColor(R.color.grey));
//        mButtonStartPause.setText("Start");
//    }


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
                    PageIndicatorView pageIndicatorView=(PageIndicatorView)findViewById(R.id.pageIndicatorView);
                    pageIndicatorView.setVisibility(View.GONE);
                    Toast toast=Toast.makeText(getApplicationContext(),"Your Session is Terminated ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0); // last two args are X and Y are used for setting position
                    //toast.setDuration(100000);//you can even use milliseconds to display toast
                    toast.setDuration(LENGTH_LONG);
                    toast.show();
                    mButtonStartPause.setVisibility(View.GONE);
                    mButtoncancel.setVisibility(View.GONE);
                } else if (i == dialogInterface.BUTTON_NEGATIVE) {
                    mButtonStartPause.setEnabled(true);
                    mButtonStartPause.setText("Resume");
                    mButtoncancel.setEnabled(false);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
               filters = new ArrayList<ScanFilter>();
            }
            scanLeDevice(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }

    @Override
    protected void onDestroy() {
        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanLeDevice(final boolean enable) {

        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
//                    if (Build.VERSION.SDK_INT < 21) {
//                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    } else {
//                        mLEScanner.stopScan(mScanCallback);
//
//                    }
                }
            }, SCAN_PERIOD);


            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
//            if (Build.VERSION.SDK_INT < 21) {
//                mBluetoothAdapter.startLeScan(mLeScanCallback);
//            } else {
//                mLEScanner.startScan(filters, settings, mScanCallback);
//            }
        }
        else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);

        }
        invalidateOptionsMenu();




//        else {
//            if (Build.VERSION.SDK_INT < 21) {
//                mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            } else {
//                mLEScanner.stopScan(mScanCallback);
//            }
//        }
    }


//    private ScanCallback mScanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            Log.i("callbackType", String.valueOf(callbackType));
//            Log.i("result", result.toString());
//            BluetoothDevice btDevice = result.getDevice();
//            connectToDevice(btDevice);
//        }
//
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            for (ScanResult sr : results) {
//                Log.i("ScanResult - Results", sr.toString());
//            }
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            Log.e("Scan Failed", "Error Code: " + errorCode);
//        }
//    };

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
                    if ((macAddressBytes[0] == 0x00) && (macAddressBytes[1] == 0xA0) && (macAddressBytes[2] == 0x50)) {

                        Log.d("SMARTBREATH DEVICe", device.getName());


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("onLeScan", device.toString());
                                connectToDevice(device);
                            }

                        });
                    }



                }
            };



    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            scanLeDevice(false);// will stop after first device detection
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
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
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            gatt.readCharacteristic(services.get(1).getCharacteristics().get
                    (0));
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            gatt.disconnect();
        }
    };

}

