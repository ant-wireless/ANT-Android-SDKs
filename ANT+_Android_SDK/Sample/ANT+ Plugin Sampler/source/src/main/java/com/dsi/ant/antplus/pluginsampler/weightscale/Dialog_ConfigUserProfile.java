/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2013
All rights reserved.
*/

package com.dsi.ant.antplus.pluginsampler.weightscale;

import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusWeightScalePcc.UserProfile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Dialog to allow configuring a weight scale user profile
 */
public class Dialog_ConfigUserProfile extends DialogFragment
{
    TextView tv_userProfileID;
    EditText et_age;
    EditText et_height;
    EditText et_activityLevel;
    CheckBox cb_lifetimeAthlete;
    RadioButton rb_female;
    RadioButton rb_male;

    UserProfile profile;

    /**
     * Constructor.
     * @param profile The user profile object to display/modify.
     */
    public Dialog_ConfigUserProfile(UserProfile profile)
    {
        this.profile = profile;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("User Profile Configuration");
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View detailsView = inflater.inflate(R.layout.dialog_weightscale_configuserprofile, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(detailsView);

        // Add action buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                profile.age = Integer.parseInt(et_age.getText().toString());
                profile.height = Integer.parseInt(et_height.getText().toString());
                profile.activityLevel = Integer.parseInt(et_activityLevel.getText().toString());
                profile.lifetimeAthlete = cb_lifetimeAthlete.isChecked();
                if(rb_male.isChecked())
                    profile.gender = AntPlusWeightScalePcc.Gender.MALE;
                else
                    profile.gender = AntPlusWeightScalePcc.Gender.FEMALE;
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

        tv_userProfileID = (TextView) detailsView.findViewById(R.id.textView_UserProfileID);
        et_age = (EditText) detailsView.findViewById(R.id.editText_Age);
        et_height = (EditText) detailsView.findViewById(R.id.editText_Height);
        et_activityLevel = (EditText) detailsView.findViewById(R.id.editText_ActivityLevel);
        cb_lifetimeAthlete = (CheckBox) detailsView.findViewById(R.id.checkBox_LifetimeAthlete);
        rb_female = (RadioButton) detailsView.findViewById(R.id.radioButton_Female);
        rb_male = (RadioButton) detailsView.findViewById(R.id.radioButton_Male);

        // Set data
        tv_userProfileID.setText(String.valueOf(profile.getUserProfileID()));
        et_age.setText(String.valueOf(profile.age));
        et_height.setText(String.valueOf(profile.height));
        et_activityLevel.setText(String.valueOf(profile.activityLevel));
        cb_lifetimeAthlete.setChecked(profile.lifetimeAthlete);
        if(profile.gender == AntPlusWeightScalePcc.Gender.MALE)
            rb_male.setChecked(true);
        else
            rb_female.setChecked(true);

        return builder.create();
    }

}
