/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2013
All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler;

import java.math.BigDecimal;
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

import com.dsi.ant.antplus.pluginsampler.multidevicesearch.Activity_MultiDeviceSearchSampler;
import com.dsi.ant.plugins.antplus.pcc.AntPlusEnvironmentPcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusEnvironmentPcc.ITemperatureDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IManufacturerIdentificationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IProductInformationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

/**
 * Connects to Environment Plugin and display all the event data.
 */
public class Activity_EnvironmentSampler extends Activity
{
    AntPlusEnvironmentPcc envPcc = null;
    PccReleaseHandle<AntPlusEnvironmentPcc> releaseHandle = null;

    TextView tv_status;

    TextView tv_estTimestamp;

    TextView tv_currentTemperature;
    TextView tv_eventCount;
    TextView tv_lowLast24Hours;
    TextView tv_highLast24Hours;

    TextView tv_hardwareRevision;
    TextView tv_manufacturerID;
    TextView tv_modelNumber;

    TextView tv_mainSoftwareRevision;
    TextView tv_supplementalSoftwareRevision;
    TextView tv_serialNumber;

    IPluginAccessResultReceiver<AntPlusEnvironmentPcc> mResultReceiver = new IPluginAccessResultReceiver<AntPlusEnvironmentPcc>()
    {
        // Handle the result, connecting to events on success or reporting
        // failure to user.
        @Override
        public void onResultReceived(AntPlusEnvironmentPcc result,
            RequestAccessResult resultCode, DeviceState initialDeviceState)
        {
            switch (resultCode)
            {
                case SUCCESS:
                    envPcc = result;
                    tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                    subscribeToEvents();
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    Toast.makeText(Activity_EnvironmentSampler.this, "Channel Not Available",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case ADAPTER_NOT_DETECTED:
                    Toast
                        .makeText(
                            Activity_EnvironmentSampler.this,
                            "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.",
                            Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case BAD_PARAMS:
                    // Note: Since we compose all the params ourself, we should
                    // never see this result
                    Toast.makeText(Activity_EnvironmentSampler.this, "Bad request parameters.",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case OTHER_FAILURE:
                    Toast.makeText(Activity_EnvironmentSampler.this,
                        "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT)
                        .show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case DEPENDENCY_NOT_INSTALLED:
                    tv_status.setText("Error. Do Menu->Reset.");
                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder(
                        Activity_EnvironmentSampler.this);
                    adlgBldr.setTitle("Missing Dependency");
                    adlgBldr
                        .setMessage("The required service\n\""
                            + AntPlusEnvironmentPcc.getMissingDependencyName()
                            + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
                    adlgBldr.setCancelable(true);
                    adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent startStore = null;
                            startStore = new Intent(Intent.ACTION_VIEW, Uri
                                .parse("market://details?id="
                                    + AntPlusEnvironmentPcc
                                        .getMissingDependencyPackageName()));
                            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            Activity_EnvironmentSampler.this.startActivity(startStore);
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
                    Toast.makeText(Activity_EnvironmentSampler.this,
                        "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                default:
                    Toast.makeText(Activity_EnvironmentSampler.this,
                        "Unrecognized result: " + resultCode, Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
            }
        }

        /**
         * Subscribe to all the heart rate events, connecting them to display
         * their data.
         */
        private void subscribeToEvents()
        {
            envPcc.subscribeTemperatureDataEvent(new ITemperatureDataReceiver()
            {
                @Override
                public void onNewTemperatureData(final long estTimestamp,
                    final EnumSet<EventFlag> eventFlags, final BigDecimal currentTemperature,
                    final long eventCount, final BigDecimal lowLast24Hours,
                    final BigDecimal highLast24Hours)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            tv_currentTemperature.setText(String.valueOf(currentTemperature));
                            tv_eventCount.setText(String.valueOf(eventCount));
                            tv_lowLast24Hours.setText(String.valueOf(lowLast24Hours));
                            tv_highLast24Hours.setText(String.valueOf(highLast24Hours));
                        }
                    });
                }
            });

            envPcc
                .subscribeManufacturerIdentificationEvent(new IManufacturerIdentificationReceiver()
                {
                    @Override
                    public void onNewManufacturerIdentification(final long estTimestamp,
                        final EnumSet<EventFlag> eventFlags, final int hardwareRevision,
                        final int manufacturerID, final int modelNumber)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                tv_hardwareRevision.setText(String.valueOf(hardwareRevision));
                                tv_manufacturerID.setText(String.valueOf(manufacturerID));
                                tv_modelNumber.setText(String.valueOf(modelNumber));
                            }
                        });
                    }
                });

            envPcc.subscribeProductInformationEvent(new IProductInformationReceiver()
            {
                @Override
                public void onNewProductInformation(final long estTimestamp,
                    final EnumSet<EventFlag> eventFlags, final int mainSoftwareRevision,
                    final int supplementalSoftwareRevision, final long serialNumber)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            tv_mainSoftwareRevision.setText(String
                                .valueOf(mainSoftwareRevision));

                            if (supplementalSoftwareRevision == -2)
                                // Plugin Service installed does not support supplemental revision
                                tv_supplementalSoftwareRevision.setText("?");
                            else if (supplementalSoftwareRevision == 0xFF)
                                // Invalid supplemental revision
                                tv_supplementalSoftwareRevision.setText("");
                            else
                                // Valid supplemental revision
                                tv_supplementalSoftwareRevision.setText(", " + String
                                    .valueOf(supplementalSoftwareRevision));

                            tv_serialNumber.setText(String.valueOf(serialNumber));
                        }
                    });
                }
            });
        }
    };

    // Receives state changes and shows it on the status display line
    IDeviceStateChangeReceiver mDeviceStateChangeReceiver = new IDeviceStateChangeReceiver()
    {
        @Override
        public void onDeviceStateChange(final DeviceState newDeviceState)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    tv_status.setText(envPcc.getDeviceName() + ": " + newDeviceState);
                    if (newDeviceState == DeviceState.DEAD)
                        envPcc = null;
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment);

        tv_status = (TextView)findViewById(R.id.textView_Status);

        tv_estTimestamp = (TextView)findViewById(R.id.textView_EstTimestamp);

        tv_currentTemperature = (TextView)findViewById(R.id.textView_CurrentTemperature);
        tv_eventCount = (TextView)findViewById(R.id.textView_EventCount);
        tv_lowLast24Hours = (TextView)findViewById(R.id.textView_LowLast24Hours);
        tv_highLast24Hours = (TextView)findViewById(R.id.textView_HighLast24Hours);

        tv_hardwareRevision = (TextView)findViewById(R.id.textView_HardwareRevision);
        tv_manufacturerID = (TextView)findViewById(R.id.textView_ManufacturerID);
        tv_modelNumber = (TextView)findViewById(R.id.textView_ModelNumber);

        tv_mainSoftwareRevision = (TextView)findViewById(R.id.textView_MainSoftwareRevision);
        tv_supplementalSoftwareRevision = (TextView)findViewById(R.id.textView_SupplementalSoftwareRevision);
        tv_serialNumber = (TextView)findViewById(R.id.textView_SerialNumber);

        resetPcc();
    }

    /**
     * Resets the PCC connection to request access again and clears any existing display data.
     */
    private void resetPcc()
    {
        //Release the old access if it exists
        if(releaseHandle != null)
        {
            releaseHandle.close();
        }


        //Reset the text display
        tv_status.setText("Connecting...");

        tv_estTimestamp.setText("---");

        tv_currentTemperature.setText("---");
        tv_eventCount.setText("---");
        tv_lowLast24Hours.setText("---");
        tv_highLast24Hours.setText("---");

        tv_hardwareRevision.setText("---");
        tv_manufacturerID.setText("---");
        tv_modelNumber.setText("---");

        tv_mainSoftwareRevision.setText("---");
        tv_supplementalSoftwareRevision.setText("");
        tv_serialNumber.setText("---");

        Intent intent = getIntent();
        if (intent.hasExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT))
        {
            // device has already been selected through the multi-device search
            MultiDeviceSearchResult result = intent
                .getParcelableExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT);
            releaseHandle = AntPlusEnvironmentPcc.requestAccess(this, result.getAntDeviceNumber(),
                0, mResultReceiver, mDeviceStateChangeReceiver);
        } else
        {
            // starts the plugins UI search
            releaseHandle = AntPlusEnvironmentPcc.requestAccess(this, this, mResultReceiver,
                mDeviceStateChangeReceiver);
        }
    }

    @Override
    protected void onDestroy()
    {
        releaseHandle.close();
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
