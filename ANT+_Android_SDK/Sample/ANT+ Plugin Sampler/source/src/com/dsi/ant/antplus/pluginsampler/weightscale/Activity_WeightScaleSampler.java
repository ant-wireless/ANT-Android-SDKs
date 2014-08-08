/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2013
All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.weightscale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.LayoutController_FileId;
import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.antplus.pluginsampler.multidevicesearch.Activity_MultiDeviceSearchSampler;
import com.dsi.ant.plugins.antplus.common.AntFsCommon.IAntFsProgressUpdateReceiver;
import com.dsi.ant.plugins.antplus.common.FitFileCommon.FitFile;
import com.dsi.ant.plugins.antplus.common.FitFileCommon.IFitFileDownloadedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.AdvancedMeasurement;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.BodyWeightStatus;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.Gender;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.IAdvancedMeasurementFinishedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.IBasicMeasurementFinishedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.IBodyWeightBroadcastReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.ICapabilitiesRequestFinishedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.IDownloadAllHistoryFinishedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.UserProfile;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.WeightScaleRequestStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.AntFsRequestStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.AntFsState;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IManufacturerIdentificationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IProductInformationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;
import com.garmin.fit.Decode;
import com.garmin.fit.DeviceInfoMesg;
import com.garmin.fit.DeviceInfoMesgListener;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.FileIdMesgListener;
import com.garmin.fit.FitRuntimeException;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.UserProfileMesg;
import com.garmin.fit.UserProfileMesgListener;
import com.garmin.fit.WeightScaleMesg;
import com.garmin.fit.WeightScaleMesgListener;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Connects to Weight Scale Plugin and display all the event data.
 */
public class Activity_WeightScaleSampler extends FragmentActivity
{
    AntPlusWeightScalePcc wgtPcc = null;
    PccReleaseHandle<AntPlusWeightScalePcc> releaseHandle = null;

    ArrayList<Closeable> layoutControllerList;

    UserProfile userProfile = new UserProfile();

    Button button_requestBasicMeasurement;
    Button button_requestAdvancedMeasurement;
    Button button_requestCapabilities;
    Button button_configUserProfile;
    Button button_requestDownloadAllHistory;

    ProgressDialog antFsProgressDialog;

    LinearLayout linearLayout_FitDataView;

    TextView tv_status;

    TextView tv_estTimestamp;

    TextView tv_bodyWeightResult;
    TextView tv_bodyWeightBroadcast;
    TextView tv_hydrationPercentage;
    TextView tv_bodyFatPercentage;
    TextView tv_muscleMass;
    TextView tv_boneMass;
    TextView tv_activeMetabolicRate;
    TextView tv_basalMetabolicRate;

    TextView tv_userProfileExchangeSupport;
    TextView tv_userProfileSelected;
    TextView tv_userProfileID;
    TextView tv_historySupport;

    TextView tv_hardwareRevision;
    TextView tv_manufacturerID;
    TextView tv_modelNumber;

    TextView tv_mainSoftwareRevision;
    TextView tv_supplementalSoftwareRevision;
    TextView tv_serialNumber;

