/*
 * This software is subject to the license described in the License.txt file
 * included with this software distribution. You may not use this file except in compliance
 * with this license.
 *
 * Copyright (c) Garmin Canada Inc. 2019
 * All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.geocache;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.Dialog_ProgressWaiter;
import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc.GeocacheRequestStatus;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc.IAuthTokenRequestFinishedReceiver;

import java.util.Random;

/**
 * Dialog allowing to request an authentication token from a geocache device.
 */
public class Dialog_GeoRequestAuthToken extends DialogFragment
{
    EditText editText_Nonce;
    EditText editText_SerialNumber;
    TextView textView_Status;

    private AntPlusGeocachePcc geoPcc;
    private long requestingDeviceSerialNum;
    private int deviceID;

    /**
     * Constructor.
     * @param geoPcc The PCC instance the device is available on.
     * @param deviceID The device ID of the device to request a token from.
     * @param requestingDeviceSerialNum The serial number of the application making the request.
     */
    public Dialog_GeoRequestAuthToken(AntPlusGeocachePcc geoPcc, int deviceID, long requestingDeviceSerialNum)
    {
        this.geoPcc = geoPcc;
        this.deviceID = deviceID;
        this.requestingDeviceSerialNum = requestingDeviceSerialNum;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Auth Token Request");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View detailsView = inflater.inflate(R.layout.dialog_geocache_reqauthtoken, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(detailsView);

        // Add action buttons
        //Note we override the positive button in show() below so we can prevent it from closing
        builder.setPositiveButton("Request Token", null);
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Let dialog dismiss
            }
        });

        editText_Nonce = (EditText)detailsView.findViewById(R.id.editText_Nonce);
        editText_SerialNumber = (EditText)detailsView.findViewById(R.id.editText_SerialNumber);
        textView_Status = (TextView)detailsView.findViewById(R.id.textView_Status);

        Random r = new Random();
        editText_Nonce.setText(String.valueOf(r.nextInt(65535)));
        editText_SerialNumber.setText(String.valueOf(requestingDeviceSerialNum));

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
                public void onClick(View v)
                {
                    final Dialog_ProgressWaiter progressDialog = new Dialog_ProgressWaiter("Requesting Auth Token");

                    int nonce;
                    long serialNum;
                    try
                    {
                        nonce = Integer.parseInt(editText_Nonce.getText().toString());
                        serialNum = Long.parseLong(editText_SerialNumber.getText().toString());
                    }
                    catch(NumberFormatException e)
                    {
                        Toast.makeText(getActivity(), "Could not parse number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //Use the current deviceID and PIN
                    boolean reqSubmitted = geoPcc.requestAuthToken(deviceID, nonce, serialNum,
                        new IAuthTokenRequestFinishedReceiver()
                    {
                        @Override
                        public void onNewAuthTokenRequestFinished(
                            GeocacheRequestStatus status, final long authToken)
                        {
                            StringBuilder resultDesc = new StringBuilder("Error Requesting Token: ");

                            switch(status)
                            {
                                case SUCCESS:
                                    getActivity().runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            progressDialog.dismiss();
                                            textView_Status.setText("Authentication token received: 0x" + Long.toHexString(authToken));
                                        }
                                    });
                                    return;

                                case FAIL_DEVICE_NOT_IN_LIST:
                                    resultDesc.append("Device no longer in list");
                                    break;
                                case FAIL_ALREADY_BUSY_EXTERNAL:
                                    resultDesc.append("Device is busy");
                                    break;
                                case FAIL_DEVICE_COMMUNICATION_FAILURE:
                                    resultDesc.append("Communication with device failed");
                                    break;
                                case FAIL_BAD_PARAMS:
                                    resultDesc.append("Bad Parameters");
                                    break;
                                case UNRECOGNIZED:
                                    //TODO This flag indicates that an unrecognized value was sent by the service, an upgrade of your PCC may be required to handle this new value.
                                    resultDesc.append("Unrecognized failure");
                                    break;
                            }

                            final String resultStr = resultDesc.toString();
                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), resultStr, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    },
                    progressDialog.getUpdateReceiver());

                    if(reqSubmitted)
                        progressDialog.show(getActivity().getSupportFragmentManager(), "RequestAuthTokenDialog");
                    else
                        Toast.makeText(getActivity(), "Error Requesting Token: PCC already busy or dead", Toast.LENGTH_SHORT).show();


                    //now both dialogs stay open. They get closed in the programming result handler.
                }
            });
        }
    }
}
