/*
 * This software is subject to the license described in the License.txt file
 * included with this software distribution. You may not use this file except in compliance
 * with this license.
 *
 * Copyright (c) Garmin Canada Inc. 2019
 * All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.controls;

import java.util.EnumSet;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.plugins.antplus.pcc.controls.AntPlusAudioControllableDevicePcc;
import com.dsi.ant.plugins.antplus.pcc.controls.AntPlusAudioControllableDevicePcc.IAudioCommandReceiver;
import com.dsi.ant.plugins.antplus.pcc.controls.defines.AudioDeviceCapabilities;
import com.dsi.ant.plugins.antplus.pcc.controls.defines.AudioDeviceState;
import com.dsi.ant.plugins.antplus.pcc.controls.defines.AudioRepeatState;
import com.dsi.ant.plugins.antplus.pcc.controls.defines.AudioShuffleState;
import com.dsi.ant.plugins.antplus.pcc.controls.defines.AudioVideoCommandNumber;
import com.dsi.ant.plugins.antplus.pcc.controls.defines.CommandStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

/**
 * Connects to Controls Plugin, using the Audio mode, and receives audio commands from a remote
 */
public class Activity_AudioControllableDeviceSampler extends Activity
{
    AntPlusAudioControllableDevicePcc ctrlPcc = null;
    PccReleaseHandle<AntPlusAudioControllableDevicePcc> releaseHandle;
    AudioDeviceCapabilities capabilities;

    int DeviceNumber = 0; // Set to Zero for the pluging to automatically generate the ID

    TextView tv_status;
    TextView tv_deviceNumber;

    TextView tv_estTimestamp;

    TextView tv_commandNumber;
    TextView tv_sequenceNumber;
    TextView tv_remoteSerialNumber;
    TextView tv_remoteManufacturerID;
    TextView tv_commandData;

