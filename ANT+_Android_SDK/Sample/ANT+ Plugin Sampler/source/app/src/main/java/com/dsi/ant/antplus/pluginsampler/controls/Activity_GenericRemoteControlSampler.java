/*
 * This software is subject to the license described in the License.txt file
 * included with this software distribution. You may not use this file except in compliance
 * with this license.
 *
 * Copyright (c) Garmin Canada Inc. 2019
 * All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.controls;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.plugins.antplus.pcc.controls.AntPlusGenericRemoteControlPcc;
import com.dsi.ant.plugins.antplus.pcc.controls.AntPlusGenericRemoteControlPcc.IGenericCommandFinishedReceiver;
import com.dsi.ant.plugins.antplus.pcc.controls.defines.ControlsMode;
import com.dsi.ant.plugins.antplus.pcc.controls.defines.GenericCommandNumber;
import com.dsi.ant.plugins.antplus.pcc.controls.pccbase.AntPlusBaseRemoteControlPcc.ControlDeviceCapabilities;
import com.dsi.ant.plugins.antplus.pcc.controls.pccbase.AntPlusBaseRemoteControlPcc.IControlDeviceAvailabilityReceiver;
import com.dsi.ant.plugins.antplus.pcc.controls.pccbase.AntPlusBaseRemoteControlPcc.IRemoteControlAsyncScanResultReceiver;
import com.dsi.ant.plugins.antplus.pcc.controls.pccbase.AntPlusBaseRemoteControlPcc.RemoteControlAsyncScanController;
import com.dsi.ant.plugins.antplus.pcc.controls.pccbase.AntPlusBaseRemoteControlPcc.RemoteControlAsyncScanResultDeviceInfo;
import com.dsi.ant.plugins.antplus.pcc.defines.BatteryStatus;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestStatus;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IBatteryStatusReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IManufacturerIdentificationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IProductInformationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;
import com.dsi.ant.plugins.utility.log.LogAnt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;

public class Activity_GenericRemoteControlSampler extends Activity
{
    private static final String TAG = Activity_GenericRemoteControlSampler.class.getSimpleName();

    AntPlusGenericRemoteControlPcc remotePcc;
    PccReleaseHandle<AntPlusGenericRemoteControlPcc> remoteReleaseHandle;

    Button button_sendCommand;

    Spinner sp_commandNumber;

    TextView tv_status;

    TextView tv_estTimestamp;

    TextView tv_deviceId;

    TextView tv_maximumNumberRemotes;
    TextView tv_audioControlSupported;
    TextView tv_genericControlSupported;
    TextView tv_videoControlSupported;
    TextView tv_burstCommandSupported;

    TextView tv_hardwareRevision;
    TextView tv_manufacturerID;
    TextView tv_modelNumber;

    TextView tv_cumulativeOperatingTime;
    TextView tv_batteryVoltage;
    TextView tv_batteryStatus;
    TextView tv_cumulativeOperatingTimeResolution;

    TextView tv_softwareRevision;
    TextView tv_serialNumber;

    RemoteControlAsyncScanController<AntPlusGenericRemoteControlPcc> genericRemoteScanController;
    ArrayList<RemoteControlAsyncScanResultDeviceInfo> mAlreadyConnectedDeviceInfos;
    ArrayList<RemoteControlAsyncScanResultDeviceInfo> mScannedDeviceInfos;
    ArrayAdapter<String> adapter_devNameList;
    ArrayAdapter<String> adapter_connDevNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        initScanDisplay();
        super.onCreate(savedInstanceState);

        handleReset();
    }

    private void initScanDisplay()
    {
        setContentView(com.dsi.ant.antplus.pluginsampler.R.layout.activity_async_scan);

        tv_status = (TextView)findViewById(R.id.textView_Status);
        tv_status.setText("Scanning for controllable devices asynchronously...");

        mAlreadyConnectedDeviceInfos = new ArrayList<RemoteControlAsyncScanResultDeviceInfo>();
        mScannedDeviceInfos = new ArrayList<RemoteControlAsyncScanResultDeviceInfo>();

        //Setup already connected devices list
        adapter_connDevNameList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        final ListView listView_alreadyConnectedDevs = (ListView)findViewById(R.id.listView_AlreadyConnectedDevices);
        listView_alreadyConnectedDevs.setAdapter(adapter_connDevNameList);
        listView_alreadyConnectedDevs.setOnItemClickListener(new OnItemClickListener()
                {
                    //Return the id of the selected already connected device
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
                    {
                        listView_alreadyConnectedDevs.setEnabled(false);
                        requestConnectToResult(mAlreadyConnectedDeviceInfos.get(pos));
                    }
                });

        //Setup found devices display list
        adapter_devNameList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        final ListView listView_Devices = (ListView)findViewById(R.id.listView_FoundDevices);
        listView_Devices.setAdapter(adapter_devNameList);
        listView_Devices.setOnItemClickListener(new OnItemClickListener()
                {
                    //Return the id of the selected already connected device
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
                    {
                        listView_Devices.setEnabled(false);
                        requestConnectToResult(mScannedDeviceInfos.get(pos));
                    }
                });
    }

    /**
     * Requests access to the given search result.
     * @param asyncScanResultDeviceInfo The search result to attempt to connect to.
     */
    protected void requestConnectToResult(final RemoteControlAsyncScanResultDeviceInfo asyncScanResultDeviceInfo)
    {
        //Inform the user we are connecting
        runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        tv_status.setText("Connecting to " + asyncScanResultDeviceInfo.resultInfo.getDeviceDisplayName());
                    }
                });

        remoteReleaseHandle = genericRemoteScanController.requestDeviceAccess(asyncScanResultDeviceInfo.resultInfo,
            new IPluginAccessResultReceiver<AntPlusGenericRemoteControlPcc>()
            {
                @Override
                public void onResultReceived(AntPlusGenericRemoteControlPcc result,
                    RequestAccessResult resultCode, DeviceState initialDeviceState)
                {
                    if(resultCode == RequestAccessResult.SEARCH_TIMEOUT)
                    {
                        //On a connection timeout the scan automatically resumes, so we inform the user, and go back to scanning
                        runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                Toast.makeText(Activity_GenericRemoteControlSampler.this, "Timed out attempting to connect, try again", Toast.LENGTH_LONG).show();
                                tv_status.setText("Scanning for controllable devices asynchronously...");

                                // The attempted request access is finished so another request access is allowed
                                ((ListView)findViewById(R.id.listView_AlreadyConnectedDevices)).setEnabled(true);
                                ((ListView)findViewById(R.id.listView_FoundDevices)).setEnabled(true);
                            }
                        });
                    }
                    else
                    {
                        //Otherwise the results, including SUCCESS, behave the same as
                        IPluginAccessResultReceiver.onResultReceived(result, resultCode, initialDeviceState);
                    }
                }
            },
            IDeviceStateChangeReceiver);
    }

    protected void showDataDisplay(String status)
    {
        setContentView(R.layout.activity_generic_remote_control);

        button_sendCommand = (Button)findViewById(R.id.button_SendCommand);
        sp_commandNumber = (Spinner)findViewById(R.id.spinner_CommandNumber);

        tv_status = (TextView)findViewById(R.id.textView_Status);

        tv_estTimestamp = (TextView)findViewById(R.id.textView_EstTimestamp);

        tv_deviceId = (TextView)findViewById(R.id.textView_DeviceID);

        tv_maximumNumberRemotes = (TextView)findViewById(R.id.textView_MaxRemotes);
        tv_audioControlSupported = (TextView)findViewById(R.id.textView_AudioSupported);
        tv_genericControlSupported = (TextView)findViewById(R.id.textView_GenericSupported);
        tv_videoControlSupported = (TextView)findViewById(R.id.textView_VideoSupported);
        tv_burstCommandSupported = (TextView)findViewById(R.id.textView_SequenceSupported);

        tv_cumulativeOperatingTime = (TextView)findViewById(R.id.textView_CumulativeOperatingTime);
        tv_batteryVoltage = (TextView)findViewById(R.id.textView_BatteryVoltage);
        tv_batteryStatus = (TextView)findViewById(R.id.textView_BatteryStatus);
        tv_cumulativeOperatingTimeResolution = (TextView)findViewById(R.id.textView_CumulativeOperatingTimeResolution);

        tv_hardwareRevision = (TextView)findViewById(R.id.textView_HardwareRevision);
        tv_manufacturerID = (TextView)findViewById(R.id.textView_ManufacturerID);
        tv_modelNumber = (TextView)findViewById(R.id.textView_ModelNumber);

        tv_softwareRevision = (TextView)findViewById(R.id.textView_SoftwareRevision);
        tv_serialNumber = (TextView)findViewById(R.id.textView_SerialNumber);

        //TODO: have the labels in the spinner include their command number along with their name
        sp_commandNumber.setAdapter(new ArrayAdapter<GenericCommandNumber>(this, android.R.layout.simple_list_item_1, GenericCommandNumber.getGenericCommands()));

        button_sendCommand.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                try
                {
                    GenericCommandNumber commandNumber = (GenericCommandNumber)sp_commandNumber.getSelectedItem();

                    if (commandNumber != GenericCommandNumber.UNRECOGNIZED)
                    {
                        remotePcc.RequestGenericCommand( new IGenericCommandFinishedReceiver()
                        {

                            @Override
                            public void onGenericCommandFinished(final long estTimestamp,
                                    final EnumSet<EventFlag> eventFlags, final RequestStatus status)
                            {
                                LogAnt.d(TAG, "onGenericCommandFinished fired");
                                runOnUiThread(
                                        new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                                switch(status)
                                                {
                                                    case SUCCESS:
                                                        Toast.makeText(Activity_GenericRemoteControlSampler.this, "Request Successfully Sent", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    default:
                                                        Toast.makeText(Activity_GenericRemoteControlSampler.this, "Request Failed to be Sent", Toast.LENGTH_SHORT).show();
                                                        break;
                                                }
                                            }
                                        });
                             }

                        },
                        commandNumber);
                    }
                    else
                    {
                        Toast.makeText(Activity_GenericRemoteControlSampler.this, "Command number " + commandNumber + " unrecognised.", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (NumberFormatException e)
                {
                    Toast.makeText(Activity_GenericRemoteControlSampler.this, "Please enter a valid command number", Toast.LENGTH_SHORT).show();
                }
            }
        });

      //Reset the text display
        tv_status.setText(status);

        tv_estTimestamp.setText("---");
        tv_deviceId.setText("---");

        tv_maximumNumberRemotes.setText("---");
        tv_audioControlSupported.setText("---");
        tv_genericControlSupported.setText("---");
        tv_videoControlSupported.setText("---");
        tv_burstCommandSupported.setText("---");

        tv_cumulativeOperatingTime.setText("---");
        tv_batteryVoltage.setText("---");
        tv_batteryStatus.setText("---");
        tv_cumulativeOperatingTimeResolution.setText("---");

        tv_hardwareRevision.setText("---");
        tv_manufacturerID.setText("---");
        tv_modelNumber.setText("---");

        tv_softwareRevision.setText("---");
        tv_serialNumber.setText("---");
    }

    private void handleReset()
    {
        //Release the old access if it exists
        if(remoteReleaseHandle != null)
        {
            remoteReleaseHandle.close();
            remoteReleaseHandle = null;
        }
        if(genericRemoteScanController != null)
        {
            genericRemoteScanController.closeScanController();
            genericRemoteScanController = null;
        }

        EnumSet<ControlsMode> requestModes = EnumSet.of(ControlsMode.GENERIC_MODE);

        //AntPlusGenericRemoteControlPcc.requestAccessByDeviceNumber(requestModes, this, 0, 0, IPluginAccessResultReceiver, IDeviceStateChangeReceiver);

        //TODO: maybe we should use preferred search after all?
        genericRemoteScanController = AntPlusGenericRemoteControlPcc.requestRemoteControlAsyncScanController(requestModes, this, 0,
                new IRemoteControlAsyncScanResultReceiver()
                {
                    @Override
                    public void onSearchStopped(RequestAccessResult reasonStopped)
                    {
                        //The triggers calling this function use the same codes and require the same actions as those received by the standard access result receiver
                        IPluginAccessResultReceiver.onResultReceived(null, reasonStopped, DeviceState.DEAD);
                    }

                    @Override
                    public void onSearchResult(final RemoteControlAsyncScanResultDeviceInfo deviceFound)
                    {
                        for(RemoteControlAsyncScanResultDeviceInfo i: mScannedDeviceInfos)
                        {
                            //The current implementation of the async scan will reset it's ignore list every 30s,
                            //So we have to handle checking for duplicates in our list if we run longer than that
                            if(i.resultInfo.getAntDeviceNumber() == deviceFound.resultInfo.getAntDeviceNumber())
                            {
                                //Found already connected device, ignore
                                return;
                            }
                        }

                        //We split up devices already connected to the plugin from un-connected devices to make this information more visible to the user,
                        //since the user most likely wants to be aware of which device they are already using in another app
                        if(deviceFound.resultInfo.isAlreadyConnected())
                        {
                            mAlreadyConnectedDeviceInfos.add(deviceFound);
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if(adapter_connDevNameList.isEmpty())   //connected device category is invisible unless there are some present
                                    {
                                        findViewById(R.id.listView_AlreadyConnectedDevices).setVisibility(View.VISIBLE);
                                        findViewById(R.id.textView_AlreadyConnectedTitle).setVisibility(View.VISIBLE);
                                    }
                                    adapter_connDevNameList.add(deviceFound.resultInfo.getDeviceDisplayName());
                                    adapter_connDevNameList.notifyDataSetChanged();
                                }
                            });
                    }
                    else
                    {
                        mScannedDeviceInfos.add(deviceFound);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                adapter_devNameList.add(deviceFound.resultInfo.getDeviceDisplayName());
                                adapter_devNameList.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        );
        initScanDisplay();
    }

    protected IPluginAccessResultReceiver<AntPlusGenericRemoteControlPcc> IPluginAccessResultReceiver =
        new IPluginAccessResultReceiver<AntPlusGenericRemoteControlPcc>()
        {
        //Handle the result, connecting to events on success or reporting failure to user.
        @Override
        public void onResultReceived(AntPlusGenericRemoteControlPcc result, RequestAccessResult resultCode,
            DeviceState initialDeviceState)
        {
            showDataDisplay("Connecting...");
            switch(resultCode)
            {
                case SUCCESS:
                    remotePcc = result;
                    tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                    subscribeToEvents();
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    Toast.makeText(Activity_GenericRemoteControlSampler.this, "Channel Not Available", Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case OTHER_FAILURE:
                    Toast.makeText(Activity_GenericRemoteControlSampler.this, "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                case DEPENDENCY_NOT_INSTALLED:
                    tv_status.setText("Error. Do Menu->Reset.");
                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder(Activity_GenericRemoteControlSampler.this);
                    adlgBldr.setTitle("Missing Dependency");
                    adlgBldr.setMessage("The required service\n\"" + AntPlusGenericRemoteControlPcc.getMissingDependencyName() + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
                    adlgBldr.setCancelable(true);
                    adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent startStore = null;
                            startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + AntPlusGenericRemoteControlPcc.getMissingDependencyPackageName()));
                            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            Activity_GenericRemoteControlSampler.this.startActivity(startStore);
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
                    //TODO This flag indicates that an unrecognized value was sent by the service, an upgrade of your PCC may be required to handle this new value.
                    Toast.makeText(Activity_GenericRemoteControlSampler.this, "Failed: UNRECOGNIZED. Upgrade Required?", Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
                default:
                    Toast.makeText(Activity_GenericRemoteControlSampler.this, "Unrecognized result: " + resultCode, Toast.LENGTH_SHORT).show();
                    tv_status.setText("Error. Do Menu->Reset.");
                    break;
            }
        }
        };

    //Receives state changes and shows it on the status display line
    protected  IDeviceStateChangeReceiver IDeviceStateChangeReceiver =
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
                    tv_status.setText(remotePcc.getDeviceName() + ": " + newDeviceState);
                    if(newDeviceState == DeviceState.DEAD)
                        remotePcc = null;
                }
            });
        }
    };

    private void subscribeToEvents()
    {
        remotePcc.subscribeControlDeviceAvailabilityEvent(new IControlDeviceAvailabilityReceiver()
        {
            @Override
            public void onNewControlDeviceAvailability(final long estTimestamp,
                    final EnumSet<EventFlag> eventFlags,
                    final ControlDeviceCapabilities controlDeviceAvailabilities)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_maximumNumberRemotes.setText(String.valueOf(controlDeviceAvailabilities.getMaximumNumberRemotesConnected()));
                        tv_audioControlSupported.setText(String.valueOf(controlDeviceAvailabilities.getAudioControlSupported()));
                        tv_genericControlSupported.setText(String.valueOf(controlDeviceAvailabilities.getGenericControlSupported()));
                        tv_videoControlSupported.setText(String.valueOf(controlDeviceAvailabilities.getVideoControlSupported()));
                        tv_burstCommandSupported.setText(String.valueOf(controlDeviceAvailabilities.getBurstCommandSupported()));
                    }
                });
            }
        });

        remotePcc.subscribeBatteryStatusEvent(new IBatteryStatusReceiver()
        {
            @Override
            public void onNewBatteryStatus(final long estTimestamp,
                    EnumSet<EventFlag> eventFlags, final long cumulativeOperatingTime,
                    final BigDecimal batteryVoltage, final BatteryStatus batteryStatus,
                    final int cumulativeOperatingTimeResolution, int numberOfBatteries, int batteryIdentifier) {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_estTimestamp.setText(String.valueOf(estTimestamp));

                        tv_cumulativeOperatingTime.setText(String.valueOf(cumulativeOperatingTime));
                        tv_batteryVoltage.setText(batteryVoltage.toPlainString());
                        tv_batteryStatus.setText(batteryStatus.toString());
                        tv_cumulativeOperatingTimeResolution.setText(String.valueOf(cumulativeOperatingTimeResolution));
                    }
                });

            }
        });

        remotePcc.subscribeManufacturerIdentificationEvent(new IManufacturerIdentificationReceiver()
        {
            @Override
            public void onNewManufacturerIdentification(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final int hardwareRevision,
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

        remotePcc.subscribeProductInformationEvent(new IProductInformationReceiver()
        {
            @Override
            public void onNewProductInformation(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final int softwareRevision,
                    final int supplementalSoftwareRevision, final long serialNumber)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_estTimestamp.setText(String.valueOf(estTimestamp));

                        tv_softwareRevision.setText(String.valueOf(softwareRevision));
                        tv_serialNumber.setText(String.valueOf(serialNumber));
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(
                R.menu.activity_heart_rate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_reset:
                initScanDisplay();
                handleReset();
                tv_status.setText("Resetting...");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy()
    {
        if(remoteReleaseHandle != null)
        {
            remoteReleaseHandle.close();
            remoteReleaseHandle = null;
        }
        if(genericRemoteScanController != null)
        {
            genericRemoteScanController.closeScanController();
            genericRemoteScanController = null;
        }

        super.onDestroy();
    }
}
