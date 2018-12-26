/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2013
All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler;

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
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc.ICalculatedCadenceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc.IMotionAndCadenceDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc.IRawCadenceDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver;
import com.dsi.ant.plugins.antplus.pcc.defines.BatteryStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusBikeSpdCadCommonPcc.IBatteryStatusReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.ICumulativeOperatingTimeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.IManufacturerAndSerialReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusLegacyCommonPcc.IVersionAndModelReceiver;
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

import java.math.BigDecimal;
import java.util.EnumSet;

/**
 * Connects to Bike Cadence Plugin and display all the event data.
 */
public class Activity_BikeCadenceSampler extends Activity
{
    AntPlusBikeCadencePcc bcPcc = null;
    PccReleaseHandle<AntPlusBikeCadencePcc> bcReleaseHandle = null;
    AntPlusBikeSpeedDistancePcc bsPcc = null;
    PccReleaseHandle<AntPlusBikeSpeedDistancePcc> bsReleaseHandle = null;

    TextView tv_status;

    TextView tv_estTimestamp;

    TextView tv_calculatedCadence;
    TextView tv_cumulativeRevolutions;
    TextView tv_timestampOfLastEvent;

    TextView tv_isSpdAndCadCombo;
    TextView tv_calculatedSpeed;

    TextView tv_cumulativeOperatingTime;

    TextView tv_manufacturerID;
    TextView tv_serialNumber;

    TextView tv_hardwareVersion;
    TextView tv_softwareVersion;
    TextView tv_modelNumber;


    TextView textView_BatteryVoltage;
    TextView textView_BatteryStatus;

    TextView textView_IsPedallingStopped;





