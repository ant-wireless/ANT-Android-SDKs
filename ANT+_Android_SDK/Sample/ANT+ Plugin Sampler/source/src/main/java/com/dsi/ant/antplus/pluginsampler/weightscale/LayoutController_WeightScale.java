/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2013
All rights reserved.
*/

package com.dsi.ant.antplus.pluginsampler.weightscale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dsi.ant.antplus.pluginsampler.R;
import com.garmin.fit.DateTime;
import com.garmin.fit.Fit;
import com.garmin.fit.WeightScaleMesg;

import java.io.Closeable;
import java.io.IOException;

/**
 * Controls the blood pressure display
 */
public class LayoutController_WeightScale implements Closeable
{
    private ViewGroup viewGroup_Parent;
    private View view_layout;

    /**
     * Constructor
     * @param inflator Parent view inflator.
     * @param viewGroup_Parent Parent view.
     * @param fitWeightScaleMesg The weight scale data
     */
    public LayoutController_WeightScale(LayoutInflater inflator, ViewGroup viewGroup_Parent, WeightScaleMesg fitWeightScaleMesg)
    {
        this.viewGroup_Parent = viewGroup_Parent;
        this.view_layout = inflator.inflate(R.layout.layout_weightscale, null);

        TextView textView_Timestamp = (TextView)view_layout.findViewById(R.id.textView_Timestamp);
        TextView textView_UserProfileIndex = (TextView)view_layout.findViewById(R.id.textView_UserProfileIndex);
        TextView textView_Weight = (TextView)view_layout.findViewById(R.id.textView_Weight);
        TextView textView_PercentFat = (TextView)view_layout.findViewById(R.id.textView_PercentFat);
        TextView textView_PercentHydration = (TextView)view_layout.findViewById(R.id.textView_PercentHydration);
        TextView textView_VisceralFatMass = (TextView)view_layout.findViewById(R.id.textView_VisceralFatMass);
        TextView textView_BoneMass = (TextView)view_layout.findViewById(R.id.textView_BoneMass);
        TextView textView_MuscleMass = (TextView)view_layout.findViewById(R.id.textView_MuscleMass);
        TextView textView_BasalMet = (TextView)view_layout.findViewById(R.id.textView_BasalMet);
        TextView textView_ActiveMet = (TextView)view_layout.findViewById(R.id.textView_ActiveMet);

        //NOTE: All linked data messages must have the SAME timestamp
        if((fitWeightScaleMesg.getTimestamp() != null) && (!fitWeightScaleMesg.getTimestamp().getTimestamp().equals(DateTime.INVALID)))
            textView_Timestamp.setText(fitWeightScaleMesg.getTimestamp().getTimestamp().toString() + "s");
        else
            textView_Timestamp.setText("N/A");

        if((fitWeightScaleMesg.getUserProfileIndex() != null) && (!fitWeightScaleMesg.getUserProfileIndex().equals(Fit.UINT16_INVALID)))
            textView_UserProfileIndex.setText(fitWeightScaleMesg.getUserProfileIndex().toString());
        else
            textView_UserProfileIndex.setText("N/A");

        if((fitWeightScaleMesg.getWeight() != null) && (!fitWeightScaleMesg.getWeight().equals(Fit.UINT16_INVALID.floatValue())))
            textView_Weight.setText(fitWeightScaleMesg.getWeight().toString() + "kg");
        else
            textView_Weight.setText("N/A");

        if((fitWeightScaleMesg.getPercentFat() != null) && (!fitWeightScaleMesg.getPercentFat().equals(Fit.UINT16_INVALID.floatValue())))
            textView_PercentFat.setText(fitWeightScaleMesg.getPercentFat().toString() + "%");
        else
            textView_PercentFat.setText("N/A");

        if((fitWeightScaleMesg.getPercentHydration() != null) && (!fitWeightScaleMesg.getPercentHydration().equals(Fit.UINT16_INVALID.floatValue())))
            textView_PercentHydration.setText(fitWeightScaleMesg.getPercentHydration().toString() + "%");
        else
            textView_PercentHydration.setText("N/A");

        if((fitWeightScaleMesg.getVisceralFatMass() != null) && (!fitWeightScaleMesg.getVisceralFatMass().equals(Fit.UINT16_INVALID.floatValue())))
            textView_VisceralFatMass.setText(fitWeightScaleMesg.getVisceralFatMass().toString() + "kg");
        else
            textView_VisceralFatMass.setText("N/A");

        if((fitWeightScaleMesg.getBoneMass() != null) && (!fitWeightScaleMesg.getBoneMass().equals(Fit.UINT16_INVALID.floatValue())))
            textView_BoneMass.setText(fitWeightScaleMesg.getBoneMass().toString() + "kg");
        else
            textView_BoneMass.setText("N/A");

        if((fitWeightScaleMesg.getMuscleMass() != null) && (!fitWeightScaleMesg.getMuscleMass().equals(Fit.UINT16_INVALID.floatValue())))
            textView_MuscleMass.setText(fitWeightScaleMesg.getMuscleMass().toString() + "kg");
        else
            textView_MuscleMass.setText("N/A");

        if((fitWeightScaleMesg.getBasalMet() != null) && (!fitWeightScaleMesg.getBasalMet().equals(Fit.UINT16_INVALID.floatValue())))
            textView_BasalMet.setText(fitWeightScaleMesg.getBasalMet().toString() + "kcal/day");
        else
            textView_BasalMet.setText("N/A");

        if((fitWeightScaleMesg.getActiveMet() != null) && (!fitWeightScaleMesg.getActiveMet().equals(Fit.UINT16_INVALID.floatValue())))
            textView_ActiveMet.setText(fitWeightScaleMesg.getActiveMet().toString() + "kcal/day");
        else
            textView_ActiveMet.setText("N/A");

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
