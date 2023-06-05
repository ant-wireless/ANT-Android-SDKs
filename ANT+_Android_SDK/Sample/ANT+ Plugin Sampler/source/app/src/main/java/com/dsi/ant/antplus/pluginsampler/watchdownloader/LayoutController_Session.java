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
import com.garmin.fit.DateTime;
import com.garmin.fit.Fit;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.Sport;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Displays the session data.
 */
public class LayoutController_Session implements Closeable
{
    private ViewGroup viewGroup_Parent;
    private View view_layout;

    /**
     * Constructor.
     * @param inflator The inflator used to inflate the display.
     * @param viewGroup_Parent The viewgroup to add this display to.
     * @param mesg The session data.
     */
    public LayoutController_Session(LayoutInflater inflator, ViewGroup viewGroup_Parent, SessionMesg mesg)
    {
        this.viewGroup_Parent = viewGroup_Parent;
        this.view_layout = inflator.inflate(R.layout.layout_session, null);

        TextView textView_Timestamp = (TextView)view_layout.findViewById(R.id.textView_Timestamp);
        TextView textView_StartTime = (TextView)view_layout.findViewById(R.id.textView_StartTime);
        TextView textView_ElapsedTime = (TextView)view_layout.findViewById(R.id.textView_ElapsedTime);
        TextView textView_Sport = (TextView)view_layout.findViewById(R.id.textView_Sport);
        TextView textView_Event = (TextView)view_layout.findViewById(R.id.textView_Event);
        TextView textView_EventType = (TextView)view_layout.findViewById(R.id.textView_EventType);

        if((mesg.getTimestamp() != null) && (!mesg.getTimestamp().getTimestamp().equals(DateTime.INVALID)))
            textView_Timestamp.setText(mesg.getTimestamp().toString());
        else
            textView_Timestamp.setText("N/A");

        if((mesg.getStartTime() != null) && (!mesg.getStartTime().getTimestamp().equals(DateTime.INVALID)))
            textView_StartTime.setText(mesg.getStartTime().toString());
        else
            textView_StartTime.setText("N/A");

        if((mesg.getTotalElapsedTime() != null) && (!mesg.getTotalElapsedTime().equals(Fit.FLOAT32_INVALID)))
            textView_ElapsedTime.setText(mesg.getTotalElapsedTime().toString() + "s");
        else
            textView_ElapsedTime.setText("N/A");

        if((mesg.getSport() != null) && (!mesg.getSport().equals(Sport.INVALID)))
            textView_Sport.setText(mesg.getSport().toString());
        else
            textView_Sport.setText("N/A");

        if((mesg.getEvent() != null))
            textView_Event.setText(mesg.getEvent().toString());
        else
            textView_Event.setText("N/A");

        if((mesg.getEventType() != null))
            textView_EventType.setText(mesg.getEventType().toString());
        else
            textView_EventType.setText("N/A");

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
