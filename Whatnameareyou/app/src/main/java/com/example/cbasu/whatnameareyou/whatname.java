package com.example.cbasu.whatnameareyou;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class whatname extends ActionBarActivity {
    TimerTask mTimerTask;
    final Handler handler1 = new Handler();
    Timer t1 = new Timer();
    TextView hTextView1;
    TextView hTextView2;
    TableRow hTableRow;
    Button hButton, hButtonStop;
    private BluetoothAdapter mBtadapter = BluetoothAdapter.getDefaultAdapter();


    private int nCounter = 0;
    long start_time = 0,end_time = 0, y = 0;
    String saveddata;
    //ArrayList discoveredDevices = new ArrayList();
    String devicefound;
    String filename = "Whatname";
    short rssi;
    String name;
    long getTime;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatname);
        hTextView1 = (TextView)findViewById(R.id.textView1);
        hTextView2 = (TextView)findViewById(R.id.textView2);
        hButton = (Button)findViewById(R.id.button);
        hButton.setOnClickListener(mButtonStartListener);
        hButtonStop = (Button)findViewById(R.id.button2);
        hButtonStop.setOnClickListener(mButtonStopListener);
        //BluBotDbHelper db = new BluBotDbHelper(this);
        //db.addValue(name,rssi,getTime);
    } // end onCreate

    BroadcastReceiver discoveryMonitor = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (mBtadapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
                hTextView2.setText("Bluetooth discovery started at " + new Date().getTime());
                start_time = new Date().getTime();
            }
            else if (mBtadapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                hTextView2.setText("Bluetooth discovery finished at " + new Date().getTime());
                end_time = new Date().getTime();
                saveddata = "start_time is " + start_time + " end_time is " + end_time;
                appendLog(saveddata);
                appendLog(devicefound);
                internappend(devicefound);
                Log.d("WNAY",saveddata);

            }
        }
    };

    public void monitorDiscovery() {
        registerReceiver(discoveryMonitor, new IntentFilter(
                mBtadapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(discoveryMonitor, new IntentFilter(
                mBtadapter.ACTION_DISCOVERY_FINISHED));
        //return end_time;

    }



    View.OnClickListener mButtonStartListener = new OnClickListener() {
        public void onClick(View v) {
            doTimerTask();
        }

    };

    View.OnClickListener mButtonStopListener = new OnClickListener() {
        public void onClick(View v) {
            stopTask();

        }
    };


    public void doTimerTask(){

        mTimerTask = new TimerTask() {
            public void run() {
                handler1.post(new Runnable() {
                    public void run() {
                        nCounter++;
                        // update TextView

                        //hTextView1.setText("Timer: " + nCounter);



                        if (!mBtadapter.isEnabled()) {
                            mBtadapter.enable();
                        }
                        else if (mBtadapter.isEnabled()){
                            if (mBtadapter.isDiscovering()){
                                mBtadapter.cancelDiscovery();
                            }
                            mBtadapter.startDiscovery();
                            monitorDiscovery();
                            discoverDevices();
                            //return discoveredDevices;
                        }

                    }
                });
            }};

        // public void schedule (TimerTask task, long delay, long period)
        t1.schedule(mTimerTask, 1000, 2000);  //

    }






    private void discoverDevices() {
        //while (!mBtadapter.isDiscovering()) { // Wait till discovery starts
        if (mBtadapter.isEnabled()){
            BroadcastReceiver discoveryResult = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    BluetoothDevice remoteDevice;
                    remoteDevice = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,
                            Short.MIN_VALUE);
                    getTime = new Date().getTime();
                    name = remoteDevice.getName();

                }

            };
            devicefound = "Discovered: " + name + "  RSSI: "
                    + rssi + " time " + getTime;
            hTextView1.setText(devicefound + "_" + nCounter);
            BluBotDbHelper db = new BluBotDbHelper(this);
            db.addValue(name,rssi,getTime);
            registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));

            //return;

        }
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
            mBtadapter.disable();
        }

    }

    public void appendLog(String text)
    {
        File logFile = new File("sdcard/wnaylog.file");
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


    public void internappend(String string) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

