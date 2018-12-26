/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2013
All rights reserved.
*/

package com.dsi.ant.antplus.pluginsampler.fitnessequipment;

import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;


/**
 * Dialog to allow configuring a weight scale user profile
 */
public class Dialog_ConfigSettings extends DialogFragment
{
    public static final String SETTINGS_NAME = "settings_friendly_name";
    public static final String SETTINGS_AGE = "settings_age";
    public static final String SETTINGS_HEIGHT = "settings_height";
    public static final String SETTINGS_WEIGHT = "settings_weight";
    public static final String SETTINGS_GENDER = "settings_gender";
    public static final String INCLUDE_WORKOUT = "include_workout";

    EditText et_friendlyName;
    EditText et_age;
    EditText et_height;
    EditText et_weight;
    RadioButton rb_female;
    RadioButton rb_male;
    CheckBox cb_workout;

    Settings settings;

    public Settings getSettings()
    {
        return settings;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Settings Configuration");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View detailsView = inflater.inflate(R.layout.dialog_fe_settings, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(detailsView);

        // Add action buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent i = new Intent(Dialog_ConfigSettings.this.getActivity(), Activity_FitnessEquipmentSampler.class);
                Bundle b = new Bundle();
                b.putString(SETTINGS_NAME, et_friendlyName.getText().toString());
                b.putShort(SETTINGS_AGE, Short.parseShort(et_age.getText().toString()));
                b.putFloat(SETTINGS_HEIGHT, Float.parseFloat(et_height.getText().toString())/100f); // Convert to m
                b.putFloat(SETTINGS_WEIGHT, Float.parseFloat(et_weight.getText().toString()));
                b.putBoolean(SETTINGS_GENDER, rb_male.isChecked());
                b.putBoolean(INCLUDE_WORKOUT,cb_workout.isChecked());
                i.putExtras(b);
                startActivity(i);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //Let dialog dismiss
                    }
                });

        et_friendlyName = (EditText) detailsView.findViewById(R.id.editText_FriendlyName);
        et_age = (EditText) detailsView.findViewById(R.id.editText_Age);
        et_height = (EditText) detailsView.findViewById(R.id.editText_Height);
        et_weight = (EditText) detailsView.findViewById(R.id.editText_Weight);
        rb_female = (RadioButton) detailsView.findViewById(R.id.radioButton_Female);
        rb_male = (RadioButton) detailsView.findViewById(R.id.radioButton_Male);
        cb_workout = (CheckBox) detailsView.findViewById(R.id.checkBox_Workout);

        return builder.create();
    }

}
