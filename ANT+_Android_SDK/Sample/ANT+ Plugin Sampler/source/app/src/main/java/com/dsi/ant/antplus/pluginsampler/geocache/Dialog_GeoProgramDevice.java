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
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.ResultReceiver;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.Dialog_ProgressWaiter;
import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc.GeocacheDeviceData;
import com.dsi.ant.plugins.antplus.pcc.AntPlusGeocachePcc.GeocacheRequestStatus;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Dialog to allow programming a geocache device.
 */
public class Dialog_GeoProgramDevice extends DialogFragment
{
    EditText editText_IdString;
    EditText editText_PIN;
    EditText editText_Latitude;
    EditText editText_Longitude;
    EditText editText_HintString;
    EditText editText_NumVisits;
    TextView textView_LastVisitDate;
    TextView textView_LastVisitTime;

    CheckBox checkBox_EnableIdString;
    CheckBox checkBox_EnablePIN;
    CheckBox checkBox_EnableLatitude;
    CheckBox checkBox_EnableLongitude;
    CheckBox checkBox_EnableHintString;
    CheckBox checkBox_EnableLastVisit;

    RadioButton radioButton_ClearExisitingData;

    private GeocacheDeviceData deviceData;
    private AntPlusGeocachePcc geoPcc;
    private ResultReceiver resultRcvr;
    private GregorianCalendar currentDisplayDatetime;

