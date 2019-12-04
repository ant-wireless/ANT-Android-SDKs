/*
 * This software is subject to the license described in the License.txt file
 * included with this software distribution. You may not use this file except in compliance
 * with this license.
 *
 * Copyright (c) Garmin Canada Inc. 2019
 * All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.heartrate;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.AsyncScanResultDeviceInfo;
import com.dsi.ant.plugins.antplus.pccbase.AsyncScanController.IAsyncScanResultReceiver;

import java.util.ArrayList;



/**
 * Requests access to the heart rate using the asynchronous scan method, showing the scan results as a list.
 */
public class Activity_AsyncScanHeartRateSampler extends Activity_HeartRateDisplayBase
{
    TextView mTextView_Status;
    ArrayList<AsyncScanController.AsyncScanResultDeviceInfo> mAlreadyConnectedDeviceInfos;
    ArrayList<AsyncScanController.AsyncScanResultDeviceInfo> mScannedDeviceInfos;
    ArrayAdapter<String> adapter_devNameList;
    ArrayAdapter<String> adapter_connDevNameList;

    AsyncScanController<AntPlusHeartRatePcc> hrScanCtrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        initScanDisplay();
        super.onCreate(savedInstanceState);
    }

    private void initScanDisplay()
    {
        setContentView(com.dsi.ant.antplus.pluginsampler.R.layout.activity_async_scan);

        mTextView_Status = (TextView)findViewById(R.id.textView_Status);
        mTextView_Status.setText("Scanning for heart rate devices asynchronously...");

        mAlreadyConnectedDeviceInfos = new ArrayList<AsyncScanController.AsyncScanResultDeviceInfo>();
        mScannedDeviceInfos = new ArrayList<AsyncScanController.AsyncScanResultDeviceInfo>();

        //Setup already connected devices list
        adapter_connDevNameList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        ListView listView_alreadyConnectedDevs = (ListView)findViewById(R.id.listView_AlreadyConnectedDevices);
        listView_alreadyConnectedDevs.setAdapter(adapter_connDevNameList);
        listView_alreadyConnectedDevs.setOnItemClickListener(new OnItemClickListener()
                {
                    //Return the id of the selected already connected device
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
                    {
                        requestConnectToResult(mAlreadyConnectedDeviceInfos.get(pos));
                    }
                });


        //Setup found devices display list
        adapter_devNameList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        ListView listView_Devices = (ListView)findViewById(R.id.listView_FoundDevices);
        listView_Devices.setAdapter(adapter_devNameList);
        listView_Devices.setOnItemClickListener(new OnItemClickListener()
                {
                    //Return the id of the selected already connected device
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
                    {
                        requestConnectToResult(mScannedDeviceInfos.get(pos));
                    }
                });
    }

    /**
     * Requests access to the given search result.
     * @param asyncScanResultDeviceInfo The search result to attempt to connect to.
     */
    protected void requestConnectToResult(final AsyncScanResultDeviceInfo asyncScanResultDeviceInfo)
    {
        //Inform the user we are connecting
        runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        mTextView_Status.setText("Connecting to " + asyncScanResultDeviceInfo.getDeviceDisplayName());
                        releaseHandle = hrScanCtrl.requestDeviceAccess(asyncScanResultDeviceInfo,
                                new IPluginAccessResultReceiver<AntPlusHeartRatePcc>()
                                {
                                @Override
                                public void onResultReceived(AntPlusHeartRatePcc result,
                                    RequestAccessResult resultCode, DeviceState initialDeviceState)
                                {
                                    if(resultCode == RequestAccessResult.SEARCH_TIMEOUT)
                                    {
                                        //On a connection timeout the scan automatically resumes, so we inform the user, and go back to scanning
                                        runOnUiThread(new Runnable()
                                        {
                                            public void run()
                                            {
                                                Toast.makeText(Activity_AsyncScanHeartRateSampler.this, "Timed out attempting to connect, try again", Toast.LENGTH_LONG).show();
                                                mTextView_Status.setText("Scanning for heart rate devices asynchronously...");
                                            }
                                        });
                                    }
                                    else
                                    {
                                        //Otherwise the results, including SUCCESS, behave the same as
                                        base_IPluginAccessResultReceiver.onResultReceived(result, resultCode, initialDeviceState);
                                        hrScanCtrl = null;
                                    }
                                }
                                }, base_IDeviceStateChangeReceiver);
                    }
                });
    }

    /**
     * Requests the asynchronous scan controller
     */
    @Override
    protected void requestAccessToPcc()
    {
        initScanDisplay();
        hrScanCtrl = AntPlusHeartRatePcc.requestAsyncScanController(this, 0,
            new IAsyncScanResultReceiver()
        {
            @Override
            public void onSearchStopped(RequestAccessResult reasonStopped)
            {
                //The triggers calling this function use the same codes and require the same actions as those received by the standard access result receiver
                base_IPluginAccessResultReceiver.onResultReceived(null, reasonStopped, DeviceState.DEAD);
            }

            @Override
            public void onSearchResult(final AsyncScanResultDeviceInfo deviceFound)
            {
                for(AsyncScanResultDeviceInfo i: mScannedDeviceInfos)
                {
                    //The current implementation of the async scan will reset it's ignore list every 30s,
                    //So we have to handle checking for duplicates in our list if we run longer than that
                    if(i.getAntDeviceNumber() == deviceFound.getAntDeviceNumber())
                    {
                        //Found already connected device, ignore
                        return;
                    }
                }

                //We split up devices already connected to the plugin from un-connected devices to make this information more visible to the user,
                //since the user most likely wants to be aware of which device they are already using in another app
                if(deviceFound.isAlreadyConnected())
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
                            adapter_connDevNameList.add(deviceFound.getDeviceDisplayName());
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
                            adapter_devNameList.add(deviceFound.getDeviceDisplayName());
                            adapter_devNameList.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }



    /**
     * Ensures our controller is closed whenever we reset
     */
    @Override
    protected void handleReset()
    {
        if(hrScanCtrl != null)
        {
            hrScanCtrl.closeScanController();
            hrScanCtrl = null;
        }
        super.handleReset();
    }

    /**
     * Ensures our controller is closed whenever we exit
     */
    @Override
    protected void onDestroy()
    {
        if(hrScanCtrl != null)
        {
            hrScanCtrl.closeScanController();
            hrScanCtrl = null;
        }
        super.onDestroy();
    }
}
