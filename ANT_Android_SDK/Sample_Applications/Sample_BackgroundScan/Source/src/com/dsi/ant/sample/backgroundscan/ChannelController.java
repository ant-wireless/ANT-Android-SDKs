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

import android.os.RemoteException;
import android.util.Log;

import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.channel.AntCommandFailedException;
import com.dsi.ant.channel.BackgroundScanState;
import com.dsi.ant.channel.BurstState;
import com.dsi.ant.channel.EventBufferSettings;
import com.dsi.ant.channel.IAntAdapterEventHandler;
import com.dsi.ant.channel.IAntChannelEventHandler;
import com.dsi.ant.message.ChannelId;
import com.dsi.ant.message.ChannelType;
import com.dsi.ant.message.ExtendedAssignment;
import com.dsi.ant.message.LibConfig;
import com.dsi.ant.message.fromant.AcknowledgedDataMessage;
import com.dsi.ant.message.fromant.BroadcastDataMessage;
import com.dsi.ant.message.fromant.ChannelEventMessage;
import com.dsi.ant.message.fromant.DataMessage;
import com.dsi.ant.message.fromant.MessageFromAntType;
import com.dsi.ant.message.ipc.AntMessageParcel;

import java.util.Random;

public class ChannelController
{
    private static final int CHANNEL_PROOF_DEVICE_TYPE = 0x08;
    private static final int CHANNEL_PROOF_TRANSMISSION_TYPE = 1;
    
    // Set to 0 (wildcard) to find all devices.
    private static final int WILDCARD_SEARCH_DEVICE_NUMBER = 0;
    
    private static final int CHANNEL_PROOF_PERIOD = 32768; // 1 Hz
    private static final int CHANNEL_PROOF_FREQUENCY = 77;
    
    private static final String TAG = ChannelController.class.getSimpleName();
    
    private AntChannel mBackgroundScanChannel;
    private ChannelBroadcastListener mChannelBroadcastListener;
    
    private ChannelEventCallback mChannelEventCallback = new ChannelEventCallback();
    
    private boolean mIsOpen;
    private boolean mBackgroundScanInProgress = false;
    private boolean mBackgroundScanIsConfigured = false;

    static public abstract class ChannelBroadcastListener
    {
        public abstract void onBroadcastChanged(ChannelInfo newInfo);
        public abstract void onBackgroundScanStateChange(boolean backgroundScanInProgress, boolean backgroundScanIsConfigured);
        public abstract void onChannelDeath();
    }
    