    /**
     * Constructor.
     * @param geoPcc The PCC instance the device is available on.
     * @param currentDeviceData The current device data of the device to program.
     * @param resultReceiver The receiver to handle the results of this request.
     */
    public Dialog_GeoProgramDevice(AntPlusGeocachePcc geoPcc,
        GeocacheDeviceData currentDeviceData, ResultReceiver resultReceiver)
    {
        this.geoPcc = geoPcc;
        this.deviceData = currentDeviceData;
        this.resultRcvr = resultReceiver;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Program Device " + deviceData.deviceId);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View detailsView = inflater.inflate(R.layout.dialog_geocache_programdevice, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(detailsView);

        // Add action buttons
        //Note we override the positive button in show() below so we can prevent it from closing
        builder.setPositiveButton("Begin Programing", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Let dialog dismiss
            }
        });


        checkBox_EnableIdString  = (CheckBox)detailsView.findViewById(R.id.checkBox_EnableIdentifcationString);
        checkBox_EnablePIN = (CheckBox)detailsView.findViewById(R.id.checkBox_EnablePIN);
        checkBox_EnableLatitude = (CheckBox)detailsView.findViewById(R.id.checkBox_EnableLatitude);
        checkBox_EnableLongitude = (CheckBox)detailsView.findViewById(R.id.checkBox_EnableLongitude);
        checkBox_EnableHintString = (CheckBox)detailsView.findViewById(R.id.checkBox_EnableHintString);
        checkBox_EnableLastVisit = (CheckBox)detailsView.findViewById(R.id.checkBox_EnableLastVisitInfo);

        final TextView textView_NumVisitsTitle = (TextView)detailsView.findViewById(R.id.textView_NumberOfVisitsTitle);
        final TextView textView_LastVisitDateTitle = (TextView)detailsView.findViewById(R.id.textView_LastVisitDateTitle);
        final TextView textView_LastVisitTimeTitle = (TextView)detailsView.findViewById(R.id.textView_LastVisitTimeTitle);

        editText_IdString = (EditText)detailsView.findViewById(R.id.editText_IdentificationString);
        editText_PIN = (EditText)detailsView.findViewById(R.id.editText_PIN);
        editText_Latitude = (EditText)detailsView.findViewById(R.id.editText_Latitude);
        editText_Longitude = (EditText)detailsView.findViewById(R.id.editText_Longitude);
        editText_HintString = (EditText)detailsView.findViewById(R.id.editText_HintString);
        editText_NumVisits = (EditText)detailsView.findViewById(R.id.editText_NumberOfVisits);
        textView_LastVisitDate = (TextView)detailsView.findViewById(R.id.textView_LastVisitDate);
        textView_LastVisitTime = (TextView)detailsView.findViewById(R.id.textView_LastVisitTime);

        radioButton_ClearExisitingData = (RadioButton)detailsView.findViewById(R.id.radioButton_ClearExistingData);


        //Hook up checkboxes to enable/disable fields
        checkBox_EnableIdString.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                editText_IdString.setEnabled(isChecked);
                if(isChecked && editText_IdString.getText().length() == 0)
                    editText_IdString.setText("ID STR");
            }
        });
        checkBox_EnablePIN.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                editText_PIN.setEnabled(isChecked);
                if(isChecked && editText_PIN.getText().length() == 0)
                    editText_PIN.setText("123456");
            }
        });
        checkBox_EnableLatitude.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                editText_Latitude.setEnabled(isChecked);
                if(isChecked && editText_Latitude.getText().length() == 0)
                    editText_Latitude.setText("-40.1");
            }
        });
        checkBox_EnableLongitude.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                editText_Longitude.setEnabled(isChecked);
                if(isChecked && editText_Longitude.getText().length() == 0)
                    editText_Longitude.setText("-20.1");
            }
        });
        checkBox_EnableHintString.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                editText_HintString.setEnabled(isChecked);
                if(isChecked && editText_HintString.getText().length() == 0)
                    editText_HintString.setText("Hint string.");
            }
        });
        checkBox_EnableLastVisit.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                textView_NumVisitsTitle.setEnabled(isChecked);
                textView_LastVisitDateTitle.setEnabled(isChecked);
                textView_LastVisitTimeTitle.setEnabled(isChecked);
                editText_NumVisits.setEnabled(isChecked);
                textView_LastVisitDate.setEnabled(isChecked);
                textView_LastVisitTime.setEnabled(isChecked);

                if(isChecked)
                {
                    if(editText_NumVisits.length() == 0)
                        editText_NumVisits.setText("0");
                    if(textView_LastVisitDate.length() == 0)
                        textView_LastVisitDate.setText("dd/mmm/yyyy");
                    if(textView_LastVisitTime.length() == 0)
                        textView_LastVisitDate.setText("hh:mm");
                }
            }
        });

        //Set data
        editText_IdString.setText(String.valueOf(deviceData.programmableData.identificationString));
        editText_PIN.setText(String.valueOf(deviceData.programmableData.PIN));

        if(deviceData.programmableData.latitude != null)
            editText_Latitude.setText(String.valueOf(deviceData.programmableData.latitude.setScale(5, BigDecimal.ROUND_HALF_UP)));
        else
            editText_Latitude.setText("");

        if(deviceData.programmableData.longitude != null)
            editText_Longitude.setText(String.valueOf(deviceData.programmableData.longitude.setScale(5, BigDecimal.ROUND_HALF_UP)));
        else
            editText_Longitude.setText("");

        if(deviceData.programmableData.hintString != null)
            editText_HintString.setText(String.valueOf(deviceData.programmableData.hintString));
        else
            editText_HintString.setText("");

        if(deviceData.programmableData.numberOfVisits != null)
            editText_NumVisits.setText(String.valueOf(deviceData.programmableData.numberOfVisits));
        else
            editText_NumVisits.setText("");

        if(deviceData.programmableData.lastVisitTimestamp != null)
        {
            currentDisplayDatetime = deviceData.programmableData.lastVisitTimestamp;
            updateDateAndTime();
        }
        else
        {
            textView_LastVisitDate.setText("");
            textView_LastVisitTime.setText("");
        }


        //Hook up date and time fields to pickers
        textView_LastVisitDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(currentDisplayDatetime == null)
                    currentDisplayDatetime = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
                DatePickerDialog d = new DatePickerDialog(getActivity(), new OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        currentDisplayDatetime.set(year, monthOfYear, dayOfMonth);
                        updateDateAndTime();
                    }
                },
                currentDisplayDatetime.get(Calendar.YEAR),
                currentDisplayDatetime.get(Calendar.MONTH),
                currentDisplayDatetime.get(Calendar.DAY_OF_MONTH));
                d.show();
            }
        });

        textView_LastVisitTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(currentDisplayDatetime == null)
                    currentDisplayDatetime = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
                TimePickerDialog d = new TimePickerDialog(getActivity(), new OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        currentDisplayDatetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        currentDisplayDatetime.set(Calendar.MINUTE, minute);
                        updateDateAndTime();
                    }
                },
                currentDisplayDatetime.get(Calendar.HOUR_OF_DAY),
                currentDisplayDatetime.get(Calendar.MINUTE),
                false);
                d.show();
            }
        });


        return builder.create();
    }

    /**
     * Updates the last visit date and time display.
     */
    public void updateDateAndTime()
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                SimpleDateFormat df = new SimpleDateFormat("d MMM yyyy", Locale.CANADA);
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                SimpleDateFormat tf = new SimpleDateFormat("h:mm a", Locale.CANADA);
                tf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date d = currentDisplayDatetime.getTime();
                textView_LastVisitDate.setText(df.format(d));
                textView_LastVisitTime.setText(tf.format(d));
            }
        });
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
                    //Read in user set values
                    final GeocacheDeviceData newDeviceData = new GeocacheDeviceData();

                    if(checkBox_EnableIdString.isChecked())
                        newDeviceData.programmableData.identificationString = editText_IdString.getText().toString();

                    if(checkBox_EnablePIN.isChecked())
                        newDeviceData.programmableData.PIN = Long.parseLong(editText_PIN.getText().toString());

                    if(checkBox_EnableLatitude.isChecked())
                        newDeviceData.programmableData.latitude = new BigDecimal(editText_Latitude.getText().toString());

                    if(checkBox_EnableLongitude.isChecked())
                        newDeviceData.programmableData.longitude = new BigDecimal(editText_Longitude.getText().toString());

                    if(checkBox_EnableHintString.isChecked())
                        newDeviceData.programmableData.hintString = editText_HintString.getText().toString();

                    if(checkBox_EnableLastVisit.isChecked())
                    {
                        newDeviceData.programmableData.numberOfVisits = Integer.parseInt(editText_NumVisits.getText().toString());
                        newDeviceData.programmableData.lastVisitTimestamp = currentDisplayDatetime;
                    }

                    final Dialog_ProgressWaiter progressDialog = new Dialog_ProgressWaiter("Programming Device");

                    //Use the current deviceID and PIN
                    boolean reqSubmitted = geoPcc.requestDeviceProgramming(deviceData.deviceId, deviceData.programmableData.PIN,
                        radioButton_ClearExisitingData.isChecked(), newDeviceData.programmableData,
                        new AntPlusGeocachePcc.IProgrammingFinishedReceiver()
                    {
                        @Override
                        public void onNewProgrammingFinished(GeocacheRequestStatus status)
                        {
                            StringBuilder error = new StringBuilder("Error Programming: ");

                            switch(status)
                            {
                                case SUCCESS:
                                    getActivity().runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            progressDialog.dismiss();
                                            Bundle b = new Bundle();
                                            b.putParcelable(GeocacheDeviceData.KEY_DEFAULT_GEOCACHEDEVICEDATAKEY, newDeviceData);
                                            resultRcvr.send(0, b);
                                            dismiss();
                                        }
                                    });
                                    return;

                                case FAIL_DEVICE_COMMUNICATION_FAILURE:
                                    //When this occurs, it also results in the device being removed from the list,
                                    //we should bail to scan screen on this result
                                    getActivity().runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            progressDialog.dismiss();
                                            resultRcvr.send(-1, null);
                                            dismiss();
                                        }
                                    });
                                    return;

                                case FAIL_DEVICE_NOT_IN_LIST:
                                    error.append("Device no longer in list");
                                    break;
                                case FAIL_ALREADY_BUSY_EXTERNAL:
                                    error.append("Device is busy");
                                    break;
                                case FAIL_BAD_PARAMS:
                                    error.append("Bad Parameters");
                                    break;
                                case FAIL_NO_PERMISSION:
                                    error.append("No Permission");  //shouldn't happen ever here because we always send matching PIN
                                    break;
                                case FAIL_DEVICE_DATA_NOT_DOWNLOADED:
                                    error.append("Device Data Not Downloaded");
                                    break;
                                case UNRECOGNIZED:
                                    //TODO This flag indicates that an unrecognized value was sent by the service, an upgrade of your PCC may be required to handle this new value.
                                    error.append("Unrecognized failure");
                                    break;
                            }

                            final String errorStr = error.toString();
                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), errorStr, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    },
                    progressDialog.getUpdateReceiver());

                    if(reqSubmitted)
                        progressDialog.show(getActivity().getSupportFragmentManager(), "ProgrammingProgressDialog");
                    else
                        Toast.makeText(getActivity(), "Error Programming Device: PCC already busy or dead", Toast.LENGTH_SHORT).show();


                    //now both dialogs stay open. They get closed in the programming result handler.
                }
            });
        }
    }
}
