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
package com.dsi.ant.sample.acquirechannels;

import com.dsi.ant.channel.ChannelNotAvailableException;
import com.dsi.ant.sample.acquirechannels.ChannelService.ChannelChangedListener;
import com.dsi.ant.sample.acquirechannels.ChannelService.ChannelServiceComm;

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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class ChannelList extends Activity {
    private static final String TAG = ChannelList.class.getSimpleName();
    
    private final String PREF_TX_BUTTON_CHECKED_KEY = "ChannelList.TX_BUTTON_CHECKED";
    private boolean mCreateChannelAsMaster;
    
    private ChannelServiceComm mChannelService;
    
    private ArrayList<String> mChannelDisplayList = new ArrayList<String>();
    private ArrayAdapter<String> mChannelListAdapter;
    private SparseArray<Integer> mIdChannelListIndexMap = new SparseArray<Integer>();
    
    private boolean mChannelServiceBound = false;
    
    private void initButtons()
    {
        Log.v(TAG, "initButtons...");
        
        //Register Master/Slave Toggle handler
        ToggleButton toggleButton_MasterSlave = (ToggleButton)findViewById(R.id.toggleButton_MasterSlave);
        toggleButton_MasterSlave.setEnabled(mChannelServiceBound);
        toggleButton_MasterSlave.setChecked(mCreateChannelAsMaster);
        toggleButton_MasterSlave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean enabled)
            {
                mCreateChannelAsMaster = enabled;
            }
        });
        
        //Register Add Channel Button handler
        Button button_addChannel = (Button)findViewById(R.id.button_AddChannel);
        button_addChannel.setEnabled(mChannelServiceBound);
        button_addChannel.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addNewChannel(mCreateChannelAsMaster);
            }
        });
        
        //Register Clear Channels Button handler
        Button button_clearChannels = (Button)findViewById(R.id.button_ClearChannels);
        button_clearChannels.setEnabled(mChannelServiceBound);
        button_clearChannels.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clearAllChannels();
            }
        });
        
        Log.v(TAG, "...initButtons");
    }
    
    private void initPrefs()
    {
        Log.v(TAG, "initPrefs...");
        
        // Retrieves the app's current state of channel transmission mode 
        // from preferences to handle app resuming.
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        
        mCreateChannelAsMaster = preferences.getBoolean(PREF_TX_BUTTON_CHECKED_KEY, true);
        
        Log.v(TAG, "...initPrefs");
    }
    
    private void savePrefs()
    {
        Log.v(TAG, "savePrefs...");
        
        // Saves the app's current state of channel transmission mode to preferences
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        
        editor.putBoolean(PREF_TX_BUTTON_CHECKED_KEY, mCreateChannelAsMaster);
        
        editor.commit();
        
        Log.v(TAG, "...savePrefs");
    }
    
    private void doBindChannelService()
    {
        Log.v(TAG, "doBindChannelService...");
        
        // Binds to ChannelService. ChannelService binds and manages connection between the 
        // app and the ANT Radio Service
        Intent bindIntent = new Intent(this, ChannelService.class);
        startService(bindIntent);
        mChannelServiceBound = bindService(bindIntent, mChannelServiceConnection, Context.BIND_AUTO_CREATE);
        
        if(!mChannelServiceBound)   //If the bind returns false, run the unbind method to update the GUI
            doUnbindChannelService();
        
        Log.i(TAG, "  Channel Service binding = "+ mChannelServiceBound);
        
        Log.v(TAG, "...doBindChannelService");
    }
    
    private void doUnbindChannelService()
    {
        Log.v(TAG, "doUnbindChannelService...");
        
        if(mChannelServiceBound)
        {
            unbindService(mChannelServiceConnection);

            mChannelServiceBound = false;
        }
        
        ((Button)findViewById(R.id.button_ClearChannels)).setEnabled(false);
        ((Button)findViewById(R.id.button_AddChannel)).setEnabled(false);
        ((Button)findViewById(R.id.toggleButton_MasterSlave)).setEnabled(false);
        
        Log.v(TAG, "...doUnbindChannelService");
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v(TAG, "onCreate...");
        
        mChannelServiceBound = false;
        
        setContentView(R.layout.activity_channel_list);
        
        initPrefs();

        mChannelListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mChannelDisplayList);
        ListView listView_channelList = (ListView)findViewById(R.id.listView_channelList);
        listView_channelList.setAdapter(mChannelListAdapter);
        
        if(!mChannelServiceBound) doBindChannelService();
        
        initButtons();
        
        Log.v(TAG, "...onCreate");
    }
    
    public void onBack() {
        finish();
    }
    
    @Override
    public void onDestroy()
    {
        Log.v(TAG, "onDestroy...");
        
        doUnbindChannelService();
        
        if(isFinishing()) 
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
            
            // Sets a listener that handles channel events
            mChannelService.setOnChannelChangedListener(new ChannelChangedListener()
            {
                // Occurs when a channel has new info/data
                @Override
                public void onChannelChanged(final ChannelInfo newInfo)
                {
                    Integer index = mIdChannelListIndexMap.get(newInfo.deviceNumber);

                    if(null != index && index.intValue() < mChannelDisplayList.size())
                    {
                        mChannelDisplayList.set(index.intValue(), getDisplayText(newInfo));
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mChannelListAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }

                // Updates the UI to allow/disallow acquiring new channels 
                @Override
                public void onAllowAddChannel(boolean addChannelAllowed) {
                    // Enable Add Channel button and Master/Slave toggle if
                    // adding channels is allowed
                    ((Button)findViewById(R.id.button_AddChannel)).setEnabled(addChannelAllowed);
                    ((Button)findViewById(R.id.toggleButton_MasterSlave)).setEnabled(addChannelAllowed);
                }
            });

            // Initial check when connecting to ChannelService if adding channels is allowed
            boolean allowAcquireChannel = mChannelService.isAddChannelAllowed();
            ((Button)findViewById(R.id.button_AddChannel)).setEnabled(allowAcquireChannel);
            ((Button)findViewById(R.id.toggleButton_MasterSlave)).setEnabled(allowAcquireChannel);
            
            refreshList();
            
            Log.v(TAG, "...mChannelServiceConnection.onServiceConnected");
        }
        
        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            Log.v(TAG, "mChannelServiceConnection.onServiceDisconnected...");
            
            // Clearing and disabling when disconnecting from ChannelService
            mChannelService = null;
            
            ((Button)findViewById(R.id.button_ClearChannels)).setEnabled(false);
            ((Button)findViewById(R.id.button_AddChannel)).setEnabled(false);
            ((Button)findViewById(R.id.toggleButton_MasterSlave)).setEnabled(false);
            
            Log.v(TAG, "...mChannelServiceConnection.onServiceDisconnected");
        }
    };
    
    // This method is called when 'Add Channel' button is clicked
    private void addNewChannel(final boolean isMaster)
    {
        Log.v(TAG, "addNewChannel...");
        
        if(null != mChannelService)
        {
            ChannelInfo newChannelInfo;
            try
            {
                // Telling the ChannelService to add a new channel. This method
                // in ChannelService contains code required to acquire an ANT
                // channel from ANT Radio Service.
                newChannelInfo = mChannelService.addNewChannel(isMaster);
            } catch (ChannelNotAvailableException e)
            {
                // Occurs when a channel is not available. Printing out the
                // stack trace will show why no channels are available.
                Toast.makeText(this, "Channel Not Available", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if(null != newChannelInfo)
            {
                // Adding new channel info to the list
                addChannelToList(newChannelInfo);
                mChannelListAdapter.notifyDataSetChanged();
            }
        }
        
        Log.v(TAG, "...addNewChannel");
    }
    
    private void refreshList()
    {
        Log.v(TAG, "refreshList...");
        
        if(null != mChannelService)
        {
            ArrayList<ChannelInfo> chInfoList = mChannelService.getCurrentChannelInfoForAllChannels();

            mChannelDisplayList.clear();
            for(ChannelInfo i: chInfoList)
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
        mChannelDisplayList.add(getDisplayText(channelInfo));
        
        Log.v(TAG, "...addChannelToList");
    }
    

    private static String getDisplayText(ChannelInfo channelInfo)
    {
        Log.v(TAG, "getDisplayText...");
        String displayText = null;
        
        if(channelInfo.error)
        {
            displayText = String.format("#%-6d !:%s", channelInfo.deviceNumber, channelInfo.getErrorString());
        }
        else
        {
            if(channelInfo.isMaster)
            {
                displayText = String.format("#%-6d Tx:[%2d]", channelInfo.deviceNumber, channelInfo.broadcastData[0] & 0xFF);
            }
            else
            {
                displayText = String.format("#%-6d Rx:[%2d]", channelInfo.deviceNumber, channelInfo.broadcastData[0] & 0xFF);
            }
        }
        
        Log.v(TAG, "...getDisplayText");
        
        return displayText;
    }
    

    private void clearAllChannels()
    {
        Log.v(TAG, "clearAllChannels...");
        
        if(null != mChannelService)
        {
            // Telling ChannelService to close all the channels
            mChannelService.clearAllChannels();

            mChannelDisplayList.clear();
            mIdChannelListIndexMap.clear();
            mChannelListAdapter.notifyDataSetChanged(); 
        }
        
        Log.v(TAG, "...clearAllChannels");
    }
}