    IPluginAccessResultReceiver<AntPlusBikeCadencePcc> mResultReceiver = new IPluginAccessResultReceiver<AntPlusBikeCadencePcc>()
    {
        // Handle the result, connecting to events on success or reporting
        // failure to user.
        @Override
        public void onResultReceived(AntPlusBikeCadencePcc result,
            RequestAccessResult resultCode, DeviceState initialDeviceState)
        {
            switch (resultCode)
            {
                case SUCCESS:
                    bcPcc = result;
                    tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                    subscribeToEvents();
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    Toast.makeText(Activity_BikeCadenceSampler.this, "Channel Not Available",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case ADAPTER_NOT_DETECTED:
                    Toast
                        .makeText(
                            Activity_BikeCadenceSampler.this,
                            "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.",
                            Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case BAD_PARAMS:
                    // Note: Since we compose all the params ourself, we should
                    // never see this result
                    Toast.makeText(Activity_BikeCadenceSampler.this, "Bad request parameters.",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case OTHER_FAILURE:
                    Toast.makeText(Activity_BikeCadenceSampler.this,
                        "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT)
                        .show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case DEPENDENCY_NOT_INSTALLED:
                    tv_status.setText("Error. Do Menu->Reset.");
                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder(
                        Activity_BikeCadenceSampler.this);
                    adlgBldr.setTitle("Missing Dependency");
                    adlgBldr.setMessage("The required service\n\""
                        + AntPlusBikeCadencePcc.getMissingDependencyName()
                        + "\"\n was not found. You need to install the ANT+ Plugins service or"
                        + " you may need to update your existing version if you already have "
                        + "it. Do you want to launch the Play Store to get it?");
                    adlgBldr.setCancelable(true);
                    adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent startStore = null;
                            startStore = new Intent(Intent.ACTION_VIEW, Uri
                                .parse("market://details?id="
                                    + AntPlusBikeCadencePcc
                                        .getMissingDependencyPackageName()));
                            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            Activity_BikeCadenceSampler.this.startActivity(startStore);
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
                    Toast.makeText(Activity_BikeCadenceSampler.this,
                        "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                default:
                    Toast.makeText(Activity_BikeCadenceSampler.this,
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
            bcPcc.subscribeCalculatedCadenceEvent(new ICalculatedCadenceReceiver()
            {
                @Override
                public void onNewCalculatedCadence(final long estTimestamp,
                    final EnumSet<EventFlag> eventFlags, final BigDecimal calculatedCadence)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            tv_calculatedCadence.setText(String.valueOf(calculatedCadence));
                        }
                    });

                }
            });

            bcPcc.subscribeRawCadenceDataEvent(new IRawCadenceDataReceiver()
            {
                @Override
                public void onNewRawCadenceData(final long estTimestamp,
                    final EnumSet<EventFlag> eventFlags, final BigDecimal timestampOfLastEvent,
                    final long cumulativeRevolutions)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            tv_timestampOfLastEvent.setText(String.valueOf(timestampOfLastEvent));

                            tv_cumulativeRevolutions.setText(String.valueOf(cumulativeRevolutions));
                        }
                    });
                }
            });

            if (bcPcc.isSpeedAndCadenceCombinedSensor())
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_isSpdAndCadCombo.setText("Yes");
                        tv_cumulativeOperatingTime.setText("N/A");
                        tv_manufacturerID.setText("N/A");
                        tv_serialNumber.setText("N/A");
                        tv_hardwareVersion.setText("N/A");
                        tv_softwareVersion.setText("N/A");
                        tv_modelNumber.setText("N/A");

                        tv_calculatedSpeed.setText("...");
                        bsReleaseHandle = AntPlusBikeSpeedDistancePcc.requestAccess(
                            Activity_BikeCadenceSampler.this, bcPcc.getAntDeviceNumber(), 0, true,
                            new IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc>()
                            {
                                // Handle the result, connecting to events
                                // on success or reporting failure to user.
                                @Override
                                public void onResultReceived(
                                    AntPlusBikeSpeedDistancePcc result,
                                    RequestAccessResult resultCode,
                                    DeviceState initialDeviceStateCode)
                                {
                                    switch (resultCode)
                                    {
                                        case SUCCESS:
                                            bsPcc = result;
                                            bsPcc
                                                .subscribeCalculatedSpeedEvent(new CalculatedSpeedReceiver(
                                                    new BigDecimal(2.095))
                                                {
                                                    @Override
                                                    public void onNewCalculatedSpeed(
                                                        long estTimestamp,
                                                        EnumSet<EventFlag> eventFlags,
                                                        final BigDecimal calculatedSpeed)
                                                    {
                                                        runOnUiThread(new Runnable()
                                                        {
                                                            @Override
                                                            public void run()
                                                            {
                                                                tv_calculatedSpeed.setText(String
                                                                    .valueOf(calculatedSpeed));
                                                            }
                                                        });
                                                    }
                                                });
                                            break;
                                        case CHANNEL_NOT_AVAILABLE:
                                            tv_calculatedSpeed.setText("CHANNEL NOT AVAILABLE");
                                            break;
                                        case BAD_PARAMS:
                                            tv_calculatedSpeed.setText("BAD_PARAMS");
                                            break;
                                        case OTHER_FAILURE:
                                            tv_calculatedSpeed.setText("OTHER FAILURE");
                                            break;
                                        case DEPENDENCY_NOT_INSTALLED:
                                            tv_calculatedSpeed
                                                .setText("DEPENDENCY NOT INSTALLED");
                                            break;
                                        default:
                                            tv_calculatedSpeed.setText("UNRECOGNIZED ERROR: "
                                                + resultCode);
                                            break;
                                    }
                                }
                            },
                            // Receives state changes and shows it on the
                            // status display line
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
                                            if (newDeviceState != DeviceState.TRACKING)
                                                tv_calculatedSpeed.setText(newDeviceState
                                                    .toString());
                                            if (newDeviceState == DeviceState.DEAD)
                                                bsPcc = null;
                                        }
                                    });

                                }
                            });
                    }
                });
            }
            else
            {
                // Subscribe to the events available in the pure cadence profile
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_isSpdAndCadCombo.setText("No");
                        tv_calculatedSpeed.setText("N/A");
                    }
                });

                bcPcc.subscribeCumulativeOperatingTimeEvent(new ICumulativeOperatingTimeReceiver()
                {
                    @Override
                    public void onNewCumulativeOperatingTime(final long estTimestamp,
                        final EnumSet<EventFlag> eventFlags, final long cumulativeOperatingTime)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                tv_cumulativeOperatingTime.setText(String
                                    .valueOf(cumulativeOperatingTime));
                            }
                        });
                    }
                });

                bcPcc.subscribeManufacturerAndSerialEvent(new IManufacturerAndSerialReceiver()
                {
                    @Override
                    public void onNewManufacturerAndSerial(final long estTimestamp,
                        final EnumSet<EventFlag> eventFlags, final int manufacturerID,
                        final int serialNumber)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                tv_manufacturerID.setText(String.valueOf(manufacturerID));
                                tv_serialNumber.setText(String.valueOf(serialNumber));
                            }
                        });
                    }
                });

                bcPcc.subscribeVersionAndModelEvent(new IVersionAndModelReceiver()
                {
                    @Override
                    public void onNewVersionAndModel(final long estTimestamp,
                        final EnumSet<EventFlag> eventFlags, final int hardwareVersion,
                        final int softwareVersion, final int modelNumber)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                tv_hardwareVersion.setText(String.valueOf(hardwareVersion));
                                tv_softwareVersion.setText(String.valueOf(softwareVersion));
                                tv_modelNumber.setText(String.valueOf(modelNumber));
                                }
                            });
                        }
                    });

                    bcPcc.subscribeBatteryStatusEvent(new IBatteryStatusReceiver()
                    {
                        @Override
                        public void onNewBatteryStatus(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                final BigDecimal batteryVoltage, final BatteryStatus batteryStatus)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                    textView_BatteryVoltage.setText(batteryVoltage.intValue() != -1 ? String.valueOf(batteryVoltage) + "V" : "Invalid");
                                    textView_BatteryStatus.setText(batteryStatus.toString());
                                }
                            });
                        }
                    });

                    bcPcc.subscribeMotionAndCadenceDataEvent(new IMotionAndCadenceDataReceiver()
                    {
                        @Override
                        public void onNewMotionAndCadenceData(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                final boolean isPedallingStopped)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                    textView_IsPedallingStopped.setText(String.valueOf(isPedallingStopped));
                            }
                        });
                    }
                });
            }
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
                    tv_status.setText(bcPcc.getDeviceName() + ": " + newDeviceState.toString());
                    if (newDeviceState == DeviceState.DEAD)
                        bcPcc = null;
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_cadence);

        tv_status = (TextView)findViewById(R.id.textView_Status);

        tv_estTimestamp = (TextView)findViewById(R.id.textView_EstTimestamp);

        tv_calculatedCadence = (TextView)findViewById(R.id.textView_CaluclatedCadence);
        tv_cumulativeRevolutions = (TextView)findViewById(R.id.textView_CumulativeRevolutions);
        tv_timestampOfLastEvent = (TextView)findViewById(R.id.textView_TimestampOfLastEvent);

        tv_isSpdAndCadCombo = (TextView)findViewById(R.id.textView_IsCombinedSensor);
        tv_calculatedSpeed = (TextView)findViewById(R.id.textView_CalculatedSpeed);

        tv_cumulativeOperatingTime = (TextView)findViewById(R.id.textView_CumulativeOperatingTime);

        tv_manufacturerID = (TextView)findViewById(R.id.textView_ManufacturerID);
        tv_serialNumber = (TextView)findViewById(R.id.textView_SerialNumber);

        tv_hardwareVersion = (TextView)findViewById(R.id.textView_HardwareVersion);
        tv_softwareVersion = (TextView)findViewById(R.id.textView_SoftwareVersion);
        tv_modelNumber = (TextView)findViewById(R.id.textView_ModelNumber);

        textView_BatteryVoltage = (TextView)findViewById(R.id.textView_BatteryVoltage);
        textView_BatteryStatus = (TextView)findViewById(R.id.textView_BatteryStatus);

        textView_IsPedallingStopped = (TextView)findViewById(R.id.textView_IsPedallingStopped);


        resetPcc();
    }

    /**
     * Resets the PCC connection to request access again and clears any existing display data.
     */
    private void resetPcc()
    {
        //Release the old access if it exists
        if(bcReleaseHandle != null)
        {
            bcReleaseHandle.close();
        }
        if(bsReleaseHandle != null)
        {
            bsReleaseHandle.close();
        }


        //Reset the text display
        tv_status.setText("Connecting...");

        tv_estTimestamp.setText("---");

        tv_calculatedCadence.setText("---");
        tv_cumulativeRevolutions.setText("---");
        tv_timestampOfLastEvent.setText("---");

        tv_isSpdAndCadCombo.setText("---");
        tv_calculatedSpeed.setText("---");

        tv_cumulativeOperatingTime.setText("---");

        tv_manufacturerID.setText("---");
        tv_serialNumber.setText("---");

        tv_hardwareVersion.setText("---");
        tv_softwareVersion.setText("---");
        tv_modelNumber.setText("---");

        textView_BatteryVoltage.setText("---");
        textView_BatteryStatus.setText("---");

        textView_IsPedallingStopped.setText("---");

        Intent intent = getIntent();
        if (intent.hasExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT))
        {
            // device has already been selected through the multi-device search
            MultiDeviceSearchResult result = intent
                .getParcelableExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT);
            boolean isBSC = result.getAntDeviceType().equals(DeviceType.BIKE_SPDCAD);
            bcReleaseHandle = AntPlusBikeCadencePcc.requestAccess(this,
                result.getAntDeviceNumber(), 0, isBSC, mResultReceiver, mDeviceStateChangeReceiver);
        } else
        {
            // starts the plugins UI search
            bcReleaseHandle = AntPlusBikeCadencePcc.requestAccess(this, this, mResultReceiver,
                mDeviceStateChangeReceiver);
            // AntPlusBikeCadencePcc.requestAccess(this, 0, 0, false,
            // //Asynchronous request mode
        }
    }

    @Override
    protected void onDestroy()
    {
        bcReleaseHandle.close();
        if(bsReleaseHandle != null)
        {
            bsReleaseHandle.close();
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
