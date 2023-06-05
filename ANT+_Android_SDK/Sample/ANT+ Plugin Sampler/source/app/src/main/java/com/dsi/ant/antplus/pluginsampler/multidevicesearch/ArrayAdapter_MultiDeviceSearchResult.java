/*
 * This software is subject to the license described in the License.txt file
 * included with this software distribution. You may not use this file except in compliance
 * with this license.
 *
 * Copyright (c) Garmin Canada Inc. 2019
 * All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.multidevicesearch;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.antplus.pluginsampler.multidevicesearch.Activity_MultiDeviceSearchSampler.MultiDeviceSearchResultWithRSSI;

/**
 * Adapter that displays MultiDeviceSearchResultWithRSSI in a List View with
 * layout R.layout.layout_multidevice_searchresult
 */
public class ArrayAdapter_MultiDeviceSearchResult extends
    ArrayAdapter<MultiDeviceSearchResultWithRSSI>
{
    private static final int DEFAULT_MIN_RSSI = -1;

    private ArrayList<MultiDeviceSearchResultWithRSSI> mData;
    private String[] mDeviceTypes;
    private int mMinRSSI = DEFAULT_MIN_RSSI;

    public ArrayAdapter_MultiDeviceSearchResult(Context context,
        ArrayList<MultiDeviceSearchResultWithRSSI> data)
    {
        super(context, R.layout.layout_multidevice_searchresult, data);
        mData = data;
        mDeviceTypes = context.getResources().getStringArray(R.array.device_types);
    }

    /**
     * Update the display with new data for the specified position
     */
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_multidevice_searchresult, null);
        }

        MultiDeviceSearchResultWithRSSI i = mData.get(position);

        if (i != null)
        {
            TextView tv_deviceType = (TextView) convertView
                .findViewById(R.id.textView_multiDeviceType);
            TextView tv_deviceName = (TextView) convertView
                .findViewById(R.id.textView_multiDeviceName);
            ProgressBar pb_RSSI = (ProgressBar) convertView
                .findViewById(R.id.progressBar_multiDeviceRSSI);

            if (tv_deviceType != null)
            {
                tv_deviceType.setText(mDeviceTypes[i.mDevice.getAntDeviceType().ordinal()]);
            }
            if (tv_deviceName != null)
            {
                tv_deviceName.setText(i.mDevice.getDeviceDisplayName());
            }

            // only update once i.mRSSI value has been populated
            if (pb_RSSI != null && i.mRSSI != Integer.MIN_VALUE)
            {
                // display RSSI data
                if (pb_RSSI.getVisibility() != View.VISIBLE)
                {
                    convertView.findViewById(R.id.label_RSSI).setVisibility(View.VISIBLE);
                    pb_RSSI.setVisibility(View.VISIBLE);
                }

                // Device is nearest it can be, cap to zero
                if (i.mRSSI >= 0)
                {
                    i.mRSSI = 0;
                }

                // 0 is farthest away, (- mMinRSSI) is nearest
                int nearness = i.mRSSI - mMinRSSI;

                // find the new farthest
                if (nearness < 0)
                {
                    mMinRSSI = i.mRSSI;
                    nearness = 0;
                }

                int display = 100 * nearness / -mMinRSSI;
                pb_RSSI.setProgress(display);
            }
        }

        return convertView;
    }
}
