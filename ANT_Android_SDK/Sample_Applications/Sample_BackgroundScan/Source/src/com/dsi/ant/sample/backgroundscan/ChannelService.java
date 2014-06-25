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

import com.dsi.ant.sample.backgroundscan.BuildConfig;
import com.dsi.ant.sample.backgroundscan.ChannelController.ChannelBroadcastListener;

import com.dsi.ant.AntService;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.channel.AntChannelProvider;
import com.dsi.ant.channel.Capabilities;
import com.dsi.ant.channel.ChannelNotAvailableException;
import com.dsi.ant.channel.PredefinedNetwork;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;

public class ChannelService extends Service
{
    private static final String TAG = "ChannelService";
    
    private Object mCreateChannel_LOCK = new Object();
    
    ArrayList<ChannelInfo> mChannelInfoList = new ArrayList<ChannelInfo>();
    
    ChannelChangedListener mListener;

    private boolean mAntRadioServiceBound;
    private AntService mAntRadioService = null;
    private AntChannelProvider mAntChannelProvider = null;
    
    private boolean mAllowAcquireBackgroundScanChannel = false;
    private boolean mBackgroundScanAcquired = false;
    private boolean mBackgroundScanInProgress = false;
    private boolean mActivityIsRunning = false;
    
    private ChannelController mBackgroundScanController;
    
    private ServiceConnection mAntRadioServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mAntRadioService = new AntService(service);
            
            try {
                mAntChannelProvider = mAntRadioService.getChannelProvider();
                
                boolean mChannelAvailable = (getNumberOfChannelsAvailable() > 0);
                boolean legacyInterfaceInUse = mAntChannelProvider.isLegacyInterfaceInUse();
                
                // If there are channels OR legacy interface in use, allow
                // acquire background scan
                if (mChannelAvailable || legacyInterfaceInUse) {
                    mAllowAcquireBackgroundScanChannel = true;
                }
                else {
                    // If no channels available AND legacy interface is not in
                    // use, disallow acquire background scan
                    mAllowAcquireBackgroundScanChannel = false;
                }
                
                // Attempting to acquire a background scan channel when connected
                // to ANT Radio Service
                if (mAllowAcquireBackgroundScanChannel) {
                    acquireBackgroundScanningChannel();
                    
                    if(mListener != null) {
                        mListener.onAllowStartScan(!mBackgroundScanInProgress && mBackgroundScanAcquired);
                    }
                }
                
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ChannelNotAvailableException e) {
                // If channel is not available, do not allow to start scan
                if(mListener != null) {
                    mListener.onAllowStartScan(false);
                }
            }
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            die("Binder Died");
            
            mAntChannelProvider = null;
            mAntRadioService = null;
            
            mListener.onAllowStartScan(false);
            
