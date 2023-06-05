/*
 * This software is subject to the license described in the License.txt file
 * included with this software distribution. You may not use this file except in compliance
 * with this license.
 *
 * Copyright (c) Garmin Canada Inc. 2019
 * All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.weightscale;

import com.dsi.ant.antplus.pluginsampler.R;
import com.garmin.fit.BatteryStatus;
import com.garmin.fit.DateTime;
import com.garmin.fit.DeviceInfoMesg;
import com.garmin.fit.Fit;
import com.garmin.fit.Manufacturer;

import java.io.Closeable;
import java.io.IOException;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Controls the device info display.
 */
public class LayoutController_WeightScaleDeviceInfo implements Closeable
{
    private ViewGroup viewGroup_Parent;
    private View view_layout;

    /**
     * Constructor.
     * @param inflator Parent view's inflator.
     * @param viewGroup_Parent Parent view.
     * @param fitDevInfoMsg Device Info data.
     */
    public LayoutController_WeightScaleDeviceInfo(LayoutInflater inflator, ViewGroup viewGroup_Parent, DeviceInfoMesg fitDevInfoMsg)
    {
        this.viewGroup_Parent = viewGroup_Parent;
        this.view_layout = inflator.inflate(R.layout.layout_device_info, null);

        TextView textView_Timestamp = (TextView)view_layout.findViewById(R.id.textView_Timestamp);
        TextView textView_DeviceIndex = (TextView)view_layout.findViewById(R.id.textView_DeviceIndex);
        TextView textView_DeviceType = (TextView)view_layout.findViewById(R.id.textView_DeviceType);
        TextView textView_Manufacturer = (TextView)view_layout.findViewById(R.id.textView_Manufacturer);
        TextView textView_SerialNumber = (TextView)view_layout.findViewById(R.id.textView_SerialNumber);
        TextView textView_Product = (TextView)view_layout.findViewById(R.id.textView_Product);
        TextView textView_SoftwareVersion = (TextView)view_layout.findViewById(R.id.textView_SoftwareVersion);
        TextView textView_HardwareVersion = (TextView)view_layout.findViewById(R.id.textView_HardwareVersion);
        TextView textView_CumulativeOperatingTime = (TextView)view_layout.findViewById(R.id.textView_CumulativeOperatingTime);
        TextView textView_BatteryVoltage = (TextView)view_layout.findViewById(R.id.textView_BatteryVoltage);
        TextView textView_BatteryStatus = (TextView)view_layout.findViewById(R.id.textView_BatteryStatus);

        //NOTE: All linked data messages must have the SAME timestamp
        if((fitDevInfoMsg.getTimestamp() != null) && (!fitDevInfoMsg.getTimestamp().getTimestamp().equals(DateTime.INVALID)))
            textView_Timestamp.setText(fitDevInfoMsg.getTimestamp().getTimestamp().toString() + "s");
        else
            textView_Timestamp.setText("N/A");

        if((fitDevInfoMsg.getDeviceIndex() != null) && (!fitDevInfoMsg.getDeviceIndex().equals(Fit.UINT8_INVALID)))
            textView_DeviceIndex.setText(fitDevInfoMsg.getDeviceIndex().toString());
        else
            textView_DeviceIndex.setText("N/A");

        if((fitDevInfoMsg.getDeviceType() != null) && (!fitDevInfoMsg.getDeviceType().equals(Fit.UINT8_INVALID)))
            textView_DeviceType.setText(fitDevInfoMsg.getDeviceType().toString());
        else
            textView_DeviceType.setText("N/A");

        if((fitDevInfoMsg.getManufacturer() != null) && (!fitDevInfoMsg.getManufacturer().equals(Manufacturer.INVALID)))
            textView_Manufacturer.setText(fitDevInfoMsg.getManufacturer().toString());
        else
            textView_Manufacturer.setText("N/A");

        if((fitDevInfoMsg.getSerialNumber() != null) && (!fitDevInfoMsg.getSerialNumber().equals(Fit.UINT32Z_INVALID)))
            textView_SerialNumber.setText(fitDevInfoMsg.getSerialNumber().toString());
        else
            textView_SerialNumber.setText("N/A");

        if((fitDevInfoMsg.getProduct() != null) && (!fitDevInfoMsg.getProduct().equals(Fit.UINT16_INVALID)))
            textView_Product.setText(fitDevInfoMsg.getProduct().toString());
        else
            textView_Product.setText("N/A");

        if((fitDevInfoMsg.getSoftwareVersion() != null) && (!fitDevInfoMsg.getSoftwareVersion().equals(Fit.UINT16_INVALID.floatValue())))
            textView_SoftwareVersion.setText(fitDevInfoMsg.getSoftwareVersion().toString());
        else
            textView_SoftwareVersion.setText("N/A");

        if((fitDevInfoMsg.getHardwareVersion() != null) && (!fitDevInfoMsg.getHardwareVersion().equals(Fit.UINT16_INVALID.shortValue())))
            textView_HardwareVersion.setText(fitDevInfoMsg.getHardwareVersion().toString());
        else
            textView_HardwareVersion.setText("N/A");

        if((fitDevInfoMsg.getCumOperatingTime() != null) && (!fitDevInfoMsg.getCumOperatingTime().equals(Fit.UINT32_INVALID)))
            textView_CumulativeOperatingTime.setText(fitDevInfoMsg.getCumOperatingTime().toString() + "s");
        else
            textView_CumulativeOperatingTime.setText("N/A");

        if((fitDevInfoMsg.getBatteryVoltage() != null) && (!fitDevInfoMsg.getBatteryVoltage().equals(Fit.UINT16_INVALID.floatValue())))
            textView_BatteryVoltage.setText(fitDevInfoMsg.getBatteryVoltage().toString() + "V");
        else
            textView_BatteryVoltage.setText("N/A");

        if((fitDevInfoMsg.getBatteryStatus() != null) && (!fitDevInfoMsg.getBatteryStatus().equals(BatteryStatus.INVALID)))
            textView_BatteryStatus.setText(fitDevInfoMsg.getBatteryStatus().toString());
        else
            textView_BatteryStatus.setText("N/A");

        viewGroup_Parent.addView(view_layout);
    }

    @Override
    public void close() throws IOException
    {
        viewGroup_Parent.removeView(view_layout);
        viewGroup_Parent = null;
        view_layout = null;
    }

}
