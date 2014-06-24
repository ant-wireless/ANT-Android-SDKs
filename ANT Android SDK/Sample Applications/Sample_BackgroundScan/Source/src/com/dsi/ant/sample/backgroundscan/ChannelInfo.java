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

import com.dsi.ant.message.fromant.DataMessage;

public class ChannelInfo
{
    public final int deviceNumber;
    
    /** Master / Slave */
    public final boolean isMaster;
    
    public byte[] broadcastData = new byte[DataMessage.LENGTH_STANDARD_PAYLOAD];
    
    public boolean error;
    private String mErrorMessage;
    
    public ChannelInfo(int deviceNumber, boolean isMaster)
    {
        this.deviceNumber = deviceNumber;
        this.isMaster = isMaster;
        
        error = false;
        mErrorMessage = null;
    }
    
    public void die(String errorMessage)
    {
        error = true;
        mErrorMessage = errorMessage;
    }
    
    public String getErrorString()
    {
        return mErrorMessage;
    }
}
