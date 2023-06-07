/*
 * This software is subject to the license described in the License.txt file
 * included with this software distribution. You may not use this file except in compliance
 * with this license.
 *
 * Copyright (c) Garmin Canada Inc. 2019
 * All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeCadencePcc.ICalculatedCadenceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedAccumulatedDistanceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.CalculatedSpeedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.IMotionAndSpeedDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBikeSpeedDistancePcc.IRawSpeedAndDistanceDataReceiver;
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

/**
 * Connects to Bike Speed Plugin and display all the event data.
 */
public class Activity_BikeSpeedDistanceSampler extends Activity
{
    AntPlusBikeSpeedDistancePcc bsdPcc = null;
    PccReleaseHandle<AntPlusBikeSpeedDistancePcc> bsdReleaseHandle = null;
    AntPlusBikeCadencePcc bcPcc = null;
    PccReleaseHandle<AntPlusBikeCadencePcc> bcReleaseHandle = null;

    TextView tv_status;

    TextView tv_estTimestamp;

    TextView tv_calculatedSpeed;
    TextView tv_calculatedAccumulatedDistance;

    TextView tv_cumulativeRevolutions;
    TextView tv_timestampOfLastEvent;

    TextView tv_isSpdAndCadCombo;
    TextView tv_calculatedCadence;

    TextView tv_cumulativeOperatingTime;

    TextView tv_manufacturerID;
    TextView tv_serialNumber;

    TextView tv_hardwareVersion;
    TextView tv_softwareVersion;
    TextView tv_modelNumber;


    TextView textView_BatteryVoltage;
    TextView textView_BatteryStatus;

    TextView textView_IsStopped;



    IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc> mResultReceiver = new IPluginAccessResultReceiver<AntPlusBikeSpeedDistancePcc>()
    {
        // Handle the result, connecting to events on success or reporting
        // failure to user.
        @Override
        public void onResultReceived(AntPlusBikeSpeedDistancePcc result,
            RequestAccessResult resultCode, DeviceState initialDeviceState)
        {
            switch (resultCode)
            {
                case SUCCESS:
                    bsdPcc = result;
                    tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                    subscribeToEvents();
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    Toast.makeText(Activity_BikeSpeedDistanceSampler.this, "Channel Not Available",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case ADAPTER_NOT_DETECTED:
                    Toast
                        .makeText(
                            Activity_BikeSpeedDistanceSampler.this,
                            "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.",
                            Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case BAD_PARAMS:
                    // Note: Since we compose all the params ourself, we should
                    // never see this result
                    Toast.makeText(Activity_BikeSpeedDistanceSampler.this,
                        "Bad request parameters.", Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case OTHER_FAILURE:
                    Toast.makeText(Activity_BikeSpeedDistanceSampler.this,
                        "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT)
                        .show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case DEPENDENCY_NOT_INSTALLED:
                    tv_status.setText("Error. Do Menu->Reset.");
                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder(
                        Activity_BikeSpeedDistanceSampler.this);
                    adlgBldr.setTitle("Missing Dependency");
                    adlgBldr
                        .setMessage("The required service\n\""
                            + AntPlusBikeSpeedDistancePcc.getMissingDependencyName()
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
                                    + AntPlusBikeSpeedDistancePcc
                                        .getMissingDependencyPackageName()));
                            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            Activity_BikeSpeedDistanceSampler.this.startActivity(startStore);
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
                    Toast.makeText(Activity_BikeSpeedDistanceSampler.this,
                        "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                default:
                    Toast.makeText(Activity_BikeSpeedDistanceSampler.this,
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
            // 2.095m circumference = an average 700cx23mm road tire
            bsdPcc.subscribeCalculatedSpeedEvent(new CalculatedSpeedReceiver(new BigDecimal(2.095))
            {
                @Override
                public void onNewCalculatedSpeed(final long estTimestamp,
                    final EnumSet<EventFlag> eventFlags, final BigDecimal calculatedSpeed)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            tv_calculatedSpeed.setText(String.valueOf(calculatedSpeed));
                        }
                    });
                }
            });

            bsdPcc
                .subscribeCalculatedAccumulatedDistanceEvent(new CalculatedAccumulatedDistanceReceiver(
                    new BigDecimal(2.095)) // 2.095m circumference = an average
                                           // 700cx23mm road tire
                {

                    @Override
                    public void onNewCalculatedAccumulatedDistance(final long estTimestamp,
                        final EnumSet<EventFlag> eventFlags,
                        final BigDecimal calculatedAccumulatedDistance)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                tv_calculatedAccumulatedDistance.setText(String
                                    .valueOf(calculatedAccumulatedDistance.setScale(3,
                                        RoundingMode.HALF_UP)));
                            }
                        });
                    }
                });

