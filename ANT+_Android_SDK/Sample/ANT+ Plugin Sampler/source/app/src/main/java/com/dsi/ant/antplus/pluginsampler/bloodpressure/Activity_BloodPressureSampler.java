/*
 * This software is subject to the license described in the License.txt file
 * included with this software distribution. You may not use this file except in compliance
 * with this license.
 *
 * Copyright (c) Garmin Canada Inc. 2019
 * All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.bloodpressure;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.Dialog_ProgressWaiter;
import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.antplus.pluginsampler.multidevicesearch.Activity_MultiDeviceSearchSampler;
import com.dsi.ant.plugins.antplus.common.AntFsCommon.IAntFsProgressUpdateReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBloodPressurePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBloodPressurePcc.BloodPressureMeasurement;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBloodPressurePcc.DownloadMeasurementsStatusCode;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBloodPressurePcc.IDownloadMeasurementsStatusReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBloodPressurePcc.IMeasurementDownloadedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusBloodPressurePcc.IResetDataAndSetTimeFinishedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusEnvironmentPcc;
import com.dsi.ant.plugins.antplus.pcc.defines.AntFsRequestStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.AntFsState;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

/**
 * Manages the blood pressure display sample.
 */
public class Activity_BloodPressureSampler extends FragmentActivity
{
    AntPlusBloodPressurePcc bpPcc;
    PccReleaseHandle<AntPlusBloodPressurePcc> releaseHandle = null;
    ArrayList<Closeable> layoutControllerList;

    Button button_getAntFsMfgID;
    Button button_requestResetData;
    CheckBox checkBox_setTimeOnReset;
    Button button_requestDownloadAllHistory;
    CheckBox checkBox_downloadNewOnly;
    CheckBox checkBox_enableMonitoring;
    Button button_stopDataMonitor;
    LinearLayout linearLayout_FitDataView;

    ProgressDialog antFsProgressDialog;

    TextView tv_status;
    TextView tv_mfgID;

