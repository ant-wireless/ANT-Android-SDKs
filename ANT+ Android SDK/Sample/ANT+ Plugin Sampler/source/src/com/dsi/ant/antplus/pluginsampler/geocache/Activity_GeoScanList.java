/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2013
All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.geocache;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.Dialog_ProgressWaiter;
import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc.DeviceChangingCode;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc.GeocacheDeviceData;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc.GeocacheRequestStatus;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc.IAvailableDeviceListReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc.IDataDownloadFinishedReceiver;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Connects to Geocache Plugin and displays the available devices list.
 */
public class Activity_GeoScanList extends FragmentActivity
{
    AntPlusGeocachePcc geoPcc = null;
    PccReleaseHandle<AntPlusGeocachePcc> releaseHandle = null;

    TextView tv_status;

    List<Map<String,String>> deviceList_Display;
    SimpleAdapter adapter_deviceList_Display;

    boolean bDevicesInList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        tv_status = (TextView)findViewById(R.id.textView_Status);

        deviceList_Display = new ArrayList<Map<String,String>>();
        adapter_deviceList_Display = new SimpleAdapter(this, deviceList_Display, android.R.layout.simple_list_item_2, new String[]{"title","desc"}, new int[]{android.R.id.text1,android.R.id.text2});

        ListView listView_Devices = (ListView)findViewById(R.id.listView_deviceList);
        listView_Devices.setAdapter(adapter_deviceList_Display);


