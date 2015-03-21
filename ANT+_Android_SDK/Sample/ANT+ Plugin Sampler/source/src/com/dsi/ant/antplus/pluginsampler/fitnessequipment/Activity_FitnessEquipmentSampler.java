/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2014
All rights reserved.
 */

package com.dsi.ant.antplus.pluginsampler.fitnessequipment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.antplus.pluginsampler.R;
import com.dsi.ant.antplus.pluginsampler.multidevicesearch.Activity_MultiDeviceSearchSampler;
import com.dsi.ant.plugins.antplus.common.FitFileCommon.FitFile;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.CalculatedTrainerDistanceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.CalculatedTrainerSpeedReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.CalibrationInProgress;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.CalibrationResponse;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.Capabilities;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.CommandStatus;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.EquipmentState;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.EquipmentType;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.HeartRateDataSource;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.IBasicResistanceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.IBikeDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.ICalculatedTrainerPowerReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.ICalibrationInProgressReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.ICalibrationResponseReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.ICapabilitiesReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.IClimberDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.ICommandStatusReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.IEllipticalDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.IFitnessEquipmentStateReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.IGeneralFitnessEquipmentDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.IGeneralMetabolicDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.IGeneralSettingsReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.ILapOccuredReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.INordicSkierDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.IRawTrainerDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.IRawTrainerTorqueDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.IRowerDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.ITargetPowerReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.ITrackResistanceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.ITrainerStatusReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.ITreadmillDataReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.IUserConfigurationReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.IWindResistanceReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.Settings;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.TrainerDataSource;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.TrainerStatusFlag;
import com.dsi.ant.plugins.antplus.pcc.AntPlusFitnessEquipmentPcc.UserConfiguration;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pcc.defines.EventFlag;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestStatus;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.CommonDataPage;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IManufacturerIdentificationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IProductInformationReceiver;
import com.dsi.ant.plugins.antplus.pccbase.AntPlusCommonPcc.IRequestFinishedReceiver;
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.EnumSet;

/**
 * Connects to Fitness Equipment Plugin and display all the event data.
 */
public class Activity_FitnessEquipmentSampler extends Activity
{
    AntPlusFitnessEquipmentPcc fePcc = null;
    PccReleaseHandle<AntPlusFitnessEquipmentPcc> releaseHandle = null;
    Settings settings;
    FitFile[] files;

    TextView tv_status;
    TextView tv_estTimestamp;

    TextView tv_feType;
    TextView tv_state;
    TextView tv_laps;
    TextView tv_cycleLength;
    TextView tv_inclinePercentage;
    TextView tv_resistanceLevel;
    TextView tv_mets;
    TextView tv_caloricBurn;
    TextView tv_calories;
    TextView tv_time;
    TextView tv_distance;
    TextView tv_speed;
    TextView tv_heartRate;
    TextView tv_heartRateSource;
    TextView tv_treadmillCadence;
    TextView tv_treadmillNegVertDistance;
    TextView tv_treadmillPosVertDistance;
    TextView tv_ellipticalPosVertDistance;
    TextView tv_ellipticalStrides;
    TextView tv_ellipticalCadence;
    TextView tv_ellipticalPower;
    TextView tv_bikeCadence;
    TextView tv_bikePower;
    TextView tv_rowerStrokes;
    TextView tv_rowerCadence;
    TextView tv_rowerPower;
    TextView tv_climberStrideCycles;
    TextView tv_climberCadence;
    TextView tv_climberPower;
    TextView tv_skierStrides;
    TextView tv_skierCadence;
    TextView tv_skierPower;

    TextView textView_CalculatedPower;
    TextView textView_CalculatedPowerSource;
    TextView textView_CalculatedSpeed;
    TextView textView_CalculatedSpeedSource;
    TextView textView_CalculatedDistance;
    TextView textView_CalculatedDistanceSource;
    TextView textView_TrainerUpdateEventCount;
    TextView textView_TrainerInstantaneousCadence;
    TextView textView_TrainerInstantaneousPower;
    TextView textView_TrainerAccumulatedPower;
    TextView textView_TrainerTorqueUpdateEventCount;
    TextView textView_AccumulatedWheelTicks;
    TextView textView_AccumulatedWheelPeriod;
    TextView textView_TrainerAccumulatedTorque;
    TextView textView_MaximumResistance;
    TextView textView_BasicResistanceSupport;
    TextView textView_TargetPowerSupport;
    TextView textView_SimulationModeSupport;
    TextView textView_ZeroOffsetCalPending;
    TextView textView_SpinDownCalPending;
    TextView textView_TemperatureCondition;
    TextView textView_SpeedCondition;
    TextView textView_CurrentTemperature;
    TextView textView_TargetSpeed;
    TextView textView_TargetSpinDownTime;
    TextView textView_ZeroOffsetCalSuccess;
    TextView textView_SpinDownCalSuccess;
    TextView textView_Temperature;
    TextView textView_ZeroOffset;
    TextView textView_SpinDownTime;
    TextView textView_LastRxCmdId;
    TextView textView_SequenceNumber;
    TextView textView_CommandStatus;
    TextView textView_RawData;
    TextView textView_TotalResistanceStatus;
    TextView textView_TargetPowerStatus;
    TextView textView_WindResistanceCoefficientStatus;
    TextView textView_WindSpeedStatus;
    TextView textView_DraftingFactorStatus;
    TextView textView_GradeStatus;
    TextView textView_RollingResistanceCoefficientStatus;
    TextView textView_UserWeight;
    TextView textView_BicycleWeight;
    TextView textView_BicycleWheelDiameter;
    TextView textView_GearRatio;
    TextView textView_TotalResistance;
    TextView textView_TargetPower;
    TextView textView_Grade;
    TextView textView_RollingResistanceCoefficient;
    TextView textView_WindResistanceCoefficient;
    TextView textView_WindSpeed;
    TextView textView_DraftingFactor;

    TextView tv_hardwareRevision;
    TextView tv_manufacturerID;
    TextView tv_modelNumber;

    TextView tv_mainSoftwareRevision;
    TextView tv_supplementalSoftwareRevision;
    TextView tv_serialNumber;

    TextView tv_deviceNumber;

    Button button_requestZeroOffsetCalibration;
    Button button_requestSpinDownCalibration;
    Button button_requestCapabilities;
    Button button_setUserConfiguration;
    Button button_requestUserConfiguration;
    Button button_setBasicResistance;
    Button button_setTargetPower;
    Button button_setWindResistance;
    Button button_setTrackResistance;
    Button button_requestCommandStatus;
    Button button_requestBasicResistance;
    Button button_requestTargetPower;
    Button button_requestWindResistance;
    Button button_requestTrackResistance;
    Button button_requestCommonDataPage;

