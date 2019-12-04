/*
 * This software is subject to the license described in the License.txt file
 * included with this software distribution. You may not use this file except in compliance
 * with this license.
 *
 * Copyright (c) Garmin Canada Inc. 2019
 * All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.bloodpressure;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dsi.ant.antplus.pluginsampler.R;
import com.garmin.fit.BloodPressureMesg;
import com.garmin.fit.DateTime;
import com.garmin.fit.Fit;
import com.garmin.fit.HrType;

import java.io.Closeable;
import java.io.IOException;

/**
 * Controls the blood pressure display
 */
public class LayoutController_BloodPressure implements Closeable
{
    private ViewGroup viewGroup_Parent;
    private View view_layout;

    /**
     * Constructor
     * @param inflator Parent view inflator.
     * @param viewGroup_Parent Parent view.
     * @param fitBpmMesg The blood pressure data.
     */
    public LayoutController_BloodPressure(LayoutInflater inflator, ViewGroup viewGroup_Parent, BloodPressureMesg fitBpmMesg)
    {
        this.viewGroup_Parent = viewGroup_Parent;
        this.view_layout = inflator.inflate(R.layout.layout_bloodpressure, null);

        TextView textView_Timestamp = (TextView)view_layout.findViewById(R.id.textView_Timestamp);
        TextView textView_UserProfileIndex = (TextView)view_layout.findViewById(R.id.textView_UserProfileIndex);
        TextView textView_SystolicPressure = (TextView)view_layout.findViewById(R.id.textView_SystolicPressure);
        TextView textView_DiastolicPressure = (TextView)view_layout.findViewById(R.id.textView_DiastolicPressure);
        TextView textView_MeanArterialPressure = (TextView)view_layout.findViewById(R.id.textView_MeanArterialPressure);
        TextView textView_HeartRate = (TextView)view_layout.findViewById(R.id.textView_HeartRate);
        TextView textView_MapThreeSampleMean = (TextView)view_layout.findViewById(R.id.textView_MapThreeSampleMean);
        TextView textView_MapMorningValues = (TextView)view_layout.findViewById(R.id.textView_MapMorningValues);
        TextView textView_MapEveningValues = (TextView)view_layout.findViewById(R.id.textView_MapEveningValues);
        TextView textView_HeartRateType = (TextView)view_layout.findViewById(R.id.textView_HeartRateType);
        TextView textView_Status = (TextView)view_layout.findViewById(R.id.textView_Status);

        //NOTE: All linked data messages must have the SAME timestamp
        if((fitBpmMesg.getTimestamp() != null) && (!fitBpmMesg.getTimestamp().getTimestamp().equals(DateTime.INVALID)))
            textView_Timestamp.setText(fitBpmMesg.getTimestamp().toString());
        else
            textView_Timestamp.setText("N/A");

        if((fitBpmMesg.getUserProfileIndex() != null) && (!fitBpmMesg.getUserProfileIndex().equals(Fit.UINT16_INVALID)))
            textView_UserProfileIndex.setText(fitBpmMesg.getUserProfileIndex().toString());
        else
            textView_UserProfileIndex.setText("N/A");

        if((fitBpmMesg.getSystolicPressure() != null) && (!fitBpmMesg.getSystolicPressure().equals(Fit.UINT16_INVALID)))
            textView_SystolicPressure.setText(fitBpmMesg.getSystolicPressure().toString() + "mmHg");
        else
            textView_SystolicPressure.setText("N/A");

        if((fitBpmMesg.getDiastolicPressure() != null) && (!fitBpmMesg.getDiastolicPressure().equals(Fit.UINT16_INVALID)))
            textView_DiastolicPressure.setText(fitBpmMesg.getDiastolicPressure().toString() + "mmHg");
        else
            textView_DiastolicPressure.setText("N/A");

        if((fitBpmMesg.getMeanArterialPressure() != null) && (!fitBpmMesg.getMeanArterialPressure().equals(Fit.UINT16_INVALID)))
            textView_MeanArterialPressure.setText(fitBpmMesg.getMeanArterialPressure().toString() + "mmHg");
        else
            textView_MeanArterialPressure.setText("N/A");

        if((fitBpmMesg.getHeartRate() != null) && (!fitBpmMesg.getHeartRate().equals(Fit.UINT8_INVALID)))
            textView_HeartRate.setText(fitBpmMesg.getHeartRate().toString() + "bpm");
        else
            textView_HeartRate.setText("N/A");

        if((fitBpmMesg.getMap3SampleMean() != null) && (!fitBpmMesg.getMap3SampleMean().equals(Fit.UINT16_INVALID)))
            textView_MapThreeSampleMean.setText(fitBpmMesg.getMap3SampleMean().toString() + "mmHg");
        else
            textView_MapThreeSampleMean.setText("N/A");

        if((fitBpmMesg.getMap3SampleMean() != null) && (!fitBpmMesg.getMap3SampleMean().equals(Fit.UINT16_INVALID)))
            textView_MapThreeSampleMean.setText(fitBpmMesg.getMap3SampleMean().toString() + "mmHg");
        else
            textView_MapThreeSampleMean.setText("N/A");

        if((fitBpmMesg.getMapMorningValues() != null) && (!fitBpmMesg.getMapMorningValues().equals(Fit.UINT16_INVALID)))
            textView_MapMorningValues.setText(fitBpmMesg.getMapMorningValues().toString() + "mmHg");
        else
            textView_MapMorningValues.setText("N/A");

        if((fitBpmMesg.getMapEveningValues() != null) && (!fitBpmMesg.getMapEveningValues().equals(Fit.UINT16_INVALID)))
            textView_MapEveningValues.setText(fitBpmMesg.getMapEveningValues().toString() + "mmHg");
        else
            textView_MapEveningValues.setText("N/A");

        if((fitBpmMesg.getHeartRateType() != null) && (!fitBpmMesg.getHeartRateType().equals(HrType.INVALID)))
            textView_HeartRateType.setText(fitBpmMesg.getHeartRateType().toString());
        else
            textView_HeartRateType.setText("N/A");

        if((fitBpmMesg.getStatus() != null))
            textView_Status.setText(fitBpmMesg.getStatus().toString());
        else
            textView_Status.setText("N/A");

        this.viewGroup_Parent.addView(view_layout, 0);
    }

    @Override
    public void close() throws IOException
    {
        viewGroup_Parent.removeView(view_layout);
        viewGroup_Parent = null;
        view_layout = null;
    }

}