    public ChannelController(AntChannel antChannel, ChannelBroadcastListener broadcastListener)
    {
        mBackgroundScanChannel = antChannel;
        
        try {
            if(antChannel != null) {
                // Checking the ANT chip's current background scan state; only one background scan can occur at a time
                mBackgroundScanInProgress = mBackgroundScanChannel.getBackgroundScanState().isInProgress();
                mBackgroundScanIsConfigured = mBackgroundScanChannel.getBackgroundScanState().isConfigured();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error communicating with ARS", e);
        }
        mChannelBroadcastListener = broadcastListener;
        mChannelBroadcastListener.onBackgroundScanStateChange(mBackgroundScanInProgress, mBackgroundScanIsConfigured);
        
        // Setting the received channel to be a background scanning channel
        configureBackgroundScanningChannel();
    }
    
    public void configureBackgroundScanningChannel() {
        // Setting the channel ID to search for any device (i.e. device number
        // is 0)
        ChannelId channelId = new ChannelId(WILDCARD_SEARCH_DEVICE_NUMBER,
                CHANNEL_PROOF_DEVICE_TYPE, CHANNEL_PROOF_TRANSMISSION_TYPE);

        try
        {
            // Setting channel and adapter event handlers
            mBackgroundScanChannel.setChannelEventHandler(mChannelEventCallback);
            
            // This adapter receives adapter wide events, such as background scan state changes
            mBackgroundScanChannel.setAdapterEventHandler(mAdapterEventHandler);

            // To set up a background scan channel, extended assign needs to be
            // used with background scanning set to enabled
            ExtendedAssignment extendedAssign = new ExtendedAssignment();
            extendedAssign.enableBackgroundScanning();
            
            // Assigning channel as slave (RX channel) with the desired extended
            // assignment features
            mBackgroundScanChannel.assign(ChannelType.BIDIRECTIONAL_SLAVE, extendedAssign);
            
            // Setting extended data to be received with each ANT message via
            // Lib Config
            LibConfig libconfig = new LibConfig();
            libconfig.setEnableChannelIdOutput(true);
            mBackgroundScanChannel.setAdapterWideLibConfig(libconfig);
            
            // Configuring channel
            mBackgroundScanChannel.setChannelId(channelId);
            mBackgroundScanChannel.setPeriod(CHANNEL_PROOF_PERIOD);
            mBackgroundScanChannel.setRfFrequency(CHANNEL_PROOF_FREQUENCY);
            
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        } catch (AntCommandFailedException e) {
            // This will release, and therefore unassign if required
            Log.e(TAG, "", e);
        }
    }
    
    public void openBackgroundScanningChannel() {
        if (null != mBackgroundScanChannel)
        {
            if (mIsOpen)
            {
                Log.w(TAG, "Channel was already opened");
            }
            else
            {
                try {
                    if (!mBackgroundScanInProgress) {
                        // Opening channel
                        mBackgroundScanChannel.open();
                        mIsOpen = true;
                        Log.d(TAG, "Opened background scanning channel");
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "", e);
                } catch (AntCommandFailedException e) {
                    // This will release, and therefore unassign if required
                    Log.e(TAG, "", e);
                }
            }
        }
        else
        {
            Log.w(TAG, "No channel available");
        }
    }

    private IAntAdapterEventHandler mAdapterEventHandler = new IAntAdapterEventHandler() {

        @Override
        public void onEventBufferSettingsChange(EventBufferSettings newEventBufferSettings) {
            // Not using the event buffer; can ignore these events
        }
        
        @Override
        // Called whenever the background scan state has changed
        public void onBackgroundScanStateChange(BackgroundScanState newBackgroundScanState) {
            Log.i(TAG, "Received Background scan state change: " + newBackgroundScanState.toString());
            
            // Applications can use this to determine if it is safe to
            // open a background scan if scan was previously used by other app
            mBackgroundScanInProgress = newBackgroundScanState.isInProgress();
            mBackgroundScanIsConfigured = newBackgroundScanState.isConfigured();
            
            mChannelBroadcastListener.onBackgroundScanStateChange(mBackgroundScanInProgress, mBackgroundScanIsConfigured);
        }

        @Override
        public void onBurstStateChange(BurstState newBurstSate) {
            // Not bursting; can ignore these events
        }

        @Override
        public void onLibConfigChange(LibConfig newLibConfig) {
            Log.i(TAG, "Received Lib Config change: " + newLibConfig.toString());
        }
        
    };
    
    public class ChannelEventCallback implements IAntChannelEventHandler
    {
        private void updateData(DataMessage dataMessage) {
            
            // Constructing channel info from extended data received from each received message
            int deviceNumber = dataMessage.getExtendedData().getChannelId().getDeviceNumber();
            ChannelInfo receivedChannelInfo = new ChannelInfo(deviceNumber, false);
            receivedChannelInfo.broadcastData = dataMessage.getPayload();
            // Passes found channel info onto ChannelService and then onto ChannelList
            mChannelBroadcastListener.onBroadcastChanged(receivedChannelInfo);
        }

        @Override
        public void onChannelDeath()
        {
            mChannelBroadcastListener.onChannelDeath();
        }
        
        @Override
        public void onReceiveMessage(MessageFromAntType messageType, AntMessageParcel antParcel) {
            Log.d(TAG, "Rx: "+ antParcel);

            switch(messageType)
            {
                // Only need to worry about receiving data
                case BROADCAST_DATA:
                    // Rx Data
                    updateData(new BroadcastDataMessage(antParcel));
                    break;
                case ACKNOWLEDGED_DATA:
                    // Rx Data
                    updateData(new AcknowledgedDataMessage(antParcel));
                    break;
                case CHANNEL_EVENT:
                    ChannelEventMessage eventMessage = new ChannelEventMessage(antParcel);
                    
                    switch(eventMessage.getEventCode())
                    {
                        case TX:
                        case RX_SEARCH_TIMEOUT:
                            // This channel is a background scanning channel and will not timeout
                        case CHANNEL_CLOSED:
                        case CHANNEL_COLLISION:
                        case RX_FAIL:
                        case RX_FAIL_GO_TO_SEARCH:
                        case TRANSFER_RX_FAILED:
                        case TRANSFER_TX_COMPLETED:
                        case TRANSFER_TX_FAILED:
                        case TRANSFER_TX_START:
                        case UNKNOWN:
                         // TODO More complex communication will need to handle these events
                            break;
                    }
                    break;
                case ANT_VERSION:
                case BURST_TRANSFER_DATA:
                case CAPABILITIES:
                case CHANNEL_ID:
                case CHANNEL_RESPONSE:
                case CHANNEL_STATUS:
                case SERIAL_NUMBER:
                case OTHER:
                 // TODO More complex communication will need to handle these message types
                    break;
            }
        }
    }
    
    public void close()
    {
        // TODO kill all our resources
        if (null != mBackgroundScanChannel)
        {
            mIsOpen = false;
            
            mBackgroundScanChannel.release();
            mBackgroundScanChannel = null;
        }
    }
}
