/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2013
All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.controls;

import java.util.EnumSet;

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
import com.dsi.ant.plugins.antplus.pcc.controls.AntPlusGenericControllableDevicePcc;
import com.dsi.ant.plugins.antplus.pcc.controls.AntPlusGenericControllableDevicePcc.IGenericCommandReceiver;
import com.dsi.ant.plugins.antplus.pcc.controls.defines.CommandStatus;
import com.dsi.ant.plugins.antplus.pcc.controls.defines.GenericCommandNumber;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

/**
 * Connects to Controls Plugin, using the Generic mode, and receives generic commands from a remote
 */
public class Activity_GenericControllableDeviceSampler extends Activity
{
    AntPlusGenericControllableDevicePcc ctrlPcc = null;
    PccReleaseHandle<AntPlusGenericControllableDevicePcc> releaseHandle;

    int DeviceNumber = 0; // Set to Zero for the pluging to automatically generate the ID

    TextView tv_status;
    TextView tv_deviceNumber;

    TextView tv_estTimestamp;

    TextView tv_commandNumber;
    TextView tv_sequenceNumber;
    TextView tv_remoteSerialNumber;
    TextView tv_remoteManufacturerID;
    TextView tv_commandData;


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

        resetPcc();

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


        releaseHandle = AntPlusGenericControllableDevicePcc.requestAccess(this, new IPluginAccessResultReceiver<AntPlusGenericControllableDevicePcc>()
            {
            @Override
            public void onResultReceived(AntPlusGenericControllableDevicePcc result, RequestAccessResult requestAccessResult, DeviceState initialDeviceState)
            {
                switch(requestAccessResult)
                {
                    case SUCCESS:
                        ctrlPcc = result;
                        tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                        tv_deviceNumber.setText(String.valueOf(ctrlPcc.getAntDeviceNumber()));
                        break;
                    case CHANNEL_NOT_AVAILABLE:
                        Toast.makeText(Activity_GenericControllableDeviceSampler.this, "Channel Not Available", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    case OTHER_FAILURE:
                        Toast.makeText(Activity_GenericControllableDeviceSampler.this, "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    case DEPENDENCY_NOT_INSTALLED:
                        tv_status.setText("Error. Do Menu->Reset.");
                        AlertDialog.Builder adlgBldr = new AlertDialog.Builder(Activity_GenericControllableDeviceSampler.this);
                        adlgBldr.setTitle("Missing Dependency");
                        adlgBldr.setMessage("The required service\n\"" + AntPlusGenericControllableDevicePcc.getMissingDependencyName() + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
                        adlgBldr.setCancelable(true);
                        adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent startStore = null;
                                startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + AntPlusGenericControllableDevicePcc.getMissingDependencyPackageName()));
                                startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                Activity_GenericControllableDeviceSampler.this.startActivity(startStore);
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
                        Toast.makeText(Activity_GenericControllableDeviceSampler.this,
                            "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                            Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    default:
                        Toast.makeText(Activity_GenericControllableDeviceSampler.this, "Unrecognized result: " + requestAccessResult, Toast.LENGTH_SHORT).show();
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
            new IGenericCommandReceiver()
            {

                @Override
                public CommandStatus onNewGenericCommand(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final int serialNumber,
                    final int manufacturerID, final int sequenceNumber, final GenericCommandNumber commandNumber)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));
                            tv_sequenceNumber.setText(String.valueOf(sequenceNumber));
                            tv_remoteSerialNumber.setText(String.valueOf(serialNumber));
                            tv_remoteManufacturerID.setText(String.valueOf(manufacturerID));

                            GenericCommandNumber tempCommand = commandNumber;

                            if(tempCommand != GenericCommandNumber.UNRECOGNIZED)
                                tv_commandNumber.setText(tempCommand.toString());
                            else
                                tv_commandNumber.setText(commandNumber.toString());
                        }
                    });
                    return CommandStatus.PASS;
                }

            },
            DeviceNumber);
    }

    @Override
    protected void onDestroy()
    {
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
}
