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
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc.ICalorieDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc.IComputationTimestampReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc.IDataLatencyReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc.IDistanceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc.IInstantaneousCadenceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc.IInstantaneousSpeedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc.ISensorStatusReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc.IStrideCountReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc.SensorHealth;
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc.SensorLocation;
import com.dsi.ant.plugins.antplus.pcc.AntPlusStrideSdmPcc.SensorUseState;
import com.dsi.ant.plugins.antplus.pcc.defines.BatteryStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IManufacturerIdentificationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IManufacturerSpecificDataReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IProductInformationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

/**
 * Connects to Stride Sdm Plugin and receives data
 */
public class Activity_StrideSdmSampler extends Activity
{
    AntPlusStrideSdmPcc sdmPcc = null;
    PccReleaseHandle<AntPlusStrideSdmPcc> releaseHandle = null;

    TextView tv_status;

    TextView tv_estTimestamp;

    TextView tv_instantaneousSpeed;
    TextView tv_instantaneousCadence;
    TextView tv_ComputationTimestamp;

    TextView tv_cumulativeDistance;
    TextView tv_cumulativeStrides;

    TextView tv_cumulativeCalories;

    TextView tv_updateLatency;

    TextView tv_StatusFlagLocation;
    TextView tv_StatusFlagBattery;
    TextView tv_StatusFlagHealth;
    TextView tv_StatusFlagUseState;

    TextView tv_manufacturerID;
    TextView tv_serialNumber;
    TextView tv_modelNumber;

    TextView tv_hardwareRevision;
    TextView tv_mainSoftwareRevision;
    TextView tv_supplementalSoftwareRevision;

    TextView tv_manufacturerSpecificData;