    boolean subscriptionsDone = false;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitnessequipment);

        tv_status = (TextView)findViewById(R.id.textView_Status);
        tv_deviceNumber = (TextView)findViewById(R.id.textView_DeviceNumber);

        tv_estTimestamp = (TextView)findViewById(R.id.textView_EstTimestamp);

        tv_feType = (TextView) findViewById(R.id.textView_FitnessEquipmentType);
        tv_state = (TextView) findViewById(R.id.textView_State);
        tv_laps = (TextView) findViewById(R.id.textView_Laps);
        tv_cycleLength = (TextView) findViewById(R.id.textView_CycleLength);
        tv_inclinePercentage = (TextView) findViewById(R.id.textView_InclinePercentage);
        tv_resistanceLevel = (TextView) findViewById(R.id.textView_ResistanceLevel);
        tv_mets = (TextView) findViewById(R.id.textView_METS);
        tv_caloricBurn = (TextView) findViewById(R.id.textView_CaloricBurn);
        tv_calories = (TextView) findViewById(R.id.textView_Calories);
        tv_time = (TextView) findViewById(R.id.textView_Time);
        tv_distance = (TextView) findViewById(R.id.textView_Distance);
        tv_speed = (TextView) findViewById(R.id.textView_Speed);
        tv_heartRate = (TextView) findViewById(R.id.textView_HeartRate);
        tv_heartRateSource = (TextView) findViewById(R.id.textView_HeartRateSource);
        tv_treadmillCadence = (TextView) findViewById(R.id.textView_TreadmillCadence);
        tv_treadmillNegVertDistance = (TextView) findViewById(R.id.textView_TreadmillNegVertDistance);
        tv_treadmillPosVertDistance = (TextView) findViewById(R.id.textView_TreadmillPosVertDistance);
        tv_ellipticalPosVertDistance = (TextView) findViewById(R.id.textView_EllipticalPosVertDistance);
        tv_ellipticalStrides = (TextView) findViewById(R.id.textView_EllipticalStrides);
        tv_ellipticalCadence = (TextView) findViewById(R.id.textView_EllipticalCadence);
        tv_ellipticalPower = (TextView) findViewById(R.id.textView_EllipticalPower);
        tv_bikeCadence = (TextView) findViewById(R.id.textView_BikeCadence);
        tv_bikePower = (TextView) findViewById(R.id.textView_BikePower);
        tv_rowerStrokes = (TextView) findViewById(R.id.textView_RowerStrokes);
        tv_rowerCadence = (TextView) findViewById(R.id.textView_RowerCadence);
        tv_rowerPower = (TextView) findViewById(R.id.textView_RowerPower);
        tv_climberStrideCycles = (TextView) findViewById(R.id.textView_ClimberStrideCycles);
        tv_climberCadence = (TextView) findViewById(R.id.textView_ClimberCadence);
        tv_climberPower = (TextView) findViewById(R.id.textView_ClimberPower);
        tv_skierStrides = (TextView) findViewById(R.id.textView_SkierStrides);
        tv_skierCadence = (TextView) findViewById(R.id.textView_SkierCadence);
        tv_skierPower = (TextView) findViewById(R.id.textView_SkierPower);

        textView_CalculatedPower = (TextView) findViewById(R.id.textView_CalculatedPower);
        textView_CalculatedPowerSource = (TextView) findViewById(R.id.textView_CalculatedPowerSource);
        textView_CalculatedSpeed = (TextView) findViewById(R.id.textView_CalculatedSpeed);
        textView_CalculatedSpeedSource = (TextView) findViewById(R.id.textView_CalculatedSpeedSource);
        textView_CalculatedDistance = (TextView) findViewById(R.id.textView_CalculatedDistance);
        textView_CalculatedDistanceSource = (TextView) findViewById(R.id.textView_CalculatedDistanceSource);
        textView_TrainerUpdateEventCount = (TextView) findViewById(R.id.textView_TrainerUpdateEventCount);
        textView_TrainerInstantaneousCadence = (TextView) findViewById(R.id.textView_TrainerInstantaneousCadence);
        textView_TrainerInstantaneousPower = (TextView) findViewById(R.id.textView_TrainerInstantaneousPower);
        textView_TrainerAccumulatedPower = (TextView) findViewById(R.id.textView_TrainerAccumulatedPower);
        textView_TrainerTorqueUpdateEventCount = (TextView) findViewById(R.id.textView_TrainerTorqueUpdateEventCount);
        textView_AccumulatedWheelTicks = (TextView) findViewById(R.id.textView_AccumulatedWheelTicks);
        textView_AccumulatedWheelPeriod = (TextView) findViewById(R.id.textView_AccumulatedWheelPeriod);
        textView_TrainerAccumulatedTorque = (TextView) findViewById(R.id.textView_TrainerAccumulatedTorque);
        textView_MaximumResistance = (TextView) findViewById(R.id.textView_MaximumResistance);
        textView_BasicResistanceSupport = (TextView) findViewById(R.id.textView_BasicResistanceSupport);
        textView_TargetPowerSupport = (TextView) findViewById(R.id.textView_TargetPowerSupport);
        textView_SimulationModeSupport = (TextView) findViewById(R.id.textView_SimulationModeSupport);
        textView_ZeroOffsetCalPending = (TextView) findViewById(R.id.textView_ZeroOffsetCalPending);
        textView_SpinDownCalPending = (TextView) findViewById(R.id.textView_SpinDownCalPending);
        textView_TemperatureCondition = (TextView) findViewById(R.id.textView_TemperatureCondition);
        textView_SpeedCondition = (TextView) findViewById(R.id.textView_SpeedCondition);
        textView_CurrentTemperature = (TextView) findViewById(R.id.textView_CurrentTemperature);
        textView_TargetSpeed = (TextView) findViewById(R.id.textView_TargetSpeed);
        textView_TargetSpinDownTime = (TextView) findViewById(R.id.textView_TargetSpinDownTime);
        textView_ZeroOffsetCalSuccess = (TextView) findViewById(R.id.textView_ZeroOffsetCalSuccess);
        textView_SpinDownCalSuccess = (TextView) findViewById(R.id.textView_SpinDownCalSuccess);
        textView_Temperature = (TextView) findViewById(R.id.textView_Temperature);
        textView_ZeroOffset = (TextView) findViewById(R.id.textView_ZeroOffset);
        textView_SpinDownTime = (TextView) findViewById(R.id.textView_SpinDownTime);
        textView_LastRxCmdId = (TextView) findViewById(R.id.textView_LastRxCmdId);
        textView_SequenceNumber = (TextView) findViewById(R.id.textView_SequenceNumber);
        textView_CommandStatus = (TextView) findViewById(R.id.textView_CommandStatus);
        textView_RawData = (TextView) findViewById(R.id.textView_RawData);
        textView_TotalResistanceStatus = (TextView) findViewById(R.id.textView_TotalResistanceStatus);
        textView_TargetPowerStatus = (TextView) findViewById(R.id.textView_TargetPowerStatus);
        textView_WindResistanceCoefficientStatus = (TextView) findViewById(R.id.textView_WindResistanceCoefficientStatus);
        textView_WindSpeedStatus = (TextView) findViewById(R.id.textView_WindSpeedStatus);
        textView_DraftingFactorStatus = (TextView) findViewById(R.id.textView_DraftingFactorStatus);
        textView_GradeStatus = (TextView) findViewById(R.id.textView_GradeStatus);
        textView_RollingResistanceCoefficientStatus = (TextView) findViewById(R.id.textView_RollingResistanceCoefficientStatus);
        textView_UserWeight = (TextView) findViewById(R.id.textView_UserWeight);
        textView_BicycleWeight = (TextView) findViewById(R.id.textView_BicycleWeight);
        textView_BicycleWheelDiameter = (TextView) findViewById(R.id.textView_BicycleWheelDiameter);
        textView_GearRatio = (TextView) findViewById(R.id.textView_GearRatio);
        textView_TotalResistance = (TextView) findViewById(R.id.textView_TotalResistance);
        textView_TargetPower = (TextView) findViewById(R.id.textView_TargetPower);
        textView_Grade = (TextView) findViewById(R.id.textView_Grade);
        textView_RollingResistanceCoefficient = (TextView) findViewById(R.id.textView_RollingResistanceCoefficient);
        textView_WindResistanceCoefficient = (TextView) findViewById(R.id.textView_WindResistanceCoefficient);
        textView_WindSpeed = (TextView) findViewById(R.id.textView_WindSpeed);
        textView_DraftingFactor = (TextView) findViewById(R.id.textView_DraftingFactor);

        tv_hardwareRevision = (TextView)findViewById(R.id.textView_HardwareRevision);
        tv_manufacturerID = (TextView)findViewById(R.id.textView_ManufacturerID);
        tv_modelNumber = (TextView)findViewById(R.id.textView_ModelNumber);

        tv_mainSoftwareRevision = (TextView)findViewById(R.id.textView_MainSoftwareRevision);
        tv_supplementalSoftwareRevision = (TextView)findViewById(R.id.textView_SupplementalSoftwareRevision);
        tv_serialNumber = (TextView)findViewById(R.id.textView_SerialNumber);

        button_requestZeroOffsetCalibration = (Button)findViewById(R.id.button_requestZeroOffsetCalibration);
        button_requestSpinDownCalibration = (Button)findViewById(R.id.button_requestSpinDownCalibration);
        button_requestCapabilities = (Button)findViewById(R.id.button_requestCapabilities);
        button_setUserConfiguration = (Button)findViewById(R.id.button_setUserConfiguration);
        button_requestUserConfiguration = (Button)findViewById(R.id.button_requestUserConfiguration);
        button_setBasicResistance = (Button)findViewById(R.id.button_setBasicResistance);
        button_setTargetPower = (Button)findViewById(R.id.button_setTargetPower);
        button_setWindResistance = (Button)findViewById(R.id.button_setWindResistance);
        button_setTrackResistance = (Button)findViewById(R.id.button_setTrackResistance);
        button_requestCommandStatus = (Button)findViewById(R.id.button_requestCommandStatus);
        button_requestBasicResistance = (Button)findViewById(R.id.button_requestBasicResistance);
        button_requestTargetPower = (Button)findViewById(R.id.button_requestTargetPower);
        button_requestWindResistance = (Button)findViewById(R.id.button_requestWindResistance);
        button_requestTrackResistance = (Button)findViewById(R.id.button_requestTrackResistance);
        button_requestCommonDataPage = (Button)findViewById(R.id.button_requestCommonDataPage);

        b = getIntent().getExtras();
        if(b != null)
        {
            String name = b.getString(Dialog_ConfigSettings.SETTINGS_NAME);
            Settings.Gender gender = Settings.Gender.FEMALE;
            if(b.getBoolean(Dialog_ConfigSettings.SETTINGS_GENDER))
                gender = Settings.Gender.MALE;
            short age = b.getShort(Dialog_ConfigSettings.SETTINGS_AGE);
            float height = b.getFloat(Dialog_ConfigSettings.SETTINGS_HEIGHT);
            float weight = b.getFloat(Dialog_ConfigSettings.SETTINGS_WEIGHT);

            settings = new Settings(name, gender, age, height, weight);

            if(b.getBoolean(Dialog_ConfigSettings.INCLUDE_WORKOUT))
            {
                try
                {
                    // Make available a FIT workout file to the fitness equipment
                    // The sample file included with this project was obtained from the FIT SDK, v7.10
                    InputStream is = getAssets().open("WorkoutRepeatSteps.fit");
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int next;
                    while((next = is.read()) != -1)
                        bos.write(next);
                    bos.flush();
                    is.close();
                    FitFile workoutFile = new FitFile(bos.toByteArray());
                    workoutFile.setFileType((short) 5);  // Make sure to set the File Type, so this information is also available to the fitness equipment
                    // Refer to the FIT SDK for more details on FIT file types
                    files = new FitFile[] { workoutFile};
                }
                catch (IOException e)
                {
                    files = null;
                }
            }
        }

        final IRequestFinishedReceiver requestFinishedReceiver = new IRequestFinishedReceiver()
        {
            @Override
            public void onNewRequestFinished(final RequestStatus requestStatus)
            {
                runOnUiThread(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            switch(requestStatus)
                            {
                                case SUCCESS:
                                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Successfully Sent", Toast.LENGTH_SHORT).show();
                                    break;
                                case FAIL_PLUGINS_SERVICE_VERSION:
                                    Toast.makeText(Activity_FitnessEquipmentSampler.this,
                                        "Plugin Service Upgrade Required?",
                                        Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Failed to be Sent", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
            }
        };

        button_requestZeroOffsetCalibration.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO The fitness equipment may request calibration from the user, which is when this command would be sent.
                boolean submitted = fePcc.requestZeroOffsetCalibration(requestFinishedReceiver, null, null);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }

        });

        button_requestSpinDownCalibration.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO The fitness equipment may request calibration from the user, which is when this command would be sent.
                boolean submitted = fePcc.requestSpinDownCalibration(requestFinishedReceiver, null, null);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }

        });

        button_requestCapabilities.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO The capabilities should be requested before attempting to send new control settings to determine which modes are supported.
                boolean submitted = fePcc.requestCapabilities(requestFinishedReceiver, null);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }
        });

        button_setUserConfiguration.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                UserConfiguration config = new UserConfiguration();
                config.bicycleWeight = new BigDecimal("10.00");         //10kg bike weight
                config.gearRatio = new BigDecimal("0.03");              //0.03 gear ratio
                config.bicycleWheelDiameter = new BigDecimal("0.70");   //0.70m wheel diameter
                config.userWeight = new BigDecimal("75.00");            //75kg user

                boolean submitted = fePcc.requestSetUserConfiguration(config, requestFinishedReceiver);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }
        });

        button_requestUserConfiguration.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean submitted = fePcc.requestUserConfiguration(requestFinishedReceiver, null);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }
        });

        button_setBasicResistance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO The capabilities should be requested before attempting to send new control settings to determine which modes are supported.
                boolean submitted = fePcc.getTrainerMethods().requestSetBasicResistance(new BigDecimal("4.5"), requestFinishedReceiver);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }
        });

        button_setTargetPower.setOnClickListener(new View.OnClickListener()
        {
            BigDecimal targetPower = new BigDecimal("42.25");   //42.25%

            @Override
            public void onClick(View v)
            {
                //TODO The capabilities should be requested before attempting to send new control settings to determine which modes are supported.
                boolean submitted = fePcc.getTrainerMethods().requestSetTargetPower(targetPower, requestFinishedReceiver);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }
        });

        button_setWindResistance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO The capabilities should be requested before attempting to send new control settings to determine which modes are supported.
                //null indicates default values
                boolean submitted = fePcc.getTrainerMethods().requestSetWindResistance(null, null, null, null, null, requestFinishedReceiver);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }
        });

        button_setTrackResistance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO The capabilities should be requested before attempting to send new control settings to determine which modes are supported.
                //null indicates default values
                boolean submitted = fePcc.getTrainerMethods().requestSetTrackResistance(null, null, requestFinishedReceiver);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }
        });

        button_requestCommandStatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO This can be requested after a command is sent to determine it's status on the trainer.
                boolean submitted = fePcc.getTrainerMethods().requestCommandStatus(requestFinishedReceiver, null);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }
        });

        button_requestBasicResistance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean submitted = fePcc.getTrainerMethods().requestBasicResistance(requestFinishedReceiver, null);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }
        });

        button_requestTargetPower.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean submitted = fePcc.getTrainerMethods().requestTargetPower(requestFinishedReceiver, null);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }
        });

        button_requestWindResistance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean submitted = fePcc.getTrainerMethods().requestWindResistance(requestFinishedReceiver, null);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }
        });

        button_requestTrackResistance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean submitted = fePcc.getTrainerMethods().requestTrackResistance(requestFinishedReceiver, null);

                if(!submitted)
                    Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
            }
        });

        button_requestCommonDataPage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Create a String array of names from the enum
                final CommonDataPage[] commonDataPages = CommonDataPage.values();
                String[] names = new String[commonDataPages.length];

                for(int i = 0; i < names.length; i++)
                {
                    names[i] = commonDataPages[i].name();
                }

                // Build the list alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_FitnessEquipmentSampler.this);
                builder.setTitle("Pick a common data page")
                .setItems(names, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        CommonDataPage selectedCommonDataPage = commonDataPages[which];
                        boolean submitted = fePcc.requestCommonDataPage(selectedCommonDataPage, requestFinishedReceiver);

                        if(!submitted)
                            Toast.makeText(Activity_FitnessEquipmentSampler.this, "Request Could not be Made", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        resetPcc();
    }

    /**
     * Resets the PCC connection to request access again and clears any existing display data.
     */
    private void resetPcc()
    {
        //Release the old access if it exists
        if(releaseHandle != null)
            releaseHandle.close();

        //Reset event subscriptions
        subscriptionsDone = false;

        //Reset the text display
        tv_status.setText("Connecting...");

        tv_estTimestamp.setText("---");

        tv_feType.setText("---");
        tv_state.setText("---");
        tv_laps.setText("---");
        tv_cycleLength.setText("---");
        tv_inclinePercentage.setText("---");
        tv_resistanceLevel.setText("---");
        tv_mets.setText("---");
        tv_caloricBurn.setText("---");
        tv_calories.setText("---");
        tv_time.setText("---");
        tv_distance.setText("---");
        tv_speed.setText("---");
        tv_heartRate.setText("---");
        tv_heartRateSource.setText("---");
        tv_treadmillCadence.setText("---");
        tv_treadmillNegVertDistance.setText("---");
        tv_treadmillPosVertDistance.setText("---");
        tv_ellipticalStrides.setText("---");
        tv_ellipticalCadence.setText("---");
        tv_ellipticalPower.setText("---");
        tv_bikeCadence.setText("---");
        tv_bikePower.setText("---");
        tv_rowerStrokes.setText("---");
        tv_rowerCadence.setText("---");
        tv_rowerPower.setText("---");
        tv_climberStrideCycles.setText("---");
        tv_climberPower.setText("---");
        tv_skierStrides.setText("---");
        tv_skierCadence.setText("---");
        tv_skierPower.setText("---");

        tv_hardwareRevision.setText("---");
        tv_manufacturerID.setText("---");
        tv_modelNumber.setText("---");

        tv_mainSoftwareRevision.setText("---");
        tv_supplementalSoftwareRevision.setText("");
        tv_serialNumber.setText("---");

        final IPluginAccessResultReceiver<AntPlusFitnessEquipmentPcc> mPluginAccessResultReceiver =
                new IPluginAccessResultReceiver<AntPlusFitnessEquipmentPcc>()
                {
                //Handle the result, connecting to events on success or reporting failure to user.
                @Override
                public void onResultReceived(AntPlusFitnessEquipmentPcc result,
                    RequestAccessResult resultCode, DeviceState initialDeviceState)
                {
                    switch(resultCode)
                    {
                        case SUCCESS:
                            fePcc = result;
                            tv_deviceNumber.setText(String.valueOf(fePcc.getAntDeviceNumber()));    //Get device ID
                            if(initialDeviceState == DeviceState.CLOSED)
                                tv_status.setText(fePcc.getDeviceName() + ": " + "Waiting for FE Session Request");
                            else
                                tv_status.setText(result.getDeviceName() + ": " + initialDeviceState);
                            subscribeToEvents();
                            break;
                        case CHANNEL_NOT_AVAILABLE:
                            Toast.makeText(Activity_FitnessEquipmentSampler.this, "Channel Not Available", Toast.LENGTH_SHORT).show();
                            tv_status.setText("Error. Do Menu->Reset.");
                            break;
                        case ADAPTER_NOT_DETECTED:
                            Toast.makeText(Activity_FitnessEquipmentSampler.this, "ANT Adapter Not Available. Built-in ANT hardware or external adapter required.", Toast.LENGTH_SHORT).show();
                            tv_status.setText("Error. Do Menu->Reset.");
                            break;
                        case BAD_PARAMS:
                            //Note: Since we compose all the params ourself, we should never see this result
                            Toast.makeText(Activity_FitnessEquipmentSampler.this, "Bad request parameters.", Toast.LENGTH_SHORT).show();
                            tv_status.setText("Error. Do Menu->Reset.");
                            break;
                        case OTHER_FAILURE:
                            Toast.makeText(Activity_FitnessEquipmentSampler.this, "RequestAccess failed. See logcat for details.", Toast.LENGTH_SHORT).show();
                            tv_status.setText("Error. Do Menu->Reset.");
                            break;
                        case DEPENDENCY_NOT_INSTALLED:
                            tv_status.setText("Error. Do Menu->Reset.");
                            AlertDialog.Builder adlgBldr = new AlertDialog.Builder(Activity_FitnessEquipmentSampler.this);
                            adlgBldr.setTitle("Missing Dependency");
                            adlgBldr.setMessage("The required service\n\"" + AntPlusFitnessEquipmentPcc.getMissingDependencyName() + "\"\n was not found. You need to install the ANT+ Plugins service or you may need to update your existing version if you already have it. Do you want to launch the Play Store to get it?");
                            adlgBldr.setCancelable(true);
                            adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Intent startStore = null;
                                    startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + AntPlusFitnessEquipmentPcc.getMissingDependencyPackageName()));
                                    startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    Activity_FitnessEquipmentSampler.this.startActivity(startStore);
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
                            break;
                        case USER_CANCELLED:
                            tv_status.setText("Cancelled. Do Menu->Reset.");
                            break;
                        case UNRECOGNIZED:
                            Toast.makeText(Activity_FitnessEquipmentSampler.this,
                                "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                                Toast.LENGTH_SHORT).show();
                            tv_status.setText("Error. Do Menu->Reset.");
                            break;
                        default:
                            Toast.makeText(Activity_FitnessEquipmentSampler.this, "Unrecognized result: " + resultCode, Toast.LENGTH_SHORT).show();
                            tv_status.setText("Error. Do Menu->Reset.");
                            break;
                    }
                }

                /**
                 * Subscribe to all the data events, connecting them to display their data.
                 */
                private void subscribeToEvents()
                {
                    fePcc.subscribeGeneralFitnessEquipmentDataEvent(new IGeneralFitnessEquipmentDataReceiver()
                    {
                        @Override
                        public void onNewGeneralFitnessEquipmentData(final long estTimestamp,
                                EnumSet<EventFlag> eventFlags, final BigDecimal elapsedTime,
                                final long cumulativeDistance, final BigDecimal instantaneousSpeed,
                                final boolean virtualInstantaneousSpeed, final int instantaneousHeartRate,
                                final HeartRateDataSource heartRateDataSource)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    tv_estTimestamp.setText(String.valueOf(estTimestamp));
                                    if(elapsedTime.intValue() == -1)
                                        tv_time.setText("Invalid");
                                    else
                                        tv_time.setText(String.valueOf(elapsedTime) + "s");

                                    if(cumulativeDistance == -1)
                                        tv_distance.setText("Invalid");
                                    else
                                        tv_distance.setText(String.valueOf(cumulativeDistance) + "m");

                                    if(instantaneousSpeed.intValue() == -1)
                                        tv_speed.setText("Invalid");
                                    else
                                        tv_speed.setText(String.valueOf(instantaneousSpeed) + "m/s");

                                    if(virtualInstantaneousSpeed)
                                        tv_speed.setText(tv_speed.getText() + " (Virtual)");

                                    if(instantaneousHeartRate == -1)
                                        tv_heartRate.setText("Invalid");
                                    else
                                        tv_heartRate.setText(String.valueOf(instantaneousHeartRate) + "bpm");

                                    switch(heartRateDataSource)
                                    {
                                        case ANTPLUS_HRM:
                                        case EM_5KHz:
                                        case HAND_CONTACT_SENSOR:
                                        case UNKNOWN:
                                            tv_heartRateSource.setText(heartRateDataSource.toString());
                                            break;
                                        case UNRECOGNIZED:
                                            Toast.makeText(Activity_FitnessEquipmentSampler.this,
                                                "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                                                Toast.LENGTH_SHORT).show();
                                            break;
                                    }

                                }
                            });
                        }
                    });

                    fePcc.subscribeLapOccuredEvent(new ILapOccuredReceiver()
                    {

                        @Override
                        public void onNewLapOccured(final long estTimestamp, final EnumSet<EventFlag> eventFlags,
                            final int lapCount)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                    tv_laps.setText(String.valueOf(lapCount));
                                }
                            });
                        }

                    });

                    fePcc.subscribeGeneralSettingsEvent(new IGeneralSettingsReceiver()
                    {

                        @Override
                        public void onNewGeneralSettings(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final BigDecimal cycleLength,
                            final BigDecimal inclinePercentage, final int resistanceLevel)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                    if(cycleLength.intValue() == -1)
                                        tv_cycleLength.setText("Invalid");
                                    else
                                        tv_cycleLength.setText(String.valueOf(cycleLength) + "m");

                                    if(inclinePercentage.intValue() == 0x7FFF)
                                        tv_inclinePercentage.setText("Invalid");
                                    else
                                        tv_inclinePercentage.setText(String.valueOf(inclinePercentage) + "%");

                                    if(resistanceLevel == -1)
                                        tv_resistanceLevel.setText("Invalid");
                                    else
                                        //TODO If this is a Fitness Equipment Controls device, this represents the current set resistance level at 0.5% per unit from 0% to 100%
                                        tv_resistanceLevel.setText(String.valueOf(resistanceLevel));
                                }
                            });

                        }

                    });

                    fePcc.subscribeGeneralMetabolicDataEvent(new IGeneralMetabolicDataReceiver()
                    {
                        @Override
                        public void onNewGeneralMetabolicData(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final BigDecimal instantaneousMetabolicEquivalents,
                            final BigDecimal instantaneousCaloricBurn, final long cumulativeCalories)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                    if(instantaneousMetabolicEquivalents.intValue() == -1)
                                        tv_mets.setText("Invalid");
                                    else
                                        tv_mets.setText(String.valueOf(instantaneousMetabolicEquivalents) + "METs");

                                    if(instantaneousCaloricBurn.intValue() == -1)
                                        tv_caloricBurn.setText("Invalid");
                                    else
                                        tv_caloricBurn.setText(String.valueOf(instantaneousCaloricBurn) + "kcal/h");

                                    if(cumulativeCalories == -1)
                                        tv_calories.setText("Invalid");
                                    else
                                        tv_calories.setText(String.valueOf(cumulativeCalories) + "kcal");
                                }
                            });
                        }
                    });

                    fePcc.subscribeManufacturerIdentificationEvent(new IManufacturerIdentificationReceiver()
                    {
                        @Override
                        public void onNewManufacturerIdentification(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final int hardwareRevision,
                            final int manufacturerID, final int modelNumber)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                    tv_hardwareRevision.setText(String.valueOf(hardwareRevision));
                                    tv_manufacturerID.setText(String.valueOf(manufacturerID));
                                    tv_modelNumber.setText(String.valueOf(modelNumber));
                                }
                            });
                        }
                    });

                    fePcc.subscribeProductInformationEvent(new IProductInformationReceiver()
                    {
                        @Override
                        public void onNewProductInformation(final long estTimestamp,
                            final EnumSet<EventFlag> eventFlags, final int mainSoftwareRevision,
                            final int supplementalSoftwareRevision, final long serialNumber)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                    tv_mainSoftwareRevision.setText(String
                                        .valueOf(mainSoftwareRevision));

                                    if (supplementalSoftwareRevision == -2)
                                        // Plugin Service installed does not support supplemental revision
                                        tv_supplementalSoftwareRevision.setText("?");
                                    else if (supplementalSoftwareRevision == 0xFF)
                                        // Invalid supplemental revision
                                        tv_supplementalSoftwareRevision.setText("");
                                    else
                                        // Valid supplemental revision
                                        tv_supplementalSoftwareRevision.setText(", " + String
                                            .valueOf(supplementalSoftwareRevision));

                                    tv_serialNumber.setText(String.valueOf(serialNumber));
                                }
                            });
                        }
                    });
                }
                };

        IDeviceStateChangeReceiver mDeviceStateChangeReceiver =
                //Receives state changes and shows it on the status display line
                new IDeviceStateChangeReceiver()
                {
                    @Override
                    public void onDeviceStateChange(final DeviceState newDeviceState)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                //Note: The state here is the state of our data receiver channel which is closed until the ANTFS session is established
                                if(newDeviceState == DeviceState.CLOSED)
                                {
                                    tv_status.setText(fePcc.getDeviceName() + ": " + "Waiting for FE Session Request");
                                }
                                else
                                {
                                    tv_status.setText(fePcc.getDeviceName() + ": " + newDeviceState);
                                }
                            }
                        });
                    }
                };

        IFitnessEquipmentStateReceiver mFitnessEquipmentStateReceiver =
                new IFitnessEquipmentStateReceiver()
        {
            @Override
            public void onNewFitnessEquipmentState(final long estTimestamp,
                EnumSet<EventFlag> eventFlags, final EquipmentType equipmentType,
                final EquipmentState equipmentState)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tv_estTimestamp.setText(String.valueOf(estTimestamp));


                        switch(equipmentType)
                        {
                            case GENERAL:
                                tv_feType.setText("GENERAL");
                                break;
                            case TREADMILL:
                                tv_feType.setText("TREADMILL");

                                if(subscriptionsDone)
                                    break;

                                fePcc.getTreadmillMethods().subscribeTreadmillDataEvent(new ITreadmillDataReceiver()
                                {

                                    @Override
                                    public void onNewTreadmillData(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final int instantaneousCadence,
                                        final BigDecimal cumulativeNegVertDistance, final BigDecimal cumulativePosVertDistance)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                                if(instantaneousCadence == -1)
                                                    tv_treadmillCadence.setText("Invalid");
                                                else
                                                    tv_treadmillCadence.setText(String.valueOf(instantaneousCadence) + "strides/min");
                                                if(cumulativeNegVertDistance.intValue() == 1)
                                                    tv_treadmillNegVertDistance.setText("Invalid");
                                                else
                                                    tv_treadmillNegVertDistance.setText(String.valueOf(cumulativeNegVertDistance) + "m");
                                                if(cumulativePosVertDistance.intValue() == -1)
                                                    tv_treadmillPosVertDistance.setText("Invalid");
                                                else
                                                    tv_treadmillPosVertDistance.setText(String.valueOf(cumulativePosVertDistance) + "m");
                                            }
                                        });

                                    }});
                                subscriptionsDone = true;
                                break;
                            case ELLIPTICAL:
                                tv_feType.setText("ELLIPTICAL");

                                if(subscriptionsDone)
                                    break;

                                fePcc.getEllipticalMethods().subscribeEllipticalDataEvent(new IEllipticalDataReceiver()
                                {

                                    @Override
                                    public void onNewEllipticalData(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final BigDecimal cumulativePosVertDistance,
                                        final long cumulativeStrides, final int instantaneousCadence, final int instantaneousPower)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                                if(instantaneousCadence == -1)
                                                    tv_ellipticalCadence.setText("Invalid");
                                                else
                                                    tv_ellipticalCadence.setText(String.valueOf(instantaneousCadence) + "strides/min");
                                                if(cumulativePosVertDistance.intValue() == -1)
                                                    tv_ellipticalPosVertDistance.setText("Invalid");
                                                else
                                                    tv_ellipticalPosVertDistance.setText(String.valueOf(cumulativePosVertDistance) + "m");
                                                if(cumulativeStrides == -1)
                                                    tv_ellipticalStrides.setText("Invalid");
                                                else
                                                    tv_ellipticalStrides.setText(String.valueOf(cumulativeStrides));
                                                if(instantaneousPower == -1)
                                                    tv_ellipticalPower.setText("Invalid");
                                                else
                                                    tv_ellipticalPower.setText(String.valueOf(instantaneousPower) + "W");
                                            }
                                        });
                                    }
                                });
                                subscriptionsDone = true;
                                break;
                            case BIKE:
                                tv_feType.setText("BIKE");

                                if(subscriptionsDone)
                                    break;

                                fePcc.getBikeMethods().subscribeBikeDataEvent(new IBikeDataReceiver()
                                {

                                    @Override
                                    public void onNewBikeData(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final int instantaneousCadence,
                                        final int instantaneousPower)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                                if(instantaneousCadence == -1)
                                                    tv_bikeCadence.setText("Invalid");
                                                else
                                                    tv_bikeCadence.setText(String.valueOf(instantaneousCadence) + "rpm");
                                                if(instantaneousPower == -1)
                                                    tv_bikePower.setText("Invalid");
                                                else
                                                    tv_bikePower.setText(String.valueOf(instantaneousPower) + "W");
                                            }
                                        });

                                    }
                                });
                                subscriptionsDone = true;
                                break;
                            case ROWER:
                                tv_feType.setText("ROWER");

                                if(subscriptionsDone)
                                    break;

                                fePcc.getRowerMethods().subscribeRowerDataEvent(new IRowerDataReceiver()
                                {

                                    @Override
                                    public void onNewRowerData(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final long cumulativeStrokes,
                                        final int instantaneousCadence, final int instantaneousPower)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                                if(cumulativeStrokes == -1)
                                                    tv_rowerStrokes.setText("Invalid");
                                                else
                                                    tv_rowerStrokes.setText(String.valueOf(cumulativeStrokes));
                                                if(instantaneousCadence == -1)
                                                    tv_rowerCadence.setText("Invalid");
                                                else
                                                    tv_rowerCadence.setText(String.valueOf(instantaneousCadence) + "strokes/min");
                                                if(instantaneousPower == -1)
                                                    tv_rowerPower.setText("Invalid");
                                                else
                                                    tv_rowerPower.setText(String.valueOf(instantaneousPower) + "W");
                                            }
                                        });


                                    }
                                });
                                subscriptionsDone = true;
                                break;
                            case CLIMBER:
                                tv_feType.setText("CLIMBER");

                                if(subscriptionsDone)
                                    break;

                                fePcc.getClimberMethods().subscribeClimberDataEvent(new IClimberDataReceiver()
                                {

                                    @Override
                                    public void onNewClimberData(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final long cumulativeStrideCycles,
                                        final int instantaneousCadence, final int instantaneousPower)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                                if(cumulativeStrideCycles == -1)
                                                    tv_climberStrideCycles.setText("Invalid");
                                                else
                                                    tv_climberStrideCycles.setText(String.valueOf(cumulativeStrideCycles));
                                                if(instantaneousCadence == -1)
                                                    tv_climberCadence.setText("Invalid");
                                                else
                                                    tv_climberCadence.setText(String.valueOf(instantaneousCadence) + "strides/min");
                                                if(instantaneousPower == -1)
                                                    tv_climberPower.setText("Invalid");
                                                else
                                                    tv_climberPower.setText(String.valueOf(instantaneousPower) + "W");
                                            }
                                        });

                                    }
                                });
                                subscriptionsDone = true;
                                break;
                            case NORDICSKIER:
                                tv_feType.setText("NORDIC SKIER");

                                if(subscriptionsDone)
                                    break;

                                fePcc.getNordicSkierMethods().subscribeNordicSkierDataEvent(new INordicSkierDataReceiver()
                                {
                                    @Override
                                    public void onNewNordicSkierData(final long estTimestamp, final EnumSet<EventFlag> eventFlags, final long cumulativeStrides,
                                        final int instantaneousCadence, final int instantaneousPower)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                                if(cumulativeStrides == -1)
                                                    tv_skierStrides.setText("Invalid");
                                                else
                                                    tv_skierStrides.setText(String.valueOf(cumulativeStrides));
                                                if(instantaneousCadence == -1)
                                                    tv_skierCadence.setText("Invalid");
                                                else
                                                    tv_skierCadence.setText(String.valueOf(instantaneousCadence) + "strides/min");
                                                if(instantaneousPower == -1)
                                                    tv_skierPower.setText("Invalid");
                                                else
                                                    tv_skierPower.setText(String.valueOf(instantaneousPower) + "W");
                                            }
                                        });

                                    }

                                });
                                subscriptionsDone = true;
                                break;
                            case TRAINER:
                                tv_feType.setText("TRAINER");

                                if(subscriptionsDone)
                                    break;

                                fePcc.getTrainerMethods().subscribeCalculatedTrainerPowerEvent(new ICalculatedTrainerPowerReceiver()
                                {
                                    @Override
                                    public void onNewCalculatedTrainerPower(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final TrainerDataSource dataSource, final BigDecimal calculatedPower)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));
                                                textView_CalculatedPower.setText(String.valueOf(calculatedPower) + "W");
                                                String source;

                                                //NOTE: The calculated power event will send an initial value code if it needed to calculate a NEW average.
                                                //This is important if using the calculated power event to record user data, as an initial value indicates an average could not be guaranteed.
                                                //The event prioritizes calculating with torque data over power only data.
                                                switch(dataSource)
                                                {
                                                    case COAST_OR_STOP_DETECTED:
                                                        //A coast or stop condition detected by the ANT+ Plugin.
                                                        //This is automatically sent by the plugin after 3 seconds of unchanging events.
                                                        //NOTE: This value should be ignored by apps which are archiving the data for accuracy.
                                                    case INITIAL_VALUE_TRAINER_DATA:
                                                        //New data calculated from initial value data source
                                                    case TRAINER_DATA:
                                                    case INITIAL_VALUE_TRAINER_TORQUE_DATA:
                                                        //New data calculated from initial value data source
                                                    case TRAINER_TORQUE_DATA:
                                                        source = dataSource.toString();
                                                        break;
                                                    case UNRECOGNIZED:
                                                        Toast.makeText(
                                                            Activity_FitnessEquipmentSampler.this,
                                                            "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                                                            Toast.LENGTH_SHORT).show();
                                                    default:
                                                        source = "N/A";
                                                        break;
                                                }

                                                textView_CalculatedPowerSource.setText(source);
                                            }
                                        });
                                    }
                                });
                                fePcc.getTrainerMethods().subscribeCalculatedTrainerSpeedEvent(new CalculatedTrainerSpeedReceiver(new BigDecimal("0.70"))   //0.70m wheel diameter
                                {
                                    @Override
                                    public void onNewCalculatedTrainerSpeed(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final TrainerDataSource dataSource, final BigDecimal calculatedSpeed)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));
                                                textView_CalculatedSpeed.setText(String.valueOf(calculatedSpeed) + "km/h");
                                                String source;

                                                //NOTE: The calculated speed event will send an initial value code if it needed to calculate a NEW average.
                                                //This is important if using the calculated speed event to record user data, as an initial value indicates an average could not be guaranteed.
                                                switch(dataSource)
                                                {
                                                    case COAST_OR_STOP_DETECTED:
                                                        //A coast or stop condition detected by the ANT+ Plugin.
                                                        //This is automatically sent by the plugin after 3 seconds of unchanging events.
                                                        //NOTE: This value should be ignored by apps which are archiving the data for accuracy.
                                                    case INITIAL_VALUE_TRAINER_TORQUE_DATA:
                                                        //New data calculated from initial value data source
                                                    case TRAINER_TORQUE_DATA:
                                                        source = dataSource.toString();
                                                        break;
                                                    case UNRECOGNIZED:
                                                        Toast.makeText(
                                                            Activity_FitnessEquipmentSampler.this,
                                                            "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                                                            Toast.LENGTH_SHORT).show();
                                                    default:
                                                        source = "N/A";
                                                        break;
                                                }

                                                textView_CalculatedSpeedSource.setText(source);
                                            }
                                        });
                                    }
                                });
                                fePcc.getTrainerMethods().subscribeCalculatedTrainerDistanceEvent(new CalculatedTrainerDistanceReceiver(new BigDecimal("0.70")) //0.70m wheel diameter
                                {

                                    @Override
                                    public void onNewCalculatedTrainerDistance(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final TrainerDataSource dataSource, final BigDecimal calculatedDistance)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));
                                                textView_CalculatedDistance.setText(String.valueOf(calculatedDistance) + "m");
                                                String source;

                                                //NOTE: The calculated speed event will send an initial value code if it needed to calculate a NEW average.
                                                //This is important if using the calculated speed event to record user data, as an initial value indicates an average could not be guaranteed.
                                                switch(dataSource)
                                                {
                                                    case COAST_OR_STOP_DETECTED:
                                                        //A coast or stop condition detected by the ANT+ Plugin.
                                                        //This is automatically sent by the plugin after 3 seconds of unchanging events.
                                                        //NOTE: This value should be ignored by apps which are archiving the data for accuracy.
                                                    case INITIAL_VALUE_TRAINER_TORQUE_DATA:
                                                        //New data calculated from initial value data source
                                                    case TRAINER_TORQUE_DATA:
                                                        source = dataSource.toString();
                                                        break;
                                                    case UNRECOGNIZED:
                                                        Toast.makeText(
                                                        Activity_FitnessEquipmentSampler.this,
                                                        "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                                                        Toast.LENGTH_SHORT).show();
                                                    default:
                                                        source = "N/A";
                                                        break;
                                                }

                                                textView_CalculatedDistanceSource.setText(source);
                                            }
                                        });
                                    }
                                });
                                fePcc.getTrainerMethods().subscribeRawTrainerDataEvent(new IRawTrainerDataReceiver()
                                {
                                    @Override
                                    public void onNewRawTrainerData(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final long updateEventCount, final int instantaneousCadence, final int instantaneousPower,
                                            final long accumulatedPower)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                                //NOTE: If the update event count has not incremented then the data on this page has not changed.
                                                //Please refer to the ANT+ Fitness Equipment Device Profile for more information.
                                                textView_TrainerUpdateEventCount.setText(String.valueOf(updateEventCount));

                                                if(instantaneousCadence != -1)
                                                    textView_TrainerInstantaneousCadence.setText(String.valueOf(instantaneousCadence) + "RPM");
                                                else
                                                    textView_TrainerInstantaneousCadence.setText("N/A");

                                                if(instantaneousPower != -1)
                                                    textView_TrainerInstantaneousPower.setText(String.valueOf(instantaneousPower) + "W");
                                                else
                                                    textView_TrainerInstantaneousPower.setText("N/A");

                                                if(accumulatedPower != -1)
                                                    textView_TrainerAccumulatedPower.setText(String.valueOf(accumulatedPower) + "W");
                                                else
                                                    textView_TrainerAccumulatedPower.setText("N/A");
                                            }
                                        });
                                    }
                                });
                                fePcc.getTrainerMethods().subscribeRawTrainerTorqueDataEvent(new IRawTrainerTorqueDataReceiver()
                                {
                                    @Override
                                    public void onNewRawTrainerTorqueData(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final long updateEventCount, final long accumulatedWheelTicks, final BigDecimal accumulatedWheelPeriod,
                                            final BigDecimal accumulatedTorque)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                                textView_TrainerTorqueUpdateEventCount.setText(String.valueOf(updateEventCount));
                                                textView_AccumulatedWheelTicks.setText(String.valueOf(accumulatedWheelTicks) + "rotations");
                                                textView_AccumulatedWheelPeriod.setText(String.valueOf(accumulatedWheelPeriod) + "s");
                                                textView_TrainerAccumulatedTorque.setText(String.valueOf(accumulatedTorque) + "Nm");
                                            }
                                        });
                                    }
                                });
                                fePcc.subscribeCapabilitiesEvent(new ICapabilitiesReceiver()
                                {

                                    @Override
                                    public void onNewCapabilities(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final Capabilities capabilities)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                                if(capabilities.maximumResistance != null)
                                                    textView_MaximumResistance.setText(String.valueOf(capabilities.maximumResistance) + "N");
                                                else
                                                    textView_MaximumResistance.setText("N/A");

                                                textView_BasicResistanceSupport.setText(capabilities.basicResistanceModeSupport ? "True" : "False");
                                                textView_TargetPowerSupport.setText(capabilities.targetPowerModeSupport ? "True" : "False");
                                                textView_SimulationModeSupport.setText(capabilities.simulationModeSupport ? "True" : "False");
                                            }
                                        });
                                    }
                                });
                                fePcc.getTrainerMethods().subscribeTrainerStatusEvent(new ITrainerStatusReceiver()
                                {
                                    @Override
                                    public void onNewTrainerStatus(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final EnumSet<TrainerStatusFlag> trainerStatusFlags)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                                for(TrainerStatusFlag flag : trainerStatusFlags)
                                                {
                                                    switch(flag)
                                                    {
                                                        case BICYCLE_POWER_CALIBRATION_REQUIRED:
                                                            break;
                                                        case MAXIMUM_POWER_LIMIT_REACHED:
                                                            break;
                                                        case MINIMUM_POWER_LIMIT_REACHED:
                                                            break;
                                                        case RESISTANCE_CALIBRATION_REQUIRED:
                                                            break;
                                                        case UNRECOGNIZED_FLAG_PRESENT:
                                                            break;
                                                        case USER_CONFIGURATION_REQUIRED:
                                                            break;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                                fePcc.subscribeCalibrationInProgressEvent(new ICalibrationInProgressReceiver()
                                {
                                    @Override
                                    public void onNewCalibrationInProgress(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final CalibrationInProgress calibrationInProgress)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));
                                                textView_ZeroOffsetCalPending.setText(calibrationInProgress.zeroOffsetCalibrationPending ? "Pending" : "Not Requested");
                                                textView_SpinDownCalPending.setText(calibrationInProgress.spinDownCalibrationPending ? "Pending" : "Not Requested");

                                                switch(calibrationInProgress.temperatureCondition)
                                                {
                                                    case CURRENT_TEMPERATURE_OK:
                                                    case CURRENT_TEMPERATURE_TOO_HIGH:
                                                    case CURRENT_TEMPERATURE_TOO_LOW:
                                                    case NOT_APPLICABLE:
                                                    case UNRECOGNIZED:
                                                    default:
                                                        textView_TemperatureCondition.setText(calibrationInProgress.temperatureCondition.toString());
                                                        break;

                                                }

                                                switch(calibrationInProgress.speedCondition)
                                                {
                                                    case CURRENT_SPEED_OK:
                                                    case CURRENT_SPEED_TOO_LOW:
                                                    case NOT_APPLICABLE:
                                                    case UNRECOGNIZED:
                                                    default:
                                                        textView_SpeedCondition.setText(calibrationInProgress.speedCondition.toString());
                                                        break;

                                                }

                                                if(calibrationInProgress.currentTemperature != null)
                                                    textView_CurrentTemperature.setText(calibrationInProgress.currentTemperature.toString() + "C");
                                                else
                                                    textView_CurrentTemperature.setText("N/A");

                                                if(calibrationInProgress.targetSpeed != null)
                                                    textView_TargetSpeed.setText(calibrationInProgress.currentTemperature.toString() + "m/s");
                                                else
                                                    textView_TargetSpeed.setText("N/A");

                                                if(calibrationInProgress.targetSpinDownTime != null)
                                                    textView_TargetSpinDownTime.setText(calibrationInProgress.targetSpinDownTime.toString() + "ms");
                                                else
                                                    textView_TargetSpinDownTime.setText("N/A");
                                            }
                                        });
                                    }
                                });
                                fePcc.subscribeCalibrationResponseEvent(new ICalibrationResponseReceiver()
                                {

                                    @Override
                                    public void onNewCalibrationResponse(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final CalibrationResponse calibrationResponse)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));
                                                textView_ZeroOffsetCalSuccess.setText(calibrationResponse.zeroOffsetCalibrationSuccess ? "Success" : "N/A");
                                                textView_SpinDownCalSuccess.setText(calibrationResponse.spinDownCalibrationSuccess ? "Success" : "N/A");

                                                if(calibrationResponse.temperature != null)
                                                    textView_Temperature.setText(calibrationResponse.temperature.toString() + "C");
                                                else
                                                    textView_Temperature.setText("N/A");

                                                if(calibrationResponse.zeroOffset != null)
                                                    textView_ZeroOffset.setText(calibrationResponse.zeroOffset.toString());
                                                else
                                                    textView_ZeroOffset.setText("N/A");

                                                if(calibrationResponse.spinDownTime != null)
                                                    textView_SpinDownTime.setText(calibrationResponse.spinDownTime.toString() + "ms");
                                                else
                                                    textView_SpinDownTime.setText("N/A");

                                            }
                                        });
                                    }
                                });
                                fePcc.getTrainerMethods().subscribeCommandStatusEvent(new ICommandStatusReceiver()
                                {

                                    @Override
                                    public void onNewCommandStatus(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final CommandStatus commandStatus)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));

                                                if(commandStatus.lastReceivedSequenceNumber != -1)
                                                    textView_SequenceNumber.setText(String.valueOf(commandStatus.lastReceivedSequenceNumber));
                                                else
                                                    textView_SequenceNumber.setText("No control page Rx'd");

                                                switch(commandStatus.status)
                                                {
                                                    case FAIL:
                                                    case NOT_SUPPORTED:
                                                    case PASS:
                                                    case PENDING:
                                                    case REJECTED:
                                                    case UNINITIALIZED:
                                                    case UNRECOGNIZED:
                                                    default:
                                                        textView_CommandStatus.setText(commandStatus.status.toString());
                                                        break;
                                                }

                                                String rawData = "";
                                                for(byte b : commandStatus.rawResponseData)
                                                    rawData += "[" + b + "]";
                                                textView_RawData.setText(rawData);

                                                textView_LastRxCmdId.setText(commandStatus.lastReceivedCommandId.toString());
                                                switch(commandStatus.lastReceivedCommandId)
                                                {
                                                    case BASIC_RESISTANCE:
                                                        textView_TotalResistanceStatus.setText(commandStatus.totalResistance.toString()+ "%");
                                                        break;
                                                    case TARGET_POWER:
                                                        textView_TargetPowerStatus.setText(commandStatus.targetPower.toString() + "W");
                                                        break;
                                                    case WIND_RESISTANCE:
                                                        textView_WindResistanceCoefficientStatus.setText(commandStatus.windResistanceCoefficient.toString() + "kg/m");
                                                        textView_WindSpeedStatus.setText(commandStatus.windSpeed.toString() + "km/h");
                                                        textView_DraftingFactorStatus.setText(commandStatus.draftingFactor.toString());
                                                        break;
                                                    case TRACK_RESISTANCE:
                                                        textView_GradeStatus.setText(commandStatus.grade.toString() + "%");
                                                        textView_RollingResistanceCoefficientStatus.setText(commandStatus.rollingResistanceCoefficient.toString());
                                                        break;
                                                    case NO_CONTROL_PAGE_RECEIVED:
                                                        break;
                                                    case UNRECOGNIZED:
                                                        break;
                                                    default:
                                                        break;
                                                }

                                            }
                                        });
                                    }
                                });
                                fePcc.subscribeUserConfigurationEvent(new IUserConfigurationReceiver()
                                {
                                    @Override
                                    public void onNewUserConfiguration(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final UserConfiguration userConfiguration)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));
                                                textView_UserWeight.setText(userConfiguration.userWeight != null ? userConfiguration.userWeight.toString() + "kg" : "N/A");
                                                textView_BicycleWeight.setText(userConfiguration.bicycleWeight != null ? userConfiguration.bicycleWeight.toString() + "kg" : "N/A");
                                                textView_BicycleWheelDiameter.setText(userConfiguration.bicycleWheelDiameter != null ? userConfiguration.bicycleWheelDiameter.toString() + "m" : "N/A");
                                                textView_GearRatio.setText(userConfiguration.gearRatio != null ? userConfiguration.gearRatio.toString() : "N/A");
                                            }
                                        });
                                    }
                                });
                                fePcc.getTrainerMethods().subscribeBasicResistanceEvent(new IBasicResistanceReceiver()
                                {

                                    @Override
                                    public void onNewBasicResistance(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final BigDecimal totalResistance)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));
                                                textView_TotalResistance.setText(totalResistance.toString() + "%");
                                            }
                                        });
                                    }
                                });
                                fePcc.getTrainerMethods().subscribeTargetPowerEvent(new ITargetPowerReceiver()
                                {

                                    @Override
                                    public void onNewTargetPower(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final BigDecimal targetPower)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));
                                                textView_TargetPower.setText(targetPower.toString() + "W");
                                            }
                                        });
                                    }
                                });
                                fePcc.getTrainerMethods().subscribeTrackResistanceEvent(new ITrackResistanceReceiver()
                                {

                                    @Override
                                    public void onNewTrackResistance(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final BigDecimal grade, final BigDecimal rollingResistanceCoefficient)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));
                                                textView_Grade.setText(grade.toString() + "%");
                                                textView_RollingResistanceCoefficient.setText(rollingResistanceCoefficient.toString());
                                            }
                                        });
                                    }
                                });
                                fePcc.getTrainerMethods().subscribeWindResistanceEvent(new IWindResistanceReceiver()
                                {

                                    @Override
                                    public void onNewWindResistance(final long estTimestamp, EnumSet<EventFlag> eventFlags,
                                            final BigDecimal windResistanceCoefficient, final int windSpeed, final BigDecimal draftingFactor)
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                tv_estTimestamp.setText(String.valueOf(estTimestamp));
                                                textView_WindResistanceCoefficient.setText(windResistanceCoefficient.toString() + "kg/m");
                                                textView_WindSpeed.setText(String.valueOf(windSpeed) + "km/h");
                                                textView_DraftingFactor.setText(draftingFactor.toString());
                                            }
                                        });
                                    }
                                });
                                subscriptionsDone = true;
                                break;
                            case UNKNOWN:
                                tv_feType.setText("UNKNOWN");
                                break;
                            case UNRECOGNIZED:
                                tv_feType.setText("UNRECOGNIZED type, PluginLib upgrade required?");
                                break;
                            default:
                                tv_feType.setText("INVALID: " + String.valueOf(equipmentType));
                                break;
                        }

                        switch(equipmentState)
                        {
                            case ASLEEP_OFF:
                                tv_state.setText("OFF");
                                break;
                            case READY:
                                tv_state.setText("READY");
                                break;
                            case IN_USE:
                                tv_state.setText("IN USE");
                                break;
                            case FINISHED_PAUSED:
                                tv_state.setText("FINISHED/PAUSE");
                                break;
                            case UNRECOGNIZED:
                                Toast.makeText(Activity_FitnessEquipmentSampler.this,
                                    "Failed: UNRECOGNIZED. PluginLib Upgrade Required?",
                                    Toast.LENGTH_SHORT).show();
                            default:
                                tv_state.setText("INVALID: " + equipmentState);
                        }
                    }
                });
            }

        };

        //Make the access request
        //If the Activity was passed the bundle it indicates this Activity was started intended for traditional Fitness Equipment, otherwise connect to broadcast based FE Controls
        //TODO Both PCC request types may be done concurrently on separate release handles in order to simultaneously support both types of FE without requiring prior knowledge of types
        if (b == null)
        {
            releaseHandle = AntPlusFitnessEquipmentPcc.requestNewOpenAccess(this,
                this, mPluginAccessResultReceiver, mDeviceStateChangeReceiver,
                mFitnessEquipmentStateReceiver);
        }
        else if (b.containsKey(
            Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT))
        {
            // device has already been selected through the multi-device search
            MultiDeviceSearchResult result = b.getParcelable(
                Activity_MultiDeviceSearchSampler.EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT);

            releaseHandle = AntPlusFitnessEquipmentPcc.requestNewOpenAccess(this,
                result.getAntDeviceNumber(), 0, mPluginAccessResultReceiver,
                mDeviceStateChangeReceiver, mFitnessEquipmentStateReceiver);
        }
        else
        {
            releaseHandle = AntPlusFitnessEquipmentPcc.requestNewPersonalSessionAccess(this,
                mPluginAccessResultReceiver, mDeviceStateChangeReceiver,
                mFitnessEquipmentStateReceiver, 0, settings, files);
        }
    }

    @Override
    protected void onDestroy()
    {
        releaseHandle.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_heart_rate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_reset:
                resetPcc();
                tv_status.setText("Resetting...");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