    IPluginAccessResultReceiver<AntPlusWeightScalePcc> mResultReceiver = new IPluginAccessResultReceiver<AntPlusWeightScalePcc>()
    {
        // Handle the result, connecting to events on success or reporting
        // failure to user.
        @Override
        public void onResultReceived(AntPlusWeightScalePcc result,
            RequestAccessResult resultCode, DeviceState initialDeviceState)
        {
            switch (resultCode)
            {
                case SUCCESS:
                    wgtPcc = result;
                    tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                    setRequestButtonsEnabled(true);
                    subscribeToEvents();
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    Toast.makeText(Activity_WeightScaleSampler.this, "Channel Not Available",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case ADAPTER_NOT_DETECTED:
                    Toast
                        .makeText(
                            Activity_WeightScaleSampler.this,
                            "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.",
                            Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case BAD_PARAMS:
                    // Note: Since we compose all the params ourself, we should
                    // never see this result
                    Toast.makeText(Activity_WeightScaleSampler.this, "Bad request parameters.",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case OTHER_FAILURE:
                    Toast.makeText(Activity_WeightScaleSampler.this,
                        "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT)
                        .show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case DEPENDENCY_NOT_INSTALLED:
                    tv_status.setText("Error. Do Menu->Reset.");
                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder(
                        Activity_WeightScaleSampler.this);
                    adlgBldr.setTitle("Missing Dependency");
                    adlgBldr
                        .setMessage("The required service\n\""
                            + AntPlusWeightScalePcc.getMissingDependencyName()
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
                                    + AntPlusWeightScalePcc
                                        .getMissingDependencyPackageName()));
                            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            Activity_WeightScaleSampler.this.startActivity(startStore);
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
                    Toast.makeText(Activity_WeightScaleSampler.this,
                        "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                        Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                default:
                    Toast.makeText(Activity_WeightScaleSampler.this,
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
            wgtPcc
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

                                tv_manufacturerID.setText(String.valueOf(manufacturerID));

                                if (hardwareRevision != -1)
                                    tv_hardwareRevision.setText(String.valueOf(hardwareRevision));

                                if (modelNumber != -1)
                                    tv_modelNumber.setText(String.valueOf(modelNumber));
                            }
                        });
                    }
                });

            wgtPcc.subscribeProductInformationEvent(new IProductInformationReceiver()
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

            wgtPcc.subscribeBodyWeightBroadcastEvent(new IBodyWeightBroadcastReceiver()
            {
                @Override
                public void onNewBodyWeightBroadcast(final long estTimestamp,
                    EnumSet<EventFlag> eventFlags,
                    final BodyWeightStatus bodyWeightStatus, final BigDecimal bodyWeight)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            tv_estTimestamp.setText(String.valueOf(estTimestamp));

                            if (bodyWeightStatus == BodyWeightStatus.VALID)
                                tv_bodyWeightBroadcast.setText(bodyWeight.toString());
                            else
                                tv_bodyWeightBroadcast.setText(bodyWeightStatus.toString());
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
                    tv_status.setText(wgtPcc.getDeviceName() + ": " + newDeviceState);
                    if (newDeviceState == DeviceState.DEAD)
                    {
                        if (antFsProgressDialog != null)
                            antFsProgressDialog.dismiss();

                        wgtPcc = null;

                        setRequestButtonsEnabled(false);
                    }
                }
            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e("WeightScale", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weightscale);

        layoutControllerList = new ArrayList<Closeable>();

        button_requestBasicMeasurement = (Button)findViewById(R.id.button_requestBasicMeasurement);
        button_requestAdvancedMeasurement = (Button) findViewById(R.id.button_requestAdvancedMeasurement);
        button_requestCapabilities = (Button) findViewById(R.id.button_requestCapabilities);
        button_configUserProfile = (Button) findViewById(R.id.button_configUserProfile);
        button_requestDownloadAllHistory = (Button) findViewById(R.id.button_requestDownloadAllHistory);

        linearLayout_FitDataView = (LinearLayout) findViewById(R.id.linearLayout_WeightScaleCards);

        tv_status = (TextView)findViewById(R.id.textView_Status);
        tv_estTimestamp = (TextView)findViewById(R.id.textView_EstTimestamp);
        tv_bodyWeightResult = (TextView)findViewById(R.id.textView_BodyWeightResult);
        tv_bodyWeightBroadcast = (TextView)findViewById(R.id.textView_BodyWeightBroadcast);
        tv_bodyFatPercentage = (TextView) findViewById(R.id.textView_BodyFatPercentage);
        tv_hydrationPercentage = (TextView) findViewById(R.id.textView_HydrationPercentage);
        tv_muscleMass = (TextView) findViewById(R.id.textView_MuscleMass);
        tv_boneMass = (TextView) findViewById(R.id.textView_BoneMass);
        tv_activeMetabolicRate = (TextView) findViewById(R.id.textView_ActiveMetabolicRate);
        tv_basalMetabolicRate = (TextView) findViewById(R.id.textView_BasalMetabolicRate);

        tv_userProfileExchangeSupport = (TextView) findViewById(R.id.textView_UserProfileExchangeSupport);
        tv_userProfileSelected = (TextView) findViewById(R.id.textView_UserProfileSelected);
        tv_userProfileID = (TextView) findViewById(R.id.textView_UserProfileID);
        tv_historySupport = (TextView) findViewById(R.id.textView_HistorySupport);


        tv_hardwareRevision = (TextView)findViewById(R.id.textView_HardwareRevision);
        tv_manufacturerID = (TextView)findViewById(R.id.textView_ManufacturerID);
        tv_modelNumber = (TextView)findViewById(R.id.textView_ModelNumber);

        tv_mainSoftwareRevision = (TextView)findViewById(R.id.textView_MainSoftwareRevision);
        tv_supplementalSoftwareRevision = (TextView)findViewById(R.id.textView_SupplementalSoftwareRevision);
        tv_serialNumber = (TextView)findViewById(R.id.textView_SerialNumber);

        userProfile.age = 32;
        userProfile.height = 160;
        userProfile.gender = Gender.FEMALE;
        userProfile.lifetimeAthlete = false;
        userProfile.activityLevel = 4;

        button_requestBasicMeasurement.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                boolean submitted = wgtPcc.requestBasicMeasurement(new IBasicMeasurementFinishedReceiver()
                {

                    @Override
                    public void onBasicMeasurementFinished(long estTimestamp,
                        EnumSet<EventFlag> eventFlags, final WeightScaleRequestStatus status, final BigDecimal bodyWeight)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                setRequestButtonsEnabled(true);
                                if(checkRequestResult(status))
                                {
                                    if(bodyWeight.intValue() == -1)
                                        tv_bodyWeightResult.setText("Invalid");
                                    else
                                        tv_bodyWeightResult.setText(String.valueOf(bodyWeight) + "kg");
                                }
                            }
                        });
                    }
                });

