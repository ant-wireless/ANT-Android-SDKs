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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.Settings;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc.GeocacheDeviceData;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc.ProgrammableGeocacheDeviceData;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.TimeZone;

/**
 * Displays the device details and allows launching the programming or request access token screens.
 */
public class Dialog_GeoDeviceDetails extends DialogFragment
{
    TextView textView_IdString;
    TextView textView_PIN;
    TextView textView_Latitude;
    TextView textView_Longitude;
    TextView textView_HintString;
    TextView textView_LastVisit;
    TextView textView_NumVisits;
    TextView textView_HardwareVer;
    TextView textView_ManfID;
    TextView textView_ModelNum;
    TextView textView_SoftwareVer;
    TextView textView_SerialNum;
    TextView textView_BatteryVoltage;
    TextView textView_BatteryStatus;
    TextView textView_OperatingTime;
    TextView textView_OperatingTimeResolution;

    private GeocacheDeviceData deviceData;
    private AntPlusGeocachePcc geoPcc;

    /**
     * Constructor.
     * @param geoPcc The PCC instance the device is available on.
     * @param deviceData The device data of the device.
     */
    public Dialog_GeoDeviceDetails(AntPlusGeocachePcc geoPcc,
            GeocacheDeviceData deviceData)
    {
        this.geoPcc = geoPcc;
        this.deviceData = deviceData;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Device Details, ID: " + deviceData.deviceId);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View detailsView = inflater.inflate(R.layout.dialog_geocache_deviceinfo, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(detailsView);

        // Add action buttons
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener()
               {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       // sign in the user ...
                   }
               });

        textView_IdString = (TextView)detailsView.findViewById(R.id.textView_IdentificationString);
        textView_PIN = (TextView)detailsView.findViewById(R.id.textView_PIN);
        textView_Latitude = (TextView)detailsView.findViewById(R.id.textView_Latitude);
        textView_Longitude = (TextView)detailsView.findViewById(R.id.textView_Longitude);
        textView_HintString = (TextView)detailsView.findViewById(R.id.textView_HintString);
        textView_LastVisit = (TextView)detailsView.findViewById(R.id.textView_LastVisitTimestamp);
        textView_NumVisits = (TextView)detailsView.findViewById(R.id.textView_NumberOfVisits);
        textView_HardwareVer = (TextView)detailsView.findViewById(R.id.textView_HardwareRevision);
        textView_ManfID = (TextView)detailsView.findViewById(R.id.textView_ManufacturerID);
        textView_ModelNum = (TextView)detailsView.findViewById(R.id.textView_ModelNumber);
        textView_SoftwareVer = (TextView)detailsView.findViewById(R.id.textView_SoftwareRevision);
        textView_SerialNum = (TextView)detailsView.findViewById(R.id.textView_SerialNumber);
        textView_BatteryVoltage = (TextView)detailsView.findViewById(R.id.textView_BatteryVoltage);
        textView_BatteryStatus = (TextView)detailsView.findViewById(R.id.textView_BatteryStatus);
        textView_OperatingTime = (TextView)detailsView.findViewById(R.id.textView_CumulativeOperatingTime);
        textView_OperatingTimeResolution = (TextView)detailsView.findViewById(R.id.textView_CumulativeOperatingTimeResolution);

        refreshData();

        //Set up button handlers
        Button button_ProgramDevice = (Button)detailsView.findViewById(R.id.button_ProgramDevice);
        button_ProgramDevice.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //Warn user they are going to program a device that was not originally programmed by this device
                        //so they will be overwriting someone else's data
                        //Note: The device based PIN is generated from the Settings.Secure.ANDROID_ID
                        //and is persistent for this app on the phone it is installed on.
                        if((deviceData.programmableData.identificationString.trim().length() != 0 //If the device itself is programmed
                                    && deviceData.programmableData.PIN != 0xFFFFFFFF)    //and the PIN is programmed
                                && !getDeviceBasedPIN(getActivity()).equals(deviceData.programmableData.PIN)) //and the app's persistent PIN doesn't match the programmed one
                        {
                            //Warn the user the PINs don't match and make them accept responsibility for reprogramming someone else's geocache
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                            dialogBuilder.setTitle("Incorrect PIN");
                            dialogBuilder.setMessage("Your device's PIN does not match the geocache's PIN. This means you are probably trying to overwrite someone else's geocache data. If you continue, this might make them upset and hurt their feelings");
                            dialogBuilder.setPositiveButton("I don't care, do it anyway", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            showProgramDevice();
                                        }
                                    });
                            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            //let dialog be dismissed
                                        }
                                    });

                            dialogBuilder.create().show();
                        }
                        else    //else the PIN matches or it is unprogrammed so we can proceed straight to programming
                        {
                            showProgramDevice();
                        }
                    }
                });

        Button button_ReqAuthToken = (Button)detailsView.findViewById(R.id.button_RequestAuthToken);
        button_ReqAuthToken.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Dialog_GeoRequestAuthToken reqDialog = new Dialog_GeoRequestAuthToken(geoPcc, deviceData.deviceId, getDeviceBasedPIN(getActivity()));
                        reqDialog.show(getFragmentManager(), "ReqAuthTokenDialog");
                    }
                });

        return builder.create();
    }

    /**
     * Launches the dialog to program the device.
     */
    public void showProgramDevice()
    {
        //Show the programming dialog
        Dialog_GeoProgramDevice programDialog = new Dialog_GeoProgramDevice(geoPcc, deviceData, new ResultReceiver(null)
                {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData)
                    {
                        if(resultCode == 0) //Succesful programming
                        {
                            //Update our current info
                            resultData.setClassLoader(this.getClass().getClassLoader());
                            ProgrammableGeocacheDeviceData progdData = ((GeocacheDeviceData)resultData.getParcelable(GeocacheDeviceData.KEY_DEFAULT_GEOCACHEDEVICEDATAKEY)).programmableData;
                            if(progdData.identificationString != null)
                                deviceData.programmableData.identificationString = progdData.identificationString;
                            if(progdData.PIN != null)
                                deviceData.programmableData.PIN = progdData.PIN;
                            if(progdData.latitude != null)
                                deviceData.programmableData.latitude = progdData.latitude;
                            if(progdData.longitude != null)
                                deviceData.programmableData.longitude = progdData.longitude;
                            if(progdData.hintString != null)
                                deviceData.programmableData.hintString = progdData.hintString;
                            if(progdData.lastVisitTimestamp != null)
                                deviceData.programmableData.lastVisitTimestamp = progdData.lastVisitTimestamp;
                            if(progdData.numberOfVisits != null)
                                deviceData.programmableData.numberOfVisits = progdData.numberOfVisits;

                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Toast.makeText(getActivity(), "Programming Successful", Toast.LENGTH_SHORT).show();
                                    refreshData();
                                }
                            });
                        }
                        else if(resultCode == -1)   //Device communication failure, device was removed from list and data is now invalid, bail to scan list
                        {
                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Toast.makeText(getActivity(), "Device Communication Failure, dropped from list", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            });
                        }
                    }
                });
        programDialog.show(getFragmentManager(), "ProgramDeviceDialog");
    }

    protected void refreshData()
    {
        //Set data display with current values
        if(deviceData.programmableData.identificationString != null)
            textView_IdString.setText(String.valueOf(deviceData.programmableData.identificationString));
        if(deviceData.programmableData.PIN != null)
            textView_PIN.setText(String.valueOf(deviceData.programmableData.PIN));
        if(deviceData.programmableData.latitude != null)
            textView_Latitude.setText(String.valueOf(deviceData.programmableData.latitude.setScale(5, BigDecimal.ROUND_HALF_UP)));
        if(deviceData.programmableData.longitude != null)
            textView_Longitude.setText(String.valueOf(deviceData.programmableData.longitude.setScale(5, BigDecimal.ROUND_HALF_UP)));
        if(deviceData.programmableData.hintString != null)
            textView_HintString.setText(String.valueOf(deviceData.programmableData.hintString));
        if(deviceData.programmableData.lastVisitTimestamp != null)
        {
            DateFormat df = DateFormat.getDateTimeInstance();
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            textView_LastVisit.setText(df.format(deviceData.programmableData.lastVisitTimestamp.getTime()));
        }
        if(deviceData.programmableData.numberOfVisits != null)
            textView_NumVisits.setText(String.valueOf(deviceData.programmableData.numberOfVisits));

        textView_HardwareVer.setText(String.valueOf(deviceData.hardwareRevision));
        textView_ManfID.setText(String.valueOf(deviceData.manufacturerID));
        textView_ModelNum.setText(String.valueOf(deviceData.modelNumber));
        textView_SoftwareVer.setText(String.valueOf(deviceData.softwareRevision));
        textView_SerialNum.setText(String.valueOf(deviceData.serialNumber));
        textView_BatteryVoltage.setText(String.valueOf(deviceData.batteryVoltage));
        textView_BatteryStatus.setText(deviceData.batteryStatus.toString());
        textView_OperatingTime.setText(String.valueOf(deviceData.cumulativeOperatingTime));
        textView_OperatingTimeResolution.setText(String.valueOf(deviceData.cumulativeOperatingTimeResolution));
    }

    /**
     * This function returns a PIN that should be static and persistent on the device running this phone.
     * The geocache profile states
     * The ANDROID_ID is not perfect for a static identifier but does the job fine enough for a demo app.
     * (see {@link "http://android-developers.blogspot.ca/2011/03/identifying-app-installations.html"})
     * @param context The current context.
     * @return PIN for programming that is unique to and persistent on this device
     */
    public static Long getDeviceBasedPIN(Context context)
    {
        String IdString = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        if(IdString == null)
            return 336699L;
        else
            return Long.decode("0x" + IdString.substring(IdString.length() - 9)) % 4294967295L;
    }

}