    private Timer updateTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controllable_device);

        tv_status = (TextView)findViewById(R.id.textView_Status);
        tv_deviceNumber = (TextView)findViewById(R.id.textView_DeviceNumber);

        tv_estTimestamp = (TextView)findViewById(R.id.textView_EstTimestamp);

        tv_commandNumber = (TextView)findViewById(R.id.textView_CommandNumber);
        tv_sequenceNumber = (TextView)findViewById(R.id.textView_SequenceNumber);
        tv_remoteSerialNumber = (TextView)findViewById(R.id.textView_RemoteSerialNumber);
        tv_remoteManufacturerID = (TextView)findViewById(R.id.textView_RemoteManufacturerID);
        tv_commandData = (TextView)findViewById(R.id.textView_CommandData);

        // Configure capabilities
        capabilities = new AudioDeviceCapabilities();
        capabilities.customRepeatModeSupport = true;
        capabilities.customShuffleModeSupport = false;

        resetPcc();

        updateTimer = new Timer();  // Timer is used for very rudimentary data simulation

    }

    private void resetPcc()
    {
        if(releaseHandle != null)
        {
            releaseHandle.close();
            releaseHandle = null;
        }

        tv_status.setText("Connecting...");
        tv_deviceNumber.setText("---");

        tv_estTimestamp.setText("---");

        tv_commandNumber.setText("---");
        tv_sequenceNumber.setText("---");
        tv_remoteSerialNumber.setText("---");
        tv_remoteManufacturerID.setText("---");
        tv_commandData.setText("---");


        releaseHandle = AntPlusAudioControllableDevicePcc.requestAccess(this, new IPluginAccessResultReceiver<AntPlusAudioControllableDevicePcc>()
            {
            @Override
            public void onResultReceived(AntPlusAudioControllableDevicePcc result, RequestAccessResult requestAccessResult, DeviceState initialDeviceState)
            {
                switch(requestAccessResult)
                {
                    case SUCCESS:
                        ctrlPcc = result;
                        tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                        tv_deviceNumber.setText(String.valueOf(ctrlPcc.getAntDeviceNumber()));
                        updateTimer.schedule(new SimulateDataTask(), 2000, 2000); // Start generating simulated data
                        break;
                    case CHANNEL_NOT_AVAILABLE:
                        Toast.makeText(Activity_AudioControllableDeviceSampler.this, "Channel Not Available", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    case OTHER_FAILURE:
                        Toast.makeText(Activity_AudioControllableDeviceSampler.this, "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    case DEPENDENCY_NOT_INSTALLED:
                        tv_status.setText("Error. Do Menu->Reset.");
                        AlertDialog.Builder adlgBldr = new AlertDialog.Builder(Activity_AudioControllableDeviceSampler.this);
                        adlgBldr.setTitle("Missing Dependency");
                        adlgBldr.setMessage("The required service\n\"" + AntPlusAudioControllableDevicePcc.getMissingDependencyName() + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
                        adlgBldr.setCancelable(true);
                        adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent startStore = null;
                                startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + AntPlusAudioControllableDevicePcc.getMissingDependencyPackageName()));
                                startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                Activity_AudioControllableDeviceSampler.this.startActivity(startStore);
                            }
                        });
                        adlgBldr.setNegativeButton("Cancel", new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });

                        final AlertDialog waitDialog = adlgBldr.create();
                        waitDialog.show();
                        break;
                    case USER_CANCELLED:
                        tv_status.setText("Cancelled. Do Menu->Reset.");
                        break;
                    case UNRECOGNIZED:
                        Toast.makeText(Activity_AudioControllableDeviceSampler.this,
                            "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                            Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    default:
                        Toast.makeText(Activity_AudioControllableDeviceSampler.this, "Unrecognized result: " + requestAccessResult, Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                }
            }
            },
            new IDeviceStateChangeReceiver()
            {
                @Override
                public void onDeviceStateChange(final DeviceState newDeviceState)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_status.setText(ctrlPcc.getDeviceName() + ": " + newDeviceState.toString());
                        }
                    });
                }
            },
            new IAudioCommandReceiver()
            {

                @Override
                public CommandStatus onNewAudioCommand(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final int serialNumber,
                    final int sequenceNumber, final AudioVideoCommandNumber commandNumber, final int commandData)
                {
                    runOnUiThread(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));
                            tv_sequenceNumber.setText(String.valueOf(sequenceNumber));
                            tv_remoteSerialNumber.setText(String.valueOf(serialNumber));
                            tv_commandData.setText(String.valueOf(commandData));
                            tv_commandNumber.setText(commandNumber.toString());
                        }
                    });
                    return CommandStatus.PASS;
                }
            },
            capabilities, DeviceNumber);
    }

    @Override
    protected void onDestroy()
    {
        if(updateTimer != null)
        {
            updateTimer.cancel();
            updateTimer = null;
        }

        if(releaseHandle != null)
        {
            releaseHandle.close();
            releaseHandle = null;
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_heart_rate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_reset:
                resetPcc();
                tv_status.setText("Resetting...");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Very rudimentary simulation just for testing, cycle through values every 2 seconds
    class SimulateDataTask extends TimerTask
    {
        int totalTrackTime = 248; // 4:08 min
        int currentTime = 0;
        int currentVolume = 0;
        AudioDeviceState deviceState = AudioDeviceState.OFF;
        AudioRepeatState repeatState = AudioRepeatState.OFF_UNSUPPORTED;
        AudioShuffleState shuffleState = AudioShuffleState.OFF_UNSUPPORTED;

        @Override
        public void run()
        {
            // For testing purposes, this just generates random values for volume & track time
            if(ctrlPcc != null)
            {
                ctrlPcc.updateAudioStatus(currentVolume, totalTrackTime, currentTime, deviceState, repeatState, shuffleState);

                // Cycle current track time from 0 to total track time
                if(currentTime == totalTrackTime)
                    currentTime = 0;
                else
                    currentTime += 2;

                // Cycle volume from 0 - 100
                if(currentVolume == 100)
                    currentVolume = 0;
                else
                    currentVolume++;

                // Cycle through the states
                if(deviceState == AudioDeviceState.REWIND)
                    deviceState = AudioDeviceState.OFF;
                else
                {
                    int tempState = deviceState.getIntValue();
                    deviceState = AudioDeviceState.getValueFromInt(++tempState);
                }

                if(repeatState == AudioRepeatState.CUSTOM)
                    repeatState = AudioRepeatState.OFF_UNSUPPORTED;
                else
                {
                    int tempState = repeatState.getIntValue();
                    repeatState = AudioRepeatState.getValueFromInt(++tempState);
                }

                if(shuffleState == AudioShuffleState.CUSTOM)
                    shuffleState = AudioShuffleState.OFF_UNSUPPORTED;
                else
                {
                    int tempState = shuffleState.getIntValue();
                    shuffleState = AudioShuffleState.getValueFromInt(++tempState);
                }
            }
        }
    }

}
