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
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.bloodpressure.Activity_BloodPressureSampler;
import com.dsi.ant.antplus.pluginsampler.controls.Activity_AudioControllableDeviceSampler;
import com.dsi.ant.antplus.pluginsampler.controls.Activity_AudioRemoteControlSampler;
import com.dsi.ant.antplus.pluginsampler.controls.Activity_GenericControllableDeviceSampler;
import com.dsi.ant.antplus.pluginsampler.controls.Activity_GenericRemoteControlSampler;
import com.dsi.ant.antplus.pluginsampler.controls.Activity_VideoControllableDeviceSampler;
import com.dsi.ant.antplus.pluginsampler.controls.Activity_VideoRemoteControlSampler;
import com.dsi.ant.antplus.pluginsampler.fitnessequipment.Activity_FitnessEquipmentSampler;
import com.dsi.ant.antplus.pluginsampler.fitnessequipment.Dialog_ConfigSettings;
import com.dsi.ant.antplus.pluginsampler.geocache.Activity_GeoScanList;
import com.dsi.ant.antplus.pluginsampler.heartrate.Activity_AsyncScanHeartRateSampler;
import com.dsi.ant.antplus.pluginsampler.heartrate.Activity_SearchUiHeartRateSampler;
import com.dsi.ant.antplus.pluginsampler.multidevicesearch.Activity_MultiDeviceFilter;
import com.dsi.ant.antplus.pluginsampler.watchdownloader.Activity_WatchScanList;
import com.dsi.ant.antplus.pluginsampler.weightscale.Activity_WeightScaleSampler;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc;
import com.dsi.ant.plugins.pluginlib.version.PluginLibVersionInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard 'menu' of available sampler activities
 */
public class Activity_Dashboard extends FragmentActivity
{
    protected ListAdapter mAdapter;
    protected ListView mList;

    //Initialize the list
    @SuppressWarnings("serial") //Suppress warnings about hash maps not having custom UIDs
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            Log.i("ANT+ Plugin Sampler", "Version: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (NameNotFoundException e)
        {
            Log.i("ANT+ Plugin Sampler", "Version: " + e.toString());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        List<Map<String,String>> menuItems = new ArrayList<Map<String,String>>();
        menuItems.add(new HashMap<String,String>(){{put("title","Heart Rate Display");put("desc","Receive from HRM sensors");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Bike Power Display");put("desc","Receive from Bike Power sensors");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Bike Cadence Display");put("desc","Receive from Bike Cadence sensors");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Bike Speed and Distance Display");put("desc","Receive from Bike Speed sensors");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Stride SDM Display");put("desc","Receive from SDM sensors");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Watch Downloader Utility");put("desc","Download data from watches");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Fitness Equipment Display");put("desc","Receive from a fitness equipment console");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Fitness Equipment Controls Display");put("desc","Receive from controlable fitness equipment");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Blood Pressure Display");put("desc","Download measurements from blood pressure sensors");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Weight Scale Display");put("desc","Receive from weight scales");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Environment Display");put("desc","Receive from Tempe sensors");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Geocache Utility");put("desc","Read and program Geocache sensors");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Audio Controllable Device");put("desc","Transmit audio player status and receive commands from remote control");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Audio Remote Control");put("desc","Transmit audio player commands and receive status from audio controllable devices");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Video Controllable Device");put("desc","Transmit video player status and receive commands from remote control");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Video Remote Control");put("desc","Transmit video player commands and receive status from video controllable devices");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Generic Controllable Device");put("desc","Receive generic commands from remote control");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Generic Remote Control");put("desc","Transmit generic commands to a generic controllable device");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Async Scan Demo");put("desc","Connect to HRM sensors using the asynchronous scan method");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Multi Device Search");put("desc","Search for multiple device types on the same channel");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Launch ANT+ Plugin Manager");put("desc","Controls device database and default settings");}});

        SimpleAdapter adapter = new SimpleAdapter(this, menuItems, android.R.layout.simple_list_item_2, new String[]{"title","desc"}, new int[]{android.R.id.text1,android.R.id.text2});
        setListAdapter(adapter);

        try
        {
            ((TextView)findViewById(R.id.textView_PluginSamplerVersion)).setText("Sampler Version: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (NameNotFoundException e)
        {
            ((TextView)findViewById(R.id.textView_PluginSamplerVersion)).setText("Sampler Version: ERR");
        }
        ((TextView)findViewById(R.id.textView_PluginLibVersion)).setText("Built w/ PluginLib: " + PluginLibVersionInfo.PLUGINLIB_VERSION_STRING);
        ((TextView)findViewById(R.id.textView_PluginsPkgVersion)).setText("Installed Plugin Version: " + AntPluginPcc.getInstalledPluginsVersionString(this));
    }

    //Launch the appropriate activity/action when a selection is made
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        int j=0;

        if(position == j++)
        {
            Intent i = new Intent(this, Activity_SearchUiHeartRateSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_BikePowerSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_BikeCadenceSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_BikeSpeedDistanceSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_StrideSdmSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_WatchScanList.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            // Settings must be configured before starting the FE Activity
            Dialog_ConfigSettings dialog = new Dialog_ConfigSettings();
            dialog.show(getSupportFragmentManager(), "Configure User Profile");
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_FitnessEquipmentSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_BloodPressureSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_WeightScaleSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_EnvironmentSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_GeoScanList.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_AudioControllableDeviceSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_AudioRemoteControlSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_VideoControllableDeviceSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_VideoRemoteControlSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_GenericControllableDeviceSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_GenericRemoteControlSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_AsyncScanHeartRateSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_MultiDeviceFilter.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            /**
             * Launches the ANT+ Plugin Manager. The ANT+ Plugin Manager provides access to view and modify devices
             * saved in the plugin device database and control default plugin settings. It is also available as a
             * stand alone application, but the ability to launch it from your own application is useful in situations
             * where a user wants extra convenience or doesn't already have the stand alone launcher installed. For example,
             * you could place this launch command in your application's own settings menu.
             */
            if(!AntPluginPcc.startPluginManagerActivity(this))
            {
                AlertDialog.Builder adlgBldr = new AlertDialog.Builder(this);
                adlgBldr.setTitle("Missing Dependency");
                adlgBldr.setMessage("This application requires the ANT+ Plugins, would you like to install them?");
                adlgBldr.setCancelable(true);
                adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent startStore = null;
                        startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.dsi.ant.plugins.antplus"));
                        startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        Activity_Dashboard.this.startActivity(startStore);
                    }
                });
                adlgBldr.setNegativeButton("Cancel", new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                final AlertDialog waitDialog = adlgBldr.create();
                waitDialog.show();
            }
        }
        else
        {
            Toast.makeText(this, "This menu item is not implemented", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets the list display to the give adapter
     * @param adapter Adapter to set list display to
     */
    public void setListAdapter(ListAdapter adapter)
    {
        synchronized (this)
        {
            if (mList != null)
                return;
            mAdapter = adapter;
            mList = (ListView)findViewById(android.R.id.list);
            mList.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?>  parent, View v, int position, long id)
                {
                    onListItemClick((ListView)parent, v, position, id);
                }
            });
            mList.setAdapter(adapter);
        }
    }
}
