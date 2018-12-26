/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2014
All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.multidevicesearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;

import java.util.EnumSet;

/**
 * Starts the search activity after allowing user to select desired device types
 */
public class Activity_MultiDeviceFilter extends Activity
{
    Context mContext;
    ListView mListView;
    Button mSearch;
    Button mSelectAll;

    ArrayAdapter<String> mDeviceTypeListAdapter;
    SparseArray<DeviceType> mDeviceTypeList = new SparseArray<DeviceType>();
    SparseBooleanArray mIsChecked;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multidevice_filter);

        mContext = getApplicationContext();
        mSearch = (Button) findViewById(R.id.button_StartMultiDeviceSearch);
        mSelectAll = (Button) findViewById(R.id.button_SelectAll);

        mListView = (ListView) findViewById(R.id.listView_MultiDeviceFilter);
        mListView.setItemsCanFocus(false);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mDeviceTypeListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, getResources().getStringArray(
                        R.array.device_types));
        mListView.setAdapter(mDeviceTypeListAdapter);

        mIsChecked = mListView.getCheckedItemPositions();

        // Add all device types
        int i = 0;
        mDeviceTypeList.put(i++, DeviceType.BIKE_POWER);
        mDeviceTypeList.put(i++, DeviceType.CONTROLLABLE_DEVICE);
        mDeviceTypeList.put(i++, DeviceType.FITNESS_EQUIPMENT);
        mDeviceTypeList.put(i++, DeviceType.BLOOD_PRESSURE);
        mDeviceTypeList.put(i++, DeviceType.GEOCACHE);
        mDeviceTypeList.put(i++, DeviceType.ENVIRONMENT);
        mDeviceTypeList.put(i++, DeviceType.WEIGHT_SCALE);
        mDeviceTypeList.put(i++, DeviceType.HEARTRATE);
        mDeviceTypeList.put(i++, DeviceType.BIKE_SPDCAD);
        mDeviceTypeList.put(i++, DeviceType.BIKE_CADENCE);
        mDeviceTypeList.put(i++, DeviceType.BIKE_SPD);
        mDeviceTypeList.put(i++, DeviceType.STRIDE_SDM);

        mSearch.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EnumSet<DeviceType> set = EnumSet.noneOf(DeviceType.class);

                for (int j = 0; j < mIsChecked.size(); j++)
                {
                    int key = mIsChecked.keyAt(j);
                    if (mIsChecked.get(key))
                        set.add(mDeviceTypeList.get(key));
                }

                if (set.isEmpty())
                {
                    Toast.makeText(mContext, "Please select device type(s) to filter on.",
                            Toast.LENGTH_SHORT).show();

                } else
                {
                    Intent i = new Intent(mContext, Activity_MultiDeviceSearchSampler.class);
                    Bundle args = new Bundle();
                    args.putSerializable(Activity_MultiDeviceSearchSampler.FILTER_KEY, set);
                    i.putExtra(Activity_MultiDeviceSearchSampler.BUNDLE_KEY, args);
                    // Listen for search stopped results
                    startActivityForResult(i, Activity_MultiDeviceSearchSampler.RESULT_SEARCH_STOPPED);
                }
            }
        });

        mSelectAll.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                for (int i = 0; i < mListView.getCount(); i++)
                {
                    mListView.setItemChecked(i, true);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == Activity_MultiDeviceSearchSampler.RESULT_SEARCH_STOPPED)
        {
            RequestAccessResult result = RequestAccessResult.getValueFromInt(data.getIntExtra(Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT, 0));
            switch(result)
            {
                case SUCCESS:
                    // Do nothing on success
                    break;
                case CHANNEL_NOT_AVAILABLE:
                    Toast.makeText(this, "Channel Not Available", Toast.LENGTH_SHORT).show();
                    break;
                case ADAPTER_NOT_DETECTED:
                    Toast.makeText(
                            this,
                            "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.",
                            Toast.LENGTH_SHORT).show();
                    break;
                case BAD_PARAMS:
                    // Note: Since we compose all the params ourself, we should
                    // never see this result
                    Toast.makeText(this, "Bad request parameters.", Toast.LENGTH_SHORT).show();
                    break;
                case OTHER_FAILURE:
                    Toast.makeText(this, "RequestAccess failed. See logcat for details.",
                            Toast.LENGTH_SHORT).show();
                    break;
                case DEPENDENCY_NOT_INSTALLED:
                    AlertDialog.Builder adlgBldr = new AlertDialog.Builder(this);
                    adlgBldr.setTitle("Missing Dependency");
                    adlgBldr.setMessage("The required service\n\""
                            + AntPlusHeartRatePcc.getMissingDependencyName()
                            + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
                    adlgBldr.setCancelable(true);
                    adlgBldr.setPositiveButton("Go to Store", new android.content.DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent startStore = null;
                            startStore = new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id="
                                            + AntPlusHeartRatePcc.getMissingDependencyPackageName()));
                            startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            mContext.startActivity(startStore);
                        }
                    });
                    adlgBldr.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });

                    final AlertDialog waitDialog = adlgBldr.create();
                    waitDialog.show();
                    break;
                case USER_CANCELLED:
                    break;
                case UNRECOGNIZED:
                    Toast.makeText(this, "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Unrecognized result: " + result, Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    }

}