    IPluginAccessResultReceiver<AntPlusStrideSdmPcc> mResultReceiver = new IPluginAccessResultReceiver<AntPlusStrideSdmPcc>()
    {
        @Override
        public void onResultReceived(AntPlusStrideSdmPcc result,
            RequestAccessResult resultCode, DeviceState initialDeviceState)
        {
            switch (resultCode)
            {
                case SUCCESS:
                    sdmPcc = result;
                    tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                    subscribeToEvents();
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    Toast.makeText(Activity_StrideSdmSampler.this, "Channel Not Available",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case ADAPTER_NOT_DETECTED:
                    Toast
                        .makeText(
                            Activity_StrideSdmSampler.this,
                            "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.",
                            Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case BAD_PARAMS:
                    // Note: Since we compose all the params ourself, we should
                    // never see this result
                    Toast.makeText(Activity_StrideSdmSampler.this, "Bad request parameters.",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case OTHER_FAILURE:
                    Toast.makeText(Activity_StrideSdmSampler.this,
                        "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT)
                        .show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case DEPENDENCY_NOT_INSTALLED:
                    tv_status.setText("Error. Do Menu->Reset.");
                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder(
                        Activity_StrideSdmSampler.this);
                    adlgBldr.setTitle("Missing Dependency");
                    adlgBldr
                        .setMessage("The required service\n\""
                            + AntPlusStrideSdmPcc.getMissingDependencyName()
                            + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
                    adlgBldr.setCancelable(true);
                    adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent startStore = null;
                            startStore = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id="
                                    + AntPlusStrideSdmPcc.getMissingDependencyPackageName()));
                            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            Activity_StrideSdmSampler.this.startActivity(startStore);
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
                    Toast.makeText(Activity_StrideSdmSampler.this,
                        "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                default:
                    Toast.makeText(Activity_StrideSdmSampler.this,
                        "Unrecognized result: " + resultCode, Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
            }
        }

        private void subscribeToEvents()
        {
            sdmPcc.subscribeInstantaneousSpeedEvent(new IInstantaneousSpeedReceiver()
            {
                @Override
                public void onNewInstantaneousSpeed(
                    final long estTimestamp, final EnumSet<EventFlag> eventFlags,
                    final BigDecimal instantaneousSpeed)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            tv_instantaneousSpeed.setText(String
                                .valueOf(instantaneousSpeed));
                        }
                    });
                }

            });

            sdmPcc.subscribeInstantaneousCadenceEvent(new IInstantaneousCadenceReceiver()
            {
                @Override
                public void onNewInstantaneousCadence(
                    final long estTimestamp, final EnumSet<EventFlag> eventFlags,
                    final BigDecimal instantaneousCadence)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            tv_instantaneousCadence.setText(String
                                .valueOf(instantaneousCadence));
                        }
                    });
                }
            });

            sdmPcc.subscribeDistanceEvent(new IDistanceReceiver()
            {
                @Override
                public void onNewDistance(final long estTimestamp,
                    final EnumSet<EventFlag> eventFlags,
                    final BigDecimal cumulativeDistance)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            tv_cumulativeDistance.setText(String
                                .valueOf(cumulativeDistance));
                        }
                    });
                }
            });

            sdmPcc.subscribeStrideCountEvent(new IStrideCountReceiver()
            {
                @Override
                public void onNewStrideCount(final long estTimestamp,
                    final EnumSet<EventFlag> eventFlags, final long cumulativeStrides)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            tv_cumulativeStrides.setText(String.valueOf(cumulativeStrides));
                        }
                    });
                }
            });

            sdmPcc.subscribeComputationTimestampEvent(new IComputationTimestampReceiver()
            {
                @Override
                public void onNewComputationTimestamp(final long estTimestamp,
                    final EnumSet<EventFlag> eventFlags,
                    final BigDecimal timestampOfLastComputation)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            tv_ComputationTimestamp.setText(String
                                .valueOf(timestampOfLastComputation));
                        }
                    });
                }
            });

            sdmPcc.subscribeDataLatencyEvent(new IDataLatencyReceiver()
            {
                @Override
                public void onNewDataLatency(final long estTimestamp,
                    final EnumSet<EventFlag> eventFlags, final BigDecimal updateLatency)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            tv_updateLatency.setText(String.valueOf(updateLatency));
                        }
                    });
                }
            });

            sdmPcc.subscribeSensorStatusEvent(new ISensorStatusReceiver()
            {
                @Override
                public void onNewSensorStatus(final long estTimestamp,
                    EnumSet<EventFlag> eventFlags,
                    final SensorLocation sensorLocation, final BatteryStatus batteryStatus,
                    final SensorHealth sensorHealth, final SensorUseState useState)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            tv_StatusFlagLocation.setText(sensorLocation.toString());
                            tv_StatusFlagBattery.setText(batteryStatus.toString());
                            tv_StatusFlagHealth.setText(sensorHealth.toString());
                            tv_StatusFlagUseState.setText(useState.toString());
                        }
                    });
                }
            });

            sdmPcc.subscribeCalorieDataEvent(new ICalorieDataReceiver()
            {
                @Override
                public void onNewCalorieData(
                    final long estTimestamp, final EnumSet<EventFlag> eventFlags,
                    final long cumulativeCalories)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            tv_cumulativeCalories.setText(String
                                .valueOf(cumulativeCalories));
                        }
                    });
                }
            });

            sdmPcc
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

            sdmPcc.subscribeProductInformationEvent(new IProductInformationReceiver()
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

            sdmPcc.subscribeManufacturerSpecificDataEvent(new IManufacturerSpecificDataReceiver()
            {
                @Override
                public void onNewManufacturerSpecificData(final long estTimestamp,
                        final EnumSet<EventFlag> eventFlags, final byte[] rawDataBytes) {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            StringBuffer hexString = new StringBuffer();
                            for (int i = 0; i < rawDataBytes.length; i++)
                            {
                                hexString
                                        .append("[")
                                        .append(String.format("%02X",
                                                rawDataBytes[i] & 0xFF)).append("]");
                            }
                            tv_manufacturerSpecificData.setText(hexString.toString());
                        }
                    });
                }
            });
        }
    };

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
                    tv_status.setText(sdmPcc.getDeviceName() + ": " + newDeviceState);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stride_sdm);

        tv_status = (TextView)findViewById(R.id.textView_Status);

        tv_estTimestamp = (TextView)findViewById(R.id.textView_EstTimestamp);

        tv_instantaneousSpeed = (TextView)findViewById(R.id.textView_InstantaneousSpeed);
        tv_instantaneousCadence = (TextView)findViewById(R.id.textView_InstantaneousCadence);
        tv_ComputationTimestamp = (TextView)findViewById(R.id.textView_ComputationTimestamp);

        tv_cumulativeDistance = (TextView)findViewById(R.id.textView_CumulativeDistance);
        tv_cumulativeStrides = (TextView)findViewById(R.id.textView_CumulativeStrides);

        tv_cumulativeCalories = (TextView)findViewById(R.id.textView_CumulativeCalories);

        tv_updateLatency = (TextView)findViewById(R.id.textView_UpdateLatency);

        tv_StatusFlagLocation = (TextView)findViewById(R.id.textView_StatusFlagLocation);
        tv_StatusFlagBattery = (TextView)findViewById(R.id.textView_StatusFlagBattery);
        tv_StatusFlagHealth = (TextView)findViewById(R.id.textView_StatusFlagHealth);
        tv_StatusFlagUseState = (TextView)findViewById(R.id.textView_StatusFlagUseState);

        tv_manufacturerID = (TextView)findViewById(R.id.textView_ManufacturerID);
        tv_serialNumber = (TextView)findViewById(R.id.textView_SerialNumber);

        tv_modelNumber = (TextView)findViewById(R.id.textView_ModelNumber);

        tv_hardwareRevision = (TextView)findViewById(R.id.textView_HardwareRevision);
        tv_mainSoftwareRevision = (TextView)findViewById(R.id.textView_MainSoftwareRevision);
        tv_supplementalSoftwareRevision = (TextView)findViewById(R.id.textView_SupplementalSoftwareRevision);

        tv_manufacturerSpecificData = (TextView)findViewById(R.id.textView_ManufacturerSpecificData);

        resetPcc();
    }

    private void resetPcc()
    {
        if(releaseHandle != null)
        {
            releaseHandle.close();
        }

        tv_status.setText("Connecting...");

        tv_estTimestamp.setText("---");

        tv_instantaneousSpeed.setText("---");
        tv_instantaneousCadence.setText("---");
        tv_ComputationTimestamp.setText("---");

        tv_cumulativeDistance.setText("---");
        tv_cumulativeStrides.setText("---");

        tv_cumulativeCalories.setText("---");

        tv_updateLatency.setText("---");

        tv_StatusFlagLocation.setText("---");
        tv_StatusFlagBattery.setText("---");
        tv_StatusFlagHealth.setText("---");
        tv_StatusFlagUseState.setText("---");

        tv_manufacturerID.setText("---");
        tv_serialNumber.setText("---");

        tv_modelNumber.setText("---");

        tv_hardwareRevision.setText("---");
        tv_mainSoftwareRevision.setText("---");
        tv_supplementalSoftwareRevision.setText("");

        Intent intent = getIntent();
        if (intent.hasExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT))
        {
            // device has already been selected through the multi-device search
            MultiDeviceSearchResult result = intent
                .getParcelableExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT);
            releaseHandle = AntPlusStrideSdmPcc.requestAccess(this, result.getAntDeviceNumber(), 0,
                mResultReceiver, mDeviceStateChangeReceiver);
        } else
        {
            // starts the plugins UI search
            releaseHandle = AntPlusStrideSdmPcc.requestAccess(this, this, mResultReceiver,
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
