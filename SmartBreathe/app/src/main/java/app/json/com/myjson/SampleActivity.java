package app.json.com.myjson;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.rd.PageIndicatorView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import app.json.com.myjson.Bluetooth.BluetoothLeService;
import app.json.com.myjson.fragments.FragAboutUs;
import app.json.com.myjson.fragments.FragDashBoard;
import app.json.com.myjson.fragments.FragProtocalInfo;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTING;
import static android.widget.Toast.LENGTH_LONG;
public class SampleActivity extends AppCompatActivity  {
    TextView tvDashboard, tvProtocal, tvAboutUs;
    RelativeLayout rStartStop;

    private static final long START_TIME_IN_MILLIS = 300100;//5 minutes
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtoncancel;
    private CountDownTimer mcountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private  long TIME=mTimeLeftInMillis;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String TAG="Bluetooth2";
    private boolean mSearchEnabled = false;
    private BluetoothGatt mBluetoothGatt;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ProgressBar mProgressBar = null;
    String[] descriptionData = {"   Warm Up","EPO","Oxygen\r\nWash","HGS \r\nSprint","Cool Down","HGS"};
    StateProgressBar stateProgressBar;
    BluetoothDevice smartDevice;
    //PageIndicatorView pageIndicatorView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }





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

        setContentView(R.layout.activity_main);
        initialize();
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "bluetoothManager.getAdapter()==null.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        //setListAdapter(mLeDeviceListAdapter);
        mHandler = new Handler();
        scanLeDevice(true);

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

//        // Initializes list view adapter.
//        mLeDeviceListAdapter = new LeDeviceListAdapter();
//        setListAdapter(mLeDeviceListAdapter);
//        scanLeDevice(true);

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
                mTimeLeftInMillis=60100;//1 minutes
                startTimer(mTimeLeftInMillis);
                TIME=mTimeLeftInMillis;
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
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
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
            Log.d("strt","scaning");
        } else {
            mScanning = false;
            mBluetoothAdapter.
                    stopLeScan(mLeScanCallback);
            Log.d("strt","scaning");

        }
       invalidateOptionsMenu();
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
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
                                Log.d("SMARTBREATH DEVICe",device.getName());
                        mLeDeviceListAdapter.addDevice(device);
                        mLeDeviceListAdapter.notifyDataSetChanged();
                        if (true){
                            mBluetoothAdapter.stopLeScan((BluetoothAdapter.LeScanCallback) this);
                            Log.d("SROP","SCANING STOP");
                        }
                        // Previously connected device.  Try to reconnect.







//                        runOnUiThread(new Runnable() {
//                            @Override
//                           public void run() {
//                                if (!mSearchEnabled) {
//
//                                    try {
//
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        });
                    }
                }
            };
    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;
        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
//            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }
        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }
        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }
        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
        //For Pairing


//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
////            ViewHolder viewHolder;
//            // General ListView optimization code.
//            if (view == null) {
//                view = mInflator.inflate(R.layout.listitem_device, null);
//                viewHolder = new ViewHolder();
//                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
//                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
//                view.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) view.getTag();
//            }

//            BluetoothDevice device = mLeDevices.get(i);
//            final String deviceName = device.getName();
//            if (deviceName != null && deviceName.length() > 0)
//                viewHolder.deviceName.setText(deviceName);
//            else
//                viewHolder.deviceName.setText(R.string.unknown_device);
//            viewHolder.deviceAddress.setText(device.getAddress());
//
//            return view;
//        }
    }
    private void pairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);

        } catch (Exception e) {
//                if (mProgressdialog != null && mProgressdialog.isShowing()) {
//                    mProgressdialog.dismiss();
//                }
        }

    }

}