        //Set the list to download the data for the selected device and display it.
        listView_Devices.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
            {
                if(geoPcc == null)
                    return;

                if(!bDevicesInList)
                    return;

                int deviceID = Integer.parseInt(deviceList_Display.get(pos).get("desc"));

                final Dialog_ProgressWaiter progressDialog = new Dialog_ProgressWaiter("Downloading device data");

                boolean reqSubmitted = geoPcc.requestDeviceData(deviceID, true,
                    //Display the results if successful or report failures to user
                    new IDataDownloadFinishedReceiver()
                {
                    @Override
                    public void onNewDataDownloadFinished(GeocacheRequestStatus status,
                        GeocacheDeviceData downloadedData)
                    {
                        StringBuilder error = new StringBuilder("Error Downloading Data: ");

                        switch(status)
                        {
                            case SUCCESS:
                                final Dialog_GeoDeviceDetails detailsDialog = new Dialog_GeoDeviceDetails(geoPcc, downloadedData);
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        progressDialog.dismiss();
                                        detailsDialog.show(getSupportFragmentManager(), "DeviceDetails");
                                    }
                                });
                                return;

                            case FAIL_DEVICE_NOT_IN_LIST:
                                error.append("Device no longer in list");
                                break;
                            case FAIL_ALREADY_BUSY_EXTERNAL:
                                error.append("Device is busy");
                                break;
                            case FAIL_DEVICE_COMMUNICATION_FAILURE:
                                error.append("Communication with device failed");
                                break;
                            case UNRECOGNIZED:
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        Toast.makeText(Activity_GeoScanList.this,
                                            "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                                            Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            default:
                                break;
                        }

                        final String errorStr = error.toString();
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                progressDialog.dismiss();
                                Toast.makeText(Activity_GeoScanList.this, errorStr, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                },
                progressDialog.getUpdateReceiver()
                    );

                if(reqSubmitted)
                    progressDialog.show(getSupportFragmentManager(), "DownloadProgressDialog");
                else
                    Toast.makeText(Activity_GeoScanList.this, "Error Downloading Data: PCC already busy or dead", Toast.LENGTH_SHORT).show();
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

        //Reset the device list display
        bDevicesInList = false;
        deviceList_Display.clear();
        HashMap<String,String> listItem = new HashMap<String,String>();
        listItem.put("title", "No Devices Found");
        listItem.put("desc", "No results received from plugin yet...");
        deviceList_Display.add(listItem);
        adapter_deviceList_Display.notifyDataSetChanged();

        tv_status.setText("Connecting...");

        //Make the access request
        releaseHandle = AntPlusGeocachePcc.requestListAndRequestAccess(this,
            new IPluginAccessResultReceiver<AntPlusGeocachePcc>()
            {
            @Override
            public void onResultReceived(AntPlusGeocachePcc result, RequestAccessResult resultCode,
                DeviceState initialDeviceState)
            {
                switch(resultCode)
                {
                    case SUCCESS:
                        geoPcc = result;
                        tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                        geoPcc.requestCurrentDeviceList();
                        //subscribeToEvents();
                        break;
                    case CHANNEL_NOT_AVAILABLE:
                        Toast.makeText(Activity_GeoScanList.this, "Channel Not Available", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    case ADAPTER_NOT_DETECTED:
                        Toast.makeText(Activity_GeoScanList.this, "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    case BAD_PARAMS:
                        //Note: Since we compose all the params ourself, we should never see this result
                        Toast.makeText(Activity_GeoScanList.this, "Bad request parameters.", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    case OTHER_FAILURE:
                        Toast.makeText(Activity_GeoScanList.this, "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    case DEPENDENCY_NOT_INSTALLED:
                        tv_status.setText("Error. Do Menu->Reset.");
                        AlertDialog.Builder adlgBldr = new AlertDialog.Builder(Activity_GeoScanList.this);
                        adlgBldr.setTitle("Missing Dependency");
                        adlgBldr.setMessage("The required service\n\"" + AntPlusGeocachePcc.getMissingDependencyName() + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
                        adlgBldr.setCancelable(true);
                        adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent startStore = null;
                                startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + AntPlusGeocachePcc.getMissingDependencyPackageName()));
                                startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                Activity_GeoScanList.this.startActivity(startStore);
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
                        Toast.makeText(Activity_GeoScanList.this,
                            "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                            Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                    default:
                        Toast.makeText(Activity_GeoScanList.this, "Unrecognized result: " + resultCode, Toast.LENGTH_SHORT).show();
                        tv_status.setText("Error. Do Menu->Reset.");
                        break;
                }
            }
            },
            //Receives state changes and shows it on the status display line
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
                            tv_status.setText(geoPcc.getDeviceName() + ": " + newDeviceState);
                            if(newDeviceState == DeviceState.DEAD)
                                geoPcc = null;
                        }
                    });
                }
            },
            //Receives the device list updates and displays the current list
            new IAvailableDeviceListReceiver()
            {
                @Override
                public void onNewAvailableDeviceList(int[] deviceIDs,
                    String[] deviceIdentifierStrings, DeviceChangingCode changeCode,
                    int changingDeviceID)
                {
                    deviceList_Display.clear();

                    if(deviceIDs.length != 0)
                    {
                        bDevicesInList = true;
                        for(int i=0; i < deviceIDs.length; ++i)
                        {
                            if(deviceIdentifierStrings[i].trim().length() == 0)
                                deviceIdentifierStrings[i] = "<Unprogrammed Name>";
                            else if(deviceIdentifierStrings[i].contentEquals("_________"))
                                deviceIdentifierStrings[i] = "<Unprogrammed/Invalid Name>";

                            HashMap<String,String> listItem = new HashMap<String,String>();
                            listItem.put("title", deviceIdentifierStrings[i]);
                            listItem.put("desc", Integer.toString(deviceIDs[i]));

                            deviceList_Display.add(listItem);
                        }
                    }
                    else
                    {
                        bDevicesInList = false;
                        HashMap<String,String> listItem = new HashMap<String,String>();
                        listItem.put("title", "No Devices Found");
                        listItem.put("desc", "No geocaches sensors detected in range yet...");
                        deviceList_Display.add(listItem);
                    }

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            adapter_deviceList_Display.notifyDataSetChanged();
                        }
                    });
                }
            }
            );
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