    IPluginAccessResultReceiver<AntPlusBloodPressurePcc> mResultReceiver = new IPluginAccessResultReceiver<AntPlusBloodPressurePcc>()
    {
        // Handle the result, connecting to events on success or reporting
        // failure to user.
        @Override
        public void onResultReceived(AntPlusBloodPressurePcc result,
            RequestAccessResult resultCode, DeviceState initialDeviceState)
        {
            switch (resultCode)
            {
                case SUCCESS:
                    bpPcc = result;
                    setRequestUiEnabled(true);
                    tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    Toast.makeText(Activity_BloodPressureSampler.this, "Channel Not Available",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case ADAPTER_NOT_DETECTED:
                    Toast
                        .makeText(
                            Activity_BloodPressureSampler.this,
                            "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.",
                            Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case BAD_PARAMS:
                    // Note: Since we compose all the params ourself, we should
                    // never see this result
                    Toast.makeText(Activity_BloodPressureSampler.this, "Bad request parameters.",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case OTHER_FAILURE:
                    Toast.makeText(Activity_BloodPressureSampler.this,
                        "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT)
                        .show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case DEPENDENCY_NOT_INSTALLED:
                    tv_status.setText("Error. Do Menu->Reset.");
                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder(
                        Activity_BloodPressureSampler.this);
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

                            Activity_BloodPressureSampler.this.startActivity(startStore);
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
                    // This flag indicates that an unrecognized value was sent
                    // by the service, an upgrade of your PCC may be required to
                    // handle this new value.
                    Toast.makeText(Activity_BloodPressureSampler.this,
                        "Failed: UNRECOGNIZED. PluginLib or Plugin Service Upgrade Required?",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                default:
                    Toast.makeText(Activity_BloodPressureSampler.this,
                        "Unrecognized result: " + resultCode, Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
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
                    tv_status.setText(bpPcc.getDeviceName() + ": " + newDeviceState);
                    if (newDeviceState == DeviceState.DEAD)
                    {
                        if (antFsProgressDialog != null)
                            antFsProgressDialog.dismiss();

                        bpPcc = null;
                        setRequestUiEnabled(false);
                        button_stopDataMonitor.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodpressure);

        layoutControllerList = new ArrayList<Closeable>();

        button_getAntFsMfgID = (Button) findViewById(R.id.button_getMfgId);
        button_requestResetData = (Button) findViewById(R.id.button_requestResetDataAndSetTime);
        checkBox_setTimeOnReset = (CheckBox)findViewById(R.id.checkbox_setTime);
        button_requestDownloadAllHistory = (Button) findViewById(R.id.button_requestDownloadAllHistory);
        checkBox_downloadNewOnly = (CheckBox)findViewById(R.id.checkbox_newdataonly);
        checkBox_enableMonitoring = (CheckBox)findViewById(R.id.checkbox_monitor);
        button_stopDataMonitor = (Button) findViewById(R.id.button_StopBloodPressureDataMonitor);
        linearLayout_FitDataView = (LinearLayout) findViewById(R.id.linearLayout_BloodPressureCards);

        tv_status = (TextView)findViewById(R.id.textView_Status);
        tv_mfgID = (TextView) findViewById(R.id.textView_AntFsMfgId);

        button_stopDataMonitor.setOnClickListener(
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if(bpPcc == null)
                        return;

                    bpPcc.cancelDownloadMeasurementsMonitor();
                    button_stopDataMonitor.setVisibility(View.GONE);
                }
            });

        button_requestDownloadAllHistory.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(bpPcc == null)
                        return;

                    antFsProgressDialog = new ProgressDialog(Activity_BloodPressureSampler.this);
                    antFsProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    antFsProgressDialog.setMessage("Sending Request...");
                    antFsProgressDialog.setCancelable(false);
                    antFsProgressDialog.setIndeterminate(false);

                    boolean onlyNew = checkBox_downloadNewOnly.isChecked();
                    boolean monitor = checkBox_enableMonitoring.isChecked();

                    boolean submitted = bpPcc.requestDownloadMeasurements(onlyNew, monitor,
                            new IDownloadMeasurementsStatusReceiver()
                            {
                                @Override
                                public void onDownloadMeasurementsStatus(final DownloadMeasurementsStatusCode statusCode,
                                        final AntFsRequestStatus finishedCode)
                                {
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            handleDownloadMeasurementsState(statusCode, finishedCode);
                                        }
                                    });
                                }

                                private void handleDownloadMeasurementsState(DownloadMeasurementsStatusCode statusCode,
                                        AntFsRequestStatus finishedCode)
                                {
                                    switch(statusCode)
                                    {
                                        case FINISHED:
                                            handleFinishedCode(finishedCode);
                                            setRequestUiEnabled(true);
                                            break;
                                        case PROGRESS_MONITORING:
                                            Toast.makeText(Activity_BloodPressureSampler.this,
                                                    "Monitoring for new downloads.",
                                                    Toast.LENGTH_SHORT).show();
                                            antFsProgressDialog.dismiss();
                                            button_stopDataMonitor.setVisibility(View.VISIBLE);
                                            break;
                                        case PROGRESS_SYNCING_WITH_DEVICE:
                                            Toast.makeText(Activity_BloodPressureSampler.this,
                                                    "Synchronizing with device.",
                                                    Toast.LENGTH_SHORT).show();
                                            break;
                                        case UNRECOGNIZED:
                                            Toast.makeText(Activity_BloodPressureSampler.this,
                                                "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                                                Toast.LENGTH_SHORT).show();
                                            setRequestUiEnabled(true);
                                            break;
                                        default:
                                            setRequestUiEnabled(true);
                                            break;
                                    }
                                }

                                private void handleFinishedCode(AntFsRequestStatus finishedCode)
                                {
                                    antFsProgressDialog.dismiss();

                                    switch(finishedCode)
                                    {
                                        case SUCCESS:
                                            Toast.makeText(Activity_BloodPressureSampler.this, "DownloadAllHistory finished successfully.", Toast.LENGTH_SHORT).show();
                                            break;

                                        case FAIL_ALREADY_BUSY_EXTERNAL:
                                            Toast.makeText(Activity_BloodPressureSampler.this, "DownloadAllHistory failed, device busy.", Toast.LENGTH_SHORT).show();
                                            break;
                                        case FAIL_DEVICE_COMMUNICATION_FAILURE:
                                            Toast.makeText(Activity_BloodPressureSampler.this, "DownloadAllHistory failed, communication error.", Toast.LENGTH_SHORT).show();
                                            break;
                                        case FAIL_AUTHENTICATION_REJECTED:
                                            //NOTE: This is thrown when authentication has failed, most likely when user action is required to enable pairing
                                            Toast.makeText(Activity_BloodPressureSampler.this, "DownloadAllHistory failed, authentication rejected.", Toast.LENGTH_LONG).show();
                                            break;
                                        case FAIL_DEVICE_TRANSMISSION_LOST:
                                            Toast.makeText(Activity_BloodPressureSampler.this, "DownloadAllHistory failed, transmission lost.", Toast.LENGTH_SHORT).show();
                                            break;
                                        case FAIL_PLUGINS_SERVICE_VERSION:
                                            Toast.makeText(Activity_BloodPressureSampler.this,
                                                "Failed: Plugin Service Upgrade Required?",
                                                Toast.LENGTH_SHORT).show();
                                            setRequestUiEnabled(true);
                                            break;
                                        case UNRECOGNIZED:
                                            Toast.makeText(Activity_BloodPressureSampler.this,
                                                "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                                                Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            },
                            new IMeasurementDownloadedReceiver()
                            {
                                @Override
                                public void onMeasurementDownloaded(final BloodPressureMeasurement measurement) {
                                    //Add Blood Pressure Layout to the list of layouts displayed to the user
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            layoutControllerList.add(0, new LayoutController_BloodPressure(
                                                    getLayoutInflater(),
                                                    linearLayout_FitDataView,
                                                    measurement.asBloodPressureFitMesg()));
                                        }
                                    });
                                }
                            },
                            new IAntFsProgressUpdateReceiver()
                            {
                                @Override
                                public void onNewAntFsProgressUpdate(final AntFsState stateCode,
                                    final long transferredBytes, final long totalBytes)
                                {
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            switch(stateCode)
                                            {
                                                //In Link state and requesting to link with the device in order to pass to Auth state
                                                case LINK_REQUESTING_LINK:
                                                    antFsProgressDialog.setMax(4);
                                                    antFsProgressDialog.setProgress(1);
                                                    antFsProgressDialog.setMessage("In Link State: Requesting Link.");
                                                    break;

                                                //In Authentication state, processing authentication commands
                                                case AUTHENTICATION:
                                                    antFsProgressDialog.setMax(4);
                                                    antFsProgressDialog.setProgress(2);
                                                    antFsProgressDialog.setMessage("In Authentication State.");
                                                    break;

                                                //In Authentication state, currently attempting to pair with the device
                                                //NOTE: Feedback SHOULD be given to the user here as pairing typically requires user interaction with the device
                                                case AUTHENTICATION_REQUESTING_PAIRING:
                                                    antFsProgressDialog.setMax(4);
                                                    antFsProgressDialog.setProgress(2);
                                                    antFsProgressDialog.setMessage("In Authentication State: User Pairing Requested.");
                                                    break;

                                                //In Transport state, no requests are currently being processed
                                                case TRANSPORT_IDLE:
                                                    antFsProgressDialog.setMax(4);
                                                    antFsProgressDialog.setProgress(3);
                                                    antFsProgressDialog.setMessage("Requesting download (In Transport State: Idle)...");
                                                    break;

                                                //In Transport state, files are currently being downloaded
                                                case TRANSPORT_DOWNLOADING:
                                                    antFsProgressDialog.setMessage("In Transport State: Downloading.");
                                                    antFsProgressDialog.setMax(100);

                                                    if(transferredBytes >= 0 && totalBytes > 0)
                                                    {
                                                        int progress = (int)(transferredBytes*100/totalBytes);
                                                        antFsProgressDialog.setProgress(progress);
                                                    }
                                                    break;

                                                case UNRECOGNIZED:
                                                    //This flag indicates that an unrecognized value was sent by the service, an upgrade of your PCC may be required to handle this new value.
                                                    Toast.makeText(Activity_BloodPressureSampler.this, "Failed: UNRECOGNIZED. PluginLib or Plugin Service Upgrade Required?", Toast.LENGTH_SHORT).show();
                                                    break;
                                                default:
                                                    Log.w("BloodPressureSampler", "Unknown ANT-FS State Code Received: " + stateCode);
                                                    break;
                                               }
                                        }
                                    });
                                }
                            });

                    if(submitted)
                    {
                        clearLayoutList();

                        setRequestUiEnabled(false);
                        antFsProgressDialog.show();
                    }
                    else
                    {
                        Toast.makeText(Activity_BloodPressureSampler.this, "Error Downloading Measurements: PCC already busy or dead", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        button_getAntFsMfgID.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if(bpPcc != null)
                    tv_mfgID.setText(Integer.toString(bpPcc.getAntFsManufacturerID()));
            }
        });

        button_requestResetData.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(bpPcc == null)
                            return;

                        final Dialog_ProgressWaiter progressDialog = new Dialog_ProgressWaiter("Resetting Device...");
                        boolean doSetTime = checkBox_setTimeOnReset.isChecked();

                        boolean submitted = bpPcc.requestResetDataAndSetTime(doSetTime, new IResetDataAndSetTimeFinishedReceiver()
                                {
                                    @Override
                                    public void onNewResetDataAndSetTimeFinished(final AntFsRequestStatus statusCode)
                                    {
                                        //Unrecognized or fail plugins service version indicates the progress dialog would never have started
                                        if(statusCode == AntFsRequestStatus.UNRECOGNIZED)
                                        {
                                            Toast.makeText(Activity_BloodPressureSampler.this,
                                                "Reset Failed - " + statusCode + ". Plugin Lib needs upgrade.",
                                                Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        else if (statusCode == AntFsRequestStatus.FAIL_PLUGINS_SERVICE_VERSION)
                                        {
                                            Toast.makeText(Activity_BloodPressureSampler.this,
                                                "Reset Failed - " + statusCode + ". Plugin Service needs upgrade.",
                                                Toast.LENGTH_LONG).show();
                                            return;
                                        }


                                        progressDialog.dismiss();

                                        runOnUiThread(new Runnable()
                                                {
                                                    @Override
                                                    public void run()
                                                    {
                                                        if(statusCode == AntFsRequestStatus.SUCCESS)
                                                            Toast.makeText(Activity_BloodPressureSampler.this, "Reset Complete", Toast.LENGTH_SHORT).show();
                                                        else
                                                            Toast.makeText(Activity_BloodPressureSampler.this, "Reset Failed - " + statusCode, Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                }, null);   //TODO use AntFsProgressReceiver

                        if(submitted)
                            progressDialog.show(getSupportFragmentManager(), "ResetProgressDialog");
                        else
                            Toast.makeText(Activity_BloodPressureSampler.this, "Error Resetting Device: PCC likely busy or dead", Toast.LENGTH_SHORT).show();
                    }
                });

        resetPcc();
    }

    private void resetPcc()
    {
        //Release the old access if it exists
        if(releaseHandle != null)
        {
            releaseHandle.close();
        }

        clearLayoutList();

        Intent intent = getIntent();
        if (intent.hasExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT))
        {
            // device has already been selected through the multi-device search
            MultiDeviceSearchResult result = intent
                .getParcelableExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT);
            releaseHandle = AntPlusBloodPressurePcc.requestAccess(this,
                result.getAntDeviceNumber(), 0, mResultReceiver, mDeviceStateChangeReceiver);
        } else
        {
            // starts the plugins UI search
            releaseHandle = AntPlusBloodPressurePcc.requestAccess(this, this, mResultReceiver,
                mDeviceStateChangeReceiver);
        }
    }

    private void setRequestUiEnabled(boolean enabled)
    {
        button_getAntFsMfgID.setEnabled(enabled);
        button_requestResetData.setEnabled(enabled);
        checkBox_setTimeOnReset.setEnabled(enabled);
        button_requestDownloadAllHistory.setEnabled(enabled);
        checkBox_downloadNewOnly.setEnabled(enabled);
        checkBox_enableMonitoring.setEnabled(enabled);
    }

    private void clearLayoutList()
    {
        if(!layoutControllerList.isEmpty())
        {
            for(Closeable controller : layoutControllerList)
                try
                {
                    controller.close();
                } catch (IOException e)
                {
                    //Never happens
                }

            layoutControllerList.clear();
        }
    }

    @Override
    protected void onDestroy()
    {
        releaseHandle.close();

        clearLayoutList();

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
