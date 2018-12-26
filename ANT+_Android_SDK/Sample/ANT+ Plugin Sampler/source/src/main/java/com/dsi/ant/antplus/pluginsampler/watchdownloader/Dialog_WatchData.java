/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2013
All rights reserved.
*/

package com.dsi.ant.antplus.pluginsampler.watchdownloader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.LayoutController_FileId;
import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.plugins.antplus.common.FitFileCommon.FitFile;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWatchDownloaderPcc.DeviceInfo;
import com.garmin.fit.ActivityMesg;
import com.garmin.fit.ActivityMesgListener;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.FileIdMesgListener;
import com.garmin.fit.FitRuntimeException;
import com.garmin.fit.MesgBroadcaster;
import com.garmin.fit.SessionMesg;
import com.garmin.fit.SessionMesgListener;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays the activity info.
 */
public class Dialog_WatchData extends DialogFragment
{
    ArrayList<Closeable> layoutControllerList;
    List<FitFile> fitFileList;
    DeviceInfo deviceData;
    LinearLayout linearLayout_FitDataView;

    /**
     * Constructor.
     * @param deviceData The device this data is from.
     * @param fitFileList List of FIT files to decode and display.
     */
    public Dialog_WatchData(DeviceInfo deviceData, List<FitFile> fitFileList)
    {
        layoutControllerList = new ArrayList<Closeable>();
        this.fitFileList = fitFileList;
        this.deviceData = deviceData;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Data for: " + deviceData.getDisplayName());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View detailsView = inflater.inflate(R.layout.dialog_watchdata, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(detailsView);

        // Add action buttons
        builder.setPositiveButton("Close", null);

        linearLayout_FitDataView = (LinearLayout)detailsView.findViewById(R.id.linearLayout_DataCards);

        FileIdMesgListener fileIdMesgListener = new FileIdMesgListener()
        {
            @Override
            public void onMesg(final FileIdMesg mesg)
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        layoutControllerList.add(new LayoutController_FileId(inflater, linearLayout_FitDataView, mesg));
                    }
                });
            }
        };

        ActivityMesgListener activityMesgListener = new ActivityMesgListener()
        {
            @Override
            public void onMesg(final ActivityMesg mesg)
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        layoutControllerList.add(new LayoutController_Activity(inflater, linearLayout_FitDataView, mesg));
                    }
                });
            }
        };

        SessionMesgListener sessionMesgListener = new SessionMesgListener()
        {
            @Override
            public void onMesg(final SessionMesg mesg)
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        layoutControllerList.add(new LayoutController_Session(inflater, linearLayout_FitDataView, mesg));
                    }
                });
            }
        };

        MesgBroadcaster mesgBroadcaster;

        // If we have downloaded fit file to display, hides the no activities available text view
        if(!fitFileList.isEmpty()) {
            TextView textview = (TextView)detailsView.findViewById(R.id.textView_no_data_page);
            textview.setVisibility(TextView.INVISIBLE);
        }

        for(final FitFile downloadedFitFile : fitFileList)
            try
            {
                mesgBroadcaster = new MesgBroadcaster();
                mesgBroadcaster.addListener(activityMesgListener);
                mesgBroadcaster.addListener(fileIdMesgListener);
                mesgBroadcaster.addListener(sessionMesgListener);
                Log.e("WatchDownloaderSampler", "Begin decoding file");
                mesgBroadcaster.run(downloadedFitFile.getInputStream());
                Log.e("WatchDownloaderSampler", "End decoding file");
            }
            catch (FitRuntimeException e)
            {
                Log.e("WatchDownloaderSampler", "Error decoding FIT file: " + e.getMessage());
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getActivity(),
                                "Error decoding FIT file", Toast.LENGTH_LONG).show();
                    }
                });
            }

        return builder.create();
    }

    @Override
    public void onStart()
    {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            clearLayoutList();
                            dismiss();
                        }
                    });
                }
            });
        }
    }
    private void clearLayoutList()
    {
        if(!layoutControllerList.isEmpty())
        {
            for(Closeable controller : layoutControllerList)
                try
                {
                    controller.close();
                }
                catch (IOException e)
                {
                //Never happens
                }

            layoutControllerList.clear();
        }
    }
}

