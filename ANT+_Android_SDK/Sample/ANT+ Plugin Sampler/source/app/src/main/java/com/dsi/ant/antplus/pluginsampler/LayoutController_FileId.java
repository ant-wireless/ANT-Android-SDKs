/*
 * This software is subject to the license described in the License.txt file
 * included with this software distribution. You may not use this file except in compliance
 * with this license.
 *
 * Copyright (c) Garmin Canada Inc. 2019
 * All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dsi.ant.antplus.pluginsampler.R;
import com.garmin.fit.DateTime;
import com.garmin.fit.File;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.Fit;
import com.garmin.fit.Manufacturer;

import java.io.Closeable;
import java.io.IOException;

/**
 * Blood Pressure File ID display
 */
public class LayoutController_FileId implements Closeable
{
    private ViewGroup viewGroup_Parent;
    private View view_layout;

    /**
     * Constructor.
     * @param inflator Parent view's inflator.
     * @param viewGroup_Parent Parent view.
     * @param fitFileIdMesg File ID data.
     */
    public LayoutController_FileId(LayoutInflater inflator, ViewGroup viewGroup_Parent, FileIdMesg fitFileIdMesg)
    {
        this.viewGroup_Parent = viewGroup_Parent;
        this.view_layout = inflator.inflate(R.layout.layout_file_id, null);

        TextView textView_Type = (TextView)view_layout.findViewById(R.id.textView_Type);
        TextView textView_Manufacturer = (TextView)view_layout.findViewById(R.id.textView_Manufacturer);
        TextView textView_Product = (TextView)view_layout.findViewById(R.id.textView_Product);
        TextView textView_SerialNumber = (TextView)view_layout.findViewById(R.id.textView_SerialNumber);
        TextView textView_TimeCreated = (TextView)view_layout.findViewById(R.id.textView_TimeCreated);
        TextView textView_Number = (TextView)view_layout.findViewById(R.id.textView_Number);

        if(fitFileIdMesg.getType() != null && fitFileIdMesg.getType() != File.INVALID)
            textView_Type.setText(fitFileIdMesg.getType().toString());
        else
            textView_Type.setText("N/A");

        if((fitFileIdMesg.getManufacturer() != null) && (!fitFileIdMesg.getManufacturer().equals(Manufacturer.INVALID)))
            textView_Manufacturer.setText(fitFileIdMesg.getManufacturer().toString());
        else
            textView_Manufacturer.setText("N/A");

        if((fitFileIdMesg.getProduct() != null) && (!fitFileIdMesg.getProduct().equals(Fit.UINT16_INVALID)))
            textView_Product.setText(fitFileIdMesg.getProduct().toString());
        else
            textView_Product.setText("N/A");

        if((fitFileIdMesg.getSerialNumber() != null) && (!fitFileIdMesg.getSerialNumber().equals(Fit.UINT32Z_INVALID)))
            textView_SerialNumber.setText(fitFileIdMesg.getSerialNumber().toString());
        else
            textView_SerialNumber.setText("N/A");

        if((fitFileIdMesg.getTimeCreated() != null) && (!fitFileIdMesg.getTimeCreated().getTimestamp().equals(DateTime.INVALID)))
            textView_TimeCreated.setText(fitFileIdMesg.getTimeCreated().toString());
        else
            textView_TimeCreated.setText("N/A");

        if((fitFileIdMesg.getNumber() != null) && (!fitFileIdMesg.getNumber().equals(Fit.UINT16_INVALID)))
            textView_Number.setText(fitFileIdMesg.getNumber().toString());
        else
            textView_Number.setText("N/A");

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
