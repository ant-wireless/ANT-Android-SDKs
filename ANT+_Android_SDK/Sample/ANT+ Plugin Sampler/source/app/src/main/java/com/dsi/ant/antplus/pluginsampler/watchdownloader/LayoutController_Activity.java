/*
 * This software is subject to the license described in the License.txt file
 * included with this software distribution. You may not use this file except in compliance
 * with this license.
 *
 * Copyright (c) Garmin Canada Inc. 2019
 * All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.watchdownloader;

import java.io.Closeable;
import java.io.IOException;

import com.dsi.ant.antplus.pluginsampler.R;
import com.garmin.fit.ActivityMesg;
import com.garmin.fit.DateTime;
import com.garmin.fit.Fit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Displays the activity data.
 */
public class LayoutController_Activity implements Closeable
{
    private ViewGroup viewGroup_Parent;
    private View view_layout;

    /**
     * Constructor.
     * @param inflator The inflator used to inflate the display.
     * @param viewGroup_Parent The viewgroup to add this display to.
     * @param mesg The activity data.
     */
    public LayoutController_Activity(LayoutInflater inflator, ViewGroup viewGroup_Parent, ActivityMesg mesg)
    {
        this.viewGroup_Parent = viewGroup_Parent;
        this.view_layout = inflator.inflate(R.layout.layout_activity, null);

        TextView textView_Timestamp = (TextView)view_layout.findViewById(R.id.textView_Timestamp);
        TextView textView_NumSessions = (TextView)view_layout.findViewById(R.id.textView_NumSessions);
        TextView textView_Type = (TextView)view_layout.findViewById(R.id.textView_Type);
        TextView textView_Event = (TextView)view_layout.findViewById(R.id.textView_Event);
        TextView textView_EventType = (TextView)view_layout.findViewById(R.id.textView_EventType);
        TextView textView_TotalTimerTime = (TextView)view_layout.findViewById(R.id.textView_TotalTimerTime);
        TextView textView_EventGroup = (TextView)view_layout.findViewById(R.id.textView_EventGroup);

        if((mesg.getTimestamp() != null) && (!mesg.getTimestamp().getTimestamp().equals(DateTime.INVALID)))
            textView_Timestamp.setText(mesg.getTimestamp().toString());
        else
            textView_Timestamp.setText("N/A");

        if((mesg.getNumSessions() != null) && (mesg.getNumSessions().intValue() != Fit.UINT16_INVALID))
            textView_NumSessions.setText(mesg.getNumSessions().toString());
        else
            textView_NumSessions.setText("N/A");

        if((mesg.getType() != null))
            textView_Type.setText(mesg.getType().toString());
        else
            textView_Type.setText("N/A");

        if((mesg.getEvent() != null))
            textView_Event.setText(mesg.getEvent().toString());
        else
            textView_Event.setText("N/A");

        if((mesg.getEventType() != null))
            textView_EventType.setText(mesg.getEventType().toString());
        else
            textView_EventType.setText("N/A");

        if((mesg.getTotalTimerTime() != null && !mesg.getTotalTimerTime().equals(Fit.FLOAT32_INVALID)))
            textView_TotalTimerTime.setText(mesg.getTotalTimerTime().toString() + "s");
        else
            textView_TotalTimerTime.setText("N/A");

        if((mesg.getEventGroup() != null && !mesg.getEventGroup().equals(Fit.UINT8_INVALID)))
            textView_EventGroup.setText(mesg.getTotalTimerTime().toString() + "s");
        else
            textView_EventGroup.setText("N/A");

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