            bsdPcc.subscribeRawSpeedAndDistanceDataEvent(new IRawSpeedAndDistanceDataReceiver()
            {
                @Override
                public void onNewRawSpeedAndDistanceData(final long estTimestamp,
                    final EnumSet<EventFlag> eventFlags,
                    final BigDecimal timestampOfLastEvent, final long cumulativeRevolutions)
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

            if (bsdPcc.isSpeedAndCadenceCombinedSensor())
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

                        tv_calculatedCadence.setText("...");

                        bcReleaseHandle = AntPlusBikeCadencePcc.requestAccess(
                            Activity_BikeSpeedDistanceSampler.this,
                            bsdPcc.getAntDeviceNumber(), 0, true,
                            new IPluginAccessResultReceiver<AntPlusBikeCadencePcc>()
                            {
                                // Handle the result, connecting to events
                                // on success or reporting failure to user.
                                @Override
                                public void onResultReceived(AntPlusBikeCadencePcc result,
                                    RequestAccessResult resultCode,
                                    DeviceState initialDeviceStateCode)
                                {
                                    switch (resultCode)
                                    {
                                        case SUCCESS:
                                            bcPcc = result;
                                            bcPcc
                                                .subscribeCalculatedCadenceEvent(new ICalculatedCadenceReceiver()
                                                {
                                                    @Override
                                                    public void onNewCalculatedCadence(
                                                        long estTimestamp,
                                                        EnumSet<EventFlag> eventFlags,
                                                        final BigDecimal calculatedCadence)
                                                    {
                                                        runOnUiThread(new Runnable()
                                                        {
                                                            @Override
                                                            public void run()
                                                            {
                                                                tv_calculatedCadence.setText(String
                                                                    .valueOf(calculatedCadence));
                                                            }
                                                        });
                                                    }
                                                });
                                            break;
                                        case CHANNEL_NOT_AVAILABLE:
                                            tv_calculatedCadence
                                                .setText("CHANNEL NOT AVAILABLE");
                                            break;
                                        case BAD_PARAMS:
                                            tv_calculatedCadence.setText("BAD_PARAMS");
                                            break;
                                        case OTHER_FAILURE:
                                            tv_calculatedCadence.setText("OTHER FAILURE");
                                            break;
                                        case DEPENDENCY_NOT_INSTALLED:
                                            tv_calculatedCadence
                                                .setText("DEPENDENCY NOT INSTALLED");
                                            break;
                                        default:
                                            tv_calculatedCadence.setText("UNRECOGNIZED ERROR: "
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
                                                tv_calculatedCadence.setText(newDeviceState
                                                    .toString());
                                            if (newDeviceState == DeviceState.DEAD)
                                                bcPcc = null;
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
                        tv_calculatedCadence.setText("N/A");
                    }
                });

                bsdPcc.subscribeCumulativeOperatingTimeEvent(new ICumulativeOperatingTimeReceiver()
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

                bsdPcc.subscribeManufacturerAndSerialEvent(new IManufacturerAndSerialReceiver()
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

                bsdPcc.subscribeVersionAndModelEvent(new IVersionAndModelReceiver()
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

                    bsdPcc.subscribeBatteryStatusEvent(new IBatteryStatusReceiver()
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

                    bsdPcc.subscribeMotionAndSpeedDataEvent(new IMotionAndSpeedDataReceiver()
                    {
                        @Override
                        public void onNewMotionAndSpeedData(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                final boolean isStopped)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                    textView_IsStopped.setText(String.valueOf(isStopped));
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
                    tv_status.setText(bsdPcc.getDeviceName() + ": " + newDeviceState);
                    if (newDeviceState == DeviceState.DEAD)
                        bsdPcc = null;
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_speeddistance);

        tv_status = (TextView)findViewById(R.id.textView_Status);

        tv_estTimestamp = (TextView)findViewById(R.id.textView_EstTimestamp);

        tv_calculatedSpeed = (TextView)findViewById(R.id.textView_CalculatedSpeed);
        tv_calculatedAccumulatedDistance = (TextView)findViewById(R.id.textView_CalculatedAccumulatedDistance);
        tv_cumulativeRevolutions = (TextView)findViewById(R.id.textView_CumulativeRevolutions);
        tv_timestampOfLastEvent = (TextView)findViewById(R.id.textView_TimestampOfLastEvent);

        tv_isSpdAndCadCombo = (TextView)findViewById(R.id.textView_IsCombinedSensor);
        tv_calculatedCadence = (TextView)findViewById(R.id.textView_CalculatedCadence);

        tv_cumulativeOperatingTime = (TextView)findViewById(R.id.textView_CumulativeOperatingTime);

        tv_manufacturerID = (TextView)findViewById(R.id.textView_ManufacturerID);
        tv_serialNumber = (TextView)findViewById(R.id.textView_SerialNumber);

        tv_hardwareVersion = (TextView)findViewById(R.id.textView_HardwareVersion);
        tv_softwareVersion = (TextView)findViewById(R.id.textView_SoftwareVersion);
        tv_modelNumber = (TextView)findViewById(R.id.textView_ModelNumber);

        textView_BatteryVoltage = (TextView)findViewById(R.id.textView_BatteryVoltage);
        textView_BatteryStatus = (TextView)findViewById(R.id.textView_BatteryStatus);

        textView_IsStopped = (TextView)findViewById(R.id.textView_IsStopped);

        resetPcc();
    }

    /**
     * Resets the PCC connection to request access again and clears any existing display data.
     */
    private void resetPcc()
    {
        //Release the old access if it exists
        if(bsdReleaseHandle != null)
        {
            bsdReleaseHandle.close();
        }
        if(bcReleaseHandle != null)
        {
            bcReleaseHandle.close();
        }


        //Reset the text display
        tv_status.setText("Connecting...");

        tv_estTimestamp.setText("---");

        tv_calculatedSpeed.setText("---");
        tv_calculatedAccumulatedDistance.setText("---");
        tv_cumulativeRevolutions.setText("---");
        tv_timestampOfLastEvent.setText("---");

        tv_isSpdAndCadCombo.setText("---");
        tv_calculatedCadence.setText("---");

        tv_cumulativeOperatingTime.setText("---");

        tv_manufacturerID.setText("---");
        tv_serialNumber.setText("---");

        tv_hardwareVersion.setText("---");
        tv_softwareVersion.setText("---");
        tv_modelNumber.setText("---");

        textView_BatteryVoltage.setText("---");
        textView_BatteryStatus.setText("---");

        textView_IsStopped.setText("---");

        Intent intent = getIntent();
        if (intent.hasExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT))
        {
            // device has already been selected through the multi-device search
            MultiDeviceSearchResult result = intent
                .getParcelableExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT);
            boolean isBSC = result.getAntDeviceType().equals(DeviceType.BIKE_SPDCAD);
            bsdReleaseHandle = AntPlusBikeSpeedDistancePcc.requestAccess(this,
                result.getAntDeviceNumber(), 0, isBSC, mResultReceiver, mDeviceStateChangeReceiver);
        } else
        {
            // starts the plugins UI search
            bsdReleaseHandle = AntPlusBikeSpeedDistancePcc.requestAccess(this, this,
                mResultReceiver, mDeviceStateChangeReceiver);
        }
    }

    @Override
    protected void onDestroy()
    {
        bsdReleaseHandle.close();
        if(bcReleaseHandle != null)
        {
            bcReleaseHandle.close();
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
