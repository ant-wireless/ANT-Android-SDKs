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
import com.garmin.fit.ActivityClass;
import com.garmin.fit.Fit;
import com.garmin.fit.Gender;
import com.garmin.fit.UserProfileMesg;

import java.io.Closeable;
import java.io.IOException;

/**
 * Blood Pressure User Profile Display.
 */
public class LayoutController_WeightScaleUserProfile implements Closeable
{
    private ViewGroup viewGroup_Parent;
    private View view_layout;

    /**
     * Constructor.
     * @param inflator Parent view's inflator.
     * @param viewGroup_Parent Parent view.
     * @param fitUserProfMesg User Profile data.
     */
    public LayoutController_WeightScaleUserProfile(LayoutInflater inflator, ViewGroup viewGroup_Parent, UserProfileMesg fitUserProfMesg)
    {
        this.viewGroup_Parent = viewGroup_Parent;
        this.view_layout = inflator.inflate(R.layout.layout_weightscale_user_profile, null);

        TextView textView_MessageIndex = (TextView)view_layout.findViewById(R.id.textView_MessageIndex);
        TextView textView_LocalId = (TextView)view_layout.findViewById(R.id.textView_LocalId);
        TextView textView_FriendlyName = (TextView)view_layout.findViewById(R.id.textView_FriendlyName);
        TextView textView_Gender = (TextView)view_layout.findViewById(R.id.textView_Gender);
        TextView textView_Age = (TextView)view_layout.findViewById(R.id.textView_Age);
        TextView textView_Height = (TextView)view_layout.findViewById(R.id.textView_Height);
        TextView textView_ActivityClass = (TextView)view_layout.findViewById(R.id.textView_ActivityClass);

        if((fitUserProfMesg.getMessageIndex() != null) && (!fitUserProfMesg.getMessageIndex().equals(Fit.UINT16_INVALID)))
            textView_MessageIndex.setText(fitUserProfMesg.getMessageIndex().toString());
        else
            textView_MessageIndex.setText("N/A");

        if((fitUserProfMesg.getLocalId() != null) && (!fitUserProfMesg.getLocalId().equals(Fit.UINT16_INVALID)))
            textView_LocalId.setText(fitUserProfMesg.getLocalId().toString());
        else
            textView_LocalId.setText("N/A");

        if((fitUserProfMesg.getFriendlyName() != null) && (!fitUserProfMesg.getFriendlyName().equals(Fit.STRING_INVALID)))
            textView_FriendlyName.setText(fitUserProfMesg.getFriendlyName());
        else
            textView_FriendlyName.setText("N/A");

        if((fitUserProfMesg.getGender() != null) && (!fitUserProfMesg.getGender().equals(Gender.INVALID)))
            textView_Gender.setText(fitUserProfMesg.getGender().toString());
        else
            textView_Gender.setText("N/A");

        if((fitUserProfMesg.getAge() != null) && (!fitUserProfMesg.getAge().equals(Fit.UINT8_INVALID)))
            textView_Age.setText(fitUserProfMesg.getAge().toString() + " years");
        else
            textView_Age.setText("N/A");

        if((fitUserProfMesg.getHeight() != null) && (!fitUserProfMesg.getHeight().equals(Fit.UINT8_INVALID.floatValue())))
            textView_Height.setText(fitUserProfMesg.getHeight().toString() + "m");
        else
            textView_Height.setText("N/A");

        if((fitUserProfMesg.getActivityClass() != null) && (!fitUserProfMesg.getActivityClass().equals(ActivityClass.INVALID)))
            textView_ActivityClass.setText(fitUserProfMesg.getActivityClass().toString());
        else
            textView_ActivityClass.setText("N/A");

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
