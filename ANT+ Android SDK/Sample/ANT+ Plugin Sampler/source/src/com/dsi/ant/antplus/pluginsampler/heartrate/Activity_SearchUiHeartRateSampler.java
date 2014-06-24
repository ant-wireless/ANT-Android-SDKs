/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2013
All rights reserved.
*/

package com.dsi.ant.antplus.pluginsampler.heartrate;

import android.content.Intent;
import android.os.Bundle;

import com.dsi.ant.antplus.pluginsampler.multidevicesearch.Activity_MultiDeviceSearchSampler;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult;

/**
 * Requests access to the heart rate using the plugin automatic search activity.
 */
public class Activity_SearchUiHeartRateSampler extends Activity_HeartRateDisplayBase
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        showDataDisplay("Connecting...");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void requestAccessToPcc()
    {
        Intent intent = getIntent();
        if (intent.hasExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT))
        {
            // device has already been selected through the multi-device search
            MultiDeviceSearchResult result = intent
                .getParcelableExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT);
            releaseHandle = AntPlusHeartRatePcc.requestAccess(this, result.getAntDeviceNumber(), 0,
                base_IPluginAccessResultReceiver, base_IDeviceStateChangeReceiver);
        } else
        {
            // starts the plugins UI search
            releaseHandle = AntPlusHeartRatePcc.requestAccess(this, this,
                base_IPluginAccessResultReceiver, base_IDeviceStateChangeReceiver);
        }
    }
}