                if(submitted)
                {
                    setRequestButtonsEnabled(false);
                    resetWeightRequestedDataDisplay("Computing");
                }
            }
        });

        button_requestAdvancedMeasurement.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                boolean submitted = wgtPcc.requestAdvancedMeasurement(new IAdvancedMeasurementFinishedReceiver()
                {
                    @Override
                    public void onAdvancedMeasurementFinished(long estTimestamp,
                        EnumSet<EventFlag> eventFlags, final WeightScaleRequestStatus status,
                        final AdvancedMeasurement measurement)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                setRequestButtonsEnabled(true);
                                if(checkRequestResult(status))
                                {
                                    if(measurement.bodyWeight.intValue() == -1)
                                        tv_bodyWeightResult.setText("Invalid");
                                    else
                                        tv_bodyWeightResult.setText(String.valueOf(measurement.bodyWeight) + "kg");
                                    if(measurement.hydrationPercentage.intValue() == -1)
                                        tv_hydrationPercentage.setText("Invalid");
                                    else
                                        tv_hydrationPercentage.setText(String.valueOf(measurement.hydrationPercentage) + "%");
                                    if(measurement.bodyFatPercentage.intValue() == -1)
                                        tv_bodyFatPercentage.setText("Invalid");
                                    else
                                        tv_bodyFatPercentage.setText(String.valueOf(measurement.bodyFatPercentage) + "%");
                                    if(measurement.muscleMass.intValue() == -1)
                                        tv_muscleMass.setText("Invalid");
                                    else
                                        tv_muscleMass.setText(String.valueOf(measurement.muscleMass) + "kg");
                                    if(measurement.boneMass.intValue() == -1)
                                        tv_boneMass.setText("Invalid");
                                    else
                                        tv_boneMass.setText(String.valueOf(measurement.boneMass) + "kg");
                                    if(measurement.activeMetabolicRate.intValue() == -1)
                                        tv_activeMetabolicRate.setText("Invalid");
                                    else
                                        tv_activeMetabolicRate.setText(String.valueOf(measurement.activeMetabolicRate) + "kcal");
                                    if(measurement.basalMetabolicRate.intValue() == -1)
                                        tv_basalMetabolicRate.setText("Invalid");
                                    else
                                        tv_basalMetabolicRate.setText(String.valueOf(measurement.basalMetabolicRate) + "kcal");
                                }
                            }
                        });
                    }
                },
                userProfile);

                if(submitted)
                {
                    setRequestButtonsEnabled(false);
                    resetWeightRequestedDataDisplay("Computing");
                }
            }
        });


        button_requestCapabilities.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean submitted = wgtPcc.requestCapabilities(new ICapabilitiesRequestFinishedReceiver()
                {
                    @Override
                    public void onCapabilitiesRequestFinished(long estTimestamp,
                        EnumSet<EventFlag> eventFlags, final WeightScaleRequestStatus status, final int userProfileID,
                        final boolean historySupport, final boolean userProfileExchangeSupport,
                        final boolean userProfileSelected)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                setRequestButtonsEnabled(true);

                                if(checkRequestResult(status))
                                {
                                    tv_bodyWeightResult.setText("Req Capab Success");
                                    tv_userProfileExchangeSupport.setText(String.valueOf(userProfileExchangeSupport));
                                    tv_userProfileSelected.setText(String.valueOf(userProfileSelected));
                                    tv_historySupport.setText(String.valueOf(historySupport));
                                    if(userProfileID == -1)
                                        tv_userProfileID.setText("UNASSIGNED");
                                    else
                                        tv_userProfileID.setText(String.valueOf(userProfileID));
                                }
                            }
                        });
                    }
                });

                if(submitted)
                {
                    setRequestButtonsEnabled(false);
                    resetWeightRequestedDataDisplay("Req Capab");
                }
            }
        });

        button_configUserProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Dialog_ConfigUserProfile dialog = new Dialog_ConfigUserProfile(userProfile);
                dialog.show(getSupportFragmentManager(), "Configure User Profile");
            }
        });

        button_requestDownloadAllHistory.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    antFsProgressDialog = new ProgressDialog(Activity_WeightScaleSampler.this);
                    antFsProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    antFsProgressDialog.setMessage("Sending Request...");
                    antFsProgressDialog.setCancelable(false);
                    antFsProgressDialog.setIndeterminate(false);

                    boolean submitted = wgtPcc.requestDownloadAllHistory(
                        new IDownloadAllHistoryFinishedReceiver()
                        {
                            //Process the final result of the download
                            @Override
                            public void onDownloadAllHistoryFinished(final AntFsRequestStatus status)
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        setRequestButtonsEnabled(true);
                                        antFsProgressDialog.dismiss();

                                        switch(status)
                                        {
                                            case SUCCESS:
                                                Toast.makeText(Activity_WeightScaleSampler.this, "DownloadAllHistory finished successfully.", Toast.LENGTH_SHORT).show();
                                                break;
                                            case FAIL_ALREADY_BUSY_EXTERNAL:
                                                Toast.makeText(Activity_WeightScaleSampler.this, "DownloadAllHistory failed, device busy.", Toast.LENGTH_SHORT).show();
                                                break;
                                            case FAIL_DEVICE_COMMUNICATION_FAILURE:
                                                Toast.makeText(Activity_WeightScaleSampler.this, "DownloadAllHistory failed, communication error.", Toast.LENGTH_SHORT).show();
                                                break;
                                            case FAIL_NOT_SUPPORTED:
                                                Toast.makeText(Activity_WeightScaleSampler.this, "DownloadAllHistory failed, feature not supported in weight scale.", Toast.LENGTH_LONG).show();
                                                break;
                                            case FAIL_AUTHENTICATION_REJECTED:
                                                Toast.makeText(Activity_WeightScaleSampler.this, "DownloadAllHistory failed, authentication rejected.", Toast.LENGTH_LONG).show();
                                                break;
                                            case FAIL_DEVICE_TRANSMISSION_LOST:
                                                Toast.makeText(Activity_WeightScaleSampler.this, "DownloadAllHistory failed, transmission lost.", Toast.LENGTH_SHORT).show();
                                                break;
                                            case FAIL_PLUGINS_SERVICE_VERSION:
                                                Toast.makeText(Activity_WeightScaleSampler.this,
                                                    "Failed: Plugin Service Upgrade Required?",
                                                    Toast.LENGTH_SHORT).show();
                                                break;
                                            case UNRECOGNIZED:
                                                Toast.makeText(Activity_WeightScaleSampler.this,
                                                    "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                                                    Toast.LENGTH_SHORT).show();
                                                break;
                                            default:
                                                Toast.makeText(Activity_WeightScaleSampler.this, "DownloadAllHistory failed, unrecognized code: " + status, Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    }
                                });
                            }
                        },
                        //Written using FIT SDK 7.10 Library (fit.jar)
                        new IFitFileDownloadedReceiver()
                        {
                            //Process incoming FIT file(s)
                            @Override
                            public void onNewFitFileDownloaded(
                                FitFile downloadedFitFile)
                            {

                                InputStream fitFile = downloadedFitFile.getInputStream();

                                if(!Decode.checkIntegrity(fitFile))
                                {
                                    Toast.makeText(Activity_WeightScaleSampler.this, "FIT file integrity check failed.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                //Must reset InputStream after reading it for integrity check
                                try
                                {
                                    fitFile.reset();
                                } catch (IOException e)
                                {
                                    //No IOExceptions thrown from ByteArrayInputStream
                                }

                                FileIdMesgListener fileIdMesgListener = new FileIdMesgListener()
                                {
                                    @Override
                                    public void onMesg(final FileIdMesg mesg)
                                    {
                                        //Add File ID Layout to the list of layouts displayed to the user
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                layoutControllerList.add(new LayoutController_FileId(getLayoutInflater(), linearLayout_FitDataView, mesg));
                                            }
                                        });
                                    }
                                };

                                UserProfileMesgListener userProfileMesgListener = new UserProfileMesgListener()
                                {
                                    @Override
                                    public void onMesg(final UserProfileMesg mesg)
                                    {
                                        //Add User Profile Layout to the list of layouts displayed to the user
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                layoutControllerList.add(new LayoutController_WeightScaleUserProfile(getLayoutInflater(), linearLayout_FitDataView, mesg));
                                            }
                                        });
                                    }
                                };

                                WeightScaleMesgListener weightScaleMesgListener = new WeightScaleMesgListener()
                                {
                                    @Override
                                    public void onMesg(final WeightScaleMesg mesg)
                                    {
                                        //Add Weight Scale Layout to the list of layouts displayed to the user
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                layoutControllerList.add(new LayoutController_WeightScale(getLayoutInflater(), linearLayout_FitDataView, mesg));
                                            }
                                        });
                                    }
                                };

                                DeviceInfoMesgListener deviceInfoMesgListener = new DeviceInfoMesgListener()
                                {
                                    @Override
                                    public void onMesg(final DeviceInfoMesg mesg)
                                    {
                                        //Add Device Information Layout to the list of layouts displayed to the user
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                layoutControllerList.add(new LayoutController_WeightScaleDeviceInfo(getLayoutInflater(), linearLayout_FitDataView, mesg));
                                            }
                                        });
                                    }
                                };


                                MesgBroadcaster mesgBroadcaster = new MesgBroadcaster();
                                mesgBroadcaster.addListener(fileIdMesgListener);
                                mesgBroadcaster.addListener(userProfileMesgListener);
                                mesgBroadcaster.addListener(weightScaleMesgListener);
                                mesgBroadcaster.addListener(deviceInfoMesgListener);

                                try
                                {
                                    mesgBroadcaster.run(fitFile);
                                }
                                catch (FitRuntimeException e)
                                {
                                    Log.e("WeightScaleSampler", "Error decoding FIT file: " + e.toString());
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Toast.makeText(Activity_WeightScaleSampler.this,
                                                "Error decoding FIT file", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }

                        },
                        new IAntFsProgressUpdateReceiver()
                        {
                            @Override
                            public void onNewAntFsProgressUpdate(
                                final AntFsState state, final long transferredBytes,
                                final long totalBytes)
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        switch(state)
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
                                                Toast.makeText(Activity_WeightScaleSampler.this,
                                                    "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                                                    Toast.LENGTH_SHORT).show();
                                                break;

                                            default:
                                                Log.w("WeightScaleSampler", "Unknown ANT-FS State Code Received: " + state);
                                                break;
                                        }
                                    }
                                });
                            }
                        });

                    if(submitted)
                    {
                        clearLayoutList();

                        setRequestButtonsEnabled(false);
                        antFsProgressDialog.show();
                    }
                }
            });

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

        setRequestButtonsEnabled(false);
        resetWeightRequestedDataDisplay("---");
        tv_bodyWeightBroadcast.setText("---");

        tv_estTimestamp.setText("---");

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
            releaseHandle = AntPlusWeightScalePcc.requestAccess(this, result.getAntDeviceNumber(),
                0, mResultReceiver, mDeviceStateChangeReceiver);
        } else
        {
            // starts the plugins UI search
            releaseHandle = AntPlusWeightScalePcc.requestAccess(this, this, mResultReceiver,
                mDeviceStateChangeReceiver);
        }
    }

    private void resetWeightRequestedDataDisplay(String weightValueText)
    {
        tv_bodyWeightResult.setText(weightValueText);
        tv_hydrationPercentage.setText("---");
        tv_bodyFatPercentage.setText("---");
        tv_muscleMass.setText("---");
        tv_boneMass.setText("---");
        tv_activeMetabolicRate.setText("---");
        tv_basalMetabolicRate.setText("---");
        tv_userProfileExchangeSupport.setText("---");
        tv_userProfileSelected.setText("---");
        tv_userProfileID.setText("---");
        tv_historySupport.setText("---");
    }

    private void setRequestButtonsEnabled(boolean enabled)
    {
        button_requestBasicMeasurement.setEnabled(enabled);
        button_requestAdvancedMeasurement.setEnabled(enabled);
        button_requestCapabilities.setEnabled(enabled);
        button_configUserProfile.setEnabled(enabled);
        button_requestDownloadAllHistory.setEnabled(enabled);
    }

    private boolean checkRequestResult(WeightScaleRequestStatus status)
    {
        switch(status)
        {
            case SUCCESS:
                return true;
            case FAIL_ALREADY_BUSY_EXTERNAL:
                tv_bodyWeightResult.setText("Fail: Busy");
                break;
            case FAIL_DEVICE_COMMUNICATION_FAILURE:
                tv_bodyWeightResult.setText("Fail: Comm Err");
                break;
            case FAIL_DEVICE_TRANSMISSION_LOST:
                tv_bodyWeightResult.setText("Fail: Trans Lost");
                break;
            case FAIL_PLUGINS_SERVICE_VERSION:
                Toast.makeText(Activity_WeightScaleSampler.this,
                    "Failed: Plugin Service Upgrade Required?",
                    Toast.LENGTH_SHORT).show();
                break;
            case UNRECOGNIZED:
                Toast.makeText(Activity_WeightScaleSampler.this,
                    "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                    Toast.LENGTH_SHORT).show();
                break;
            default:
                tv_bodyWeightResult.setText("Fail: " + status);
                Toast.makeText(this, "Request failed with unrecognized result:" + status, Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
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
