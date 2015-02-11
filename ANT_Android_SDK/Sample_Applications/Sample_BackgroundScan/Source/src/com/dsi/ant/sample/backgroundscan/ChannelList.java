/*
 * Copyright 2012 Dynastream Innovations Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.dsi.ant.sample.backgroundscan;

import com.dsi.ant.channel.ChannelNotAvailableException;
import com.dsi.ant.sample.backgroundscan.R;
import com.dsi.ant.sample.backgroundscan.ChannelService.ChannelChangedListener;
import com.dsi.ant.sample.backgroundscan.ChannelService.ChannelServiceComm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChannelList extends Activity {
    private static final String TAG = ChannelList.class.getSimpleName();

    private final String PREF_SCAN_STARTED_KEY = "ChannelList.SCAN_STARTED";
    private boolean mScanStarted = false;

    private ChannelServiceComm mChannelService;

    private ArrayList<String> mChannelDisplayList = new ArrayList<String>();
    private ArrayAdapter<String> mChannelListAdapter;
    private SparseArray<Integer> mIdChannelListIndexMap = new SparseArray<Integer>();

    private boolean mChannelServiceBound = false;

    private void initButtons()
    {
        Log.v(TAG, "initButtons...");

        // Register Start Scan Button handler
        Button button_startScan = (Button) findViewById(R.id.button_Scan);
        Button button_stopScan = (Button) findViewById(R.id.button_StopScan);
        button_startScan.setEnabled(false);

        button_startScan.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mChannelDisplayList.clear();
                mIdChannelListIndexMap.clear();
                mChannelListAdapter.notifyDataSetChanged();
                
                startBackgroundScan();
            }
        });

        button_stopScan.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stopBackgroundScan();
            }
        });

        Log.v(TAG, "...initButtons");
    }

    private void initPrefs()
    {
        Log.v(TAG, "initPrefs...");

        // Handle resuming the current state of data collection as saved in the
        // preference
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        mScanStarted = preferences.getBoolean(PREF_SCAN_STARTED_KEY, false);

        Log.v(TAG, "...initPrefs");
    }

    private void savePrefs()
    {
        Log.v(TAG, "savePrefs...");

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(PREF_SCAN_STARTED_KEY, mScanStarted);

        editor.commit();

        Log.v(TAG, "...savePrefs");
    }

    private void doBindChannelService()
    {
        Log.v(TAG, "doBindChannelService...");

        Intent bindIntent = new Intent(this, ChannelService.class);
        startService(bindIntent);
        mChannelServiceBound = bindService(bindIntent, mChannelServiceConnection,
                Context.BIND_AUTO_CREATE);

        if (!mChannelServiceBound) // If the bind returns false, run the unbind
                                   // method to update the GUI
            doUnbindChannelService();

        Log.i(TAG, "  Channel Service binding = " + mChannelServiceBound);

        Log.v(TAG, "...doBindChannelService");
    }

    private void doUnbindChannelService()
    {
        Log.v(TAG, "doUnbindChannelService...");

        if (mChannelServiceBound)
        {
            unbindService(mChannelServiceConnection);

            mChannelServiceBound = false;
        }

        Log.v(TAG, "...doUnbindChannelService");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate...");

        mChannelServiceBound = false;

        setContentView(R.layout.activity_channel_list);

        initPrefs();

        mChannelListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, mChannelDisplayList);
        ListView listView_channelList = (ListView) findViewById(R.id.listView_channelList);
        listView_channelList.setAdapter(mChannelListAdapter);

        if (!mChannelServiceBound)
            doBindChannelService();

        initButtons();

        Log.v(TAG, "...onCreate");
    }

    public void onBack() {
        finish();
    }

    @Override
    protected void onResume() {
        // if null then will be set in onServiceConnected()
        if(mChannelService != null) {
            mChannelService.setActivityIsRunning(true);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mChannelService != null) {
            mChannelService.setActivityIsRunning(false);
        }
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        Log.v(TAG, "onDestroy...");

        doUnbindChannelService();

        if (isFinishing())
        {
            stopService(new Intent(this, ChannelService.class));
        }

        mChannelServiceConnection = null;

        savePrefs();

        Log.v(TAG, "...onDestroy");

        super.onDestroy();
    }

    private ServiceConnection mChannelServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder)
        {
            Log.v(TAG, "mChannelServiceConnection.onServiceConnected...");

            mChannelService = (ChannelServiceComm) serviceBinder;

            mChannelService.setOnChannelChangedListener(new ChannelChangedListener()
            {
                @Override
                public void onChannelChanged(final ChannelInfo newInfo)
                {
                    final Integer index = mIdChannelListIndexMap.get(newInfo.deviceNumber);

                    // If found channel info is not in list, add it
                    if (index == null) {
                        addChannelToList(newInfo);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mChannelDisplayList.add(getDisplayText(newInfo));
                                mChannelListAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mChannelDisplayList.set(index.intValue(), getDisplayText(newInfo));
                                mChannelListAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
                
                @Override
                public void onAllowStartScan(final boolean allowStartScan) {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ((Button) findViewById(R.id.button_Scan)).setEnabled(allowStartScan);
                        }
                    });
                }
            });

            refreshList();
            mChannelService.setActivityIsRunning(true);

            Log.v(TAG, "...mChannelServiceConnection.onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            Log.v(TAG, "mChannelServiceConnection.onServiceDisconnected...");

            mChannelService = null;

            ((Button) findViewById(R.id.button_Scan)).setEnabled(false);

            Log.v(TAG, "...mChannelServiceConnection.onServiceDisconnected");
        }
    };

    private void startBackgroundScan()
    {
        Log.v(TAG, "startBackgroundScan...");

        if (null != mChannelService)
        {
            try
            {
                mChannelService.openBackgroundScanChannel();
            } catch (ChannelNotAvailableException e)
            {
                Toast.makeText(this, "Channel Not Available", Toast.LENGTH_SHORT).show();
            }
        }

        Log.v(TAG, "...startBackgroundScan");
    }

    private void refreshList()
    {
        Log.v(TAG, "refreshList...");

        if (null != mChannelService)
        {
            ArrayList<ChannelInfo> chInfoList = mChannelService
                    .getCurrentChannelInfoForAllChannels();

            mChannelDisplayList.clear();
            for (ChannelInfo i : chInfoList)
            {
                addChannelToList(i);
            }
            mChannelListAdapter.notifyDataSetChanged();
        }

        Log.v(TAG, "...refreshList");
    }

    private void addChannelToList(ChannelInfo channelInfo)
    {
        Log.v(TAG, "addChannelToList...");

        mIdChannelListIndexMap.put(channelInfo.deviceNumber, mChannelDisplayList.size());

        Log.v(TAG, "...addChannelToList");
    }

    private static String getDisplayText(ChannelInfo channelInfo)
    {
        Log.v(TAG, "getDisplayText...");
        String displayText = null;

        if (channelInfo.error)
        {
            displayText = String.format("#%-6d !:%s", channelInfo.deviceNumber,
                    channelInfo.getErrorString());
        }
        else
        {
            if (channelInfo.isMaster)
            {
                displayText = String.format("#%-6d Tx:[%2d]", channelInfo.deviceNumber,
                        channelInfo.broadcastData[0] & 0xFF);
            }
            else
            {
                displayText = String.format("#%-6d Rx:[%2d]", channelInfo.deviceNumber,
                        channelInfo.broadcastData[0] & 0xFF);
            }
        }

        Log.v(TAG, "...getDisplayText");

        return displayText;
    }

    private void stopBackgroundScan()
    {
        Log.v(TAG, "clearAllChannels...");

        if (null != mChannelService)
        {
            mChannelService.stopBackgroundScan();

            mChannelDisplayList.clear();
            mIdChannelListIndexMap.clear();
            mChannelListAdapter.notifyDataSetChanged();
        }

        Log.v(TAG, "...clearAllChannels");
    }
}