            mAllowAcquireBackgroundScanChannel = false;
        }
        
    };
    
    public interface ChannelChangedListener
    {
        void onChannelChanged(ChannelInfo newInfo);
        void onAllowStartScan(boolean allowStartScan);
    }
    
    public class ChannelServiceComm extends Binder
    {
        void setOnChannelChangedListener(ChannelChangedListener listener)
        {
            mListener = listener;
        }
        
        ArrayList<ChannelInfo> getCurrentChannelInfoForAllChannels()
        {
            return mChannelInfoList;
        }
        
        void openBackgroundScanChannel() throws ChannelNotAvailableException
        {
            mBackgroundScanController.openBackgroundScanningChannel();
        }
        
        void stopBackgroundScan() { closeBackgroundScanChannel(); }
        
        void setActivityIsRunning(boolean isRunning) {
            mActivityIsRunning = isRunning;
            
            if(isRunning && mAllowAcquireBackgroundScanChannel) {
                try {
                    // If activity has started running; try and acquire
                    acquireBackgroundScanningChannel();
                    mListener.onAllowStartScan(!mBackgroundScanInProgress && mBackgroundScanAcquired);
                } catch (ChannelNotAvailableException e) {
                    mListener.onAllowStartScan(false);
                }
            }
            
        }
    }
    
    private void closeBackgroundScanChannel()
    {
        if(mBackgroundScanController != null)
        {
            mBackgroundScanController.close();
            mBackgroundScanAcquired = false;
        }
    }

    AntChannel acquireChannel() throws ChannelNotAvailableException
    {
        AntChannel mAntChannel = null;
        if(null != mAntChannelProvider)
        {
            try
            {
                /*
                 * In order to acquire a channel that is capable of background
                 * scanning, a Capabilities object must be created with the
                 * background scanning feature set to true. Passing this
                 * Capabilities object to acquireChannel() will return a channel
                 * that is capable of being assigned (via Extended Assignment)
                 * as a background scanning channel.
                 */
                Capabilities capableOfBackgroundScan = new Capabilities();
                capableOfBackgroundScan.supportBackgroundScanning(true);
                mAntChannel = mAntChannelProvider.acquireChannel(this, PredefinedNetwork.PUBLIC,
                        capableOfBackgroundScan);
                
                // Get background scan status
                mBackgroundScanInProgress = mAntChannel.getBackgroundScanState().isInProgress();

            } catch (RemoteException e)
            {
                die("ACP Remote Ex");
            }
        }        
        return mAntChannel;
    }
    
    public void acquireBackgroundScanningChannel() throws ChannelNotAvailableException
    {
        synchronized(mCreateChannel_LOCK)
        {
            // We only want one channel; don't attempt if already acquired
            if (!mBackgroundScanAcquired) {
                
                // Acquire a channel, if no exception then set background scan
                // acquired to true
                AntChannel antChannel = acquireChannel();
                mBackgroundScanAcquired = true;
                
                if (null != antChannel)
                {
                    ChannelBroadcastListener broadcastListener = new ChannelBroadcastListener()
                    {

                        @Override
                        public void onBroadcastChanged(ChannelInfo newInfo)
                        {
                            // Pass on the received channel info to activity for display
                            mListener.onChannelChanged(newInfo);
                        }

                        @Override
                        public void onBackgroundScanStateChange(boolean backgroundScanInProgress, boolean backgroundScanIsConfigured) {
                            if(mListener == null) return;
                            
                            mBackgroundScanInProgress = backgroundScanInProgress;
                            // Allow starting background scan if no scan in progress
                            mListener.onAllowStartScan(!mBackgroundScanInProgress && mBackgroundScanAcquired);
                        }
                        
                        @Override
                        public void onChannelDeath() {
                            // Cleanup Background Scan Channel
                            closeBackgroundScanChannel();
                            
                            if(mListener == null) return;
                            
                            mListener.onAllowStartScan(false);
                        }
                    };
                    mBackgroundScanController = new ChannelController(antChannel, broadcastListener);
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return new ChannelServiceComm();
    }
    
    // Receives Channel Provider state changes
    private final BroadcastReceiver mChannelProviderStateChangedReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // Only respond to state changes if activity is running.
            if(!mActivityIsRunning) return;
            
            if(AntChannelProvider.ACTION_CHANNEL_PROVIDER_STATE_CHANGED.equals(intent.getAction())) {
                int numChannels = intent.getIntExtra(AntChannelProvider.NUM_CHANNELS_AVAILABLE, 0);
                boolean legacyInterfaceInUse = intent.getBooleanExtra(AntChannelProvider.LEGACY_INTERFACE_IN_USE, false);
                
                if(numChannels > 0) {
                    // retrieve the number of channels with the background scanning capability
                    numChannels = getNumberOfChannelsAvailable();
                }
                
                if(mAllowAcquireBackgroundScanChannel) {
                    // Was a acquire channel allowed
                    // If no channels available AND legacy interface is not in use, disallow acquiring of channels
                    if(0 == numChannels && !legacyInterfaceInUse) {
                        // not any more
                        mAllowAcquireBackgroundScanChannel = false;
                    }
                } else {
                    // Acquire channels not allowed
                    // If there are channels OR legacy interface in use, allow acquiring of channels
                    if(numChannels > 0 || legacyInterfaceInUse) {
                        // now there are
                        mAllowAcquireBackgroundScanChannel = true;
                    }
                }
                
                if(null != mListener) {
                    if(mAllowAcquireBackgroundScanChannel) {
                        try {
                            // Try and acquire a channel to be used for background scanning
                            // If successful, allow user to start scan
                            acquireBackgroundScanningChannel();
                            mListener.onAllowStartScan(!mBackgroundScanInProgress && mBackgroundScanAcquired);
                        } catch (ChannelNotAvailableException e) {
                            // Channel is not yet available; disallow user to start scan
                            mListener.onAllowStartScan(false);
                        }
                    }
                }
            }
        }
    };
    
    private int getNumberOfChannelsAvailable() {
        if(null != mAntChannelProvider) {
            
            // In order to get the number of channels that are capable of
            // background scanning, a Capabilities object must be created with
            // the background scanning feature set to true.
            Capabilities capabilities = new Capabilities();
            capabilities.supportBackgroundScanning(true);

            try {
                // By passing in Capabilities object this will return the number
                // of channels capable of background scanning.
                int numChannels = mAntChannelProvider.getNumChannelsAvailable(capabilities);
                
                Log.i(TAG, "Number of channels with background scanning capabilities: " + numChannels);
                
                return numChannels;
            } catch (RemoteException e) {
                Log.i(TAG, "", e);
            }
        }
        return 0;
    }
    
    private void doBindAntRadioService()
    {
        if(BuildConfig.DEBUG) Log.v(TAG, "doBindAntRadioService");
        
        // Start listing for channel available intents
        registerReceiver(mChannelProviderStateChangedReceiver, new IntentFilter(AntChannelProvider.ACTION_CHANNEL_PROVIDER_STATE_CHANGED));
        
        mAntRadioServiceBound = AntService.bindService(this, mAntRadioServiceConnection);
    }
    
    private void doUnbindAntRadioService()
    {
        if(BuildConfig.DEBUG) Log.v(TAG, "doUnbindAntRadioService");
        
        // Stop listing for channel available intents
        try{
            unregisterReceiver(mChannelProviderStateChangedReceiver);
        } catch (IllegalArgumentException exception) {
            if(BuildConfig.DEBUG) Log.d(TAG, "Attempting to unregister a never registered Channel Provider State Changed receiver.");
        }
        
        if(mAntRadioServiceBound)
        {
            try
            {
                unbindService(mAntRadioServiceConnection);
            }
            catch(IllegalArgumentException e)
            {
                // Not bound, that's what we want anyway
            }

            mAntRadioServiceBound = false;
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        
        mAntRadioServiceBound = false;
        
        doBindAntRadioService();
    }
    
    @Override
    public void onDestroy()
    {
        closeBackgroundScanChannel();

        doUnbindAntRadioService();
        mAntChannelProvider = null;
        
        super.onDestroy();
    }

    static void die(String error)
    {
        Log.e(TAG, "DIE: "+ error);
    }
    
}
