/*
 * This software is subject to the license described in the License.txt file
 * included with this software distribution. You may not use this file except in compliance
 * with this license.
 *
 * Copyright (c) Garmin Canada Inc. 2019
 * All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc.ISimpleProgressUpdateReceiver;

/**
 * Displays a modal waiting dialog to wait for a result and optionally show progress reports.
 */
public class Dialog_ProgressWaiter extends DialogFragment
{
    TextView textView_status;
    String actionDescription;

    /**
     * Constructor.
     * @param actionDescription The description of the task being waited for to display in the dialog.
     */
    public Dialog_ProgressWaiter(String actionDescription)
    {
        this.actionDescription = actionDescription;
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View detailsView = inflater.inflate(R.layout.dialog_progresswaiter, null);
        builder.setView(detailsView);

        textView_status = (TextView)detailsView.findViewById(R.id.textView_Status);
        setStatus(actionDescription + "...");

        return builder.create();
    }

    /**
     * Returns an update receiver that will show the progress updates on the dialog display.
     * @return The update receiver.
     */
    public ISimpleProgressUpdateReceiver getUpdateReceiver()
    {
        return updateReceiver;
    }

    private ISimpleProgressUpdateReceiver updateReceiver = new ISimpleProgressUpdateReceiver()
            {
                @Override
                public void onNewSimpleProgressUpdate(int workUnitsFinished, int totalUnitsWork)
                {
                    Log.e("DBG-WAIT", "Progress update " + workUnitsFinished + "/" + totalUnitsWork);
                    if(totalUnitsWork > 0)
                    {
                        setStatus(actionDescription + ": " + workUnitsFinished + "/" + totalUnitsWork);
                    }
                }
            };

    private void setStatus(final String newStatus)
    {
        getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        textView_status.setText(newStatus);
                    }
                });
    }
}
