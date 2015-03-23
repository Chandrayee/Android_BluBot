package com.example.cbasu.bluclient2;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//This program sets the phone to discoverable mode for maximum 3600 seconds and changes the bluetooth friendly name every 2 seconds
//The name is reset using a timer task
//On discovery cancel the original name is reset

public class blueclient extends ActionBarActivity {

    TimerTask mTimerTask;
    final Handler handler1 = new Handler();
    Timer t1 = new Timer();
    TextView hTextView1;
    TextView hTextView2;
    TableRow hTableRow;
    Button hButton, hButtonStop;
    private BluetoothAdapter mBtadapter = BluetoothAdapter.getDefaultAdapter();

    private int nCounter = 0;
    String saveddata;
    String old_name;
    String new_name;
    private int level;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blueclient);
        hTextView1 = (TextView)findViewById(R.id.textView1);
        hTextView2 = (TextView)findViewById(R.id.textView2);
        hButton = (Button)findViewById(R.id.button);
        hButton.setOnClickListener(mButtonStartListener);
        hButtonStop = (Button)findViewById(R.id.button2);
        hButtonStop.setOnClickListener(mButtonStopListener);
    } // end onCreate

    View.OnClickListener mButtonStartListener = new OnClickListener() {
        public void onClick(View v) {
            doTimerTask(); //timer task of name change every 1 second
            getoldname(); //save old name for resetting name
            set2discoverable(); //turn on discoverable mode


        }
    };

    public void getoldname() {
        if (mBtadapter.isEnabled()) {
            old_name = mBtadapter.getName();
        }
    }

    BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
    };

    public void onCreate() {
        registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }


    View.OnClickListener mButtonStopListener = new OnClickListener() {
        public void onClick(View v) {
            //mBtadapter.setName(old_name);
            stopTask();

        }
    };


    public void set2discoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);//sets phone to discoverable mode for an hour
        if (mBtadapter.isEnabled()) {
            startActivity(discoverableIntent);
        }
    }

    public void doTimerTask(){

        mTimerTask = new TimerTask() {
            public void run() {
                handler1.post(new Runnable() {
                    public void run() {
                        nCounter++;
                        // update TextView
                        onCreate();
                        hTextView1.setText("Timer: " + nCounter);
                        hTextView2.setText("Battery level is " + String.valueOf(level));
                        new_name = "BLUBot2_"+ nCounter + new Date().getTime() + "_" + String.valueOf(level);
                        if (!mBtadapter.isEnabled()) {
                            mBtadapter.enable();
                        }
                        else if (mBtadapter.isEnabled()){
                            /*if (mBtadapter.isDiscovering()){
                                mBtadapter.cancelDiscovery();
                            }*/
                            //set2discoverable();
                            mBtadapter.setName(new_name); //change Bluetooth name every 1 second
                            saveddata = "Time " + new Date().getTime() + " name " + new_name + " battery level " + level;
                            appendLog(saveddata); //save new name in a log file

                        }

                    }
                });
            }};

        // public void schedule (TimerTask task, long delay, long period)
        t1.schedule(mTimerTask, 1000, 2000);  //device name changes every 2 seconds

    }

    public void stopTask(){

        if(mTimerTask!=null){
            hTextView1.setText("Timer canceled: " + nCounter);
            hTextView2.setText("Bluetooth discovery canceled");

            Log.d("TIMER", "timer canceled");
            mTimerTask.cancel();


            if (mBtadapter.isDiscovering()) {
                mBtadapter.cancelDiscovery();
            }
            mBtadapter.setName(old_name);
            mBtadapter.disable();
        }

    }


    public void appendLog(String text)
    {
        File logFile = new File("sdcard/bluclientlog.file");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
